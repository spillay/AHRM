package com.dsleng.email

import javax.mail.internet.MimeMessage
import org.apache.commons.mail.util.MimeMessageParser
import javax.mail.Session
import java.util.Properties
import java.io.File
import java.io.FileInputStream
import play.api.libs.json._
import scala.collection.JavaConverters._
import javax.mail.Header
import javax.mail.Message
import scala.collection.mutable.ListBuffer

class EmailHandler(email: File) {
  val props = new Properties();
  val session = Session.getDefaultInstance(props, null);
  val mg = new MimeMessage(session,new FileInputStream(email))
  val parser = new MimeMessageParser(mg)
  val mimeParser = parser.parse()
    
 
  def getEmailModel(): JsValue ={
    //parser.getTo
    //val to = parser.getTo().iterator()
    val to = mimeParser.getTo().iterator().asScala.toList
    //val to = mimeParser.getMimeMessage().getRecipients(Message.RecipientType.TO).iterator.toList
    //val sto = scala.collection.JavaConverters.asScalaIteratorConverter(to).asScala.toList
    val dt = mimeParser.getMimeMessage().getSentDate().toString()
    val allFrom = mimeParser.getMimeMessage().getFrom().toSeq
    val headers = mimeParser.getMimeMessage().getAllHeaders()
    val buf = new ListBuffer[EmailHeader]()
    val fromip = ""
    while(headers.hasMoreElements()){
      val h = headers.nextElement().asInstanceOf[Header] 
      //print("headers\n" + h.getName() + ":" + h.getValue()+  "\n")
      val eh = new EmailHeader(h.getName(),h.getValue())
      buf.append(eh)
    }
    val senderIp = new scala.collection.mutable.ArrayBuffer[SenderInfo]()
    val em = new EmailModel(buf.toList,senderIp, parser.getFrom(),"",dt,parser.getSubject(),getBody(),"","")
    return Json.toJson(em)
  }
  def getBody(): String = {  
    var content: String = ""
    if (parser.hasPlainContent()){
      content = parser.getPlainContent
    } else if (parser.hasHtmlContent()){
      content = parser.getHtmlContent
    }
   
    return content
  }
  def getCleanBody(): String={
    var em = getBody()
    val idx = em.indexOfSlice("-----Original Message-----")
    if ( idx == -1 ){
      return em
    } else {
      return em.slice(0, idx)
    }
  }
}