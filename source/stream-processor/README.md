流式数据处理应用
===
支持从Kafka获取数据，处理后保存到Redis、HBase、HDFS

## How to use

1. Start Zookeeper Hadoop Spark Redis Kafka      
1. Create HBase tables
>hbase/bin/hbase shell
>create 'dop_basic', 'basic'
1. (Optional) Setting a special log4j.properties
1. Package `mvn assembly:assembly`
1. Submit Jar
>./spark-submit x.jar <Spark master>
>   Batch duration
>   kafka  zookeeper hosts
>   kafka group
>   kafka topic
>   kafka thread number
>   HDFS output address
>   HBase zookeeper hosts
>   HBase Basic table name
>   ElasticSearch host
>   ElasticSearch index
>   Redis host
>   Redis db index
     
      
## Example

     ./spark-submit \
     --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/opt/workspaces/apps/stream-procoessor/log4j-executor.properties" \
    /opt/workspaces/apps/stream-procoessor/stream-processor-0.2.0-jar-with-dependencies.jar \
    spark://dopdemo:7077 \
    20 \
    192.168.4.99:2181 \
    default \
    dop-msg \
    2 \
    hdfs://192.168.4.99:9000/demo/ \
    192.168.4.99:2181 \
    dop_basic \
    192.168.4.99:9200 \
    dop \
    192.168.4.99:6379 \
    0

## 本地调试
           
    <properties>
            <mainClass>com.asto.dop.streamprocessor.Startup</mainClass>
            <!--<spark.scope>compile</spark.scope>--> //本地调试时打开此属性
    </properties>
           
## TODO

1. 每次DStream操作都会创建一个HDFS文件，会产生很多小文件，影响性能
1. 使用createDirectStream接口以提升性能（目前没有使用是因为此接口无法保存offset导致无法获取streaming启动前生产的消息，此逻辑需要自行实现）

## Others

### 使用Spark SQL查询
    val df=sqlContext.read.parquet("hdfs://dopdemo:9000/demo/messages.parquet").toDF()
    df.registerTempTable("messages")
    sqlContext.sql("select * from messages").take(10).foreach(println)


    