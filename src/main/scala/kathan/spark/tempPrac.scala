package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import scala.math

object tempPrac {
  def parsedLines(line : String) = {
    val fields = line.split(",")
    val stationID = fields(0)
    val entryType = fields(2)
    val temperature = fields(3).toFloat * 0.1f * (9.0f / 5.0f) + 32.0f
    (stationID, entryType, temperature)
  }

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]","Temperature")
    val lines = sc.textFile("data/1800.csv")

    val rdd = lines.map(parsedLines)
    val minTemps = rdd.filter(x => x._2 == "TMIN")

    val maxTemps = rdd.filter(x => x._2 == "TMAX")

    // Convert to (stationID, temperature)
    val stationTemps = minTemps.map(x => (x._1, x._3.toFloat))
    val maxStationTemps = maxTemps.map(x => (x._1, x._3.toFloat))

    // Reduce by stationID retaining the minimum temperature found
    val minTempsByStation = stationTemps.reduceByKey( (x,y) => math.min(x,y))

    val maxTempsByStation = maxStationTemps.reduceByKey( (x,y) => math.max(x,y))


    val results = minTempsByStation.collect()

    val results2 = maxTempsByStation.collect()
    for(result <- results.sorted){
      val station = result._1
      val temperature = result._2

      print(s"The station is $station and the temperature is ${temperature} F \n")
    }
    for (result <- results2.sorted) {
      val station = result._1
      val temperature = result._2

      print(s"The station is $station and the temperature is ${temperature} F \n")
    }

  }

}
