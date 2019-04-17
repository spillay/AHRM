package com.dsleng.email

import com.dsleng.email.utils.FileStore
import com.dsleng.email.utils.MetaInfo
import com.dsleng.email.utils.TimeMgr
import com.dsleng.email.utils.Batch
import com.google.common.io.Files
import java.io.File
import play.api.libs.json._
import com.dsleng.email.utils.EMOAnalysis
import com.dsleng.email.utils.GenMetaData

object EnronEmailProcess {
  val usage = """
    Usage: EnronEmailProcess  --emailStore ["./emailStore"]
  """
  def mv(oldName: String, newName: String) =
    Files.move(new File(oldName), new File(newName))

  def resetAll(emailStore: String){
    this.reset(emailStore, "inbox");
    this.reset(emailStore, "sent_items");
  }
  def reset(emailStore: String,box: String){
    val d = new File(emailStore)
    if (d.exists && d.isDirectory) {
      d.listFiles().foreach(f => {
        //println(f.getPath() + "/" + box + "/")
        val ubox = new File(f.getPath() + "/" + box + "/")
        if (ubox.exists() && ubox.isDirectory) {
          ubox.listFiles().foreach(f => {
            //println(f.getAbsolutePath())
            var em = new File(f.getAbsolutePath())
            if (em.isFile()) {
              println(em.getAbsoluteFile())
              val nme = em.getName();
              val fd = nme.indexOf(".");
              val rp = nme.substring(fd+1, nme.length());
              if (rp != "new"){
                var newFile = f.getAbsoluteFile().toString().replace(rp, "") + "new";
                println(newFile);
                mv(f.getAbsolutePath(),newFile)
              }
            }
          })
        }

      })
    }
  }
   def processAllMetaModels(ip: String,emailStore: String, emailModel: String, index: String, fileExt: String, newExt: String) {
    println("processAllMetaModels");
    //val fileExt = ".complete";
    //val newExt = ".completed";
    val es = new ESFeeder(ip)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();

    val gen = new GenMetaData(emailStore, emailModel);
    gen.load();
    gen.getEmails().foreach(em => {
      val dir = em.dir;
      println(dir);
      var dept = em.dept;
      val prods = em.prods;
      println("user directory " + dir)
      var allbox = new File(dir);
      if (allbox.exists() && allbox.isDirectory()){
        allbox.listFiles().foreach(b=>{
          println(b);
          this.processBoxes(b.getAbsolutePath(), es, mhandler, fileExt, newExt, emo, index, dept, prods);
        })
      }
     
    })
    es.close();
  }
  def processMetaModels(ip: String,emailStore: String, emailModel: String, box: String, index: String, fileExt: String, newExt: String) {
    println("processModels");
    //val fileExt = ".complete";
    //val newExt = ".completed";
    val es = new ESFeeder(ip)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();

    val gen = new GenMetaData(emailStore, emailModel);
    gen.load();
    gen.getEmails().foreach(em => {
      val dir = em.dir;
      println(dir);
      var dept = em.dept;
      val prods = em.prods;
      println(dir + "/" + box + "/")
      val ubox = new File(dir + "/" + box + "/")
      println("fileExt: " + fileExt + " newExt " + newExt);
      if (ubox.exists() && ubox.isDirectory) {
        ubox.listFiles().filter(p => p.isFile() && p.getName().endsWith(fileExt)).sortWith(_.getName().replace(fileExt, "").toDouble < _.getName().replace(fileExt, "").toDouble).foreach(f => {
          println("file processing: " + f.getAbsolutePath())
          var em = new File(f.getAbsolutePath())
          if (em.isFile()) {
            var model = mhandler.processSimpleModel(em)
            var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, emo.processEMO(model.textContent), department = dept, product = prods)
            es.addDocument(smodel, index)
            // es.writeBULK()
            //val res = Json.toJson(smodel)
            //println(res)
            var newfile = f.getAbsoluteFile().toString();
            newfile = newfile.replace(fileExt, "")
            newfile = newfile + newExt;
            mv(f.getAbsolutePath(), newfile);
          }
        })
      }
    })
    es.close();
  }

  def processBoxes(dir: String,es: ESFeeder,mhandler: MimeHandler,fileExt: String,newExt: String,emo: EMOAnalysis,index: String,dept: String,prods: Array[String]){
      println("process Boxes " + fileExt + " directory " + dir);
      val sfileExt = ".";
      val ubox = new File(dir)
      if (ubox.exists() && ubox.isDirectory) {
        ubox.listFiles().filter(p => p.isFile() && p.getName().endsWith(sfileExt)).sortWith(_.getName().replace(sfileExt, "").toDouble < _.getName().replace(fileExt, "").toDouble).foreach(f => {
          println("file processing>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + f.getAbsolutePath())
          var em = new File(f.getAbsolutePath())
          if (em.isFile()) {
            var model = mhandler.processSimpleModel(em)
            var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, emo.processEMO(model.textContent), department = dept, product = prods)
            es.addDocument(smodel, index)
            mv(f.getAbsolutePath(), f.getAbsoluteFile() + newExt)
          }
        })
      }
      if (ubox.exists() && ubox.isDirectory) {
         ubox.listFiles().filter(p => p.isDirectory()).foreach(d=>{
           processBoxes(d.getAbsolutePath(), es, mhandler, fileExt, newExt, emo, index, dept, prods);
         })
      }
  }
   def processAllModels(ip: String,emailStore: String,index: String) {
    println("processallModels");
    val fileExt = ".complete";
    val newExt = ".completed";
    val es = new ESFeeder(ip)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();

    val gen = new GenMetaData(emailStore, "emailModel.txt");
    gen.process();
    gen.getEmails().foreach(em => {
      val dir = em.dir;
      println(dir);
      var dept = em.dept;
      val prods = em.prods;
      println(dir)
      var allbox = new File(dir);
      if (allbox.exists() && allbox.isDirectory()){
        allbox.listFiles().foreach(b=>{
          this.processBoxes(b.getAbsolutePath(), es, mhandler, fileExt, newExt, emo, index, dept, prods);
        })
      }
      
    })
    es.close();
  }
  def processModels(ip: String,emailStore: String, box: String, index: String) {
    println("processModels");
    val fileExt = ".complete";
    val newExt = ".completed";
    val es = new ESFeeder(ip)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();

    val gen = new GenMetaData(emailStore, "emailModel.txt");
    gen.process();
    gen.getEmails().foreach(em => {
      val dir = em.dir;
      println(dir);
      var dept = em.dept;
      val prods = em.prods;
      println(dir + "/" + box + "/")
      val ubox = new File(dir + "/" + box + "/")
      if (ubox.exists() && ubox.isDirectory) {
        ubox.listFiles().filter(p => p.isFile() && p.getName().endsWith(fileExt)).sortWith(_.getName().replace(fileExt, "").toDouble < _.getName().replace(fileExt, "").toDouble).foreach(f => {
          println("file processing: " + f.getAbsolutePath())
          var em = new File(f.getAbsolutePath())
          if (em.isFile()) {
            var model = mhandler.processSimpleModel(em)
            var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, emo.processEMO(model.textContent), department = dept, product = prods)
            es.addDocument(smodel, index)
            // es.writeBULK()
            //val res = Json.toJson(smodel)
            //println(res)
            mv(f.getAbsolutePath(), f.getAbsoluteFile() + newExt)
          }
        })
      }
    })
    es.close();
  }
  def processBox(ip: String,emailStore: String, box: String, index: String) {
    val es = new ESFeeder(ip)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();
    val d = new File(emailStore)
    if (d.exists && d.isDirectory) {
      d.listFiles().foreach(f => {
        println(f.getPath() + "/" + box + "/")
        val ubox = new File(f.getPath() + "/" + box + "/")
        if (ubox.exists() && ubox.isDirectory) {
          ubox.listFiles().filter(p => p.isFile() && !p.getName().endsWith(".complete")).sortWith(_.getName().replace(".", "").toDouble < _.getName().replace(".", "").toDouble).foreach(f => {
            println(f.getAbsolutePath())
            var em = new File(f.getAbsolutePath())
            if (em.isFile()) {
              var model = mhandler.processSimpleModel(em)
              var smodel = new SimpleEmailExt(f.getAbsolutePath(), model, emo.processEMO(model.textContent))
              es.addDocument(smodel, index)
              // es.writeBULK()
              //val res = Json.toJson(smodel)
              //println(res)
              mv(f.getAbsolutePath(), f.getAbsoluteFile() + "complete")
            }
          })
        }

      })
    }
    es.close();
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
        /*
        case "--batch" :: value :: tail =>
                               nextOption(map ++ Map('batch -> value.toBoolean), tail)
        case "--batch-dir" :: value :: tail =>
                               nextOption(map ++ Map('batchdir -> value.toString), tail)
        case "--start-time" :: value :: tail =>
                               nextOption(map ++ Map('start -> value.toString), tail)
        case "--mailRepo" :: value :: tail =>
                               nextOption(map ++ Map('mailrepo -> value.toString), tail)
                               */
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


    //this.processBox(emailStore, boxDir,index);
    println("Starting " + metafile);
    if (reset == "All"){
      this.resetAll(emailStore);
      println("Completed Reset");
    } else {
      if (metafile != "" ){
        println("Processing with Fixed MetaModel");
        this.processAllMetaModels(host,emailStore, metafile, index, fileExt, newExt);
        println("Completed processMetaModels for " + index);
      } else {
        println("Processing with Dynamic MetaModel - Results may vary on different runs");
        //this.processModels(host,emailStore, boxDir, index);
        this.processAllModels(host,emailStore, index);
      }
    }
    println("Terminated !!!");
    //System.exit(0);
  }
  def processfile(file: String) {
    var em = new File(file)
    val mhandler = new MimeHandler(null);
    val emo = new EMOAnalysis();
    var model = mhandler.processSimpleModel(em)
    var smodel = new SimpleEmailExt(file, model, emo.processEMO(model.textContent))
  }

}
