Additional Features
=============

## Full-Text Search

全文搜索.

新建一个表`course_chapters`。

```shell script
test=# CREATE TABLE course_chapters(
c_no text REFERENCES courses(c_no),
ch_no text,
ch_title text,
txt text,
CONSTRAINT pkt_ch PRIMARY KEY(ch_no, c_no)
);
CREATE TABLE
```

插入关于课程内容的数据。

```shell script
test=# INSERT INTO course_chapters(
c_no, ch_no,ch_title, txt)
VALUES
('CS301', 'I', 'Databases',
'We start our acquaintance with ' ||
'the fascinating world of databases'),
('CS301', 'II', 'First Steps',
'Getting more fascinated with ' ||
'the world of databases'),
('CS305', 'I', 'Local Networks',
'Here we start our adventurous journey ' ||
'through the intriguing world of networks');
INSERT 0 3
```

检查结果内容：

```shell script
test=# SELECT ch_no AS no, ch_title, txt
test-# FROM course_chapters \gx
-[ RECORD 1 ]----------------------------------------------------------------------------
no       | I
ch_title | Databases
txt      | We start our acquaintance with the fascinating world of databases
-[ RECORD 2 ]----------------------------------------------------------------------------
no       | II
ch_title | First Steps
txt      | Getting more fascinated with the world of databases
-[ RECORD 3 ]----------------------------------------------------------------------------
no       | I
ch_title | Local Networks
txt      | Here we start our adventurous journey through the intriguing world of networks


```

使用`LIKE`操作查询：

```shell script
test=# SELECT txt
FROM course_chapters
WHERE txt LIKE '%fascination%' \gx
```

我们可以得知结果：0 rows。那是因为`LIKE`没有查找到匹配的单词。

```shell script
test=# SELECT txt FROM course_chapters WHERE txt LIKE '%fascinated%' \gx
```

这时会返回第二章的结果。

```shell script
-[ RECORD 1 ]--------------------------------------------
txt | Getting more fascinated with the world of databases

```

postgres 提供了`ILIKE`关键字，允许忽略大小写匹配。另外还可以使用正则表达式进行查询。

下面我们新增一列 `tsvector`数据。


```shell script
test=# ALTER TABLE course_chapters ADD txtvector tsvector;
test=# UPDATE course_chapters SET txtvector = to_tsvector('english',txt);
test=# SELECT txtvector FROM course_chapters \gx
-[ RECORD 1 ]---------------------------------------------------------------------
txtvector | 'acquaint':4 'databas':10 'fascin':7 'start':2 'world':8
-[ RECORD 2 ]---------------------------------------------------------------------
txtvector | 'databas':8 'fascin':3 'get':1 'world':6
-[ RECORD 3 ]---------------------------------------------------------------------
txtvector | 'adventur':5 'intrigu':9 'journey':6 'network':12 'start':3 'world':10
```

可以看到，结果变了：

- 单词被分解为它们相同的部分(lexemes，词位)
- 出现数字。指示了它在文本中的位置。
- 不再有介词(preposition)，也不会有连词(conjunction)或其它停词(stop-words，指对搜索无关紧要的单词)

为了建立更高级的搜索，我们希望将文章的title也放到搜索区域。另外为了侧重权重，我们可以指派权重。

```shell script
test=# UPDATE course_chapters
SET txtvector =
setweight(to_tsvector('english',ch_title),'B')
|| ' ' ||
setweight(to_tsvector('english',txt),'D');
UPDATE 3
test=# SELECT txtvector FROM course_chapters \gx
-[ RECORD 1 ]------------------------------------------------------------------------------------
txtvector | 'acquaint':5 'databas':1B,11 'fascin':8 'start':3 'world':9
-[ RECORD 2 ]------------------------------------------------------------------------------------
txtvector | 'databas':10 'fascin':5 'first':1B 'get':3 'step':2B 'world':8
-[ RECORD 3 ]------------------------------------------------------------------------------------
txtvector | 'adventur':7 'intrigu':11 'journey':8 'local':1B 'network':2B,14 'start':5 'world':12
```

词位接收了一个相关的权重标记：B 和 D (也可以是A、B、C、D)。让后通过`to_tsquery`函数重新组装`t-_tsvector`函数的结果：

```shell script
test=# SELECT ch_title
test-# FROM course_chapters
test-# WHERE txtvector @@
test-# to_tsquery('english','fascination & database');
  ch_title   
-------------
 Databases
 First Steps
(2 rows)
```

你可以使用 `'fascinated & database'` 会发现得到相同的结果。

`english` 参数为内置分词插件。你可以安装其它分词插件。譬如 `hunspell`(语法分析)或`unaccent`(移除音符)

