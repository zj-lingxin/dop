package com.asto.dop.core

case class CoreModel(
                      occur_time:String,
                      url:String,
                      path:String,
                      query:String,
                      referer:String,
                      user_id:String,
                      user_ip:String,
                      cookie_id:String,
                      cookies:String,
                      msg_type:String
                    ) extends Serializable{
}

object CoreModel extends Serializable{
  val SPLIT="\t"

  implicit  def mkString(obj:CoreModel):String={
    obj.occur_time+
      SPLIT+obj.url+
      SPLIT+obj.path+
      SPLIT+obj.query+
      SPLIT+obj.referer+
      SPLIT+obj.user_id+
      SPLIT+obj.user_ip+
      SPLIT+obj.cookie_id+
      SPLIT+obj.cookies+
      SPLIT+obj.msg_type
  }
  
  def apply(str:String): CoreModel ={
    val fields=str.split(SPLIT)
    if(fields.length==10){
      new CoreModel(fields(0),fields(1),fields(2),fields(3),fields(4),fields(5),fields(6),fields(7),fields(8),fields(9))
    }else{
      null
    }
    
  }
  
}
