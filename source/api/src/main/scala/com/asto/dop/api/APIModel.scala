package com.asto.dop.api

case class APIModel(
                      var occur_time:String,
                      var url:String,
                      var referer:String,
                      var user_id:String,
                      var user_ip:String,
                      var cookie_id:String,
                      var cookies:String,
                      var msg_type:String
                    )