指派的权重允许展示搜索等级：

```shell script
test=# SELECT ch_title,
test-# ts_rank_cd('{0.1, 0.0, 1.0, 0.0}', txtvector, q) 
FROM course_chapters, to_tsquery('english','Databases') q 
WHERE txtvector @@ q  
ORDER BY ts_rank_cd DESC;
  ch_title   | ts_rank_cd 
-------------+------------
 Databases   |        1.1
 First Steps |        0.1
(2 rows)

```

默认地，数组{0.1, 0.2, 0.4, 1.0} 对应于 D、C、B、A权重。权重增加重要结果的返回。帮助结果等级的排序。

最后一个实验。我们希望返回带有html标签的结果。

```shell script
test=# SELECT ts_headline(
'english',
txt,
to_tsquery('english', 'world'),
'StartSel=<b>, StopSel=</b>, MaxWords=50, MinWords=5'
)
FROM course_chapters
WHERE to_tsvector('english', txt) @@
to_tsquery('english', 'world') \gx

-[ RECORD 1 ]------------------------------------------------
ts_headline | with the fascinating <b>world</b> of databases
-[ RECORD 2 ]------------------------------------------------
ts_headline | with the <b>world</b> of databases
-[ RECORD 3 ]------------------------------------------------
ts_headline | through the intriguing <b>world</b> of networks

```

`ts_headline`函数可以匹配到查询单词的内容，并加上自定义的符号。

为了提升全文搜索的速度，一些特殊的索引会被用到：`GiST`、`GIN`、和 `RUM`。区别于常规的索引，这些索引带来的全文索引的特性。



## Using JSON and JSONB


最新的DBMS数据库都开始支持NoSQL的数据类型(例如MySQL、Postgres)。我们可以使用`json`或`jsonb`类型将不规则的、或不确定的json类型数据进行存储。

其中`jsonb`支持索引。

```shell script
test=# CREATE TABLE student_details(
de_id int,
s_id int REFERENCES students(s_id),
details json,
CONSTRAINT pk_d PRIMARY KEY(s_id, de_id)
);
test=# INSERT INTO student_details(de_id,s_id,details)
VALUES
(1, 1451,
'{ "merits": "none",
"flaws":
"immoderate ice cream consumption"
}'),
(2, 1432,
'{ "hobbies":
{ "guitarist":
{ "band": "Postgressors",
"guitars":["Strat","Telec"]
}
}
}'),
(3, 1556,
'{ "hobbies": "cosplay",
"merits":
{ "mother-of-five":
{ "Basil": "m", "Simon": "m", "Lucie": "f",
"Mark": "m", "Alex": "unknown"
}
}
}'),
(4, 1451,
'{ "status": "expelled"
}');
```

检查插入的数据：

```shell script
test=# SELECT s.name, sd.details
FROM student_details sd, students s
WHERE s.s_id = sd.s_id \gx

-[ RECORD 1 ]----------------------------------------
name    | Anna
details | { "merits": "none",                        +
        | "flaws":                                   +
        | "immoderate ice cream consumption"         +
        | }
-[ RECORD 2 ]----------------------------------------
name    | Victor
details | { "hobbies":                               +
        | { "guitarist":                             +
        | { "band": "Postgressors",                  +
        | "guitars":["Strat","Telec"]                +
        | }                                          +
        | }                                          +
        | }
-[ RECORD 3 ]----------------------------------------
name    | Nina
details | { "hobbies": "cosplay",                    +
        | "merits":                                  +
        | { "mother-of-five":                        +
        | { "Basil": "m", "Simon": "m", "Lucie": "f",+
        | "Mark": "m", "Alex": "unknown"             +
        | }                                          +
        | }                                          +
        | }
-[ RECORD 4 ]----------------------------------------
name    | Anna
details | { "status": "expelled"                     +
        | }

```

我们仅关心学生的`merits`内容，进行查询时，可以使用特殊操作符`->>`匹配层级：

```shell script
test=# SELECT s.name, sd.details
FROM student_details sd, students s
WHERE s.s_id = sd.s_id
AND sd.details ->> 'merits' IS NOT NULL \gx

-[ RECORD 1 ]----------------------------------------
name    | Anna
details | { "merits": "none",                        +
        | "flaws":                                   +
        | "immoderate ice cream consumption"         +
        | }
-[ RECORD 2 ]----------------------------------------
name    | Nina
details | { "hobbies": "cosplay",                    +
        | "merits":                                  +
        | { "mother-of-five":                        +
        | { "Basil": "m", "Simon": "m", "Lucie": "f",+
        | "Mark": "m", "Alex": "unknown"             +
        | }                                          +
        | }                                          +
        | }

```

