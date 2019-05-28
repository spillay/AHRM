package com.dsleng.email.utils

import scala.collection.mutable.ArrayBuffer

class ProductMgr {
  val prods = List("Product A","Product B","Product C","Product D","Product E","Product F","Product G") 
  val prodDomain = scala.collection.mutable.HashMap.empty[String,Array[String]]
  val rand = new RandomGen()
  def getProducts(): Array[String]={
      val buf = new ArrayBuffer[String]()
      val cnt = rand.getRandom(0,prods.length)
      var localProds = new ArrayBuffer[String]()
      prods.foreach(p=>{localProds.append(p)})
      while(buf.length != cnt){
        val tail = rand.getRandom(0,1)
         val sel = rand.getRandomProduct()
        if (tail == 0){
          if ( sel == 1 ){
            buf.append(localProds(0))
            localProds.remove(0)
          }
        } else {
          if ( sel == 1 ){
            buf.append(localProds(localProds.length-1))
            localProds.remove(localProds.length-1)
          }
        }
      }
      return buf.toArray[String]
  }
  def getProducts(domain: String): Array[String]={
    if ( prodDomain.contains(domain) ){
      return prodDomain.get(domain).getOrElse(List().toArray[String])
    } else {
      val buf = new ArrayBuffer[String]()
      val cnt = rand.getRandom(0,prods.length)
      var localProds = new ArrayBuffer[String]()
      prods.foreach(p=>{localProds.append(p)})
      while(buf.length != cnt){
        val tail = rand.getRandom(0,1)
         val sel = rand.getRandomProduct()
        if (tail == 0){
          if ( sel == 1 ){
            buf.append(localProds(0))
            localProds.remove(0)
          }
        } else {
          if ( sel == 1 ){
            buf.append(localProds(localProds.length-1))
            localProds.remove(localProds.length-1)
          }
        }
      }
      prodDomain += (domain -> buf.toArray[String])
      return buf.toArray[String]
    }
  }
}
/*
object ProductMgr {
  def main(args: Array[String]): Unit = {
    val p = new ProductMgr()
    p.getProducts("one.com").foreach(println)
    println("one.com")
    p.getProducts("one.com").foreach(println)
    println("two.com")
    p.getProducts("two.com").foreach(println)
    println("three.com")
    p.getProducts("three.com").foreach(println)
  }
}
*/