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

为了继续学习更复杂的查询，我们需要创建一个更严肃的数据库并填充生产数据。

下面是一份航空公司的数据库ER图。

![airport](/images/air_flights_er.png)

主要实体是**booking**。

### Bookings

预定。

### Tickets

机票。

### Flight Segments

飞行段。

### Flights

出发地-目的地。

![flight](/images/flight_geo.png)

航班的状态包含有：

- Scheduled     排期中

The flight is available for booking. It happens one month before the planned departure date; before that time, there is no entry for this flight in the database.

- On Time       将准时达到

The flight is open for check-in(twenty-four hours before the scheduled departure) and is not delayed.

- Delayed       将延时到达

The flight is open for check-in(twenty-four hours before the scheduled departure), but is delayed.

- Departed      已过，已离开

The aircraft has already departed and is airborne.

- Arrived       已到达

The aircraft has reached the point of destination.

- Cancelled     取消

The flight is cancelled.

### Airports

机场。由三个字母 airport_code 标识。包含有 airport_name。

### Boarding Passes

登机许可。

处于检票时间，开放于排期的24小时前，乘客拥有登机通行许可。

登机许可由机票号和飞行号唯一标识。并指定唯一一个座位号(seat_no)。

### Aircraft

飞机由3个数字的 air-craft_code唯一标识。并包含有名称，最大飞行距离。

### Seats

座位。每个飞机的包厢配置有座位号，座位号对应包厢会有经济舱、舒适舱、商业舱。

### Flights View

飞行视图提供了对`flights`表的额外信息。

- 关于机场起飞信息，departure_airport, departure_airport_name, departure_city,
- 关于机场到达信息，arrival_airport, arrival_airport_name, arrival_city,
- 本机场各航班起飞时间，scheduled_departure_local, actual_departure_local,
- 本机场各航班到达时间，scheduled_arrival_local, actual_arrival_local,
- 飞行周期，scheduled_duration, actual_duration.

### Routes View

路途。

### The "now" Function

快照处理数据库。

## Sample Queries

### A Couple of Words about the Schema

连接到demo数据库：

```bash
postgres=# \c demo
You are now connected to database "demo" as user "postgres".
demo=#
```

所有我们感兴趣的实体被存储在bookings schema中。连接到数据库后，schema 会自动使用，不需要显式指定。

```bash
demo=# select * from aircrafts;
 aircraft_code |        model        | range 
---------------+---------------------+-------
 773           | Boeing 777-300      | 11100
 763           | Boeing 767-300      |  7900
 SU9           | Sukhoi Superjet-100 |  3000
 320           | Airbus A320-200     |  5700
 321           | Airbus A321-200     |  5600
 319           | Airbus A319-100     |  6700
 733           | Boeing 737-300      |  4200
 CN1           | Cessna 208 Caravan  |  1200
 CR2           | Bombardier CRJ-200  |  2700
(9 rows)

```

但对于 `bookings.now`函数，需要指定schema，以区分标准的now函数：

```bash
demo=# SELECT bookings.now();
now
------------------------
2017-08-15 18:00:00+03
(1 row)
```

查询城市和机场信息。

```bash
demo=# select airport_code, city from airports limit 5;
 airport_code |       city        
--------------+-------------------
 YKS          | Yakutsk
 MJZ          | Mirnyj
 KHV          | Khabarovsk
 PKC          | Petropavlovsk
 UUS          | Yuzhno-Sakhalinsk
(5 rows)
```

数据库的内容提供了英语和俄罗斯语。你可更改schema的语言。

```bash
demo=# SET bookings.lang = ru;
```

如果你想全局定义，执行如下命令：

```bash
demo=# ALTER DATABASE demo SET bookings.lang = ru;
ALTER DATABASE
```

但不要忘记重新连接数据库才能生效：

```bash
demo=# \c
You are now connected to database "demo" as user
"postgres".
```

如果你改为俄罗斯语，城市名会翻译为Russian：

```bash
demo=# SELECT airport_code, city
FROM airports LIMIT 5;
 airport_code |           city           
--------------+--------------------------
 YKS          | Якутск
 MJZ          | Мирный
 KHV          | Хабаровск
 PKC          | Петропавловск-Камчатский
 UUS          | Южно-Сахалинск
(5 rows)
```

想要理解它是如何工作的，你可以使用`\d+ `命令查看`aircrafts`或 `airports`的定义。

