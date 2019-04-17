package com.dsleng.email.utils

import com.pff.PSTFile
import com.pff.PSTFolder
import com.pff.PSTMessage
import com.dsleng.email.SimpleEmailModel
import com.dsleng.email.Headers
import com.dsleng.email.EmailHeader
import scala.collection.mutable.ListBuffer

class PSTMgr(filename: String) {
  var depth = -1;
  var emails = new ListBuffer[SimpleEmailModel]();
  def Process(){
     val  pstFile = new PSTFile(filename);			
			println(pstFile.getMessageStore().getDisplayName());
			//processFolder(pstFile.getRootFolder());
			val folder = pstFile.getRootFolder();
			this.ProcessFolder(folder);
  }
  def ProcessFolder(folder: PSTFolder){
     depth += 1
     if(depth>0){
       println(folder.getDisplayName());
     }
     val folders = folder.getSubFolders();
     folders.forEach(f=>{
       this.ProcessFolder(f);
     })
     if (folder.getContentCount() > 0){
       depth += 1;
			 var email: PSTMessage = folder.getNextChild().asInstanceOf[PSTMessage];
  		 while (email != null) {
  		  println(email.toString());
  		  /* 
  		  val sm = new SimpleEmailModel(
  		     new Headers((new ListBuffer[EmailHeader]()).toList),    
  		     email.getBody(),email.getBodyHTML(),
  		     email.getSenderEmailAddress(),
  		     email.getDisplayTo(),
  		     email.getMessageDeliveryTime(),
  		     email.getSubject()
  		   );
  		   emails += sm;
  		   */
  		   println("To: " + email.getDisplayTo());
  		    println("Original To: " + email.getOriginalDisplayTo());
  		   println("Sender: " + email.getSenderEmailAddress());
  		   println("content: " + email.getBody());
  		   //println(email.getReceivedByName() + ":" + email.getReceivedByAddress());
  		   //println("sender " + email.getSenderName() + " : " + email.getSenderEmailAddress());
  				//System.out.println("EmailsUBJECT: "+email.getSubject());
  				//System.out.println("EmailhTMLbODY: "+email.getBodyHTML());
  				//System.out.println("EmailbODY: "+email.getBody());
  			//	System.out.println("EmaildISPLAY: "+email.getDisplayName());
  				email = folder.getNextChild().asInstanceOf[PSTMessage];
  			}
  			depth -= 1;
     }
     depth -= 1;
  }
}