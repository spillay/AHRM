package com.dsleng.twitter

import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Durations, StreamingContext}


class Feeder {
    val spark = SparkSession
      .builder
      .appName("Twitter Streaming")
      .master("local[4]")
      //.config("spark.eventLog.enabled", "true")
      //.config("spark.eventLog.dir", "file:/usr/local/spark/history/")
      .getOrCreate()

    // 
    val ssc = new StreamingContext(spark.sparkContext, Durations.minutes(1L))

    val consumerKey = "aIhwiX1MGtVYYVpXM9dcWnTZa";
    val consumerSecret = "3qECty3Es73jSIA1TCq64rCzDg4phCBwKQl7FaeIPvxcOC5Uvx";
    val accessToken = "160628450-fuS97dtOvawanUIiteeSpF2iIhh7JRpWQfZGCP2U";
    val accessTokenSecret = "608dpbNPjEsrq8ZfQgUQKzTCrs1e7Dy3UpmU1SlR9pjF9";
    
    System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)
    
    

    def run(){
      // filter (q="@username", since_id=tweet_id)
      val stream = TwitterUtils.createStream(ssc, None)
      val res = stream.map( status => {
          status
      }).saveAsTextFiles("tweets.txt")
      
      
  
      ssc.start()
      ssc.awaitTermination()
      spark.close()
    }
    def read(){
      val rdd = spark.sparkContext.textFile("file:///home/suresh/git/AHRM/lib/com.dsleng.twitter/tweets.txt-1558663380000")
      println(rdd.first())
    }
}

object Feeder extends App {
  println("Twitter Feeder")
  val o = new Feeder()
  //o.run()
  o.read()
}