### Simple Queries

**Problem**. Who traveled from Moscow(SVO) to Novosibirsk(OVB) on seat 1A yesterday, and when was the ticket booked?

**Solution**. "The day before yesterday" is counted from the booking. now value, not from the current date.

```postgresql
SELECT t.passenger_name,
b.book_date
FROM bookings b
JOIN tickets t
ON t.book_ref = b.book_ref
JOIN boarding_passes bp
ON bp.ticket_no = t.ticket_no
JOIN flights f
ON f.flight_id = bp.flight_id
WHERE f.departure_airport = 'SVO'
AND f.arrival_airport = 'OVB'
AND f.scheduled_departure::date =
bookings.now()::date - INTERVAL '2 day'
AND bp.seat_no = '1A';
```

**Problem**. How many seats remained free on flight PG0404 yesterday?

**Solution**. There are several approaches to solving this problem. The first one uses the NOT EXISTS clause to find the seats without the corresponding boarding passes:

```postgresql
SELECT count(*)
FROM flights f
JOIN seats s
ON s.aircraft_code = f.aircraft_code
WHERE f.flight_no = 'PG0404'
AND f.scheduled_departure::date =
bookings.now()::date - INTERVAL '1 day'
AND NOT EXISTS (
SELECT NULL
FROM boarding_passes bp
WHERE bp.flight_id = f.flight_id
AND bp.seat_no = s.seat_no
);
```

第二种方式是使用差集：

```postgresql
SELECT count(*)
FROM (
SELECT s.seat_no
FROM seats s
WHERE s.aircraft_code = (
SELECT aircraft_code
FROM flights
WHERE flight_no = 'PG0404'
AND scheduled_departure::date =
bookings.now()::date - INTERVAL '1 day'
)
EXCEPT
SELECT bp.seat_no
FROM boarding_passes bp
WHERE bp.flight_id = (
SELECT flight_id
FROM flights
WHERE flight_no = 'PG0404'
AND scheduled_departure::date =
bookings.now()::date - INTERVAL '1 day'
)
) t;
```

**Problem**. Which flights had the longest delays? Print the list of hen "leaders."

**Solution**. The query only needs to include the already departed flights:

```postgresql
SELECT f.flight_no,
f.scheduled_departure,
f.actual_departure,
f.actual_departure - f.scheduled_departure
AS delay
FROM flights f
WHERE f.actual_departure IS NOT NULL
ORDER BY f.actual_departure - f.scheduled_departure
DESC
LIMIT 10;
```

### Aggregate Functions

**Problem**. What is the shortest flight duration for each possible flight from Moscow to St. Petersburg, and how many times was the flight delayed for more than an hour?

**Solution**. To solve this problem, it is convenient to use the available flights_v view instead of dealing with table  joins. You need to take into account only those flights that have already arrived.

```postgresql
SELECT f.flight_no,
f.scheduled_duration,
min(f.actual_duration),
max(f.actual_duration),
sum(CASE
WHEN f.actual_departure >
f.scheduled_departure +
INTERVAL '1 hour'
THEN 1 ELSE 0
END) delays
FROM flights_v f
WHERE f.departure_city = 'Moscow'
AND f.arrival_city = 'St. Petersburg'
AND f.status = 'Arrived'
GROUP BY f.flight_no,
f.scheduled_duration;
```

**Problem**. Find the most disciplined passengers who checked in first for all their flights. Take into account only those passengers who took at least two flights.

**Solution**. Use the fact that boarding pass numbers are issued in the check-in order.

```postgresql
SELECT t.passenger_name,
t.ticket_no
FROM tickets t
JOIN boarding_passes bp
ON bp.ticket_no = t.ticket_no
GROUP BY t.passenger_name,
t.ticket_no
HAVING max(bp.boarding_no) = 1
AND count(*) > 1;
```

**Problem**. How many people can be included into a single booking according to the available data?

**Solution**. First, let’s count the number of passengers in each booking, and then the number of bookings for each number of passengers.

```postgresql
SELECT tt.cnt,
count(*)
FROM (
SELECT t.book_ref,
count(*) cnt
FROM tickets t
GROUP BY t.book_ref
) tt
GROUP BY tt.cnt
ORDER BY tt.cnt;
```

### Window Functions

**Problem**. For each ticket, display all the included flight segments, together with connection time. Limit the result to the tickets booked a week ago.

**Solution**. Use window functions to avoid accessing the same data twice.

