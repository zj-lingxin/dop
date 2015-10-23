MySQL BinLog Monitor （Test）
===
This example uses the `https://github.com/shyiko/mysql-binlog-connector-java`  package .

# How To Use

## Prepare

1. Create a MySQL ENV . The fastest way is to use a docker , like this : 
> docker run -d  -p 3306:3306 \
>   --name mysql \
>   -v /opt/mysql-binlog:/var/lib/mysql \
>   -e MYSQL_DATABASE=test \
>   -e MYSQL_ROOT_PASSWORD=123456 \
>   -e MYSQL_USER=mysql \
>   -e MYSQL_PASSWORD=123456 \
>   mysql:5.7 \
>   mysqld \
>   --datadir=/var/lib/mysql \
>   --user=mysql \
>   --server-id=1 \
>   --log-bin=/var/log/mysql/mysql-bin.log \
>   --binlog_do_db=test

1. Connection MySQL .
1. Create a DB , name is `test`
1. Create a Table in `test`  DB , like this : 
> CREATE TABLE IF NOT EXISTS test
> (
>  id varchar(20) ,
>  occur_time varchar(14) ,
>  c_platform varchar(10) ,
>  c_system varchar(10) ,
>  PRIMARY KEY(id)
>  )ENGINE=innodb DEFAULT CHARSET=utf8
1. Modify some parameter in `BinLogSpec.scala`:
>  private val (host, port, userName, userPwd, filterTable) = ("192.168.99.100", 3306, "root", "123456", "test")
1. Start This Demo code in ScalaTest.

## Testing
Execute some SQL : 
> INSERT INTO test ( id ,  occur_time , c_platform , c_system ) VALUES( '1','20151018','pc','win10');
> UPDATE test SET c_platform = 'mobile' WHERE id = '1' ;
> DELETE FROM test WHERE id = '1' ;
    
 Watch the console output like this :
> 14:15:49.140 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures table map.TableMapEventData{tableId=109, database='test', table='test', columnTypes=15, 15, 15, 15, columnMetadata=60, 42, 30, 30, columnNullability={1, 2, 3}}
> 14:15:49.146 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures the specified change.WriteRowsEventData{tableId=109, includedColumns={0, 1, 2, 3}, rows=[
>     [1, 20151018, pc, win10]
> ]}
> 14:15:49.159 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures table map.TableMapEventData{tableId=109, database='test', table='test', columnTypes=15, 15, 15, 15, columnMetadata=60, 42, 30, 30, columnNullability={1, 2, 3}}
> 14:15:49.160 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures the specified change.UpdateRowsEventData{tableId=109, includedColumnsBeforeUpdate={0, 1, 2, 3}, includedColumns={0, 1, 2, 3}, rows=[
>     {before=[1, 20151018, pc, win10], after=[1, 20151018, mobile, win10]}
> ]}
> 14:15:49.181 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures table map.TableMapEventData{tableId=109, database='test', table='test', columnTypes=15, 15, 15, 15, columnMetadata=60, 42, 30, 30, columnNullability={1, 2, 3}}
> 14:15:49.182 [ScalaTest-run-running-BinLogSpec] DEBUG c.asto.dop.monitor.msyql.BinLogSpec - Captures the specified change.DeleteRowsEventData{tableId=109, includedColumns={0, 1, 2, 3}, rows=[
>     [1, 20151018, mobile, win10]
> ]}
     
# Enjoy !
 
