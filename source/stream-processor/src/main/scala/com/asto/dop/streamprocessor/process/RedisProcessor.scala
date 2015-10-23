package com.asto.dop.streamprocessor.process

import com.asto.dop.core.CoreModel
import com.asto.dop.streamprocessor.DOPContext
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import redis.clients.jedis.JedisPool

object RedisProcessor extends Serializable {

  def save(messages: RDD[CoreModel], context:DOPContext): Unit = {
    messages.foreachPartition {
      pRecords =>
        val jedis = RedisClient.getPool(context.redis_host).getResource
        pRecords.foreach {
          message =>
            jedis.select(context.redis_db)
            jedis.hset("test", message.occur_time, "1")
        }
        jedis.close()
    }
  }

}

object RedisClient extends Serializable with Logging {

  @transient private var instance: JedisPool = _

  def getPool(host: String): JedisPool = {
    if (instance == null) {
      log.info(s"Initialization Redis pool at $host")
      instance = new JedisPool(new GenericObjectPoolConfig(), host.split(":")(0), host.split(":")(1).toInt, 30000)
    }
    instance
  }

}
