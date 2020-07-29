create table course
(
    c_no  text primary key,
    title text,
    hours integer
);

insert into course(c_no, title, hours)
values ('CS301', 'Databases', 30),
       ('CS305', 'Networks', 60);

create table student
(
    s_id       integer primary key,
    name       text,
    start_year integer
);

insert into student(s_id, name, start_year)
values (1451, 'Anna', 2014),
       (1432, 'Victor', 2014),
       (1556, 'Nina', 2015);

create table exam
(
    s_id  integer references student (s_id),
    c_no  text references course (c_no),
    score integer,
    constraint pk primary key (s_id, c_no)
);

insert into exam(s_id, c_no, score)
values (1451, 'CS301', 5),
       (1556, 'CS301', 5),
       (1451, 'CS305', 5),
       (1432, 'CS305', 4);