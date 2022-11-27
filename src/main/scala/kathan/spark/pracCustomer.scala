package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object pracCustomer {
  def parseLines(line : String)  ={
    val fields = line.split(",")
    (fields(0).toInt,fields(2).toFloat)
  }
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]","CustomerAmount")
    val lines = sc.textFile("data/customer-orders.csv")
    val rdd = lines.map(parseLines)

    val totalByCustomer = rdd.reduceByKey((x,y) => x+y)
    val sorted = totalByCustomer.map(x => (x._2,x._1)).sortByKey()

    val results = sorted.collect()

    results.foreach(println)
  }

}
