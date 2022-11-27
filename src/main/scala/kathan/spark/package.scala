package kathan

import org.apache.spark._
import org.apache.log4j._

package object spark {
  def parseLines(line : String)= {
    val field = line.split(",")
    val age = field(2).toInt
    val numFriends = field(3).toInt

    (age,numFriends)
  }

  def main(args: Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "FriendsByAge")
    val lines = sc.textFile("data/fakefriends-noheader.csv")
    val rdd = lines.map(parseLines)

    val totalsByAge = rdd.mapValues(x => (x, 1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))

    // So now we have tuples of (age, (totalFriends, totalInstances))
    // To compute the average we divide totalFriends / totalInstances for each age.
    val averagesByAge = totalsByAge.mapValues(x => x._1 / x._2)

    // Collect the results from the RDD (This kicks off computing the DAG and actually executes the job)
    val results = averagesByAge.collect()

    // Sort and print the final results.
    results.sorted.foreach(println)
  }
}
