package com.dsleng.emo.helper

trait Performance {
  def averageTime[R](block: => R, numIter: Int = 10): Unit = {
     val t0 = System.nanoTime()
     (1 to numIter).foreach( _ => block)
     val t1 = System.nanoTime()
     val averageTimeTaken = (t1 - t0) / numIter
     val timeTakenMs = averageTimeTaken / 1000000
     println("Elapsed time: " + timeTakenMs + "ms")
   }
  def takenTime[R](block: => R): Unit = {
     val t0 = System.nanoTime()
      block
     val t1 = System.nanoTime()
     val averageTimeTaken = (t1 - t0)
     val timeTakenMs = averageTimeTaken / 1000000
     println("Elapsed time: " + timeTakenMs + "ms")
   }
}