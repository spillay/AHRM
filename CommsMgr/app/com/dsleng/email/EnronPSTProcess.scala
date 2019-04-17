package com.dsleng.email

import com.pff.PSTFile
import com.dsleng.email.utils.FileStore
import com.dsleng.email.utils.MetaInfo
import com.dsleng.email.utils.TimeMgr
import com.dsleng.email.utils.Batch
import com.google.common.io.Files
import java.io.File
import play.api.libs.json._
import com.dsleng.email.utils.EMOAnalysis
import com.dsleng.email.utils.GenMetaData
import com.dsleng.email.utils.PSTMgr
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }

object EnronPSTProcess {
   var ip = ""
   var index = ""
   val usage = """
    Usage: EnronPSTProcess  --emailStore ["./emailStore"]
  """
  def mv(oldName: String, newName: String) =
    Files.move(new File(oldName), new File(newName))
    
  def processPST(filename: String){
      val emo = new EMOAnalysis();
      val es = new ESFeeder(ip)
      val  pstFile = new PSTMgr(filename);		
      pstFile.Process();
      println("============================================================================================");
      pstFile.emails.foreach(model=>{
        var smodel = new SimpleEmailExt(filename, model, emo.processEMO(model.textContent))
        val res = Json.toJson(smodel)
        println(Json.stringify(res));
        es.addDocument(smodel, index)
      })
      es.close();
  }
  def process(emailStore: String){
     println("Processing: " + emailStore)
     val d = new File(emailStore)
     if (d.exists && d.isDirectory) {
        d.listFiles().foreach(f => {
          println(f.getPath())
          val ubox = new File(f.getPath())
          if (ubox.exists() && ubox.isDirectory) {
            ubox.listFiles().filter(p => p.isFile() && !p.getName().endsWith(".complete")).foreach(f => {
              println(f.getAbsolutePath())
              var em = new File(f.getAbsolutePath())
              if (em.isFile()) {
                this.processPST(em.getAbsolutePath())
                /*
                var model = mhandler.processSimpleModel(em)
                var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, emo.processEMO(model.textContent))
                es.addDocument(smodel, index)
                // es.writeBULK()
                //val res = Json.toJson(smodel)
                //println(res) 
                 */
                //mv(f.getAbsolutePath(), f.getAbsoluteFile() + ".complete")
              }
            })
          }
  
        })
     }
  }
  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      println(usage)
      System.exit(1)
    }

    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      def isSwitch(s: String) = (s(0) == '-')
      // 'batch translates to Symbol("batch")
      list match {
        case Nil => map
        case "--reset" :: value :: tail =>
          nextOption(map ++ Map('reset -> value.toString), tail)
        case "--emailStore" :: value :: tail =>
          nextOption(map ++ Map('emailStore -> value.toString), tail)
        case "--boxDir" :: value :: tail =>
          nextOption(map ++ Map('boxdir -> value.toString), tail)
        case "--index" :: value :: tail =>
          nextOption(map ++ Map('index -> value.toString), tail)
        case "--metaFile" :: value :: tail =>
          nextOption(map ++ Map('metafile -> value.toString), tail)
        case "--fileExt" :: value :: tail =>
          nextOption(map ++ Map('fileext -> value.toString), tail)
        case "--newExt" :: value :: tail =>
          nextOption(map ++ Map('newext -> value.toString), tail)
        case "--host" :: value :: tail =>
          nextOption(map ++ Map('host -> value.toString), tail)
        case _ => map
      }
    }
    val options = nextOption(Map(), arglist)
    println(options)
    /*
    val batch: Boolean = options.getOrElse('batch, true).asInstanceOf[Boolean]
    val start: String = options.getOrElse('start, "09-12-2017T15:00:00").asInstanceOf[String]
    val batchdir: String = options.getOrElse('batchfile, "./batch/").asInstanceOf[String]
    val conf: String = options.getOrElse('conf, "./conf/").asInstanceOf[String]
    */
    val reset: String = options.getOrElse('reset, "").asInstanceOf[String]
    val emailStore: String = options.getOrElse('emailStore, "./emailStore/").asInstanceOf[String]
    val boxDir: String = options.getOrElse('boxdir, "sent_items").asInstanceOf[String]
    val index: String = options.getOrElse('index, "email-outbox").asInstanceOf[String]
    val metafile: String = options.getOrElse('metafile, "").asInstanceOf[String]
    val fileExt: String = options.getOrElse('fileext, "").asInstanceOf[String]
    val newExt: String = options.getOrElse('newext, "").asInstanceOf[String]
    val host: String = options.getOrElse('host, "").asInstanceOf[String]

    this.ip = host;
    this.index = index;
    this.process(emailStore);
   
    println("Terminated !!!");
    //System.exit(0);
  }
}