package com.dsleng.email

import org.elasticsearch.client._
import org.apache.http.HttpHost
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.DocWriteResponse
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }

class ESFeeder(ip: String) {
  var bulkRequest = new BulkRequest();
  var bulkCnt = 0;
  //val ip = "192.168.1.5";
  var client = new RestHighLevelClient(
    RestClient.builder(
      new HttpHost(ip, 9200, "http"),
      new HttpHost(ip, 9201, "http")))

  def write(indexRequest: IndexRequest) {
    var indexResponse = client.index(indexRequest)
    if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
      println("created document")
    }
  }
  def writeBULK() {
    val bulkResponse = client.bulk(bulkRequest);
    if (bulkResponse.hasFailures()) {
      bulkResponse.forEach(item => {
        val failure = item.getFailure()
        println(failure.getMessage())
      })
    }
    bulkRequest = new BulkRequest();
    bulkCnt = 0
  }
  
   def addDocumentA(model: AEmailExt, index: String = "emaillogs") {
    var builder = XContentFactory.jsonBuilder();

 
    val jsonString = Json.toJson(model).toString()
    println(jsonString)
    var indexRequest = new IndexRequest(index, "_doc")
    indexRequest.source(jsonString, XContentType.JSON);

    bulkRequest.add(indexRequest)
    bulkCnt += 1
    if (bulkCnt == 25) {
      println("writing bulk");
      this.writeBULK()

    }
  }

  def addDocument(model: SimpleEmailExt, index: String = "emaillogs") {
    var builder = XContentFactory.jsonBuilder();

    /*
    //"Fri, 31 Aug 2001 11:22:28 -0700 (PDT)"
    //var td = "Fri, 31 Aug 2001 11:22:28 -0700 (PDT)"
    var format = new SimpleDateFormat("E, d MMM YYYY hh:mm:ss ZZZZ (zzz)", Locale.ENGLISH);
    //var format = new SimpleDateFormat("E, d MMM YYYY", Locale.ENGLISH);
    val dte = model.model.date

    val senderGeo = new GeoPoint(model.model.senderLocation.lat.toDouble,model.model.senderLocation.lon.toDouble);
    val receiverGeo = new GeoPoint(model.model.receiverLocation.lat.toDouble,model.model.receiverLocation.lon.toDouble);


    builder.startObject();
    {
      builder.field("from", model.model.from);
      builder.field("to", model.model.to);
      builder.field("content", model.model.textContent);
      builder.field("date",dte);
      builder.field("subject", model.model.subject);
      builder.field("senderIP", model.model.senderIP);
      builder.field("senderGEOInfo", model.model.senderGeoInfo);

      builder.field("senderloc", senderGeo);
      builder.field("receiverloc", receiverGeo);
      builder.field("emotions", model.emotions);
      builder.field("ec", model.ec);
      builder.field("prime", model.prime);
      builder.field("norm", model.norm);

    }
    builder.endObject();
    println("Index json: %s", builder.string());
    var indexRequest = new IndexRequest(index, "_doc")
      .source(builder);
    */

    val jsonString = Json.toJson(model).toString()
    println(jsonString)
    var indexRequest = new IndexRequest(index, "_doc")
    indexRequest.source(jsonString, XContentType.JSON);

    bulkRequest.add(indexRequest)
    bulkCnt += 1
    if (bulkCnt == 25) {
      println("writing bulk");
      this.writeBULK()

    }
  }
  def close() = {
    if (bulkCnt > 0) {
      this.writeBULK();
    }
    client.close()
  }

}

