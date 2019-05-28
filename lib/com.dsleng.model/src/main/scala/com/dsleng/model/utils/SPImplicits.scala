package com.dsleng.model.utils

import java.sql.Timestamp
import play.api.libs.json.{ JsNull, Json, JsString, JsValue, Writes }
import java.sql.Timestamp
import java.text.SimpleDateFormat
import play.api.libs.json._

object SPImplicits {
  implicit object timestampFormat extends Format[Timestamp] {
    //val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    // date formats for es "yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z"
    val format = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z")
    val output = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(output.format(ts))
  }
}
