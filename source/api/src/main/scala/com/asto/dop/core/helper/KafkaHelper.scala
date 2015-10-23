package com.asto.dop.core.helper

import java.util.Properties

import com.ecfront.common.Resp
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

case class KafkaHelper[K,V](topic:String,metadataBrokerList:String,keySerializer:Class[_]=classOf[StringSerializer],valueSerializer:Class[_]=classOf[StringSerializer])  extends LazyLogging{

  private var producer:KafkaProducer[K, V] = _

  def init(): KafkaHelper[K,V] ={
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataBrokerList)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, keySerializer.getName)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, valueSerializer.getName)
    producer=new KafkaProducer[K, V](props)
    logger.info("Kafka client init successful.")
    this
  }

  def send(message:V,callback:Resp[Void] => Unit):KafkaHelper[K,V] ={
    logger.trace(s"Thread [${Thread.currentThread().getId}] produce log : ${message.toString}")
    producer.send(new ProducerRecord[K, V](topic, message),new Callback {
      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
            if(e==null){
              callback(Resp.success(null))
            }else{
              logger.error("Kafka send error.",e)
              callback(Resp.serverError(e.getMessage))
            }
      }
    })
    this
  }

  sys.addShutdownHook({
    if (producer != null)
      producer.close()
  })

  x match{
    case "some" => //do somethings
    case _ =>
      logger.error("some error")
     throw new Exception("error info")
  }

}
