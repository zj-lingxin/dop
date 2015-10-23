package com.asto.dop.streamprocessor.process

import com.asto.dop.streamprocessor.DOPContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{Logging, SparkContext}

object HDFSProcessor extends Serializable {

  def save(rdd: RDD[String], context: DOPContext): Unit = {
    val sqlContext = SQLContextInstance.getInstance(rdd.sparkContext)
    import sqlContext.implicits._


    rdd.toDF().write.mode("append").parquet(s"${context.hdfs_outputAddress}messages.parquet")
  }

}

object SQLContextInstance extends Serializable with Logging {

  @transient private var instance: SQLContext = _

  def getInstance(sparkContext: SparkContext): SQLContext = {
    if (instance == null) {
      log.info("Initialization SQLContext Instance.")
      instance = new SQLContext(sparkContext)
    }
    instance
  }
}