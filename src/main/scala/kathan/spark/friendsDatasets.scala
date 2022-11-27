package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
object friendsDatasets {
  case class FakeFriends(id : Int, name : String, age : Int, friends : Int)
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder
      .appName("FriendsByAge")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val friends = spark
      .read
      .option("header","true")
      .option("inferSchema","true")
      .csv("data/fakefriends.csv")
      .as[FakeFriends]

    friends.printSchema()

    val friendsByAge = friends.select("age","friends")
    friendsByAge.groupBy("age").avg("friends").sort("age").show()

    friendsByAge.groupBy("age")
      .agg(round(avg("age"),2).alias("Friends Average"))
      .sort("age")
      .show()


  }

}
