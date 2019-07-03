package com.dsleng.model

import com.dsleng.email.{SimpleEmailModel,SimpleEmailExt}
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.dsleng.email.ModelJsonImplicits._

case class EmoEmailCtl(model: SimpleEmailExt,emotion: String)
case class EmailCtl(model: SimpleEmailExt)
case class TextCtl(text: String)
case class TokenCtl(model: SimpleEmailExt,tokens: TokenStrCtl)
case class TokenStrCtl(tokens: List[String])
case class NormData(emotion: String, count: Double)
case class NLPDataCtl(tokensSW: Seq[String],sw: Seq[String],tokens: Seq[String])
case class TokenDataCtl(model: SimpleEmailExt,nlp: NLPDataCtl)



case class Emo(emotion: String, words: Seq[String])
case class EmoData(emotion: String, count: Integer, words: Seq[String])

object NLPDataCtl {
  implicit val nlpJsonFormat = new RootJsonFormat[NLPDataCtl] {
    def write(o: NLPDataCtl): JsValue = {
      println("writing nlp========================")
      JsObject(
        "sw" -> JsArray(o.sw.map(s => JsString(s.replaceAll("\"", ""))).toVector),
        "tokens" -> JsArray(
            o.tokens.map(s => JsString(s.replaceAll("\"", ""))).toVector
         ),
        "tokensSW" -> JsArray(o.tokensSW.map(s => JsString(s.replaceAll("\"", "").toLowerCase())).toVector)
       )
    }
    def read(value: JsValue) = {
      println("reading nlp")
      value.asJsObject.getFields("sw","tokens","tokensSW") match {
        case Seq(JsArray(sw),JsArray(tokens),JsArray(tokensSW)) => 
          new NLPDataCtl(
              tokensSW.map(_.toString().toLowerCase()),
              sw.map(_.toString()),
              tokens.map(_.toString())
              )
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}




object EmoData {
  implicit val emJsonFormat = new RootJsonFormat[EmoData] {
    def write(o: EmoData): JsValue = {
      println("writing emodata")
      JsObject(
        "emotion" -> JsString(o.emotion),
        "count" -> JsNumber(o.count),
        "words" -> JsArray(o.words.map(JsString(_)).toVector)
       )
    }
    def read(value: JsValue) = {
      println("reading emodata",value.asJsObject.getFields("emotion"))
      println("reading emodata",value.asJsObject.getFields("count"))
      println("reading emodata",value.asJsObject.getFields("words"))
      value.asJsObject.getFields("emotion","count","words") match {
        case Seq(JsString(emotion),JsNumber(cnt),JsArray(words)) => 
          println(emotion,cnt,words)
          new EmoData(
              emotion,
              cnt.toInt,
              words.map(_.toString()))
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}
case class EmoRes(emotions: Seq[EmoData],prime: Seq[EmoData])
object EmoRes {
  implicit val implicitResWrites = new RootJsonFormat[EmoRes] {
    def write(o: EmoRes): JsValue = {
      println("writing")
      JsObject(
        "emotions" -> JsArray(o.emotions.map(_.toJson).toVector),
        "prime" -> JsArray(o.prime.map(_.toJson).toVector)
       )
    }
    def read(value: JsValue) = {
      println("reading")
      value.asJsObject.getFields("emotions","prime") match {
        case Seq(JsArray(emotion),JsArray(prime)) => 
          new EmoRes(
              emotion.map(_.convertTo[EmoData]),
              prime.map(_.convertTo[EmoData]))
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}
object TokenDataCtl {
  implicit val implicitResWrites = new RootJsonFormat[TokenDataCtl] {
    def write(o: TokenDataCtl): JsValue = {
      println("writing")
      JsObject(
        "model" -> o.model.toJson,
        "nlp" -> o.nlp.toJson
       )
    }
    def read(value: JsValue) = {
      println("reading")
      value.asJsObject.getFields("model","nlp") match {
        case Seq(JsString(model),JsString(nlp)) => 
          new TokenDataCtl(
              model.parseJson.convertTo[SimpleEmailExt],
              nlp.parseJson.convertTo[NLPDataCtl]
              )
        case _ => throw new DeserializationException("EmoRes expected")
      }
    }
  }
}
object CtlJsonImplicits extends DefaultJsonProtocol {
  implicit val impTokenStrCtl = jsonFormat1(TokenStrCtl)
  implicit val impTokenCtl = jsonFormat2(TokenCtl)
  implicit val impEmoEmailCtl = jsonFormat2(EmoEmailCtl)
  implicit val impNormData = jsonFormat2(NormData)
}
object Controllers extends App {
  val data = """
    {"sw":["1","0","0","0","0","0","0","0","0","1","0","0","0","0","0","0","1","0","0","1","0","0","0","0","0","0","0","0","0","1","0","0","0","1","0","0","0","0","1","0","0","1","0","0","1","0","0","0","0","0","0","0","0","1","0","0","0","0","1","1","0","0","0","1","0","0","1","0","0","0","0","0","1","0","0","0","1","1","0","0","0","0","0","0","1","0","0","0","0","1","0","0","0","1","0","0","0","0","0","0","0","0","1","0","0","0","1","1","0","1","1","0","0","0","1","1","0","0","0","0","0","1","0","0","0","0","1","0","0","1","0","1","0","0","1","1","0","1","1","0","1","0","0","0","1","0","0","1","0","0","0","1","1","0","1","1","0","0","1","1","0","1","0","0","0","1","0","0","1","1","1","0","0","1","1","0","1","0","1","0","1","1","0","1","0","1","0","1","0","1","0","0","0","0","0","0","0","0","0","1","0","1","1","0","0","0","1","0","1","0","1","0","0","0","0","1","0","0","0","1","0","0","1","0","1","1","0","0","1","1","0","1","1","0","0","0","0","0","0","0","0","0","1","1","0","1","1","0","0","0","0","0","0","0","0","0","0","0","0","1","0","1","0","1","0","0","0","0","0","1","0","0","1","0","1","0","0","0","1","1","0","0","0","0","0","0","1","0","1","1","0","1","0","0","0","1","0","0","0","1","0","1","0","0","0","1","0","1","0","1","0","1","0","0","0","0","0","1","0","1","0","0","1","0","0","1","0","0","1","1","0","0","0","1","0","1","0","1","0","0","0","1","0","0","1","0","1","0","1","0","1","1","0","0","1","1","0","0","1","0","1","1","0","0","1","0","0","1","1","0","1","1","0","0","0","1","0","0","0","0","0","0","1","0","0","0","0","0","0","0","1","0","0","1","0","0","0","1","0","1","0","1","1","0","0","0","0","0","0","0","1","0","1","0","1","0","0","0","0","0","0","0","0","0"],"tokens":["The","North","American","Electric","Reliability","Council","-LRB-","NERC","-RRB-","on","December","4",",","2001",",","posted","for","public","comment","a","proposed","Wholesale","Electric","Standards","Model","-LRB-","WESM","-RRB-","--","an","industry-based","consensus","process","for","developing",",","maintaining",",","and","publishing","standards","that","promote","reliable","and","efficient","wholesale","electricity","markets","throughout","North","America",".","Such","standards","would","address",",","in","an","integrated","way",",","the","whole","spectrum","of","reliability",",","market","interface",",","and","business","practice","standards","through","a","fair",",","open",",","balanced",",","and","inclusive","stakeholder","process",".","On","December","19",",","the","Federal","Energy","Regulatory","Commission","-LRB-","FERC","-RRB-","issued","an","Order","Providing","Guidance","on","the","Formation","of","a","Standards","Development","Organization","for","the","Wholesale","Electric","Industry","-LRB-","Docket","No",".","RM01-12-00","-RRB-",".","That","order","directs","the","industry","to","reach","agreement","on","the","formation","of","an","organization","to","develop","consensus","standards","for","business","practices","and","electronic","communications",".","If","the","industry","can","not","reach","consensus","on","this","issue","by","March","15",",","the","Commission","indicated","that","it","will","either","institute","its","own","procedures","to","choose","an","organization","or","will","develop","the","standards","itself",".","To","provide","the","NERC","Standing","Committees","Representation","Task","Force","-LRB-","SCRTF","-RRB-","as","well","as","the","NERC","Stakeholders","Committee","and","Board","of","Trustees","the","broadest","possible","input",",","the","Board","strongly","urges","all","interested","parties","to","comment","on","the","WESM","proposal","in","the","context","of","the","recent","FERC","order",".","Commenters","should","give","particular","attention","to","how","NERC","and","the","recently","approved","North","American","Energy","Standards","Board","-LRB-","NAESB","-RRB-","could","collaborate","in","developing","and","overseeing","a","single",",","industry-based","consensus","process","to","develop","standards","that","assure","the","continued","reliable","operation","of","the","integrated","North","American","electric","transmission","grids","as","well","as","the","development","of","business","practice","standards","and","communication","protocols","needed","to","complement","the","market","design","principles","that","FERC","has","announced","its","intention","to","develop",".","NERC","'s","SCRTF","will","use","all","comments","received","to","help","shape","its","final","proposal","to","the","NERC","Stakeholders","Committee","and","Board","of","Trustees","in","February","2002",".","We","also","intend","to","provide","the","input","we","receive","in","this","public","forum","on","the","WESM","model","to","FERC","for","its","consideration",".","For","additional","information","on","the","SCRTF","and","its","membership",",","go","to",":","www.nerc.com/committees/scrtf.html",".","Please","direct","questions","to","David","Nevius",",","NERC","Vice","President",",","at","609-452-8060",",","or","e-mail","dave.nevius@nerc.com",".","The","proposal","is","posted","at","the","following","NERC","web","site",":","http://www.nerc.com/",".","The","deadline","for","comments","is","January","8",",","2002",".","Sincerely",",","Heather","Gibbs"],"tokensSW":["North","American","Electric","Reliability","Council","-LRB-","NERC","-RRB-","December","4",",","2001",",","posted","public","comment","proposed","Wholesale","Electric","Standards","Model","-LRB-","WESM","-RRB-","--","industry-based","consensus","process","developing",",","maintaining",",","publishing","standards","promote","reliable","efficient","wholesale","electricity","markets","throughout","North","America",".","standards","would","address",",","integrated","way",",","whole","spectrum","reliability",",","market","interface",",","business","practice","standards","fair",",","open",",","balanced",",","inclusive","stakeholder","process",".","December","19",",","Federal","Energy","Regulatory","Commission","-LRB-","FERC","-RRB-","issued","Order","Providing","Guidance","Formation","Standards","Development","Organization","Wholesale","Electric","Industry","-LRB-","Docket",".","RM01-12-00","-RRB-",".","order","directs","industry","reach","agreement","formation","organization","develop","consensus","standards","business","practices","electronic","communications",".","industry","reach","consensus","issue","March","15",",","Commission","indicated","either","institute","procedures","choose","organization","develop","standards",".","provide","NERC","Standing","Committees","Representation","Task","Force","-LRB-","SCRTF","-RRB-","well","NERC","Stakeholders","Committee","Board","Trustees","broadest","possible","input",",","Board","strongly","urges","interested","parties","comment","WESM","proposal","context","recent","FERC","order",".","Commenters","should","give","particular","attention","NERC","recently","approved","North","American","Energy","Standards","Board","-LRB-","NAESB","-RRB-","could","collaborate","developing","overseeing","single",",","industry-based","consensus","process","develop","standards","assure","continued","reliable","operation","integrated","North","American","electric","transmission","grids","well","development","business","practice","standards","communication","protocols","needed","complement","market","design","principles","FERC","announced","intention","develop",".","NERC","'s","SCRTF","use","comments","received","help","shape","final","proposal","NERC","Stakeholders","Committee","Board","Trustees","February","2002",".","also","intend","provide","input","receive","public","forum","WESM","model","FERC","consideration",".","additional","information","SCRTF","membership",",","go",":","www.nerc.com/committees/scrtf.html",".","Please","direct","questions","David","Nevius",",","NERC","Vice","President",",","609-452-8060",",","e-mail","dave.nevius@nerc.com",".","proposal","posted","following","NERC","web","site",":","http://www.nerc.com/",".","deadline","comments","January","8",",","2002",".","Sincerely",",","Heather","Gibbs"]}
    """.stripMargin.parseJson.convertTo[NLPDataCtl]
  //println(data.toJson)
  println(data.tokens)
  println(data.tokensSW)
 val model = """
   {"department":"","ec":"","emotions":"none","fileName":"/Data/enron/maildir/kean-s/inbox/15.'.complete'","model":{"allHeaders":{"theHeaders":[{"name":"Message-ID","value":"<8503236.1075855418413.JavaMail.evans@thyme>"},{"name":"Date","value":"Fri, 28 Dec 2001 13:17:15 -0800 (PST)"},{"name":"From","value":"heather.gibbs@nerc.net"},{"name":"To","value":"heather@nerc.com"},{"name":"Subject","value":"Supplemental Notice -- Request for Comments on WESM Proposal"},{"name":"Mime-Version","value":"1.0"},{"name":"Content-Type","value":"text/plain; charset=us-ascii"},{"name":"Content-Transfer-Encoding","value":"7bit"},{"name":"X-From","value":"Heather Gibbs <Heather.Gibbs@nerc.net>"},{"name":"X-To","value":"heather@nerc.com"},{"name":"X-cc","value":" "},{"name":"X-bcc","value":" "},{"name":"X-Folder","value":"\\Steven_Kean_Jan2002\\Kean, Steven J.\\Inbox"},{"name":"X-Origin","value":"Kean-S"},{"name":"X-FileName","value":"skean (Non-Privileged).pst"}]},"date":"2001-01-05T16:17:15.000-0500","from":"heather.gibbs@nerc.net","htmlContent":"","receiverGeoInfo":{"city":"Scottsdale","country_code":"US","country_name":"United States","ip":"184.168.221.79","latitude":"33.6013","longitude":"-111.8867","metro_code":"753","region_code":"AZ","region_name":"Arizona","time_zone":"America/Phoenix","zip_code":"85260"},"receiverIP":"0.0.0.0","receiverLocation":{"lat":"33.6013","lon":"-111.8867"},"senderGeoInfo":{"city":"Scottsdale","country_code":"US","country_name":"United States","ip":"184.168.221.79","latitude":"33.6013","longitude":"-111.8867","metro_code":"753","region_code":"AZ","region_name":"Arizona","time_zone":"America/Phoenix","zip_code":"85260"},"senderIP":"0.0.0.0","senderLocation":{"lat":"33.6013","lon":"-111.8867"},"subject":"Supplemental Notice -- Request for Comments on WESM Proposal","textContent":"The North American Electric Reliability Council (NERC) on December 4,\n2001, posted for public comment a proposed Wholesale Electric Standards\nModel (WESM) -- an industry-based consensus process for developing,\nmaintaining, and publishing standards that promote reliable and\nefficient wholesale electricity markets throughout North America.  Such\nstandards would address, in an integrated way, the whole spectrum of\nreliability, market interface, and business practice standards through a\nfair, open, balanced, and inclusive stakeholder process.\n\nOn December 19, the Federal Energy Regulatory Commission (FERC) issued\nan Order Providing Guidance on the Formation of a Standards Development\nOrganization for the Wholesale Electric Industry (Docket No.\nRM01-12-00).  That order directs the industry to reach agreement on the\nformation of an organization to develop consensus standards for business\npractices and electronic communications.  If the industry cannot reach\nconsensus on this issue by March 15, the Commission indicated that it\nwill either institute its own procedures to choose an organization or\nwill develop the standards itself.\n\nTo provide the NERC Standing Committees Representation Task Force\n(SCRTF) as well as the NERC Stakeholders Committee and Board of Trustees\nthe broadest possible input, the Board strongly urges all interested\nparties to comment on the WESM proposal in the context of the recent\nFERC order.  Commenters should give particular attention to how NERC and\nthe recently approved North American Energy Standards Board (NAESB)\ncould collaborate in developing and overseeing a single, industry-based\nconsensus process to develop standards that assure the continued\nreliable operation of the integrated North American electric\ntransmission grids as well as the development of business practice\nstandards and communication protocols needed to complement the market\ndesign principles that FERC has announced its intention to develop.\n\nNERC's SCRTF will use all comments received to help shape its final\nproposal to the NERC Stakeholders Committee and Board of Trustees in\nFebruary 2002.  We also intend to provide the input we receive in this\npublic forum on the WESM model to FERC for its consideration. \n\nFor additional information on the SCRTF and its membership, go to:\nwww.nerc.com/committees/scrtf.html. Please direct questions to David\nNevius, NERC Vice President, at 609-452-8060, or e-mail\ndave.nevius@nerc.com.\n\nThe proposal is posted at the following NERC web site:\nhttp://www.nerc.com/. The deadline for comments is January 8, 2002.\n\nSincerely,\n\nHeather Gibbs","to":"heather@nerc.com"},"norm":"","prime":"Unknown","product":[]}
   """.stripMargin.parseJson.convertTo[SimpleEmailExt]
  val td = new TokenDataCtl(model,data)
  //println(td.toJson)
}
    