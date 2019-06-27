package com.dsleng.email

import spray.json._

case class Color(name: String, red: Int, green: Int, blue: Int)
case class Color2(name: String, red: Int, green: Int, blue: Int)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Color)
  implicit val colorFormat2 = jsonFormat4(Color2)
}


class Simple {
  import MyJsonProtocol._
  val json = Color("CadetBlue", 95, 158, 160).toJson
  val color = json.convertTo[Color]
}

object Simple extends App {
  val o = new Simple()
}