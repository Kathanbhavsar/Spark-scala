package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{FloatType, IntegerType, LongType, StringType, StructType}
import org.apache.spark.sql.functions._

object movieDataset {

  final case class Movie(movieID : Int)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("MoviePop")
      .master("local[*]")
      .getOrCreate()

    val movieSchema = new StructType()
      .add("userID",IntegerType)
      .add("movieID",IntegerType)
      .add("rating",IntegerType)
      .add("timestamp",LongType)

    import spark.implicits._

    val movies = spark.read
      .option("sep","\t")
      .schema(movieSchema)
      .csv("data/ml-100k/u.data")
      .as[Movie]


    movies.printSchema()

    val popularity = movies.groupBy("movieID").count().orderBy(desc("count"))

    popularity.show(10)

    spark.stop()

  }

}
