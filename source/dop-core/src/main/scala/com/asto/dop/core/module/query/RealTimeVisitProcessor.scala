package com.asto.dop.core.module.query

import com.asto.dop.core.entity.{VisitEntity, UserOptEntity}
import com.asto.dop.core.helper.DBHelper
import com.ecfront.common.{JsonHelper, Resp}

import scala.concurrent.Promise

/**
 * 实时访客
 */
object RealTimeVisitProcessor extends QueryProcessor {

  override protected def process(req: Map[String, String], p: Promise[Resp[Any]]): Unit = {
    if (!req.contains("source") || !req.contains("platform") || !req.contains("pageNumber")) {
      p.success(Resp.badRequest("【source】【platform】【pageNumber】不能为空"))
    } else {
      val source = req("source")
      val platform = req("platform")
      val pageNumber = req("pageNumber").toLong
      val pageSize = req.getOrElse("pageSize", "15").toInt
      val result = VisitEntity.db.page("v_source =?  AND c_platform =? ", List(source, platform), pageNumber, pageSize)
      p.success(Resp.success(JsonHelper.toJsonString(result)))
    }
  }

}
