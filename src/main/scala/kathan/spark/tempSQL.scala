package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructType}
object tempSQL {
  case class Temperature(stationID: String, date: Int, measure_type: String, temperature: Float)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder.appName("MinTemperatures").master("local[*]").getOrCreate()

    val temperatureSchema = new StructType()
      .add("stationID", StringType, nullable = true)
      .add("date", IntegerType, nullable = true)
      .add("measure_type", StringType, nullable = true)
      .add("temperature", FloatType, nullable = true)

    import spark.implicits._

    val temperature = spark.read
      .schema(temperatureSchema)
      .csv("data/1800.csv")
      .as[Temperature]

    temperature.printSchema()

    val minTemps = temperature.filter($"measure_type" === "TMIN")
    val StationTemps = minTemps.select("stationID","temperature")

    val minByStation = StationTemps.groupBy("stationID").min("temperature")

    val minTempsF = minByStation
      .withColumn("temperature",round($"min(temperature)"*0.1f*(9.0f/5.0f)+32.0f,2))
      .select("stationID","temperature").sort("temperature").show()

  }


}
