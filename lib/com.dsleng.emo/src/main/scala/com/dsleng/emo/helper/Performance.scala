package com.dsleng.emo.helper

import org.apache.log4j._

trait Performance {
  private[this] val logger = Logger.getLogger(getClass().getName());
  def averageTime[R](name: String,block: => R, numIter: Int = 10): Unit = {
     val t0 = System.nanoTime()
     (1 to numIter).foreach( _ => block)
     val t1 = System.nanoTime()
     val averageTimeTaken = (t1 - t0) / numIter
     val timeTakenMs = averageTimeTaken / 1000000
     println("Elapsed time: for " + name + " " + timeTakenMs + "ms")
     
   }
  def takenTime[R](name: String,cnt: Integer,block: => R): Unit = {
     val t0 = System.nanoTime()
      block
     val t1 = System.nanoTime()
     val averageTimeTaken = (t1 - t0)
     val timeTakenMs = averageTimeTaken / 1000000
     println("Elapsed time: for no of tokens " + cnt + " " + timeTakenMs + "ms")
     logger.info(name + "," + cnt + "," + timeTakenMs + "ms")
   }
}