package com.asto.dop.streamprocessor.process

import com.asto.dop.core.CoreModel
import com.asto.dop.streamprocessor.DOPContext
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark._

object ESProcessor extends Serializable {

  def save(messages: RDD[CoreModel], context: DOPContext): Unit = {
    messages.saveToEs(s"${context.es_index}/core")
  }

}