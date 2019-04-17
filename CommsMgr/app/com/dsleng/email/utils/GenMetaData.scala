package com.dsleng.email.utils

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.io.BufferedReader
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }
import scala.collection.mutable.ListBuffer
import scala.io.Source

case class EnronEmailInfo(dir: String, dept: String, prods: Array[String]);

object EnronEmailInfo {
  implicit val enronWrites = Json.writes[EnronEmailInfo];
  implicit val enronRead = Json.reads[EnronEmailInfo];
}

class GenMetaData(mailDir: String, output: String) {
  val outputFile = output;
  var emails = new ListBuffer[EnronEmailInfo]();
  val prodMgr = new ProductMgr();
  val depMgr = new DeptMgr();
  def process() = {
    println("Starting to process from: " + mailDir);
    val d = new File(mailDir)
    if (d.exists && d.isDirectory) {
      d.listFiles().foreach(f => {
        println(f);
        val model = new EnronEmailInfo(f.getPath(), depMgr.getDept(), prodMgr.getProducts());
        emails += model;
      })
    }
  }
  def getEmails(): ListBuffer[EnronEmailInfo] = { return emails; }
  def write() {
    val file = new File(this.outputFile)
    val bw = new PrintWriter(file)
    //val bw = new BufferedWriter(new FileWriter(file))
    getEmails().foreach(e => {
      val jsonString = Json.toJson(e).toString()
      bw.write(jsonString + "\n")
    })
    bw.close()
  }
  def print() {
    getEmails().foreach(e => {
      val jsonString = Json.toJson(e).toString()
      println(jsonString)
    })
  }
  def load() {
    this.emails.clear();
    for (line <- Source.fromFile(this.outputFile).getLines) {
      var e = Json.parse(line).as[EnronEmailInfo];
      this.emails += e
    }
  }
}

object GenMetaData {
  val usage = """
    Usage: GenMetaData  --emailStore ["./emailStore"] --output ["./fileName"]
  """
  def main(args: Array[String]) {
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
        case "--emailStore" :: value :: tail =>
          nextOption(map ++ Map('emailStore -> value.toString), tail)
        case "--output" :: value :: tail =>
          nextOption(map ++ Map('output -> value.toString), tail)
        case _ => map
      }
    }
    val options = nextOption(Map(), arglist)
    println(options)

    val emailStore: String = options.getOrElse('emailStore, "./emailStore/").asInstanceOf[String]
    val output: String = options.getOrElse('output, "emailmodel.txt").asInstanceOf[String]

    //val gen = new GenMetaData("/Data/enron/maildir");
    val gen = new GenMetaData(emailStore, output);
    gen.process();
    gen.write();
    //gen.load();
    //gen.print();
    println("Terminated Successfully!");
  }
}