package com.dsleng.store.db

import java.util.Date
import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import com.sfxcode.nosql.mongo._

case class Address(street: String, building: String,
  zipcode: String, coord: List[Double])

case class Grade(date: Date, grade: String, score: Int)

case class Restaurant(restaurant_id: String, name: String,
  borough: String, cuisine: String,
  grades: List[Grade], address: Address,
  _id: ObjectId = new ObjectId())

trait RestuarantHelper {
  private val registry = fromProviders(
  classOf[Restaurant],
  classOf[Address], classOf[Grade])

  // add MongoClient if you need to authenticate client
  val database = DatabaseProvider("test", registry)
  
  object RestaurantDAO extends MongoDAO[Restaurant](database, "restaurants")
  
  
  def findRestaurantByName(name: String): Option[Restaurant] =
    RestaurantDAO.find("name", name)

  def restaurantsSize: Long = RestaurantDAO.count()

  /**
   * result with implicit conversion to List of Entities
   */
  def findAllRestaurants(filterValues: Map[String, Any] = Map()): List[Restaurant] =
    RestaurantDAO.find(filterValues)

}
class TweetStore {
  
}

object TweetStore extends App with RestuarantHelper {

  // find specific restaurant by name as Option Result
  val restaurant = findRestaurantByName("Dj Reynolds Pub And Restaurant")

  println(restaurant)

  // use count function
  println(restaurantsSize)

  // find restaurants by filter
  private val filter = Map("address.zipcode" -> "10075", "cuisine" -> "Italian")
  val restaurants = findAllRestaurants(filter)

  restaurants.sortBy(r => r.name).foreach(r => println(r.name))

}
