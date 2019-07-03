package com.dsleng.store.es

import org.elasticsearch.client._
import org.apache.http.HttpHost
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest
import org.elasticsearch.action.admin.indices.get.GetIndexRequest
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest
import org.elasticsearch.action.bulk.BulkItemResponse
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import com.dsleng.email.{SimpleEmailExt,Headers,EmailHeader}
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level
import spray.json._
import com.dsleng.email.ModelJsonImplicits._

class BListener extends ActionListener[BulkResponse] with Logging {
  override def onResponse(response: BulkResponse) {
    if (response.hasFailures()) {
      logger.error(s"Error in Bulk Listener ${response.buildFailureMessage()}")
    }
  }
  override def onFailure(t: Exception) {
    val msg = "Failed to register bulk of sources."
    logger.error(s"Failed ${t} and exception thrown")
    throw new Exception(msg)
  }
}

class Feeder(ip: String) extends Logging {
  var bulkRequest = new BulkRequest();
  var bulkCnt = 0;
  var client = new RestHighLevelClient(
    RestClient.builder(
      new HttpHost(ip, 9200, "http"),
      new HttpHost(ip, 9201, "http")))
  println("Starting Feeder")
  def write(indexRequest: IndexRequest) {
    var indexResponse = client.index(indexRequest,RequestOptions.DEFAULT)
    if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
      logger.debug("created document")
    }
  }
  def writeBULKAsync() {
    logger.debug("writeBULKAsync")
    val listener = new BListener()
    client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener)
    bulkRequest = new BulkRequest();
    bulkCnt = 0
  }
   def writeBULK() {
    logger.debug("writeBULK")
    val listener = new BListener()
    val resp = client.bulk(bulkRequest, RequestOptions.DEFAULT)
    if (resp.hasFailures()){
      resp.getItems().foreach(r=>{
        logger.debug(r.getFailureMessage())
      })
    }
    bulkRequest = new BulkRequest();
    bulkCnt = 0
  }
   
  def addDocumentGen(doc: String, index: String = "analogemails") {
    var builder = XContentFactory.jsonBuilder();
    //val jsonString = model.toJson
    //println(jsonString)
    var indexRequest = new IndexRequest(index)
    indexRequest.source(doc, XContentType.JSON);

    bulkRequest.add(indexRequest)
    bulkCnt += 1
    if (bulkCnt == 25) {
      //println("writing bulk");
      this.writeBULK()

    }
  }

  def addDocument(model: SimpleEmailExt, index: String = "analogemails") {
    var builder = XContentFactory.jsonBuilder();
    val jsonString = model.toJson
    //println(jsonString)
    var indexRequest = new IndexRequest(index)
    indexRequest.source(jsonString, XContentType.JSON);

    bulkRequest.add(indexRequest)
    bulkCnt += 1
    if (bulkCnt == 25) {
      //println("writing bulk");
      this.writeBULK()

    }
  }
  def recreate(idxName: String){
    if (existsIndex(idxName)){
      if (deleteIndex(idxName) ){
        logger.debug(s"Successfully Deleted ${idxName}")
      } 
    } else {
      logger.debug(s"${idxName} does not exist")
    }
    // TODO: Fix ES Mapping
//    if (createIndex(idxName,EmailModel.getESMapping())){
//      logger.debug("created index")
//    }
  }
  /*
  def createIndex(name: String,map: String="None"): Boolean ={
    val idx = new CreateIndexRequest(name);
    val resp = client.indices().create(idx, RequestOptions.DEFAULT)
    val ack = resp.isAcknowledged()
    // TODO: Check if there is a need to delete the mapping
    if (ack && map != "None"){
      logger.debug("Creating Map")
      var mapping = new PutMappingRequest(name)
      mapping.`type`("_doc")
      mapping.source(map, XContentType.JSON)
      val putMappingResponse = client.indices().putMapping(mapping, RequestOptions.DEFAULT);
      return putMappingResponse.isAcknowledged()
    }
    return ack
  }
  * 
  */
  def deleteIndex(name: String): Boolean = {
    val idx = new DeleteIndexRequest(name);
    val resp = client.indices().delete(idx, RequestOptions.DEFAULT)
    return resp.isAcknowledged()
  }
  def existsIndex(name: String): Boolean = {
    val idx = new GetAliasesRequest(name)
    //idx.indices(name)
    //client.indices().exists(idx, RequestOptions.DEFAULT)
    client.indices().existsAlias(idx, RequestOptions.DEFAULT);
  }
  def close() = {
    if (bulkCnt > 0) {
      this.writeBULK();
    }
    client.close()
  }

}

