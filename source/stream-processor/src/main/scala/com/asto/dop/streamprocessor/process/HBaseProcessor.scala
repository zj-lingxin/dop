package com.asto.dop.streamprocessor.process

import com.asto.dop.core.CoreModel
import com.asto.dop.streamprocessor.DOPContext
import org.apache.spark.rdd.RDD

object HBaseProcessor extends Serializable {

  import it.nerdammer.spark.hbase._

  //Save Basic Table
  def saveToBasic(messages: RDD[CoreModel],context:DOPContext): Unit = {
    messages.map {
      message =>
        (getRowKey(message), message.msg_type, message.occur_time, message.url, message.path, message.query, message.referer, message.user_id, message.user_ip, message.cookie_id, message.cookies)
    }.toHBaseTable(context.hbase_tableBasic)
      .inColumnFamily("basic")
      .toColumns("msg_type", "occur_time", "url", "path", "query", "referer", "user_id", "user_ip", "cookie_id", "cookies")
      .save()

    /**
     * 基础表Row Key策略
     * MsgType(1位)+IPV4(12位)+occur_time(14位)+随机数(时间戳10位)
     * @param message Message
     * @return Row Key
     */
    def getRowKey(message: CoreModel): String = {
      message.msg_type + message.user_ip + message.occur_time + (System.nanoTime() + "").substring(4)
    }
  }

}