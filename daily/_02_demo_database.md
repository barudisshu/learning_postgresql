Demo Database
=============

示例文件。

安装

```shell script
$ sudo su - postgres
$ wget https://edu.postgrespro.com/demo-small-en.zip
$ zcat demo-small-en.zip | psql
```

然后执行:

```shell script
postgres# \i demo-small-en-20170815.sql
```

> 对于windows 平台，将下载解压的文件`demo-small-en-20170815`放置在psql脚本相同的文件夹。再执行即可。

本章学习从demo例子开始，介绍一个生动的实例内容。

## Description

### General Information

下面是一份航空公司的数据库ER图。

![airport](/images/air_flights_er.png)

### Bookings

### Tickets

### Flight Segments

### Flights

## Installation

### Installation from the Website

## Sample Queries

### A Couple of Words about the Schema

### Simple Queries

### Aggregate Functions

### Window Functions

### Arrays

### Recursive Queries

### Functions and Extensions