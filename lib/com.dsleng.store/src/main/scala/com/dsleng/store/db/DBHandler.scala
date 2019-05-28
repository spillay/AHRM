package com.dsleng.store.db

import org.mongodb.scala._
import org.mongodb.scala.bson.{BsonObjectId,BsonString}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class MyObj(id: BsonObjectId, name: String)

class DBHandler(dbname: String) {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val db: MongoDatabase = mongoClient.getDatabase(dbname)
  val collection: MongoCollection[Document] = db.getCollection[Document]("mycollection")

  protected def reader(doc: Document): MyObj =
    (for {
      id <- doc.get[BsonObjectId]("_id")
      name <- doc.get[BsonString]("name")
    } yield MyObj(id, name.toString)).get

  protected def writer(o: MyObj): Document = Document("_id" -> o.id, "name" -> o.name)

  def insert(obj: MyObj): Future[Completed] = collection
    .insertOne(writer(obj))
    .toFuture()
    .map(_ => Completed())

  def findOne(query: Document): Future[Option[MyObj]] = collection
    .find(query)
    .first()
    .head()
    .map(doc => Some(reader(doc)))
    .recover {
      case _ => None
    }

  def findById(id: BsonObjectId): Future[Option[MyObj]] = findOne(Document("_id" -> id))
  
}

object DBHandler extends App {
  println("MongoClient")    
  val o = new DBHandler("tweety")
}