关联到`merits`属性的学生有Anna 和Nina，但这结果不满足要求，因为Anna的merits是none的。我们改一下：

```shell script
test=# SELECT s.name, sd.details
       FROM student_details sd, students s
       WHERE s.s_id = sd.s_id
       AND sd.details ->> 'merits' IS NOT NULL
       AND sd.details ->> 'merits' != 'none';
```

这种方法不总是有效。例如我们要查找Victor会弹那种guitar：

```shell script
test=# SELECT sd.de_id, s.name, sd.details
FROM student_details sd, students s
WHERE s.s_id = sd.s_id
AND sd.details ->> 'guitars' IS NOT NULL \gx
```

该查询没有返回任何内容。因为对应的键值对藏在json层级里面。

```shell script
name    | Victor
details | { "hobbies": +
        | { "guitarist": +
        | { "band": "Postgressors", +
        | "guitars":["Strat","Telec"] +
        | } +
        | }
```

我们可以使用`#>`操作符进入到以`hobbies`开头的层级里面：

```shell script
test=# SELECT sd.de_id, s.name,
sd.details #> '{hobbies,guitarist,guitars}'
FROM student_details sd, students s
WHERE s.s_id = sd.s_id
AND sd.details #> '{hobbies,guitarist,guitars}'
IS NOT NULL \gx

-[ RECORD 1 ]---------------
de_id    | 2
name     | Victor
?column? | ["Strat","Telec"]

```

`json`类型有一个年轻的兄弟：`jsonb`。字母`b`表示数据是以二进制格式存储的。该数据可以被压缩，意味着能有更快速的搜索。`jsonb`的使用比`json`更多。

```shell script
test=# ALTER TABLE student_details
ADD details_b jsonb;
test=# UPDATE student_details
SET details_b = to_jsonb(details);
test=# SELECT de_id, details_b
FROM student_details \gx

-[ RECORD 1 ]-------------------------------------------------------------------------------------------------------------------------------
de_id     | 1
details_b | {"flaws": "immoderate ice cream consumption", "merits": "none"}
-[ RECORD 2 ]-------------------------------------------------------------------------------------------------------------------------------
de_id     | 2
details_b | {"hobbies": {"guitarist": {"band": "Postgressors", "guitars": ["Strat", "Telec"]}}}
-[ RECORD 3 ]-------------------------------------------------------------------------------------------------------------------------------
de_id     | 3
details_b | {"merits": {"mother-of-five": {"Alex": "unknown", "Mark": "m", "Basil": "m", "Lucie": "f", "Simon": "m"}}, "hobbies": "cosplay"}
-[ RECORD 4 ]-------------------------------------------------------------------------------------------------------------------------------
de_id     | 4
details_b | {"status": "expelled"}
```

我们可以发现查询的结果变了。Alex结果在Mark前面。这不是`jsonb`的缺点，这是它存储的特性。`jsonb`支持更多的操作。最常用的是“包含”操作符`@>`。它类似于`json`的`#>`。

例如，我们要找一个5个女孩子的妈妈：

```shell script
test=# SELECT s.name,
jsonb_pretty(sd.details_b) json
FROM student_details sd, students s
WHERE s.s_id = sd.s_id
AND sd.details_b @>
'{"merits":{"mother-of-five":{}}}' \gx

-[ RECORD 1 ]------------------------
name | Nina
json | {                             +
     |     "merits": {               +
     |         "mother-of-five": {   +
     |             "Alex": "unknown",+
     |             "Mark": "m",      +
     |             "Basil": "m",     +
     |             "Lucie": "f",     +
     |             "Simon": "m"      +
     |         }                     +
     |     },                        +
     |     "hobbies": "cosplay"      +
     | }


```

我们这里使用`jsonb_pretty()`函数格式化输出。

另外，可以使用`jsonb_each()`对查询结果的key-value分开展示：

```shell script
test=# SELECT s.name,
jsonb_each(sd.details_b)
FROM student_details sd, students s
WHERE s.s_id = sd.s_id
AND sd.details_b @>
'{"merits":{"mother-of-five":{}}}' \gx

-[ RECORD 1 ]-------------------------------------------------------------------------------------------------------------------------------
name       | Nina
jsonb_each | (merits,"{""mother-of-five"": {""Alex"": ""unknown"", ""Mark"": ""m"", ""Basil"": ""m"", ""Lucie"": ""f"", ""Simon"": ""m""}}")
-[ RECORD 2 ]-------------------------------------------------------------------------------------------------------------------------------
name       | Nina
jsonb_each | (hobbies,"""cosplay""")

```