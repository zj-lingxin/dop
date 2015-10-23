package com.asto.dop.api

import com.asto.dop.api.processor.MessageProcessor
import com.ecfront.common.JsonHelper
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.{HttpServerRequest, HttpServerResponse}

class APIHttpRequest extends Handler[HttpServerRequest] with LazyLogging{
  override def handle(request: HttpServerRequest): Unit = {
    if (request.method().name() == "OPTIONS") {
      APIHttpRequest.returnContent("", request.response(), "text/html")
    } else  if (request.path() == "/api/" && request.method().name() == "POST") {
      logger.trace(s"Receive a request , from ${request.remoteAddress().host()} ")
      request.bodyHandler(new Handler[Buffer] {
        override def handle(data: Buffer): Unit = {
          val apiMessage=JsonHelper.toObject(data.getString(0, data.length),classOf[APIModel])
          apiMessage.user_ip=request.remoteAddress().host()
          MessageProcessor.process(apiMessage,{
            result =>
              APIHttpRequest.returnContent(result,request.response())
          })
        }
      })
      }
  }
}

object APIHttpRequest{

  private def returnContent(result: Any, response: HttpServerResponse, contentType: String="application/json; charset=UTF-8") {
    //支持CORS
    response.setStatusCode(200).putHeader("Content-Type", contentType)
      .putHeader("Cache-Control", "no-cache")
      .putHeader("Access-Control-Allow-Origin", "*")
      .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
      .putHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, X-authentication, X-client")
      .end(JsonHelper.toJsonString(result))
  }
}
