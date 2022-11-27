package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructType}

object superDataset {
  case class SuperHeroesName(id : Int, name : String)
  case class SuperHero(value : String)
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)


    val spark = SparkSession.builder
      .appName("Superhero")
      .master("local[*]")
      .getOrCreate()

    val superHeroSchema = new StructType()
      .add("id",IntegerType,nullable = true)
      .add("name",StringType,nullable = true)

    import spark.implicits._

    val names = spark.read
      .schema(superHeroSchema)
      .option("sep"," ")
      .csv("data/Marvel-names.txt")
      .as[SuperHeroesName]

    val lines = spark.read
      .text("data/Marvel-graph.txt")
      .as[SuperHero]

    val connections = lines
      .withColumn("id",split(col("value")," ")(0))
      .withColumn("connections",size(split(col("value")," "))-1)
      .groupBy("id").agg(sum("connections").alias("connections"))

    // val obscure = connections.filter($"connections" === 1)

    // obscure.show()


    // connections.show()

    val mostPopular =  connections.sort($"connections".desc).first()

    val mostPopularName = names.filter($"id" === mostPopular(0)).select("name").first()

    //val mostObscureName = name

    println(s"${mostPopularName(0)} is the most popular superhero with ${mostPopular(1)} co-appearences")

  }

}
