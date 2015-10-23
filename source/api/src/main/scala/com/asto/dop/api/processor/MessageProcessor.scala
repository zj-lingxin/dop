package com.asto.dop.api.processor

import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

import com.asto.dop.api.{APIModel, Startup}
import com.asto.dop.core.CoreModel
import com.asto.dop.core.helper.KafkaHelper
import com.ecfront.common.Resp
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.{AsyncResult, Future, Handler}

object MessageProcessor extends LazyLogging {

  private var kafkaHelper: KafkaHelper[String, String] = _
  private val df = new SimpleDateFormat("yyyyMMddHHmmss")

  def process(apiMessage: APIModel, callback: => Resp[String] => Unit): Unit = {
    basePackageMessage(apiMessage, {
      msgResult =>
        if (msgResult) {
          val msgStr:String=msgResult.body
          kafkaHelper.send(msgStr, {
            kafkaResult =>
              callback(kafkaResult)
          })
        } else {
          callback(msgResult)
        }
    })
  }

  def init(): Unit = {
    val topic = Startup.config.getJsonObject("producer").getString("topic")
    val metadataBrokerList = Startup.config.getJsonObject("producer").getString("metadata.broker.list")
    Startup.vertx.executeBlocking(new Handler[Future[Void]] {
      override def handle(event: Future[Void]): Unit = {
        kafkaHelper = KafkaHelper[String,String](topic,metadataBrokerList).init()
        event.complete()
      }
    }, new Handler[AsyncResult[Void]] {
      override def handle(event: AsyncResult[Void]): Unit = {
      }
    })

  }

  private def basePackageMessage(apiMessage: APIModel, callback: => Resp[CoreModel] => Unit): Unit = {
    val url = if (apiMessage.url != null && apiMessage.url.trim.nonEmpty) {
      apiMessage.url.trim
    } else {
      callback(Resp.badRequest("缺少参数：【url】"))
      return
    }
    val msg_type = if (apiMessage.msg_type != null && apiMessage.msg_type.trim.nonEmpty) {
      apiMessage.msg_type.trim
    } else {
      callback(Resp.badRequest("缺少参数：【msg_type】"))
      return
    }
    val urlObj = new URL(url)
    val path = urlObj.getPath
    val query = urlObj.getQuery
    val occur_time = if (apiMessage.occur_time != null && apiMessage.occur_time.trim.nonEmpty) {
      apiMessage.occur_time.trim
    } else {
      df.format(new Date)
    }
    val referer = if (apiMessage.referer != null && apiMessage.referer.trim.nonEmpty) {
      apiMessage.referer.trim
    } else {
      ""
    }
    val user_id = if (apiMessage.user_id != null && apiMessage.user_id.trim.nonEmpty) {
      apiMessage.user_id.trim
    } else {
      ""
    }
    val user_ip = if (apiMessage.user_ip != null && apiMessage.user_ip.trim.nonEmpty) {
      apiMessage.user_ip.trim
    } else {
      ""
    }
    val cookie_id = if (apiMessage.cookie_id != null && apiMessage.cookie_id.trim.nonEmpty) {
      apiMessage.cookie_id.trim
    } else {
      ""
    }
    val cookies = if (apiMessage.cookies != null && apiMessage.cookies.trim.nonEmpty) {
      apiMessage.cookies.trim
    } else {
      ""
    }
    val message = CoreModel(occur_time, url, referer, path, query, user_id, user_ip, cookie_id, cookies, msg_type)
    callback(Resp.success(message))
  }
}
