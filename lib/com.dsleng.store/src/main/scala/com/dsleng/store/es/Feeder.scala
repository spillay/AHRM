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
import org.elasticsearch.action.bulk.BulkItemResponse
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import com.dsleng.model.EmailModel
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level

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

  def addDocument(model: EmailModel, index: String = "analogemails") {
    var builder = XContentFactory.jsonBuilder();
    val jsonString = Json.toJson(model).toString()
    //println(jsonString)
    var indexRequest = new IndexRequest(index, "_doc")
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
    if (createIndex(idxName,EmailModel.getESMapping())){
      logger.debug("created index")
    }
  }
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
  def deleteIndex(name: String): Boolean = {
    val idx = new DeleteIndexRequest(name);
    val resp = client.indices().delete(idx, RequestOptions.DEFAULT)
    return resp.isAcknowledged()
  }
  def existsIndex(name: String): Boolean = {
    val idx = new GetIndexRequest();
    idx.indices(name)
    client.indices().exists(idx, RequestOptions.DEFAULT)
  }
  def close() = {
    if (bulkCnt > 0) {
      this.writeBULK();
    }
    client.close()
  }

}


