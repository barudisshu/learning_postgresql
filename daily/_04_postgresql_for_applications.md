PostgreSQL for Applications
=============

postgres包含有用户管理的功能。

## A separate User

创建用户、指派数据库。

```shell script
postgres=# CREATE USER app PASSWORD 'p@ssw0rd';
CREATE ROLE
postgres=# CREATE DATABASE appdb OWNER app;
CREATE DATABASE
```

使用新创建的用户连接到数据库。

```shell script
postgres=# \c appdb app localhost 5432
Password for user app: ***
You are now connected to database "appdb" as user
"app" on host "127.0.0.1" at port "5432".
appdb=>
```

注意这里符号的变化。(#)为超级用户、(>)为普通用户，类似于linux的用户概念。

app用户对自己owner级别的数据库的操作没有限制：

```shell script
appdb=> CREATE TABLE greeting(s text);
CREATE TABLE
appdb=> INSERT INTO greeting VALUES ('Hello, world!');
INSERT 0 1
```

## Remote Connections

默认地，postgres仅限本地连接，要运行远程连接，修改`postgresql.conf`：

```shell script
#listen_addresses = 'localhost'
```

为

```shell script
listen_addresses = '*'
```

另外添加一行以下内容到`ph_hba.conf`：

```shell script
local all all md5
```

这里包含有四个属性：

- host: 表示从哪个network connection连接
- all 表示哪个用户
- all 表示哪个地址
- md5 表示检查算法

例如下面：

```shell script
host appdb app all md5
```

表示如果密码正确的话，允许`app`用户从任何address访问`appdb`数据库

改变配置后，要使配置生效，有两个方式，一种是重启服务，另一种是命令操作：

```shell script
postgres=# SELECT pg_reload_conf();
```

## Pinging the Server

## Backup

最简单的备份方式：

```shell script
pg_dump appdb > appdb.dump
```

如果你打开appdb.dump，它实际上是一些常规操作数据库的command，以及包含一些数据记录。你可以使用下面的操作回复数据：

```shell script
$ createdb appdb2
$ psql -d appdb2 -f appdb.dump
```