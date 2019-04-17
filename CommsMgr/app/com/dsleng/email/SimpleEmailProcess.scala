package com.dsleng.email

import com.dsleng.email.utils.FileStore
import com.dsleng.email.utils.MetaInfo
import com.dsleng.email.utils.TimeMgr
import com.dsleng.email.utils.Batch

object SimpleEmailProcess {
  val usage = """
    Usage: SimpleEmailProcess --batch [true|false] --batch-dir ["./batch/"] --start-time ["09-12-2017T15:00:00"] --conf ["./conf"] --emailStore ["./emailStore"]
  """
  val emailStore = "/Users/suresh/enron/junk/"
  def main(args: Array[String]): Unit = {
    if (args.length == 0){ 
      println(usage)
      System.exit(1)
    }
    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      // 'batch translates to Symbol("batch")
      list match {
        case Nil => map
        case "--batch" :: value :: tail =>
                               nextOption(map ++ Map('batch -> value.toBoolean), tail)
        case "--batch-dir" :: value :: tail =>
                               nextOption(map ++ Map('batchdir -> value.toString), tail)
        case "--start-time" :: value :: tail =>
                               nextOption(map ++ Map('start -> value.toString), tail)
        case "--conf" :: value :: tail =>
                               nextOption(map ++ Map('conf -> value.toString), tail)
        case "--emailStore" :: value :: tail =>
                               nextOption(map ++ Map('emailStore -> value.toString), tail)
        //case string :: opt2 :: tail if isSwitch(opt2) => 
         //                      nextOption(map ++ Map('infile -> string), list.tail)
        //case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
        //case option :: tail => println("Unknown option "+option) 
        case _ => map
      }
    }
    val options = nextOption(Map(),arglist)
    println(options)
    val batch: Boolean = options.getOrElse('batch, true).asInstanceOf[Boolean]
    val start: String = options.getOrElse('start, "09-12-2017T15:00:00").asInstanceOf[String]
    val batchdir: String = options.getOrElse('batchfile, "./batch/").asInstanceOf[String]
    val conf: String = options.getOrElse('conf, "./conf/").asInstanceOf[String]
    val emailStore: String = options.getOrElse('emailStore, "./emailStore/").asInstanceOf[String]
    
    
    val bm = new BatchMgr(emailStore,start,batchdir,
        conf + "/timefile.txt",
        conf)
    
    //val n = new MetaInfo("/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/")
    //n.Process()
    /*
    val tm = new TimeMgr(start,1,"/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/timefile.txt")
    val bat = new Batch(batchdir)
    for( a <- 1 until 1000000){
      val fd = new FileStore(emailStore,batch,bat)
      fd.process(tm);
      //Thread.sleep(1000)
    }
    tm.close()
    bat.close()
    println("completed processing emails")
    * */
    
  }

}