In the query results provided below, we can see that the time cushion between flights is several days in some cases. As a rule, these are round-trip tickets, that is, we see the time of the stay in the point of destination, not the time between connecting flights. Using the solution
for one of the problems in the “Arrays” section, you can take this fact into account when building the query.

```postgresql
SELECT tf.ticket_no,
f.departure_airport,
f.arrival_airport,
f.scheduled_arrival,
lead(f.scheduled_departure) OVER w
AS next_departure,
lead(f.scheduled_departure) OVER w -
f.scheduled_arrival AS gap
FROM bookings b
JOIN tickets t
ON t.book_ref = b.book_ref
JOIN ticket_flights tf
ON tf.ticket_no = t.ticket_no
JOIN flights f
ON tf.flight_id = f.flight_id
WHERE b.book_date =
bookings.now()::date - INTERVAL '7 day'
WINDOW w AS (PARTITION BY tf.ticket_no
ORDER BY f.scheduled_departure);
```

**Problem**. Which combinations of first and last names occur most often? What is the ratio of the passengers with such names to the total number of passengers?

**Solution**. A window function is used to calculate the total number of passengers.

```postgresql
SELECT passenger_name,
round( 100.0 * cnt / sum(cnt) OVER (), 2)
AS percent
FROM (
SELECT passenger_name,
count(*) cnt
FROM tickets
GROUP BY passenger_name
) t
ORDER BY percent DESC;
```

**Problem**. Solve the previous problem for first names and last names separately.

**Solution**. Consider a query for first names:

```postgresql
WITH p AS (
SELECT left(passenger_name,
position(' ' IN passenger_name))
AS passenger_name
FROM tickets
)
SELECT passenger_name,
round( 100.0 * cnt / sum(cnt) OVER (), 2)
AS percent
FROM (
SELECT passenger_name,
count(*) cnt
FROM p
GROUP BY passenger_name
) t
ORDER BY percent DESC;
```



### Arrays

**Problem**. There is no indication whether the ticket is oneway or round-trip. However, you can figure it out by comparing the first point of departure with the last point of destination. Display airports of departure and destination for each ticket, ignoring connections, and decide whether it’s a round-trip ticket.

**Solution**. One of the easiest solutions is to work with an array of airports converted from the list of airports in the itinerary using the array_agg aggregate function. We select the middle element of the array as the airport of destination, assuming that the outbound and inbound
ways have the same number of stops.

```postgresql
WITH t AS (
SELECT ticket_no,
a,
a[1] departure,
a[cardinality(a)] last_arrival,
a[cardinality(a)/2+1] middle
FROM (
SELECT t.ticket_no,
array_agg( f.departure_airport
ORDER BY f.scheduled_departure) ||
(array_agg( f.arrival_airport
ORDER BY f.scheduled_departure DESC)
)[1] AS a
FROM tickets t
JOIN ticket_flights tf
ON tf.ticket_no = t.ticket_no
JOIN flights f
ON f.flight_id = tf.flight_id
GROUP BY t.ticket_no
) t
)
SELECT t.ticket_no,
t.a,
t.departure,
CASE
WHEN t.departure = t.last_arrival
THEN t.middle
ELSE t.last_arrival
END arrival,
(t.departure = t.last_arrival) return_ticket
FROM t;
```

In this example, the tickets table is scanned only once. The array of airports is displayed for clarity only; for large volumes of data, it makes sense to remove it from the query.

**Problem**. Find the round-trip tickets in which the outbound route differs from the inbound one.

**Problem**. Find the pairs of airports with inbound and outbound flights departing on different days of the week. 

**Solution**. The part of the problem that involves building an array of days of the week is virtually solved in the routes view. You only have to find the intersection using the && operator:

```postgresql
SELECT r1.departure_airport,
r1.arrival_airport,
r1.days_of_week dow,
r2.days_of_week dow_back
FROM routes r1
JOIN routes r2
ON r1.arrival_airport = r2.departure_airport
AND r1.departure_airport = r2.arrival_airport
WHERE NOT (r1.days_of_week && r2.days_of_week);
```



### Recursive Queries

**Problem**. How can you get from Ust-Kut (UKX) to Neryungri(CNN) with the minimal number of connections, and what will the flight time be?

**Solution**. Here you have to find the shortest path in the graph. It can be done with the following recursive query:

