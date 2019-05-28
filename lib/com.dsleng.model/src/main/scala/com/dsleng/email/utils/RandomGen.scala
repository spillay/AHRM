package com.dsleng.email.utils

import breeze.stats.distributions.Binomial

class RandomGen {
  val binDec = new Binomial(1,0.2)
  val binGen = new Binomial(1,0.5)
  val binIP = new Binomial(1,0.2)
  val binProd = new Binomial(1,0.8)
  def getRandomProduct(): Int={
    return binProd.draw()
  }
  def getRandomDeception(): Int={
    return binDec.draw();
  }
  def getRandomGen(): Int={
    return binGen.draw();
  }
   def getRandomTZ(): Int={
    return binIP.draw();
  }
   def getRandomJump(start: Int,end: Int): Int={
     val r = new scala.util.Random()
     val ret = start + r.nextInt( (end - start) + 1 ) 
     return ret
   }
   def getRandom(start: Int,end: Int): Int={
     val r = new scala.util.Random()
     val ret = start + r.nextInt( (end - start) + 1 ) 
     return ret
   }
}

object RG {
  def main(args: Array[String]) {
    val r = new RandomGen()
    for(i <- 1 to 100){
      println(r.getRandomDeception())
    }
  }
}