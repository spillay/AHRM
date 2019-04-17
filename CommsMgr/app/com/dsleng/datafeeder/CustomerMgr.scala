package com.dsleng.datafeeder

import scala.util.Random
import java.io.File
import scala.io.Source
import java.io.PrintWriter
import scala.util.control.Breaks._

class CustomerMgr(baseDir: String) {
  val fnamesfile = baseDir + File.separator + "fnames.csv"
  val countryip = baseDir + File.separator + "country" + File.separator
  
  val fnames = Source.fromFile(fnamesfile)
  
  def getRandom(len: Int): Int ={
    val random = Random
    return random.nextInt(len)
  }
  def getRandom(start: Int,end: Int): Int ={
    if (start == end ){ return start }
    val random = Random
    return start+random.nextInt(end-start+1)
  }
  def getIP(from: Array[String],to: Array[String]): String={
    val ip1 = this.getRandom(from(0).toInt, to(0).toInt)
    val ip2 = this.getRandom(from(1).toInt, to(1).toInt)
    val ip3 = this.getRandom(from(2).toInt, to(2).toInt)
    val ip4 = this.getRandom(from(3).toInt, to(3).toInt)
    return ip1 + "." + ip2 + "." + ip3 + "." + ip4
  }
  def getByCountryIP(countrycode: String): String={
    val cfile = countryip+countrycode+".csv"
    val size = Source.fromFile(cfile).getLines().size
    val randLine = getRandom(size)
    println("Random Line: " + randLine)
    var cnt = 0;
    var line = ""
    breakable {
      Source.fromFile(cfile).getLines().foreach(l =>{
        if (cnt == randLine ){
          line = l
          break
        }
        //println("cnt " + cnt)
        cnt = cnt+1
      })
    }
    val r = line.split(',')
    val ipfrom = r(0).split('.')
    val ipto = r(1).split('.')
    return this.getIP(ipfrom, ipto)
  }
  def process(){
    println(this.getByCountryIP("us"))
    println(this.getByCountryIP("us"))
    /*
    fnames.getLines().foreach(s=>{
      val data = s.split(":")
    })*/
  }
}