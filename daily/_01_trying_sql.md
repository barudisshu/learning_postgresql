Trying SQL
=============

## Connecting via psql

连接pg进行数据库操作，有两种方式。一种是GUI方式，postgres 官方默认提供了pgAdmin；另一种方式是命令行。

对于*-Unix系统，执行如下命令：

```shell script
$ sudo -u postgres psql
```

对于windows操作系统，选择如下shell界面进入。

![gui_shell](/images/windows_gui_shell.png)

注意的是，windows的终端会出现中文乱码，可以设置TrueType 字体方便操作。

### Database

1. 创建一个名为`test`的数据库。

```shell script
postgres=# CREATE DATABASE test;
CREATE DATABASE
```

不要忘记了在末尾使用分号(; semicolon)：Postgres会认为你希望分隔多行继续输入。

2. 连接到刚刚创建的数据库。

```shell script
postgres=# \c test
You are now connected to database "test" as user "postgres".
test=#
```

你会看到，命令提示符变为了：

```shell script
test=#
```

这里的输入命令和SQL不一样。它用一个反斜杠开始(backslash)。它可以很方便地操作postgres的特定命令。例如 `test=# \c` 表示连接到test数据库。对于pgAdmin。则不需要带反斜杠。

很好几个psql命令，有些我们会经常用到。通过下面操作，列出所有的`psql`命令：

```shell script
test=# \?
```

输出的信息有点多，你可以使用 `more` 或 `less` 命令查看。

### Tables

关系型数据库管理系统把数据用表`table`来描述。表的头部为 列`columns`；数据存储的内容称为 行`rows`。数据不是排序的。也就是说，你不能按顺序解压表里面的行数据内容。

每列都会定义类型 (data type)。所有行对应该列的数据必须匹配列的类型定义。postgres提供了大量的数据类型供选择。这里我们仅简单介绍下面几种常见的：

- integer 等价于 int、int4
- text
- boolean, 逻辑类型，仅有true和false

处理要符合指定了列类型，行内的数据可以为 `undefined mark NULL`，即为空。表示未知或未设置该列的值。

下面创建一个 大学课程 的表：


```shell script
test=# CREATE TABLE courses(
test(# c_no text PRIMARY KEY,
test(# title text,
test(# hours integer
test(# );
CREATE TABLE
```

请注意上面命令提示符的变化。

这里创建了3列数据：

- `c_no` text 文档字符
- `tilte` 标题
- `hours` 整型，代表课程时长

primary key 作为约束，它表示`c_no`是唯一的、不可以为空的。

如果你希望获得更多命令的详细操作，可以通过如下命令：

```shell script
test=# \help CREATE TABLE
```

如果要获得所有帮助，执行`\help`操作。

## Filling Tables with Data

插入数据。

```shell script
test=# INSERT INTO courses(c_no, title, hours)
VALUES ('CS301', 'Databases', 30),
('CS305', 'Networks', 60);
INSERT 0 2
```

