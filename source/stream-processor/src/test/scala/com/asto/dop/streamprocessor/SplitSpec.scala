package com.asto.dop.streamprocessor

import org.scalatest.FunSuite

class SplitSpec extends FunSuite{

  test("Split Test"){
    //=================Producer Side=================
    //Produce a Message
    val msg=Message("20151015080000","http://www.yuanbaopu.com/staticpage/newguide.htm")
    //Convert to String
    val msgStr:String=msg
    // Write msgStr to FS or MQ .e.g. HDFS Kafka ...

    //=================Consumer Side=================
    val cMsgStr=msgStr //Real data from FS or MQ .e.g. HDFS Kafka ...
    //Convert to Message object
    val cMsg:Message=Message(cMsgStr)

    assert(cMsg.time=="20151015080000")
    assert(cMsg.url=="http://www.yuanbaopu.com/staticpage/newguide.htm")
  }

}


case class Message(time: String, url: String) extends Serializable

object Message extends Serializable {
  val SPLIT = "\\t"

  implicit def mkString(obj: Message): String = obj.time + SPLIT + obj.url

  def apply(str: String): Message = {
    val fields = str.split(SPLIT)
    //Error occurred !
    new Message(fields(0), fields(1))
  }

}

