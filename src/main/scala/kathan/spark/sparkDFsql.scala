package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object sparkDFsql {

  case class Person(id : Int, name : String, age : Int, friends : Int)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("Spark")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val people = spark
      .read
      .option("header","true")
      .option("inferSchema","true")
      .csv("data/fakefriends.csv")
      .as[Person]

    println("Here is our inferred Schema")
    people.printSchema()

    println("Selecting the name columns")
    people.select("name").show()

    println("Filtering people over age 21")
    people.filter(people("age") < 21).show()

    println("Grouping by age")
    people.groupBy("age").count().show()

    println("Making everyone 10 years older")
    people.select(people("age"),people("age")+10).show()

    spark.stop()
  }

}