```postgresql
WITH RECURSIVE p(
last_arrival,
destination,
hops,
flights,
flight_time,
found
) AS (
SELECT a_from.airport_code,
a_to.airport_code,
array[a_from.airport_code],
array[]::char(6)[],
interval '0',
a_from.airport_code = a_to.airport_code
FROM airports a_from,
airports a_to
WHERE a_from.airport_code = 'UKX'
AND a_to.airport_code = 'CNN'
UNION ALL
SELECT r.arrival_airport,
p.destination,
(p.hops || r.arrival_airport)::char(3)[],
(p.flights || r.flight_no)::char(6)[],
p.flight_time + r.duration,
bool_or(r.arrival_airport = p.destination)
OVER ()
FROM p
JOIN routes r
ON r.departure_airport = p.last_arrival
WHERE NOT r.arrival_airport = ANY(p.hops)
AND NOT p.found
)
SELECT hops,
flights,
flight_time
FROM p
WHERE p.last_arrival = p.destination;
```

**Problem**. What is the maximum number of connections that can be required to get from any airport to any other airport?

**Solution**. We can take the previous query as the basis for the solution. However, the first iteration must now contain all possible airport pairs, not a single pair: each airport must be connected to each other airport. For all these pairs we first find the shortest path, and then select the longest of them.

Clearly, it is only possible if the routes graph is connected. This query also uses the found attribute, but here it should be calculated separately for each pair of airports.

```postgresql
WITH RECURSIVE p(
departure,
last_arrival,
destination,
hops,
found
) AS (
SELECT a_from.airport_code,
a_from.airport_code,
a_to.airport_code,
array[a_from.airport_code],
a_from.airport_code = a_to.airport_code
FROM airports a_from,
airports a_to
UNION ALL
SELECT p.departure,
r.arrival_airport,
p.destination,
(p.hops || r.arrival_airport)::char(3)[],
bool_or(r.arrival_airport = p.destination)
OVER (PARTITION BY p.departure,
p.destination)
FROM p
JOIN routes r
ON r.departure_airport = p.last_arrival
WHERE NOT r.arrival_airport = ANY(p.hops)
AND NOT p.found
)
SELECT max(cardinality(hops)-1)
FROM p
WHERE p.last_arrival = p.destination;
```

**Problem**. Find the shortest route from Ust-Kut (UKX) to Negungri (CNN) from the flight time point of view (ignoring connection time).

**Hint**: the route may be non-optimal with regards to the number of connections.

**Solution**.

```postgresql
WITH RECURSIVE p(
last_arrival,
destination,
hops,
flights,
flight_time,
min_time
) AS (
SELECT a_from.airport_code,
a_to.airport_code,
array[a_from.airport_code],
array[]::char(6)[],
interval '0',
NULL::interval
FROM airports a_from,
airports a_to
WHERE a_from.airport_code = 'UKX'
AND a_to.airport_code = 'CNN'
UNION ALL
SELECT r.arrival_airport,
p.destination,
(p.hops || r.arrival_airport)::char(3)[],
(p.flights || r.flight_no)::char(6)[],
p.flight_time + r.duration,
least(
p.min_time, min(p.flight_time+r.duration)
FILTER (
WHERE r.arrival_airport = p.destination
) OVER ()
)
FROM p
JOIN routes r
ON r.departure_airport = p.last_arrival
WHERE NOT r.arrival_airport = ANY(p.hops)
AND p.flight_time + r.duration <
coalesce(p.min_time, INTERVAL '1 year')
)
SELECT hops,
flights,
flight_time
FROM (
SELECT hops,
flights,
flight_time,
min(min_time) OVER () min_time
FROM p
WHERE p.last_arrival = p.destination
) t
WHERE flight_time = min_time;
```

### Functions and Extensions

**Problem**. Find the distance between Kaliningrad (KGD) and Petropavlovsk-Kamchatsky (PKV).

**Solution**. We know airport coordinates. To calculate the distance, we can use the earthdistance extension (and then convert miles to kilometers).

```postgresql
CREATE EXTENSION IF NOT EXISTS cube;
CREATE EXTENSION IF NOT EXISTS earthdistance;
SELECT round(
(a_from.coordinates <@> a_to.coordinates) *
1.609344
)
FROM airports a_from,
airports a_to
WHERE a_from.airport_code = 'KGD'
AND a_to.airport_code = 'PKC';
```

**Problem**. Draw the graph of flights between all airports.