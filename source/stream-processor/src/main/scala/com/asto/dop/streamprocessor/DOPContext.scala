package com.asto.dop.streamprocessor


case class DOPContext(
                       spark_master:String,
                       spark_streamingBatchDuration:Int,
                       kafka_zookeeperHosts:String,
                       kafka_group:String,
                       kafka_topic:String,
                       kafka_threadNumbers:Int,
                       hdfs_outputAddress:String,
                       hbase_zookeeperHosts:String,
                       hbase_tableBasic:String,
                       es_host:String,
                       es_index:String,
                       redis_host:String,
                       redis_db:Int
                     ) extends Serializable
