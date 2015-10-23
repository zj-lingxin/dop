package com.asto.dop.core.business.collect

import java.util.concurrent.CountDownLatch

import com.asto.dop.core.Global
import com.asto.dop.core.business.BusinessBasicSpec
import com.asto.dop.core.entity.{UserOptEntity, VisitEntity}
import com.asto.dop.core.helper.DBHelper
import com.asto.dop.core.module.collect._
import io.vertx.core.json.JsonObject

import scala.concurrent.ExecutionContext.Implicits.global

class MockVisitSpec extends BusinessBasicSpec {

  test("Mock Visit Test") {
    val cdl = new CountDownLatch(2)

    //此场景的结果应该是1个uv,1个新访客
    for {
    //新访客A
      v1F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111111", c_platform = "pc", c_system = "windows", u_user_id = "", u_cookie_id = "A", u_cookies = "",
        v_source = "baidu", v_referer = "http://www.baidu.com/q=xx", v_url = "http://www.yuanbaopu.com/index.htm", v_action = ""
      ), "115.236.188.99")
      //新访客A访问了另一个页面
      v2F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111112", c_platform = "pc", c_system = "windows", u_user_id = "", u_cookie_id = "A", u_cookies = "",
        v_source = "", v_referer = "http://www.yuanbaopu.com/index.htm", v_url = "http://www.yuanbaopu.com/about.htm", v_action = ""
      ), "115.236.188.99")
      //新访客A访问了另一个页面
      v3F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111113", c_platform = "pc", c_system = "windows", u_user_id = "zhangsan", u_cookie_id = "A", u_cookies = "",
        v_source = "baidu", v_referer = "http://www.yuanbaopu.com/about.htm", v_url = "http://www.yuanbaopu.com/reg.htm", v_action = Global.visitRegisterAction
      ), "115.236.188.99")
      //新访客A注册了一个账号zhangsan并登录
      v4F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111114", c_platform = "pc", c_system = "windows", u_user_id = "zhangsan", u_cookie_id = "A", u_cookies = "",
        v_source = "", v_referer = "http://www.yuanbaopu.com/reg.htm", v_url = "http://www.yuanbaopu.com/login_sucess.htm", v_action = Global.visitRecomputeAction
      ), "115.236.188.99")
      //用户zhangsan访问了另一个页面
      v5F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111115", c_platform = "pc", c_system = "windows", u_user_id = "zhangsan", u_cookie_id = "A", u_cookies = "",
        v_source = "", v_referer = "http://www.yuanbaopu.com/login_sucess.htm", v_url = "http://www.yuanbaopu.com/change_pwd.htm", v_action = ""
      ), "115.236.188.99")
      //用户zhangsan换了一台电脑操作
      v6F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111116", c_platform = "pc", c_system = "mac", u_user_id = "", u_cookie_id = "B", u_cookies = "",
        v_source = "", v_referer = "", v_url = "http://www.yuanbaopu.com/index.htm", v_action = ""
      ), "115.236.188.98")
      v7F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111117", c_platform = "pc", c_system = "mac", u_user_id = "", u_cookie_id = "B", u_cookies = "",
        v_source = "", v_referer = "", v_url = "http://www.yuanbaopu.com/login.htm", v_action = ""
      ), "115.236.188.98")
      //用户zhangsan做了次登录
      v8F <- BrowserVisitProcessor.process(BrowserVisitReq(
        request_id = "111118", c_platform = "pc", c_system = "mac", u_user_id = "zhangsan", u_cookie_id = "B", u_cookies = "",
        v_source = "", v_referer = "", v_url = "http://www.yuanbaopu.com/login_sucess.htm", v_action = Global.visitRecomputeAction
      ), "115.236.188.98")
      //用户zhangsan换成了手机操作
      v9F <- AppVisitProcessor.process(AppVisitReq(
        request_id = "111119", c_system = "iphone", c_device_id = "d1", c_ipv4 = "115.236.188.89", c_gps = "",
        u_user_id = "", v_source = "91助手", v_url_path = "/user/login/", v_action = ""))
      //用户zhangsan在手机APP中登录
      v10F <- AppVisitProcessor.process(AppVisitReq(
        request_id = "111120", c_system = "iphone", c_device_id = "d1", c_ipv4 = "115.236.188.89", c_gps = "",
        u_user_id = "zhangsan", v_source = "91助手", v_url_path = "/index/", v_action = Global.visitRecomputeAction))
    } yield {
      VisitEntity.db.count("v_new_visitor = ? ", List(true)).onSuccess {
        case nvResp =>
          assert(nvResp.body == 1)
          DBHelper.get(s"SELECT COUNT(DISTINCT visitor_id) AS visitor_id FROM ${VisitEntity.db.TABLE_NAME}", List(), classOf[JsonObject]).onSuccess {
            case viResp =>
              assert(viResp.body.getLong("visitor_id") == 1)
              cdl.countDown()
          }
          UserOptEntity.db.get(UserOptEntity.FLAG_REGISTER+"_zhangsan").onSuccess {
            case optResp =>
              assert(optResp&&optResp.body.source=="baidu")
              cdl.countDown()
          }
      }
    }

    cdl.await()
  }

}

