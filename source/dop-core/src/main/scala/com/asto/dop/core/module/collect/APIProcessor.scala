package com.asto.dop.core.module.collect

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

import com.asto.dop.core.Global
import com.asto.dop.core.entity.{UserLogEntity, UserOptEntity}
import com.asto.dop.core.helper.HttpHelper
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * API访问处理器
 *
 * 当收到Binlog事件时发起此流程，
 * 流程会访问事件对应的第三方业务系统接口取数并保存
 */
object APIProcessor extends LazyLogging {

  private val df = new SimpleDateFormat("yyyyMMddHHmmss")

  def process(tableName: String): Unit = {
    if (Global.businessApi_apply._1 == tableName) {
      processApply(UserOptEntity.FLAG_APPLY, Global.businessApi_apply._2)
    }
    if (Global.businessApi_bind._1 == tableName) {
      processBind(UserOptEntity.FLAG_BIND, Global.businessApi_bind._2)
    }
    if (Global.businessApi_selfExaminePass._1 == tableName) {
      processSelfExaminePass(UserOptEntity.FLAG_SELF_EXAMINE_PASS, Global.businessApi_selfExaminePass._2)
    }
    if (Global.businessApi_bankExaminePass._1 == tableName) {
      processBankExaminePass(UserOptEntity.FLAG_BANK_EXAMINE_PASS, Global.businessApi_bankExaminePass._2)
    }
  }

  private def processApply(action: String, api: String): Unit = {
    doProcess(action, api)
  }

  private def processBind(action: String, api: String): Unit = {
    doProcess(action, api)
  }

  private def processSelfExaminePass(action: String, api: String): Unit = {
    doProcess(action, api)
  }

  private def processBankExaminePass(action: String, api: String): Unit = {
    doProcess(action, api)
  }

  private def doProcess(action: String, api: String): Unit = {
    UserLogEntity.db.get(action).onSuccess {
      case lastUpdateTimeResp =>
        val lastUpdateTime = lastUpdateTimeResp.body.last_update_time
        val currentTime = df.format(new Date())
        HttpHelper.get(s"$api?start=$lastUpdateTime&end=$currentTime", classOf[List[Map[String, String]]]).onSuccess {
          case newRecordsResp =>
            //定义一个计数器，初始值为收到的数据集合大小
            val counter = new AtomicInteger(newRecordsResp.body.length)
            newRecordsResp.body.foreach {
              record =>
                val userOpt = new UserOptEntity
                userOpt.id = action + "_" + record("id")
                val time = record("occur_time")
                userOpt.occur_time = time.toLong
                userOpt.occur_date = time.substring(0, 8).toLong
                userOpt.occur_month = time.substring(0, 6).toLong
                userOpt.occur_year = time.substring(0, 4).toLong
                userOpt.user_id = record("user_id")
                userOpt.action = action
                userOpt.platform = record("platform")
                userOpt.amount = if (record.contains("amount")) (record("amount").asInstanceOf[Double] * 1000).toLong else 0
                UserOptEntity.db.save(userOpt).onSuccess {
                  case _ =>
                    //第处理一条记录减1，到为0时表示处理完成，更新时间戳
                    if (counter.decrementAndGet() == 0) {
                      //TODO process update
                      UserLogEntity.db.update("last_update_time = ? ", "action =? ", List(currentTime, action))
                    }
                }
            }
        }
    }
  }

}