> 如果你需要从外部源导入一批数据。`INSERT`语句不是最好的选择。相反你可以使用[`COPY`](https://postgrespro.com/doc/sql-copy.html)命令。

我们还需要再建两张表： `students` 和 `exams`。

```shell script
test=# CREATE TABLE students(
s_id integer PRIMARY KEY,
name text,
start_year integer
);
CREATE TABLE
test=# INSERT INTO students(s_id, name, start_year)
VALUES (1451, 'Anna', 2014),
(1432, 'Victor', 2014),
(1556, 'Nina', 2015);
INSERT 0 3
```

考试记录需要对应都学生和课程，因此，学生-课程 是多对多的关系。这里需要引入外键约束。


```shell script
test=# CREATE TABLE exams(
s_id integer REFERENCES students(s_id),
c_no text REFERENCES courses(c_no),
score integer,
CONSTRAINT pk PRIMARY KEY(s_id, c_no)
);
CREATE TABLE
```

这里的 `references` 称为 `foreign keys` 外键。它表示该表的字段值引用于其它表。

下面我们插入一些数据。

```shell script
test=# INSERT INTO exams(s_id, c_no, score)
VALUES (1451, 'CS301', 5),
(1556, 'CS301', 5),
(1451, 'CS305', 5),
(1432, 'CS305', 4);
INSERT 0 4
```

### Data Retrieval

1. 简单的查询

```shell script
test=# SELECT title AS course_title, hours
FROM courses;
course_title | hours
--------------+-------
Databases | 30
Networks | 60
(2 rows)
```


`AS` 表示重命名查询的列结果。

查询所有列的内容，使用`*` 符号：

```shell script
test=# SELECT * FROM courses;
c_no | title | hours
-------+-------------+-------
CS301 | Databases | 30
CS305 | Networks | 60
(2 rows)
```


有一种情况是，查询的结果可能重复。如下：

```shell script
test=# SELECT start_year FROM students;
start_year
------------
2014
2014
2015
(3 rows)
```

我们可以使用`distinct`关键字进行去重：

```shell script
test=# SELECT DISTINCT start_year FROM students;
start_year
------------
2014
2015
(2 rows)
```

通常，可以使用select 后面跟任何表达式操作，如果省略表，查询结果仅显示一行。例如：

```shell script
test=# SELECT 2+2 AS result;
result
--------
4
(1 row
```

下面是带条件的查询，

```shell script
test=# SELECT * FROM courses WHERE hours > 45;
c_no | title | hours
-------+----------+-------
CS305 | Networks | 60
(1 row)
```

条件语句部分必须是逻辑类型。它包含有 =, <> (or !=), >, >=, <, <=, 当然也可以组合逻辑操作 `AND`，`OR`， `NOT` 和 括号。

对于NULL的操作有点微妙：

- 对NULL值的比较的结果是 undefined.
- NULL的逻辑操作通常是 undefined. 例如 true OR NULL = true, false AND NULL = false)
- 还有直接比较NULL值。IS NULL(IS NOT NULL). IS DISTINCT FROM (IS NOT DISTINCT FROM)
- 也适用于组合函数。


### Joins

良好设计的表不会出现重复的数据。例如exam表不会包含学生名字。基于该原则，要查询某些特定内容时，需要进行联表查询：

```shell script
test=# SELECT * FROM courses, exams;
c_no | title | hours | s_id | c_no | score
-------+-------------+-------+------+-------+-------
CS301 | Databases | 30 | 1451 | CS301 | 5
CS305 | Networks | 60 | 1451 | CS301 | 5
CS301 | Databases | 30 | 1556 | CS301 | 5
CS305 | Networks | 60 | 1556 | CS301 | 5
CS301 | Databases | 30 | 1451 | CS305 | 5
CS305 | Networks | 60 | 1451 | CS305 | 5
CS301 | Databases | 30 | 1432 | CS305 | 4
CS305 | Networks | 60 | 1432 | CS305 | 4
(8 rows)
```

改结果成为笛卡尔乘积(Cartesian product)。

如果我们要查询所有课程的分数的话：


```shell script
test=# SELECT courses.title, exams.s_id, exams.score
FROM courses, exams
WHERE courses.c_no = exams.c_no;
title | s_id | score
-------------+------+-------
Databases | 1451 | 5
Databases | 1556 | 5
Networks | 1451 | 5
Networks | 1432 | 4
(4 rows)
```

虽然结果查出来的，但是不够优雅，例如我们希望查询所有学生关于"Networks"课程的记录：


```shell script
test=# SELECT students.name, exams.score
FROM students
JOIN exams
ON students.s_id = exams.s_id
AND exams.c_no = 'CS305';
name | score
--------+-------
Anna | 5
Victor | 4
(2 rows)
```

这里引入了`Join`关键字。从DBMS看，视图和查询时等价的。你可以采用任何一种方式。

这里的结果还可以看出，不存在于exam表记录的学生会被过滤掉。为了包含所有学生，不管他有没有参加考试，使用外联表查询的方式(outer join)：

```shell script
test=# SELECT students.name, exams.score
FROM students
LEFT JOIN exams
ON students.s_id = exams.s_id
AND exams.c_no = 'CS305';
name | score
--------+-------
Anna | 5
Victor | 4
Nina |
(3 rows)
```

