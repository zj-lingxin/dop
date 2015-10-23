package com.asto.dop.streamprocessor

import com.asto.dop.streamprocessor.process.ProcessFactory
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{Logging, SparkConf}

object Startup extends Logging {

  def main(args: Array[String]): Unit = {
    if (args.length != 13) {
      System.err.println("Usage: " +
        " <Spark master> \r\n" +
        " <Batch duration> \r\n" +
        " <kafka  zookeeper hosts> \r\n" +
        " <kafka group> \r\n" +
        " <kafka topic> \r\n" +
        " <kafka thread number> \r\n" +
        " <HDFS output address> \r\n" +
        " <HBase zookeeper hosts> \r\n" +
        " <HBase Basic table name> \r\n" +
        " <ElasticSearch host> \r\n" +
        " <ElasticSearch index> \r\n" +
        " <Redis host> \r\n" +
        " <Redis db index> \r\n" +
        "example : spark-submit  x.jar local[2] 20 127.0.0.1:2181 gorup1 dop-msg 2 hdfs://127.0.0.1:9000/demo/ 127.0.0.1:2181 dop_basic 127.0.0.1:9200 dop 127.0.0.1:6379 0"
      )
      System.exit(1)
    }

    log.info("Starting Streaming Processor.")

    //Setting parameters
    val context = DOPContext(
      spark_master = args(0),
      spark_streamingBatchDuration = args(1).toInt,
      kafka_zookeeperHosts = args(2),
      kafka_group = args(3),
      kafka_topic = args(4),
      kafka_threadNumbers = args(5).toInt,
      hdfs_outputAddress = args(6),
      hbase_zookeeperHosts = args(7),
      hbase_tableBasic = args(8),
      es_host = args(9),
      es_index = args(10),
      redis_host = args(11),
      redis_db = args(12).toInt
    )
    log.info(
      s"""
         |===============DOP Streaming Processor=============
         |Usage parameters:
         |   Spark master = ${context.spark_master}
         |   Batch duration = ${context.spark_streamingBatchDuration}
         |   Kafka zookeeper hosts = ${context.kafka_zookeeperHosts}
         |   Kafka group = ${context.kafka_group}
         |   Kafka topic = ${context.kafka_topic}
         |   Kafka thread numbers = ${context.kafka_threadNumbers}
         |   HDFS output address = ${context.hdfs_outputAddress}
         |   HBase zookeeper hosts = ${context.hbase_zookeeperHosts}
         |   HBase Basic table name = ${context.hbase_tableBasic}
         |   ElasticSearch host = ${context.es_host}
         |   ElasticSearch index = ${context.es_index}
         |   Redis host = ${context.redis_host}
         |   Redis db index = ${context.redis_db}
         |=============================================
      """.stripMargin)

    val sparkConf = new SparkConf()
      .setMaster(context.spark_master)
      .setAppName("dop-stream-process")
      .set("spark.cleaner.ttl", "120000")
      .set("spark.hbase.host", context.hbase_zookeeperHosts)
      .set("es.index.auto.create", "true")
      .set("es.nodes", context.es_host.split(":")(0))
      .set("es.port", context.es_host.split(":")(1))

    val ssc = new StreamingContext(sparkConf, Seconds(context.spark_streamingBatchDuration))
    ssc.checkpoint(s"${context.hdfs_outputAddress}checkpoint")
    log.info("Connection Kafka Server.")
    val lines = KafkaUtils.createStream(
      ssc,
      context.kafka_zookeeperHosts,
      context.kafka_group,
      Map(context.kafka_topic -> context.kafka_threadNumbers),
      StorageLevel.MEMORY_AND_DISK_SER
    ).map(_._2)

    //val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
    //     ssc, Map[String, String]("metadata.broker.list" -> context.kafka_metadataBrokerList), Set(context.kafka_topic)).map(_._2)
    if (log.isDebugEnabled || log.isTraceEnabled) {
      lines.print(3)
    }
    //Process...
    lines.foreachRDD {
      rdd =>
        ProcessFactory.process(rdd, context)
    }

    ssc.start()
    log.info("Started Streaming Processor.")
    ssc.awaitTermination()

  }

}



