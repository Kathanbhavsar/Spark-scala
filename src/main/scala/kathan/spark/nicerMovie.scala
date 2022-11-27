package kathan.spark

import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.types.{IntegerType, LongType, StructType}

import scala.io.{Codec, Source}

object nicerMovie {
  case class Movie(userID : Int, movieID : Int, rating : Int, timestamp : Long)

  def loadMovieNames() : Map[Int,String] = {

    implicit val codec: Codec = Codec("ISO-8859-1")

    //val movieNames: Map[Int, String] = Map()
    var movieNames:Map[Int, String] = Map()

    val lines = Source.fromFile("data/ml-100k/u.item")

    for (line <- lines.getLines()) {
      val fields = line.split('|')
      if (fields.length > 1) {
        movieNames += (fields(0).toInt -> fields(1))
      }
    }
    lines.close()

    movieNames
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("MovieNicer")
      .master("local[*]")
      .getOrCreate()

    val nameDict = spark.sparkContext.broadcast(loadMovieNames())

    val movieSchema = new StructType()
      .add("userID", IntegerType)
      .add("movieID", IntegerType)
      .add("rating", IntegerType)
      .add("timestamp", LongType)

    import spark.implicits._

    val movies = spark.read
      .option("sep","\t")
      .schema(movieSchema)
      .csv("data/ml-100k/u.data")
      .as[Movie]

    val movieCounts = movies.groupBy("movieID").count()

    val lookUp : Int => String = (movieId : Int) => nameDict.value(movieId)

    val lookUpUdf = udf(lookUp)

    val MoviewName = movieCounts.withColumn("movieTitle",lookUpUdf(col("movieID")))

    val sortedMovie = MoviewName.sort("count")
    sortedMovie.show(sortedMovie.count.toInt)
  }
}