`WHERE`条件语句作用于结果处理上。因此，如果指定了约束提交，Nina会被过滤掉：

```shell script
test=# SELECT students.name, exams.score
FROM students
LEFT JOIN exams ON students.s_id = exams.s_id
WHERE exams.c_no = 'CS305';
name | score
--------+-------
Anna | 5
Victor | 4
(2 rows)
```

postgres对于联表查询做了优化处理。因此不要在应用层级上做联合处理，让数据库做这个工作。

### Subqueries

`SELECT`语句返回一个表，它可以作为一个查询结果，或者被用作其它sql语句的查询。例如select 内嵌的子查询。

```shell script
test=# SELECT name,
(SELECT score
FROM exams
WHERE exams.s_id = students.s_id
AND exams.c_no = 'CS305')
FROM students;
name | score
--------+-------
Anna | 5
Victor | 4
Nina |
(3 rows)
```

包含NULL值的子语句，结果将被过滤掉：

```shell script
test=# SELECT *
FROM exams
WHERE (SELECT start_year
FROM students
WHERE students.s_id = exams.s_id) > 2014;
s_id | c_no | score
------+-------+-------
1556 | CS301 | 5
(1 row)
```

子查询语句内也可以添加条件语句。

```shell script
test=# SELECT name, start_year
FROM students
WHERE s_id IN (SELECT s_id
FROM exams
WHERE c_no = 'CS305');
name | start_year
--------+------------
Anna | 2014
Victor | 2014
(2 rows)
```

或者取相反的结果值。

```shell script
test=# SELECT name, start_year
FROM students
WHERE s_id NOT IN (SELECT s_id
FROM exams
WHERE score < 5);
name | start_year
------+------------
Anna | 2014
Nina | 2015
(2 rows)
```

另一选项是使用EXISTS预制条件，检测子查询需要至少有一个记录。

```shell script
test=# SELECT name, start_year
FROM students
WHERE NOT EXISTS (SELECT s_id
FROM exams
WHERE exams.s_id = students.s_id
AND score < 5);
name | start_year
------+------------
Anna | 2014
Nina | 2015
(2 rows)
```

为了避免表冲突，你可以在子查询语句后使用任意名字代表这个查询结果。

```shell script
test=# SELECT s.name, ce.score
FROM students s
JOIN (SELECT exams.*
FROM courses, exams
WHERE courses.c_no = exams.c_no
AND courses.title = 'Databases') ce
ON s.s_id = ce.s_id;
name | score
------+-------
Anna | 5
Nina | 5
(2 rows)
```

这里的"s"是表的别名，"ce"则是子查询的别名。

不过子查询多数情况下等价于笛卡尔乘积的查询。例如：

```shell script
test=# SELECT s.name, e.score
FROM students s, courses c, exams e
WHERE c.c_no = e.c_no
AND c.title = 'Databases'
AND s.s_id = e.s_id;
```

### Sorting

要对结果进行排序，使用Order by ... asc/desc这样的操作语句。

```shell script
test=# SELECT * FROM exams
ORDER BY score, s_id, c_no DESC;
s_id | c_no | score
------+-------+-------
1432 | CS305 | 4
1451 | CS305 | 5
1451 | CS301 | 5
1556 | CS301 | 5
(4 rows)
```

### Grouping Operations

组操作表示将多行结果组合。通常使用聚集函数(aggregate functions)操作。例如，查询考试的总分数。

```shell script
test=# SELECT count(*), count(DISTINCT s_id),
avg(score)
FROM exams;
count | count | avg
-------+-------+--------------------
4 | 3 | 4.7500000000000000
(1 row)
```

或者使用GROUP BY    从句。

```shell script
test=# SELECT c_no, count(*),
count(DISTINCT s_id), vg(score)
FROM exams
GROUP BY c_no;
c_no | count | count | avg
-------+-------+-------+--------------------
CS301 | 2 | 2 | 5.0000000000000000
CS305 | 2 | 2 | 4.5000000000000000
(2 rows)
```

GROUP BY 从句后还可以跟 HAVING 子句进行条件过来。例如，查询学生分数不低于5分的学科数不少于1科的学生名。

