package com.asto.dop.core.module.query

import java.util.Date

import com.asto.dop.core.entity.VisitEntity
import com.asto.dop.core.helper.DBHelper
import com.ecfront.common.Resp

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

/**
 * 流量分析（以下所有的数据都为最近7天的数据）
 * 访客-自审通过率：访客，某段时间内有访问记录，不关心是不是新老访客，自审通过率，某段时间内申请并通过的记录
 */
object TrafficAnalysisProcessor extends QueryProcessor {

  override protected def process(req: Map[String, String], p: Promise[Resp[Any]]): Unit = {
    val today = df.format(new Date()).substring(0, 8).toLong
    val last7Day = today - 7
    val sourceCountResp = DBHelper.count(s"SELECT v_source,COUNT(DISTINCT visitor_id) FROM ${VisitEntity.db.TABLE_NAME} WHERE occur_date >= ? AND c_platform = ? GROUP BY v_source ", List(last7Day, VisitEntity.FLAG_PLATFORM_PC))

    for {
      _ <- sourceCountResp
    } yield {
      p.success(Resp.success(
        s"""
           |{}
        """.stripMargin))
    }
  }

}
