package com.dsleng.email.utils

import com.dsleng.email.EmailModel
import com.dsleng.email.MimeHandler
import com.dsleng.email.EmailAnalysis
import play.api.libs.json._
import java.io.File

object EmailProcess {
  
  //def emailStore = "/Users/suresh/enron/maildir"
  def emailStore = "/Users/suresh/enron/junk/"
  def main(args: Array[String]) {
     println("Email Process")
     
     //val fd = new FileStore(emailStore)
     //fd.process();
     
     //val ws = new EmailAnalysis()
     //val res = ws.processGender("")
     //println(res)
     
     val gender = "('male', '59.10%')"
     val ngender = gender.replace("(", "").replace(")", "").replaceAll("'", "")
     val parts = ngender.split(",")
     val jgender = parts(0) + ": " + parts(1)
     println(jgender)
  }
}