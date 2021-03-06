/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.spark.sql.catalyst.expressions

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.analysis.{TypeCheckResult, TypeCoercion}
import org.apache.spark.sql.catalyst.expressions.ArraySortLike.NullOrder
import org.apache.spark.sql.catalyst.expressions.codegen._
import org.apache.spark.sql.catalyst.expressions.codegen.Block._
import org.apache.spark.sql.catalyst.util._
import org.apache.spark.sql.catalyst.util.DateTimeUtils._
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.Platform
import org.apache.spark.unsafe.array.ByteArrayMethods
import org.apache.spark.unsafe.array.ByteArrayMethods.MAX_ROUNDED_ARRAY_LENGTH
import org.apache.spark.unsafe.types.{ByteArray, UTF8String}
import org.apache.spark.unsafe.types.CalendarInterval
import org.apache.spark.util.collection.OpenHashSet



/**
 * Checks if the array (left) has the element (right) using a regex if array element contains *
 */
@ExpressionDescription(
  usage = "_FUNC_(array, value) - Returns true if the array contains the value. Uses simple regex",
  examples = """
    Examples:
      > SELECT _FUNC_(array("hello","bye","sur*"), "surest");
       true
  """)
case class SPArrayContains(left: Expression, right: Expression, add: Expression)
  extends TernaryExpression with ImplicitCastInputTypes {

  override def children: Seq[Expression] = left :: right :: add :: Nil
  // override def dataType: DataType = BooleanType
  override def dataType: DataType = ArrayType(StringType, true)

  // @transient private lazy val ordering: Ordering[Any] =
    // TypeUtils.getInterpretedOrdering(right.dataType)

  override def inputTypes: Seq[AbstractDataType] = Seq(ArrayType, StringType, ArrayType)
//  override def inputTypes: Seq[AbstractDataType] = {
//    (left.dataType, right.dataType,add.dataType) match {
//      case (_, NullType,_) => Seq.empty
//      case (ArrayType(e1, hasNull), e2,_) =>
//        TypeCoercion.findTightestCommonType(e1, e2) match {
//          case Some(dt) => Seq(ArrayType(dt, hasNull), dt)
//          case _ => Seq.empty
//        }
//      case _ => Seq.empty
//    }
//  }

  override def checkInputDataTypes(): TypeCheckResult = {
    (left.dataType, right.dataType, add.dataType) match {
      case (_, NullType, _) =>
        TypeCheckResult.TypeCheckFailure("Null typed values cannot be used as arguments")
      case (ArrayType(e1, _), e2, _) if e1.sameType(e2) =>
        TypeUtils.checkForOrderingExpr(e2, s"function $prettyName")
      case _ => TypeCheckResult.TypeCheckFailure(s"Input to function $prettyName should have " +
        s"been ${ArrayType.simpleString} followed by a value with same element type, but it's " +
        s"[${left.dataType.catalogString}, ${right.dataType.catalogString}].")
    }
  }

  override def nullable: Boolean = {
    left.nullable || right.nullable || left.dataType.asInstanceOf[ArrayType].containsNull
  }

  override def nullSafeEval(arr: Any, value: Any, add: Any): Any = {
    var hasNull = false
    arr.asInstanceOf[ArrayData].foreach(right.dataType, (i, v) =>
      if (v == null) {
        hasNull = true
      }
    )
    if (!add.isInstanceOf[ArrayData]) {
      hasNull = true
    }
    if (hasNull) {
      null
    } else {
      false
    }
  }
  def genApply(ctx: CodegenContext, ev: ExprCode, vVal: String, add: String): String = {
    val arrlen = ctx.freshName("arrlen")
    val newArray = ctx.freshName("newArray")
    return s"""
      |    int ${arrlen} = ${add}.numElements();
      |    UTF8String[] $newArray = new UTF8String[${arrlen}+1];
      |    for(int cnt=0;cnt<${arrlen};cnt++)
      |    {
      |      $newArray[cnt] = (UTF8String) $add
      |      .get(cnt, org.apache.spark.sql.types.DataTypes.StringType);
      |    }
      |    $newArray[${arrlen}] = ${vVal};
      |    ${ev.value} = ArrayData.toArrayData(${newArray});
      """
 }

 def genContains(ctx: CodegenContext, ev: ExprCode, aVal: String, vVal: String,
      av: String, vv: String, add: String, avlen: String, vvlen: String): String = {
    return s"""
     | if (${aVal}.contains(UTF8String.fromString("*"))) {
     |    int ${vvlen} = ${vVal}.toString().length();
     |    UTF8String ${av} = ${aVal}.substring(0,${aVal}.indexOf(UTF8String.fromString("*"),0));
     |    int ${avlen} = ${av}.toString().length();
     |    UTF8String ${vv} = ${vVal};
     |    if (${vvlen} > ${avlen}) {
     |       ${vv} = ${vVal}.substring(0,${avlen});
     |    }
     |    ${vv} = ${vv}.trim();
     |    ${av} = ${av}.trim();
     |    ${this.genEquals(ctx, ev, av, vv, add)}
     | } else {
     |   ${this.genEquals(ctx, ev, aVal, vVal, add)}
     | }
      """
  }
   def genEquals(ctx: CodegenContext, ev: ExprCode, aVal: String,
      vVal: String, add: String): String = {
    return s"""
           |  if (${ctx.genEqual(right.dataType, aVal, vVal)}) {
           |     ${ev.isNull} = false;
           |     ${this.genApply(ctx, ev, vVal, add)}
           |     break;
           |  } else {
           |     ${ev.isNull} = false;
           |     ${ev.value} = ${add};
           |  }
      """
  }
  override def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode = {
    nullSafeCodeGen(ctx, ev, (arr, value, add) => {
      val av = ctx.freshName("av")
      val vv = ctx.freshName("vv")
      val i = ctx.freshName("i")
      val avlen = ctx.freshName("avlen")
      val vvlen = ctx.freshName("vvlen")
      val getValue = CodeGenerator.getValue(arr, right.dataType, i)
      val loopBodyCode = if (nullable) {
        s"""
           | // start 1
           |if ($arr.isNullAt($i)) {
           |  ${ev.isNull} = true;
           |} else {
           |  ${this.genContains(ctx, ev, getValue, value, av, vv, add, avlen, vvlen)}
           |}
         """.stripMargin
      } else {
        s"""
           |${this.genContains(ctx, ev, getValue, value, av, vv, add, avlen, vvlen)}
         """.stripMargin
      }
      s"""
         |for (int $i = 0; $i < $arr.numElements(); $i ++) {
         |  $loopBodyCode
         |}
       """.stripMargin
    })
  }



  override def prettyName: String = "sparray_contains"
}
/**
 * Checks if the array (left) has the element (right)
 */
