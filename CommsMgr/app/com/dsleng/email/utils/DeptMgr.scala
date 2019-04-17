package com.dsleng.email.utils

import scala.collection.mutable.ArrayBuffer

class DeptMgr {
  val depts = List("Finance","Operations","Information Technology","Human Resources","Executive")
  val rand = new RandomGen()
  def getDept(): String={
      val cnt = rand.getRandom(0,depts.length-1)
      return depts(cnt);
  }
}