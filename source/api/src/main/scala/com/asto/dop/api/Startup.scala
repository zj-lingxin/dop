package com.asto.dop.api

import com.asto.dop.api.processor.MessageProcessor
import com.typesafe.scalalogging.slf4j.LazyLogging
import io.vertx.core._
import io.vertx.core.http.{HttpServer, HttpServerOptions}
import io.vertx.core.json.JsonObject

class Startup extends AbstractVerticle with LazyLogging{

  override def start(startFuture: Future[Void]): Unit = {
    Startup.vertx=vertx
    Startup.config=config()
    MessageProcessor.init()

    val host=config().getJsonObject("http").getString("host")
    val port=config().getJsonObject("http").getInteger("port")
    vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true).setTcpKeepAlive(true))
      .requestHandler(new APIHttpRequest).listen(port,host,new Handler[AsyncResult[HttpServer]] {
      override def handle(event: AsyncResult[HttpServer]): Unit = {
        if (event.succeeded()) {
          startFuture.complete()
          logger.info(s"API app start successful. http://$host:$port/")
        } else {
          logger.error("Startup fail .", event.cause())
        }
      }
    })
  }

  override def stop(stopFuture: Future[Void]): Unit = {
    logger.info(s"API app stopped , Bye .")
    stopFuture.complete()
  }
}

object Startup{

  var vertx:Vertx=_
  var config: JsonObject=_

}