object Feeder extends App {
  println("Starting Feeder")
  val o = new Feeder("localhost")
  val se = """
  {"department":"Unassigned","ec":"","emotions":"[{\"count\":1,\"emotion\":\"Joy\",\"words\":[\"\\\"partie\\\"\"]},{\"count\":2,\"emotion\":\"Agreeableness\",\"words\":[\"\\\"fair\\\"\",\"\\\"strongly\\\"\"]},{\"count\":3,\"emotion\":\"Contentment\",\"words\":[\"\\\"agreement\\\"\",\"\\\"well\\\"\",\"\\\"approv\\\"\"]},{\"count\":1,\"emotion\":\"Interest\",\"words\":[\"\\\"interested\\\"\"]},{\"count\":1,\"emotion\":\"Relief\",\"words\":[\"\\\"assur\\\"\"]}]","fileName":"/Data/enron/maildir/kean-s/inbox/15.'.complete'","model":{"allHeaders":{"theHeaders":[{"name":"Message-ID","value":"<8503236.1075855418413.JavaMail.evans@thyme>"},{"name":"Date","value":"Fri, 28 Dec 2001 13:17:15 -0800 (PST)"},{"name":"From","value":"heather.gibbs@nerc.net"},{"name":"To","value":"heather@nerc.com"},{"name":"Subject","value":"Supplemental Notice -- Request for Comments on WESM Proposal"},{"name":"Mime-Version","value":"1.0"},{"name":"Content-Type","value":"text/plain; charset=us-ascii"},{"name":"Content-Transfer-Encoding","value":"7bit"},{"name":"X-From","value":"Heather Gibbs <Heather.Gibbs@nerc.net>"},{"name":"X-To","value":"heather@nerc.com"},{"name":"X-cc","value":" "},{"name":"X-bcc","value":" "},{"name":"X-Folder","value":"\\Steven_Kean_Jan2002\\Kean, Steven J.\\Inbox"},{"name":"X-Origin","value":"Kean-S"},{"name":"X-FileName","value":"skean (Non-Privileged).pst"}]},"date":"2001-01-05T16:17:15.000-0500","from":"heather.gibbs@nerc.net","htmlContent":"","receiverGeoInfo":{"city":"Scottsdale","country_code":"US","country_name":"United States","ip":"184.168.221.79","latitude":"33.6013","longitude":"-111.8867","metro_code":"753","region_code":"AZ","region_name":"Arizona","time_zone":"America/Phoenix","zip_code":"85260"},"receiverIP":"23.100.44.101","receiverLocation":{"lat":"33.6013","lon":"-111.8867"},"senderGeoInfo":{"city":"Scottsdale","country_code":"US","country_name":"United States","ip":"184.168.221.79","latitude":"33.6013","longitude":"-111.8867","metro_code":"753","region_code":"AZ","region_name":"Arizona","time_zone":"America/Phoenix","zip_code":"85260"},"senderIP":"0.0.0.0","senderLocation":{"lat":"33.6013","lon":"-111.8867"},"subject":"Supplemental Notice -- Request for Comments on WESM Proposal","textContent":"The North American Electric Reliability Council (NERC) on December 4,\n2001, posted for public comment a proposed Wholesale Electric Standards\nModel (WESM) -- an industry-based consensus process for developing,\nmaintaining, and publishing standards that promote reliable and\nefficient wholesale electricity markets throughout North America.  Such\nstandards would address, in an integrated way, the whole spectrum of\nreliability, market interface, and business practice standards through a\nfair, open, balanced, and inclusive stakeholder process.\n\nOn December 19, the Federal Energy Regulatory Commission (FERC) issued\nan Order Providing Guidance on the Formation of a Standards Development\nOrganization for the Wholesale Electric Industry (Docket No.\nRM01-12-00).  That order directs the industry to reach agreement on the\nformation of an organization to develop consensus standards for business\npractices and electronic communications.  If the industry cannot reach\nconsensus on this issue by March 15, the Commission indicated that it\nwill either institute its own procedures to choose an organization or\nwill develop the standards itself.\n\nTo provide the NERC Standing Committees Representation Task Force\n(SCRTF) as well as the NERC Stakeholders Committee and Board of Trustees\nthe broadest possible input, the Board strongly urges all interested\nparties to comment on the WESM proposal in the context of the recent\nFERC order.  Commenters should give particular attention to how NERC and\nthe recently approved North American Energy Standards Board (NAESB)\ncould collaborate in developing and overseeing a single, industry-based\nconsensus process to develop standards that assure the continued\nreliable operation of the integrated North American electric\ntransmission grids as well as the development of business practice\nstandards and communication protocols needed to complement the market\ndesign principles that FERC has announced its intention to develop.\n\nNERC's SCRTF will use all comments received to help shape its final\nproposal to the NERC Stakeholders Committee and Board of Trustees in\nFebruary 2002.  We also intend to provide the input we receive in this\npublic forum on the WESM model to FERC for its consideration. \n\nFor additional information on the SCRTF and its membership, go to:\nwww.nerc.com/committees/scrtf.html. Please direct questions to David\nNevius, NERC Vice President, at 609-452-8060, or e-mail\ndave.nevius@nerc.com.\n\nThe proposal is posted at the following NERC web site:\nhttp://www.nerc.com/. The deadline for comments is January 8, 2002.\n\nSincerely,\n\nHeather Gibbs","to":"heather@nerc.com"},"norm":"[{\"count\":0.125,\"emotion\":\"Joy\"},{\"count\":0.25,\"emotion\":\"Agreeableness\"},{\"count\":0.375,\"emotion\":\"Contentment\"},{\"count\":0.125,\"emotion\":\"Interest\"},{\"count\":0.125,\"emotion\":\"Relief\"}]","prime":"Contentment","product":[]}
   """.stripMargin.parseJson.convertTo[SimpleEmailExt]
//  se.model.allHeaders = new Headers(List[EmailHeader]())
//  se.model.textContent = ""
//  se.model.htmlContent = ""
//  se.emotions = ""
//  se.prime = ""
  println(se.toJson)
  o.addDocumentGen(se.toJson.toString(), "ahrm_test")
  o.close()
}


