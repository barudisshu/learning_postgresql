pgAdmin
=============

pgAdmin是postgres官方的GUI管理工具。

## 查询显示gis地理位置信息

```shell script
postgres=# CREATE DATABASE mountains;
CREATE DATABASE

postgres=# \c mountains
You are now connected to database "mountains" as user "postgres".
postgres=# CREATE EXTENSION postgis;
CREATE EXTENSION
postgres=# CREATE EXTENSION postgis_topology;
CREATE EXTENSION


```

1. 2D地理位置信息：

```shell script
mountains=# CREATE TABLE ski_resorts (id INTEGER NOT NULL, name VARCHAR(30) NOT NULL, state_code CHARACTER(2), country_code CHARACTER(2), location GEOGRAPHY(POINT,4326), CONSTRAINT pk_id PRIMARY KEY(id));
CREATE TABLE
mountains=# INSERT INTO ski_resorts (id, name, state_code, country_code, location) VALUES (1,'Squaw','CA','US',ST_GeogFromText('SRID=4326;POINT(-120.234811 39.196195)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts (id, name, state_code, country_code, location) VALUES (2,'Breckenridge','CO','US',ST_GeogFromText('SRID=4326;POINT(-106.067927 39.480965)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts (id, name, state_code, country_code, location) VALUES (3,'Steamboat','CO','US',ST_GeogFromText('SRID=4326;POINT(-106.80616 40.45695)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts (id, name, state_code, country_code, location) VALUES (4,'Vail','CO','US',ST_GeogFromText('SRID=4326;POINT(-106.373445 39.640031)'));
INSERT 0 1
mountains=# SELECT * FROM ski_resorts;
 id |     name     | state_code | country_code |                      location                      
----+--------------+------------+--------------+----------------------------------------------------
  1 | Squaw        | CA         | US           | 0101000020E61000006D6FB724070F5EC0C251F2EA1C994340
  2 | Breckenridge | CO         | US           | 0101000020E6100000FCE07CEA58845AC0A4C2D84290BD4340
  3 | Steamboat    | CO         | US           | 0101000020E6100000FAD51C2098B35AC01FF46C567D3A4440
  4 | Vail         | CO         | US           | 0101000020E6100000B476DB85E6975AC08DB62A89ECD14340
(4 rows)
mountains=# CREATE USER dude WITH PASSWORD 'supersecret';
CREATE ROLE
mountains=# GRANT ALL PRIVILEGES ON ski_resorts to dude;
GRANT
```

查询GIS数据：

查询经纬度：

```shell script
mountains=# SELECT ST_AsText(location) AS Dist_deg FROM ski_resorts WHERE name='Vail';
           dist_deg           
------------------------------
 POINT(-106.373445 39.640031)
(1 row)
mountains=# SELECT ST_X(ST_AsText(location)), ST_Y(ST_AsText(location)) AS Dist_deg FROM ski_resorts WHERE name='Vail';
    st_x     | dist_deg  
-------------+-----------
 -106.373445 | 39.640031
(1 row)
```

查询从Breckenridge 到 Vail的距离：

```shell script
mountains=# SELECT ST_Distance(a.location, b.location)/1000 as Dist_deg from ski_resorts a, ski_resorts b where a.name='Breckenridge' AND b.name='Vail';
    dist_deg    
----------------
 31.64270439224
(1 row)
```

2. 3D位置信息：

```shell script
mountains=# CREATE TABLE ski_resorts_3d (id INTEGER NOT NULL, name VARCHAR(30) NOT NULL, state_code CHARACTER(2), country_code CHARACTER(2), location GEOGRAPHY(POINTZ,4326), CONSTRAINT pk_id PRIMARY KEY(id));
CREATE TABLE
mountains=# INSERT INTO ski_resorts_3d (id, name, state_code, country_code, location) VALUES (2,'Breckenridge','CO','US',ST_GeographyFromText('SRID=4326;POINTZ(-106.067927 39.480965 2926)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts_3d (id, name, state_code, country_code, location) VALUES (4,'Vail','CO','US',ST_GeographyFromText('SRID=4326;POINTZ(-106.373445 39.640031 2475)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts_3d (id, name, state_code, country_code, location) VALUES (5,'Heavenly','CA','US',ST_GeographyFromText('SRID=4326;POINTZ(-119.939488 38.935541 1993)'));
INSERT 0 1
mountains=# INSERT INTO ski_resorts_3d (id, name, state_code, country_code, location) VALUES (6,'Mammoth','CA','US',ST_GeographyFromText('SRID=4326;POINTZ(-119.03792 37.65127 2424)'));
INSERT 0 1
mountains=# SELECT ST_AsText(location) AS Dist_deg FROM ski_resorts_3d WHERE name='Heavenly';
               dist_deg               
--------------------------------------
 POINT Z (-119.939488 38.935541 1993)
(1 row)
mountains=# SELECT ST_X(ST_AsText(location)), ST_Y(ST_AsText(location)), ST_Z(ST_AsText(location)) FROM ski_resorts_3d WHERE name='Heavenly';
    st_x     |   st_y    | st_z 
-------------+-----------+------
 -119.939488 | 38.935541 | 1993
(1 row)
mountains=# SELECT ST_Distance(a.location, b.location)/1000 AS spheroid_dist FROM ski_resorts_3d a, ski_resorts_3d b WHERE a.name='Heavenly' AND b.name='Mammoth';
 spheroid_dist
-----------------
 162.91764071573
(1 row)
```


## QGIS 可视化位置信息

[QGIS](https://qgis.org/en/site/about/screenshots.html#screenshots)是一个专业级的GIS应用。

简单安装：

```shell script
sudo apt-get install qgis
```

将3D位置信息打印出来：

![qgis](/images/qgis.png)

