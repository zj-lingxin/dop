API接口应用
===

## How to Use

1. mvn package
1. java -jar api-xx-fat.jar \
     -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory 
     <-instances number> \
     <-ha> \
     <-hagroup group name> \
     <-conf file> \
     <-cp other classpath>\
     
## Example

Start APPs (可启动多个JVM实例实现HA）

    java -jar api-1.0-SNAPSHOT-fat.jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -ha -hagroup m_api -conf C:\config.json
    java -jar api-1.0-SNAPSHOT-fat.jar -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -ha -hagroup m_api -conf C:\config.json
    ...

Post a Data

    Post http://127.0.0.1:8080/api/
    
    {
    "msg_type":1,
    "url":"http://www.yuanbaopu.com/index.htm?a=aa",
    "referer":"http://so.baidu.com",
    "cookie_id":"sfwodmfdsiksdrft!="
    }


