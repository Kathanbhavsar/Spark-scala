package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object pracSql {
  case class Person(id : Int, name : String, age : Int, friends : Int) // We define the schema of our database
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession
      .builder
      .appName("SparkSQL")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val schemaPeople = spark.read
      .option("header","true")
      .option("inferschema","true")
      .csv("data/fakefriends.csv")
      .as[Person]

    schemaPeople.printSchema()

    schemaPeople.createOrReplaceTempView("people")

    val teenagers = spark.sql("select * from people where age between 13 and 19")

    val result = teenagers.collect()

    result.foreach(println)

    spark.stop()
  }

}
