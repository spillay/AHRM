package com.dsleng.emo.helper

import org.apache.spark.sql.functions.udf

object SimpleUDF extends Serializable {
  def test1(lst: Seq[String]): Seq[String] ={ return lst}
  val test2 = udf{list: String => list}
  val test3 = udf{list: Float => list}
}

object Transformers extends Serializable {
   def compareStrAgainstArray2(str: String, lst: Seq[String],cnt: Integer): Integer = {
    if (lst.exists(this.emomatch(str, _))) {
      return cnt+1
    } else {
      return cnt+0
    }
  }
  def compareStrAgainstArray(str: String, lst: Seq[String],fwords: Seq[String]): Seq[String] = {
    if (lst.exists(this.emomatch(str, _))) {
      val nwords = fwords.:+(str)
      return nwords
    } else {
      return fwords
    }
  }
  def countArr(lst: Seq[String]): Integer = {
    return lst.length
  }
  def compareStrArrays(lst1: Seq[String], lst2: Seq[String]): Seq[String] = {
    val a = Set(lst1: _*)
    val b = Set(lst2:_*)
    return b.diff(a).toSeq
  }
  def emomatch(str: String, to: String): Boolean = {
    if (to.contains('*')) {
      val nto = to.substring(0, to.indexOf('*'))
      // TODO: Change this to >= to include words that match exactly and more
      if (str.length() > nto.length()) {
        val nstr = str.substring(0, nto.length()).toLowerCase()
        if (nstr.matches(nto)) {
          if (str == "sure"){print(to)}
          if (str == "problem"){print(to)}
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    } else {
      return str.toLowerCase().matches(to)
    }
    return false;
  }
}