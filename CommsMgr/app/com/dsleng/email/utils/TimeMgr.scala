package com.dsleng.email.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.PrintWriter

// 09-12-2017T15:00:00
class TimeMgr(start: String,var interval: Int,timfile: String="") {
  val format = DateTimeFormat.forPattern("MM-dd-yyyy'T'HH:mm:ss")
  var curr = format.parseDateTime(start)
  
  def getFormat(): String={
    return "MM-dd-yyyy'T'HH:mm:ss"
  }
  def getNextTime(): String ={
    if ( curr.dayOfWeek().get()  > 0 && curr.dayOfWeek().get() < 5 ){
      interval = 1
    } else {
      interval = 120
    }
    curr = curr.plusSeconds(interval)
    return curr.toString(format)
  }
  def close(){
    if ( timfile != "" ){
      val pw = new PrintWriter(timfile)
      pw.write(curr.toString())
      pw.close()
    }
  }
}