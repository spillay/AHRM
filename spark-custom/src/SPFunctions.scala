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


package org.apache.spark.sql

import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.functions.lit

object SPFunctions {
  private def withExpr(expr: Expression): Column = Column(expr)
  def addOneCustomNative(x: Column): Column = withExpr {
    Add_One_Custom_Native(x.expr)
  }
  def checkforFeature(arr: Column, value: Any): Column = withExpr {
    SPArrayContains(arr.expr, lit(value).expr)
  }
  def checkforFeature2(arr: Column, value: Any,add: Column): Column = withExpr {
    SPArrayContains2(arr.expr, lit(value).expr,add.expr)
  }
}
