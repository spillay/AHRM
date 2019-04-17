package com.dsleng.datafeeder

import com.dsleng.email.utils.IPTZ
import com.dsleng.email.utils.TimeMgr

object GenMgr {
  def main(args: Array[String]) {
     println("GenMgr")
     val tm = new TimeMgr("09-12-2017T22:00:00",30)
     println(tm.curr.toString())
     println(tm.getNextTime())
     println(tm.getNextTime())
     /*
     val cust = new CustomerMgr("/Users/suresh/git/DAT/CommsMgr/commsmgr/conf/resources/")
     cust.process()
     */
     /*
     val ipGen = new IPTZ()
     if (ipGen.checkTZ("1.2.3.4", "11/15/2013 23:00:00")){
       println("accept ip")
     } else {
       println("reject ip")
     }
     * 
     */
     //println(ipGen.getTimeZone("1.2.3.4","11/15/2013 18:00:00"))
     
  }
}