```shell script
test=# SELECT students.name
FROM students, exams
WHERE students.s_id = exams.s_id AND exams.score = 5
GROUP BY students.name
HAVING count(*) > 1;
name
------
Anna
(1 row)
```

### Changing and Deleting Data

更新或删除数据。

```shell script
test=# UPDATE courses
SET hours = hours * 2
WHERE c_no = 'CS301';
UPDATE 1
```

删除

```shell script
test=# DELETE FROM exams WHERE score < 5;
DELETE 1
```


### Transactions

扩展我们的数据库：

```shell script
test=# CREATE TABLE groups(
g_no text PRIMARY KEY,
monitor integer NOT NULL REFERENCES students(s_id)
);
CREATE TABLE
```

```shell script
test=# ALTER TABLE students
ADD g_no text REFERENCES groups(g_no);
ALTER TABLE
```

为学生表增加一个组的属性。

使用description 命令 `\d`查看表结果内容：

```shell script
test=# \d students
Table "public.students"
Column | Type | Modifiers
------------+---------+----------
s_id | integer | not null
name | text |
start_year | integer |
g_no | text |
...
```

现在，我们创建一个组"A-101"。将所有学生移进该组，并另Anna作为组长。

这里我们陷入了困惑。一方面，我们不能创建一个没有组长的组。另一方面，如果Anna不是该组的成员，我们怎么能指派她作为组长？这将导致逻辑错误。

我们偶尔会遇到两个操作必须同时执行的情况，并且任意一方的操作缺失另一方都没有意义。这种不可分割的逻辑单元操作称为**事务(transaction)**。

下面开始我们的事务：

```shell script
test=# BEGIN;
BEGIN
```

接下来，我们需要添加一个新的组，以及组长。因为我们不记得Anna的学生ID，我们需要使用查询-插入的命令。

```shell script
test=# INSERT INTO groups(g_no, monitor)
SELECT 'A-101', s_id
FROM students
WHERE name = 'Anna';
INSERT 0 1
```

此时此刻，如果我们开启一个新的终端。新的终端可以看到变更结果吗？

```shell script
postgres=# \c test
You are now connected to database "test" as user
"postgres".
test=# SELECT * FROM groups;
g_no | monitor
------+---------
(0 rows)
```

结果显示。没有。因为事务还没有完成。

我们继续。将所有学生添加到新创建的组：

```shell script
test=# UPDATE students SET g_no = 'A-101';
UPDATE 3
```

新打开的终端仍然什么都没看到。

```shell script
test=# SELECT * FROM students;
s_id | name | start_year | g_no
------+--------+------------+------
1451 | Anna | 2014 |
1432 | Victor | 2014 |
1556 | Nina | 2015 |
(3 rows)
```

因为事务还没有提交。

现在，我们提交事务。

```shell script
test=# COMMIT;
COMMIT
```

最后，在新打开的终端中，我们看到了期望的内容了。

```shell script
test=# SELECT * FROM groups;
g_no | monitor
-------+---------
A-101 | 1451
(1 row)
test=# SELECT * FROM students;
s_id | name | start_year | g_no
------+--------+------------+-------
1451 | Anna | 2014 | A-101
1432 | Victor | 2014 | A-101
1556 | Nina | 2015 | A-101
(3 rows)
```

### Useful psql Commands

有些有用的命令：

- `\?` 命令帮助
- `\h` 后面当上命令，解析命令的语法信息。
- `\x` 开关。用于扩展显示表的信息。查看"宽表”，特别是某个列字段很长的情况.
- `\l` 列出所有数据库。
- `\du` 列出所有用户。
- `\dt` 列出所有表。
- `\di` 列出所有索引。
- `\dv` 列出所有视图。
- `\df` 列出所有函数。
- `\dn` 列出所有schemas。
- `\dx` 列出所有privileges。
- `\d name` DBMS对象的详细信息。
- `\d+ name` DBMS对象的扩展信息。
- `\timing on` 显示操作的执行时间。

## Conclusion

一个重要的命令。退出。

```shell script
test=# \q
```