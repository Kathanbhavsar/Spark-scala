package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object pracWordCount {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]","WordCount")
    val lines = sc.textFile("data/book.txt")

    val words = lines.flatMap(x => x.split("\\W+"))

    val lowercaseWords = words.map(x => x.toLowerCase())
    val lowerCount = lowercaseWords.map(x => (x, 1)).reduceByKey((x, y) => x + y)

    val wordCounSorted = lowerCount.map(x => (x._2, x._1)).sortByKey()

    for(result <- wordCounSorted){
      val word = result._2
      val count = result._1

      println(s"Word: ${word} and count: ${count}")
    }
  }

}
