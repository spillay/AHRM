package com.dsleng.emo

class Simple {
  
}

object Simple extends App {
  val s1 = "Thanks"
  val s2 = "thanks"
  println(s1.toLowerCase().matches(s2))
}