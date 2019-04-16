package com.dsleng.models.design

import play.api.libs.json.{Json, _}
import scala.collection.mutable.ListBuffer
import io.swagger.annotations.{ApiModel, ApiModelProperty}


@ApiModel(description = "Key Values")
case class KV(
    @ApiModelProperty(value = "name", required = true, example = "Contentment") name: String,
    @ApiModelProperty(value = "value", required = true, example = "12") value: String
);

@ApiModel(description = "Entropy Input")
case class EntropyInput (
    @ApiModelProperty(value = "Array of Emotions", required = true, example = "[{\"Agreeableness\":0,\"Anger\":0,\"Anxiety\":0,\"Contentment\":0.5,\"Disgust\":0,\"Fear\":0,\"Interest\":0,\"Joy\":0.5,\"Pride\":0,\"Relief\":0,\"Sadness\":0,\"Shame\":0,\"Word-Agreeableness\":\"\",\"Word-Anger\":\"\",\"Word-Anxiety\":\"\",\"Word-Contentment\":\"\",\"Word-Disgust\":\"\",\"Word-Fear\":\"\",\"Word-Interest\":\"\",\"Word-Joy\":\"\",\"Word-Pride\":\"\",\"Word-Relief\":\"\",\"Word-Sadness\":\"\",\"Word-Shame\":\"\",\"date\":\"2001-12-31\",\"time\":\"2001-12-31 12:12:00\",\"neg\":0,\"sender\":\"s..shively@enron.com\"},{\"Agreeableness\":0,\"Anger\":0,\"Anxiety\":0,\"Contentment\":0,\"Disgust\":0,\"Fear\":0,\"Interest\":0,\"Joy\":0,\"Pride\":0,\"Relief\":0,\"Sadness\":0,\"Shame\":0,\"Word-Agreeableness\":\"\",\"Word-Anger\":\"\",\"Word-Anxiety\":\"\",\"Word-Contentment\":\"\",\"Word-Disgust\":\"\",\"Word-Fear\":\"\",\"Word-Interest\":\"\",\"Word-Joy\":\"\",\"Word-Pride\":\"\",\"Word-Relief\":\"\",\"Word-Sadness\":\"\",\"Word-Shame\":\"\",\"date\":\"2001-12-31\",\"time\":\"2001-12-31 12:12:00\",\"neg\":0,\"sender\":\"s..shively@enron.com\"},{\"Agreeableness\":0,\"Anger\":0,\"Anxiety\":0,\"Contentment\":0,\"Disgust\":0,\"Fear\":0,\"Interest\":0,\"Joy\":0,\"Pride\":0,\"Relief\":0,\"Sadness\":0,\"Shame\":0,\"Word-Agreeableness\":\"\",\"Word-Anger\":\"\",\"Word-Anxiety\":\"\",\"Word-Contentment\":\"\",\"Word-Disgust\":\"\",\"Word-Fear\":\"\",\"Word-Interest\":\"\",\"Word-Joy\":\"\",\"Word-Pride\":\"\",\"Word-Relief\":\"\",\"Word-Sadness\":\"\",\"Word-Shame\":\"\",\"date\":\"2001-01-02\",\"time\":\"2001-01-02 15:01:00\",\"neg\":0,\"sender\":\"s..shively@enron.com\"},{\"Agreeableness\":0,\"Anger\":0,\"Anxiety\":0,\"Contentment\":0,\"Disgust\":0,\"Fear\":0,\"Interest\":0,\"Joy\":0,\"Pride\":0,\"Relief\":0,\"Sadness\":0,\"Shame\":0,\"Word-Agreeableness\":\"\",\"Word-Anger\":\"\",\"Word-Anxiety\":\"\",\"Word-Contentment\":\"\",\"Word-Disgust\":\"\",\"Word-Fear\":\"\",\"Word-Interest\":\"\",\"Word-Joy\":\"\",\"Word-Pride\":\"\",\"Word-Relief\":\"\",\"Word-Sadness\":\"\",\"Word-Shame\":\"\",\"date\":\"2001-01-02\",\"time\":\"2001-01-02 15:01:00\",\"neg\":0,\"sender\":\"s..shively@enron.com\"}]") text: String
);

@ApiModel(description = "Emotion Input")
case class EmoInput(
    @ApiModelProperty(value = "text", required = true, example = "I had a bad day today, cause the team just seem to not understand the culture of the company. I am also happy that this happened at the beginning of the project, or I might be in for a huge nightmare. All will be good, if we can remove some people, what do you think.") text: String
);

object EmoInput {
  implicit val reader = Json.reads[EmoInput]
  implicit val writer = Json.writes[EmoInput]
}

@ApiModel(description = "Deception Input")
case class DecInput(
    @ApiModelProperty(value = "text", required = true, example = "I think we can work things out, I was not there") text: String
);


@ApiModel(description = "Emotion Response")
case class EmoResponse(
    @ApiModelProperty(value = "Emotion Count", required = true, example = "{\\\"Fear\\\": 1, \\\"Sadness\\\": 1, \\\"Joy\\\": 2}") ec: String,
    @ApiModelProperty(value = "Normalized Emotions", required = true, example = "{\\\"Fear\\\": 0.25, \\\"Sadness\\\": 0.25, \\\"Joy\\\": 0.5}") norm: String,
    @ApiModelProperty(value = "Prime Emotion", required = true, example = "\"\\\"Joy\\\"\"") prime: String
);

@ApiModel(description = "Deception Response")
case class DecResponse(
    @ApiModelProperty(value = "Deception Result", required = true, example = "\"truth\"") results: String,
    @ApiModelProperty(value = "Deception Score", required = true, example = "7") score: String
   
);

@ApiModel(description = "Entropy Response")
case class EntropyResponse(
    @ApiModelProperty(value = "Entropy Index", required = true, example = "[0.32188758248682003]") entropy: String,
    @ApiModelProperty(value = "Steady Values", required = true, example = "{\"Agreeableness\":{\"sender\":0.0},\"Anger\":{\"sender\":0.11},\"Anxiety\":{\"sender\":0.0},\"Contentment\":{\"sender\":0.468},\"Disgust\":{\"sender\":0.0},\"Fear\":{\"sender\":0.0},\"Interest\":{\"sender\":0.0},\"Joy\":{\"sender\":0.203},\"NA\":{\"sender\":0.11},\"Pride\":{\"sender\":0.11},\"Relief\":{\"sender\":0.0},\"Sadness\":{\"sender\":0.0},\"Shame\":{\"sender\":0.0},\"neg\":{\"sender\":0.0}}") steady: String
   
);

