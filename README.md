Learning PostgreSQL
==============

[![Travis](https://travis-ci.org/barudisshu/learning_postgresql.svg?branch=master)](https://travis-ci.org/barudisshu/learning_postgresql/) [![codecov](https://codecov.io/gh/barudisshu/learning_postgresql/branch/master/graph/badge.svg)](https://codecov.io/gh/barudisshu/learning_postgresql)

## 实例版本

postgersql 12

## 安装

1. 容器安装

```shell script
docker run --name postgres -e POSTGRES_PASSWORD=postgres -v /data/postgresql/data/:/var/lib/postgresql/data -p 5432:5432 --restart=always -d postgres
```

2. 进入容器

```shell script
docker exec -it postgres /bin/bash
```

3. 切换用户

```shell script
su - postgres
psql
```

4. 创建数据库

```shell script
# create database appdb;
```

5. 创建用户

```shell script
# create user app with password 'p@ssw0rd';
```

6. 授权连接

```shell script
# grant connect on database appdb to app;
```

7. 切换用户

```shell script
su - app
```

8. 授权

```shell script
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app ;
```

