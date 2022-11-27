package kathan.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{FloatType, IntegerType, StringType, StructType}
import org.apache.spark.sql.functions._

object custDataset {
  case class Customer(user_id : Int, item_id : Int, price : Float)
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("CustomerDataset")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    val schema = new StructType()
      .add("user_id",IntegerType)
      .add("item_id",IntegerType)
      .add("price",FloatType)


    val customer = spark.read
      .schema(schema)
      .csv("data/customer-orders.csv")
      .as[Customer]

    customer.printSchema()
    val important = customer.select("user_id","price")

    val price = important.groupBy("user_id")
      .agg(round(sum("price"),2)
        .alias("total_spent")).sort("user_id")

    price.show(price.count.toInt)


    //important.show()
  }

}
