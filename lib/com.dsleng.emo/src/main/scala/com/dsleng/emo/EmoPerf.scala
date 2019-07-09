package com.dsleng.emo

import com.dsleng.emo.helper._

class EmoPerf extends SparkHelper with Performance {
  import spark.sqlContext.implicits._
  
  
  
  def processEmo(tokens: Seq[String]): String ={
    val emo = new Emotions(spark)
    
    emo.processPerf(tokens)
    emo.processPerf(tokens)
    
    averageTime("process2",{
      val df = emo.processPerf(tokens)
      emo.convertToJson(df)
    },10)
    
    //emo.showPar(df)
    
    return ""
  }
  
}

object EmoPerf extends App with Performance {
  println("Starting EmoSimple")
  //val tokens = List("terrible","funny","story","peacefully","foolproof","amaze","forgive")
  //var tokens = List("north", "american", "electric", "reliability", "council", "-lrb-", "nerc", "-rrb-", "december", "4", ",", "2001", ",", "posted", "public", "comment", "proposed", "wholesale", "electric", "standards", "model", "-lrb-", "wesm", "-rrb-", "--", "industry-based", "consensus", "process", "developing", ",", "maintaining", ",", "publishing", "standards", "promote", "reliable", "efficient", "wholesale", "electricity", "markets", "throughout", "north", "america", ".", "standards", "would", "address", ",", "integrated", "way", ",", "whole", "spectrum", "reliability", ",", "market", "interface", ",", "business", "practice", "standards", "fair", ",", "open", ",", "balanced", ",", "inclusive", "stakeholder", "process", ".", "december", "19", ",", "federal", "energy", "regulatory", "commission", "-lrb-", "ferc", "-rrb-", "issued", "order", "providing", "guidance", "formation", "standards", "development", "organization", "wholesale", "electric", "industry", "-lrb-", "docket", ".", "rm01-12-00", "-rrb-", ".", "order", "directs", "industry", "reach", "agreement", "formation", "organization", "develop", "consensus", "standards", "business", "practices", "electronic", "communications", ".", "industry", "reach", "consensus", "issue", "march", "15", ",", "commission", "indicated", "either", "institute", "procedures", "choose", "organization", "develop", "standards", ".", "provide", "nerc", "standing", "committees", "representation", "task", "force", "-lrb-", "scrtf", "-rrb-", "well", "nerc", "stakeholders", "committee", "board", "trustees", "broadest", "possible", "input", ",", "board", "strongly", "urges", "interested", "parties", "comment", "wesm", "proposal", "context", "recent", "ferc", "order", ".", "commenters", "should", "give", "particular", "attention", "nerc", "recently", "approved", "north", "american", "energy", "standards", "board", "-lrb-", "naesb", "-rrb-", "could", "collaborate", "developing", "overseeing", "single", ",", "industry-based", "consensus", "process", "develop", "standards", "assure", "continued", "reliable", "operation", "integrated", "north", "american", "electric", "transmission", "grids", "well", "development", "business", "practice", "standards", "communication", "protocols", "needed", "complement", "market", "design", "principles", "ferc", "announced", "intention", "develop", ".", "nerc", "'s", "scrtf", "use", "comments", "received", "help", "shape", "final", "proposal", "nerc", "stakeholders", "committee", "board", "trustees", "february", "2002", ".", "also", "intend", "provide", "input", "receive", "public", "forum", "wesm", "model", "ferc", "consideration", ".", "additional", "information", "scrtf", "membership", ",", "go", ":", "www.nerc.com/committees/scrtf.html", ".", "please", "direct", "questions", "david", "nevius", ",", "nerc", "vice", "president", ",", "609-452-8060", ",", "e-mail", "dave.nevius@nerc.com", ".", "proposal", "posted", "following", "nerc", "web", "site", ":", "http://www.nerc.com/", ".", "deadline", "comments", "january", "8", ",", "2002", ".", "sincerely", ",", "heather", "gibbs")
  val tokens = List("hi", "folks", ",", "do", "not", "erase", ",", "this", "is", "a", "hoax", ".", "according", "to", "symantac", ".", "Consult", "others", "before", "u", "decide", "to", "erase", "this", "crucial", "part", "to", "start", "windows", ".", "have", "a", "good", "2002", "thunder", "wrote", ":", ">", "Hi", ",", "I", "am", "sorry", "to", "have", "to", "tell", "you", "this", "but", "2001", "refuses", "to", "go", "out", "quitely", ">", "for", "allof", "us", ".", ">", ">", "Someone", "who", "had", "us", "in", "their", "address", "book", "infected", "our", "computer", "with", "a", ">", "nasty", ">", ">", "virus", ".", "It", "can", "be", "transferred", "to", "all", "in", "our", "address", "book", "--", "and", "you", "happen", ">", "to", ">", ">", "be", "in", "our", "address", "book", ".", "We", "caught", "it", "before", "it", "did", "any", "damage", "to", "our", ">", ">", "computer", ".", ">", ">", ">", ">", "We", "received", "the", "message", "below", ",", "checked", "it", "out", "and", "found", "out", "we", "had", "the", ">", "virus", ">", ">", "in", "the", "C", "drive", ".", "You", "are", "in", "our", "address", "book", ",", "so", "please", "check", "you", "computer", ">", ">", "for", "the", "virus", ".", ">", ">", ">", ">", "The", "virus", "lies", "dormant", "for", "14", "days", "and", "then", "kills", "your", "hard", "drive", ".", "Here", "'s", ">", ">", "how", "to", "stop", "it", ".", "If", "you", "'ve", "got", "it", ",", "send", "this", "to", "everyone", "in", "your", "address", ">", "book", ".", ">", ">", ">", ">", "Remove", "it", "by", "following", "these", "steps", ":", ">", ">", "1", ".", "Go", "to", "``", "start", "''", "then", "to", "``", "find", "or", "search", "'", "-LRB-", "depending", "on", "your", "computer", "-RRB-", ">", ">", "2", ".", "In", "the", "``", "search", "for", "files", "or", "folders", "''", "type", "``", "sulfnbk.exe", "''", "-", "this", "is", "the", ">", ">", "virus", ">", ">", "3", ".", "In", "the", "``", "look", "in", "''", "make", "sure", "you", "'re", "searching", "drive", "C", ">", ">", "4", ".", "Hit", "``", "search", "or", "find", "''", ">", ">", "5", ".", "If", "this", "file", "shows", "up", "-LRB-", "it", "'s", "an", "ugly", "blackish", "icon", "that", "will", "have", "the", ">", "name", ">", ">", "``", "sulfnbk.exe", ">", ">", "DO", "NOT", "OPEN", "IT", "!!", ">", ">", "6", ".", "Right", "click", "on", "the", "file", "-", "go", "down", "to", "delete", "and", "left", "click", ">", ">", "7", ".", "It", "will", "ask", "you", "if", "you", "want", "to", "send", "it", "to", "the", "recycle", "bin", ",", "say", "yes", ">", ">", "8", ".", "Go", "to", "your", "desktop", "-LRB-", "where", "all", "of", "your", "icons", "are", "-RRB-", "and", "double", "click", "on", ">", ">", "recycle", "bin", ">", ">", "9", ".", "Right", "click", "on", "``", "sulfnbk.exe", "''", "and", "delete", "again", "-", "or", "empty", "bin", ">", ">", ">", ">", "If", "you", "find", "it", ",", "send", "the", "e-mail", "to", "all", "in", "your", "address", "book", "because", ">", "that", "'s", ">", ">", "how", "it", "is", "transferred", ".")

 
  println(tokens.length)
  var o = new EmoPerf()

  
   val nres = o.processEmo(tokens)
   
}
