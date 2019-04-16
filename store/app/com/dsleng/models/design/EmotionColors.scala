package com.dsleng.models.design

import play.api.libs.json.{Json, _}
import scala.collection.mutable.ListBuffer
import io.swagger.annotations.{ApiModel, ApiModelProperty}

@ApiModel(description = "Emotion Color")
case class EmotionColor(
    @ApiModelProperty(value = "Emotion", required = true, example = "Joy") emotion: String,
    @ApiModelProperty(value = "Color", required = true, example = "rgb(129,176,217)") color: String
    );

object EmotionColor {

  implicit val reader = Json.reads[EmotionColor]
  implicit val writer = Json.writes[EmotionColor]
}

@ApiModel(description = "List of Emotion Colors")
case class EmotionColors(
    @ApiModelProperty(value = "colors", required = true, example = "List of Colors") var colors: Array[EmotionColor]){
    colors = Array(
        new EmotionColor("Joy","rgb(129,176,217)"),
        new EmotionColor("Fear","rgb(254,225,166)"),
        new EmotionColor("Sadness","rgb(248,180,111)"),
        new EmotionColor("Unknown","rgb(171,171,171)"),
        new EmotionColor("Contentment","rgb(166,225,248)"),
        new EmotionColor("Disgust","rgb(241,119,178)"),
        new EmotionColor("Pride","rgb(203,173,208)"),
        new EmotionColor("Anxiety","rgb(178,136,106)"),
        new EmotionColor("Agreeableness","rgb(180,223,192)"),
        new EmotionColor("Relief","rgb(152,219,231)"),
        new EmotionColor("Interest","rgb(200,231,224)")
    );
}
object EmotionColors {

  implicit val reader = Json.reads[EmotionColors]
  implicit val writer = Json.writes[EmotionColors]
}