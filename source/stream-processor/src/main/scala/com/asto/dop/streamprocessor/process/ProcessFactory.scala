package com.asto.dop.streamprocessor.process

import com.asto.dop.core.CoreModel
import com.asto.dop.streamprocessor.DOPContext
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD


object ProcessFactory extends Serializable with Logging {

  def process(rdd: RDD[String], context: DOPContext): Unit = {
    val messages: RDD[CoreModel] = rdd.map {
      line =>
        log.debug(s"Received a message : $line")
        CoreModel(line)
    }
    ESProcessor.save(messages, context)
    /* HDFSProcessor.save(rdd, context)
     RedisProcessor.save(messages, context)
     HBaseProcessor.saveToBasic(messages, context)*/
  }

}



