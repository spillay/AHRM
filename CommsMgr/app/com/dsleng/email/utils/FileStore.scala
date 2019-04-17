package com.dsleng.email.utils

import java.io.File
import com.dsleng.email.EmailHandler
import play.api.libs.json._
import com.dsleng.email.MimeHandler
import scala.collection.mutable.ArrayBuffer
import com.dsleng.email.EmailModel
import scala.util.Try
import com.google.common.io.Files


class FileStore(dir: String,batch: Boolean,bat: Batch) extends WSHandler {
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
        d.listFiles().foreach(f=>{
           println(f.getName())
        })
        d.listFiles.filter(_.isFile).toList
    } else {
        List[File]()
    }
  }
  def mv(oldName: String, newName: String) = 
    Files.move(new File(oldName), new File(newName))
    //new File(oldName).renameTo(new File(newName))
    //Try(new File(oldName).renameTo(new File(newName))).getOrElse(false)
    
  def processMailboxes()={
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
        d.listFiles().foreach(f=>{
           println(f.getPath() +  "/inbox/")
           val ubox = new File(f.getPath()  + "/inbox/")
           if ( ubox.exists() && ubox.isDirectory){
             ubox.listFiles().filter(p=> p.isFile() && !p.getName().endsWith(".complete")).sortWith(_.getName().replace(".", "").toDouble < _.getName().replace(".", "").toDouble).foreach(f=>{
               println(f.getAbsolutePath())
               var em = new File(f.getAbsolutePath())
               if (em.isFile()){
                 
                 mv(f.getAbsolutePath(),f.getAbsoluteFile()+"complete")
               }
             })
           }
           
        })   
    }
  }
  def generateModels(mail: String): ArrayBuffer[EmailModel]={
    val in = mail + "/inbox/"
    val d = new File(in)
   // val metaData = new com.dsleng.email.utils.MetaInfo("/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/")
   // metaData.Load()
    val mlist = new ArrayBuffer[EmailModel]()
    if ( d.exists() ){
      val emails = d.listFiles.filter(_.isFile).toList
      emails.foreach(f =>{
        val em = new MimeHandler()
        val md = em.processModel(f)  
        mlist += md
      })
    }
    return mlist
  }
  def processInbox(mail: String,dt: TimeMgr){
    val in = mail + "/inbox/"
    val d = new File(in)
    val metaData = new com.dsleng.email.utils.MetaInfo("/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/")
    metaData.Load()
    if ( d.exists() ){
      val emails = d.listFiles.filter(_.isFile).toList
      emails.foreach(f =>{
        val em = new MimeHandler(metaData)
        val jsonValue = em.process(f,dt.getNextTime())
        val email = Json.prettyPrint(jsonValue)
        if ( batch ){
          bat.process(email)
        } else {
          this.processWS(email)
        }
        println(email);
        
      })
    }
  }
  def singleProcess(): JsValue = {
    //val f = new File("/Users/suresh/enron/maildir/kean-s/inbox/9.")
    val f = new File("/Users/suresh/enron/junk/email1.eml")
    val p = new EmailHandler(f)
    println(p.getBody())
    return p.getEmailModel()
  }
  def processModel(): ArrayBuffer[EmailModel]={
     val d = new File(dir)
     val emails = d.listFiles.filter(_.isDirectory())
     val mlist = new ArrayBuffer[EmailModel]()
     emails.foreach(f =>{
       mlist.appendAll(this.generateModels(f.getAbsolutePath))
     })
     return mlist
  }
  def process(dt: TimeMgr){
     val d = new File(dir)
     val emails = d.listFiles.filter(_.isDirectory())
     emails.foreach(f =>{
       println(f.getAbsolutePath)
       processInbox(f.getAbsolutePath,dt)
     })
  }
}