@ExpressionDescription(
  usage = "_FUNC_(array, value) - Returns true if the array contains the value. Uses simple regex",
  examples = """
    Examples:
      > SELECT _FUNC_(array("hello","bye","sur*"), "surest");
       true
  """)
case class SPArrayContains2(left: Expression, right: Expression)
  extends BinaryExpression with ImplicitCastInputTypes {

  override def dataType: DataType = BooleanType

  @transient private lazy val ordering: Ordering[Any] =
    TypeUtils.getInterpretedOrdering(right.dataType)

  override def inputTypes: Seq[AbstractDataType] = {
    (left.dataType, right.dataType) match {
      case (_, NullType) => Seq.empty
      case (ArrayType(e1, hasNull), e2) =>
        TypeCoercion.findTightestCommonType(e1, e2) match {
          case Some(dt) => Seq(ArrayType(dt, hasNull), dt)
          case _ => Seq.empty
        }
      case _ => Seq.empty
    }
  }

  override def checkInputDataTypes(): TypeCheckResult = {
    (left.dataType, right.dataType) match {
      case (_, NullType) =>
        TypeCheckResult.TypeCheckFailure("Null typed values cannot be used as arguments")
      case (ArrayType(e1, _), e2) if e1.sameType(e2) =>
        TypeUtils.checkForOrderingExpr(e2, s"function $prettyName")
      case _ => TypeCheckResult.TypeCheckFailure(s"Input to function $prettyName should have " +
        s"been ${ArrayType.simpleString} followed by a value with same element type, but it's " +
        s"[${left.dataType.catalogString}, ${right.dataType.catalogString}].")
    }
  }

  override def nullable: Boolean = {
    left.nullable || right.nullable || left.dataType.asInstanceOf[ArrayType].containsNull
  }

  override def nullSafeEval(arr: Any, value: Any): Any = {
    var hasNull = false
    arr.asInstanceOf[ArrayData].foreach(right.dataType, (i, v) =>
      if (v == null) {
        hasNull = true
      } else if (ordering.equiv(v, value)) {
        return true
      }
    )
    if (hasNull) {
      null
    } else {
      false
    }
  }

  override def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode = {
    nullSafeCodeGen(ctx, ev, (arr, value) => {
      val av = ctx.freshName("av")
      val vv = ctx.freshName("vv")
      val i = ctx.freshName("i")
      val getValue = CodeGenerator.getValue(arr, right.dataType, i)
      val loopBodyCode = if (nullable) {
        s"""
           | // start 1
           |if ($arr.isNullAt($i)) {
           |   ${ev.isNull} = true;
           |} else {
           |  if (${getValue}.contains(UTF8String.fromString("*"))) {
           |    UTF8String ${av} = ${getValue}
           |    .substring(0,${getValue}
           |    .indexOf(UTF8String.fromString("*"),0));
           |    UTF8String ${vv} = ${value}
           |    .substring(0,${getValue}
           |    .indexOf(UTF8String.fromString("*"),0));
           |    if (${ctx.genEqual(right.dataType, av, vv)}) {
           |      ${ev.isNull} = false;
           |      ${ev.value} = true;
           |      break;
           |    }
           |  }
           |  if (${ctx.genEqual(right.dataType, value, getValue)}) {
           |     ${ev.isNull} = false;
           |     ${ev.value} = true;
           |     break;
           |  }
           |}
         """.stripMargin
      } else {
        s"""
           |if (${ctx.genEqual(right.dataType, value, getValue)}) {
           |  ${ev.value} = true;
           |  break;
           |}
         """.stripMargin
      }
      s"""
         |for (int $i = 0; $i < $arr.numElements(); $i ++) {
         |  $loopBodyCode
         |}
       """.stripMargin
    })
  }

  override def prettyName: String = "sparray_contains2"
}


case class Add_One_Custom_Native(child: Expression) extends UnaryExpression with NullIntolerant {
  override def dataType: DataType = LongType
  override def prettyName: String = "addOneSparkNative"
  override def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode =
    defineCodeGen(ctx, ev, c => s"$c + 1")
}
