package com.dsleng.email

import com.dsleng.email.utils.TimeMgr
import com.dsleng.email.utils.Batch
import com.dsleng.email.utils.FileStore
import play.api.libs.json._

class BatchMgr(emailStore: String,start: String,batchdir: String,timefile: String,metaInfo: String) {
  val tm = new TimeMgr(start,1,timefile)
  val bat = new Batch(batchdir)
  val fd = new FileStore(emailStore,true,bat)
  val emodels = fd.processModel()
  
  // metaInfo="/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/"
  val metaData = new com.dsleng.email.utils.MetaInfo(metaInfo)
  metaData.Load()
  println("completed loading metadata")
    
  for( a <- 1 until 2){
  //for( a <- 1 until 1000000){
    emodels.foreach(m=>{
      m.dummyUpdate(metaData, tm.getNextTime()) 
      bat.process(m)
    })
  }
  
  tm.close()
  bat.close()
}