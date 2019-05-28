package com.dsleng.readers

import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.util.MimeWordDecoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import tech.blueglacier.parser.CustomContentHandler
import org.apache.james.mime4j.stream.Field
import scala.collection.mutable.ListBuffer

import java.io._
import java.net.URL;
import java.util._
import play.api.libs.json._
//import play.api.libs.ws._
import com.dsleng.email.utils.MetaInfo
import com.dsleng.email._

class MimeHandler(metaData: MetaInfo) {
  
  def this(){
    this(null)
  }
  def processSimpleModel(email: File): SimpleEmailModel={
    val mime4jParserConfig = MimeConfig.DEFAULT;
    
    
    var mimeConfig = MimeConfig.custom()
    mimeConfig.setMaxContentLen(-1) // disable
    mimeConfig.setMaxLineLen(-1) // disable
    mimeConfig.setMaxHeaderCount(-1) // disable
    mimeConfig.setMaxHeaderLen(-1) // disable
     
    
		val bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();		
		val mime4jParser = new MimeStreamParser(mimeConfig.build(),DecodeMonitor.SILENT,bodyDescriptorBuilder);
		
		
		
		val content = new CustomContentHandler()
		mime4jParser.setContentDecoding(true);
		mime4jParser.setContentHandler(content);
	
   
		
		val mailIn = new FileInputStream(email);
		
		mime4jParser.parse(mailIn);
	
		val em = content.getEmail()
	
		println("From: " + em.getFromEmailHeaderValue())
		println("To: " + em.getToEmailHeaderValue())
		println("Subject: " + em.getEmailSubject())
		
		val buf = new ListBuffer[EmailHeader]()
		val it = em.getHeader().iterator()
		while(it.hasNext()){
		  val h = it.next().asInstanceOf[Field]
		  val eh = new EmailHeader(h.getName(),h.getBody())
		  buf.append(eh)
		}
		var textContent = ""
		var htmlContent = ""
		if ( em.getHTMLEmailBody() != null && em.getHTMLEmailBody().getAttachmentSize() > 0 ){
		  htmlContent = scala.io.Source.fromInputStream(em.getHTMLEmailBody().getIs()).mkString
		}
		if ( em.getPlainTextEmailBody() != null && em.getPlainTextEmailBody().getAttachmentSize() > 0 ){
		  textContent = scala.io.Source.fromInputStream(em.getPlainTextEmailBody().getIs()).mkString
		}
		
		 //val senderIp = new ListBuffer[SenderInfo]()
		 val senderIp = new scala.collection.mutable.ArrayBuffer[SenderInfo]()
		 val eModel = new SimpleEmailModel(new Headers(buf.toList),textContent,htmlContent)
		 return eModel
  }
  def processModel(email: File): EmailModel={
    val mime4jParserConfig = MimeConfig.DEFAULT;
    
    
    var mimeConfig = MimeConfig.custom()
    mimeConfig.setMaxContentLen(-1) // disable
    mimeConfig.setMaxLineLen(-1) // disable
    mimeConfig.setMaxHeaderCount(-1) // disable
    mimeConfig.setMaxHeaderLen(-1) // disable
     
    
		val bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();		
		val mime4jParser = new MimeStreamParser(mimeConfig.build(),DecodeMonitor.SILENT,bodyDescriptorBuilder);
		
		
		
		val content = new CustomContentHandler()
		mime4jParser.setContentDecoding(true);
		mime4jParser.setContentHandler(content);
	
   
		
		val mailIn = new FileInputStream(email);
		
		mime4jParser.parse(mailIn);
	
		val em = content.getEmail()
	
		println("From: " + em.getFromEmailHeaderValue())
		println("To: " + em.getToEmailHeaderValue())
		println("Subject: " + em.getEmailSubject())
		
		val buf = new ListBuffer[EmailHeader]()
		val it = em.getHeader().iterator()
		while(it.hasNext()){
		  val h = it.next().asInstanceOf[Field]
		  val eh = new EmailHeader(h.getName(),h.getBody())
		  buf.append(eh)
		}
		var textContent = ""
		var htmlContent = ""
		if ( em.getHTMLEmailBody() != null && em.getHTMLEmailBody().getAttachmentSize() > 0 ){
		  htmlContent = scala.io.Source.fromInputStream(em.getHTMLEmailBody().getIs()).mkString
		}
		if ( em.getPlainTextEmailBody() != null && em.getPlainTextEmailBody().getAttachmentSize() > 0 ){
		  textContent = scala.io.Source.fromInputStream(em.getPlainTextEmailBody().getIs()).mkString
		}
		
		 //val senderIp = new ListBuffer[SenderInfo]()
		 val senderIp = new scala.collection.mutable.ArrayBuffer[SenderInfo]()
		 val eModel = new EmailModel(buf.toList,senderIp,em.getFromEmailHeaderValue(),em.getToEmailHeaderValue(),"",em.getEmailSubject(),textContent,htmlContent,"")
		 return eModel
  }
  def process(email: File,datetime: String): JsValue={
    val mime4jParserConfig = MimeConfig.DEFAULT;
		val bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();		
		val mime4jParser = new MimeStreamParser(mime4jParserConfig,DecodeMonitor.SILENT,bodyDescriptorBuilder);
		
		val content = new CustomContentHandler()
		mime4jParser.setContentDecoding(true);
		mime4jParser.setContentHandler(content);
		
		
		//val url = this.getClass().getClassLoader().getResource(messageFileName);
		
		val mailIn = new FileInputStream(email);
		mime4jParser.parse(mailIn);
	
		val em = content.getEmail()
	
		println("From: " + em.getFromEmailHeaderValue())
		println("To: " + em.getToEmailHeaderValue())
		println("Subject: " + em.getEmailSubject())
		
		val buf = new ListBuffer[EmailHeader]()
		val it = em.getHeader().iterator()
		while(it.hasNext()){
		  val h = it.next().asInstanceOf[Field]
		  val eh = new EmailHeader(h.getName(),h.getBody())
		  buf.append(eh)
		}
		var textContent = ""
		var htmlContent = ""
		if ( em.getHTMLEmailBody() != null && em.getHTMLEmailBody().getAttachmentSize() > 0 ){
		  htmlContent = scala.io.Source.fromInputStream(em.getHTMLEmailBody().getIs()).mkString
		}
		if ( em.getPlainTextEmailBody() != null && em.getPlainTextEmailBody().getAttachmentSize() > 0 ){
		  textContent = scala.io.Source.fromInputStream(em.getPlainTextEmailBody().getIs()).mkString
		}
		
		 //val senderIp = new ListBuffer[SenderInfo]()
		 val senderIp = new scala.collection.mutable.ArrayBuffer[SenderInfo]()
		 val eModel = new EmailModel(buf.toList,senderIp,em.getFromEmailHeaderValue(),em.getToEmailHeaderValue(),"",em.getEmailSubject(),textContent,htmlContent,"")
		 eModel.dummyUpdate(metaData,datetime)
		 return Json.toJson(eModel)
		
  }
}
