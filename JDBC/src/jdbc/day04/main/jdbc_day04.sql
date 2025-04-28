show user;
-- USER이(가) "JDBC_USER"입니다.


--- **** 회원 테이블 생성하기 **** ---
create table tbl_member
(userseq      number         not null    -- 회원번호   
,userid       varchar2(30)   not null    -- 회원아이디
,passwd       varchar2(30)   not null    -- 회원비밀번호
,name         Nvarchar2(20)  not null    -- 회원명
,mobile       varchar2(20)               -- 연락처
,point        number(10) default 0       -- 포인트
,registerday  date default sysdate       -- 가입일자
,status       number(1) default 1        -- status 컬럼의 값이 1 이면 가입된 상태, 0 이면 탈퇴
,constraint  PK_tbl_member_userseq primary key(userseq)
,constraint  UQ_tbl_member_userid unique(userid)
,constraint  CK_tbl_member_status check( status in (0,1) )
);
-- Table TBL_MEMBER이(가) 생성되었습니다.

create sequence userseq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence USERSEQ이(가) 생성되었습니다.


--- **** 게시글 테이블 생성하기 **** ---
create table tbl_board
(boardno       number         not null        -- 글번호
,fk_userid     varchar2(30)   not null        -- 작성자아이디
,subject       Nvarchar2(100) not null        -- 글제목
,contents      Nvarchar2(200) not null        -- 글내용
,writeday      date default sysdate not null  -- 작성일자 
,viewcount     number default 0 not null      -- 조회수 
,boardpasswd   varchar2(20) not null          -- 글암호
,constraint PK_tbl_board_boardno primary key(boardno)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
);
-- Table TBL_BOARD이(가) 생성되었습니다.


-- *** 게시글 테이블의 글번호 컬럼에 사용되어질 시퀀스 생성하기 *** --
create sequence seq_board
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_BOARD이(가) 생성되었습니다.


---- *** 댓글 테이블 생성하기 *** ----
create table tbl_comment
(commentno   number         not null        -- 댓글번호
,fk_boardno  number         not null        -- 원글의 글번호 
,fk_userid   varchar2(30)   not null        -- 작성자 아이디
,contents    Nvarchar2(100) not null        -- 댓글내용
,writeday    date default sysdate not null  -- 작성일자
,constraint PK_tbl_comment_commentno primary key(commentno)
,constraint FK_tbl_comment_fk_boardno foreign key(fk_boardno) references tbl_board(boardno) on delete cascade 
,constraint FK_tbl_comment_fk_userid foreign key(fk_userid) references tbl_member(userid) 
);
-- Table TBL_COMMENT이(가) 생성되었습니다.


-- *** 댓글 테이블의 댓글번호 컬럼에 사용되어질 시퀀스 생성하기 *** --
create sequence seq_comment
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_COMMENT이(가) 생성되었습니다.


---- !!!! 테이블을 생성한 이후에 테이블명과 컬럼명에 대해 주석문을 꼭 달아두도록 합시다. !!!! ----

--- *** 테이블명에 달려진 주석문 조회하기 *** ---
show user;
-- USER이(가) "HR"입니다.

select *
from user_tab_comments;

select comments
from user_tab_comments
where table_name = 'EMPLOYEES';


--- *** 특정 테이블의 컬럼명에 달려진 주석문 조회하기 *** ---
select column_name, comments
from user_col_comments
where table_name = 'EMPLOYEES';


-- **** tbl_member 테이블에 대한 주석문 달기 **** 
show user;
-- USER이(가) "JDBC_USER"입니다.

comment on table tbl_member
is '회원정보가 들어있는 테이블';
-- Comment이(가) 생성되었습니다.

comment on table tbl_board
is '게시글 테이블';
-- Comment이(가) 생성되었습니다.

comment on table tbl_comment
is '댓글 테이블';
-- Comment이(가) 생성되었습니다.

select *
from user_tab_comments;

select comments
from user_tab_comments
where table_name = 'TBL_MEMBER';


-- **** tbl_member 테이블의 컬럼에 대한 주석문 달기 **** 
desc tbl_member;

comment on column tbl_member.USERSEQ 
is '회원번호 Primary Key';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.USERID      
is '회원아이디 Unique 제약';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.PASSWD      
is '비밀번호';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.NAME        
is '회원명';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.MOBILE               
is '연락처(휴대폰번호)';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.POINT                
is '포인트 기본은 0, 최대 30';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.REGISTERDAY          
is '가입일자, 기본은 sysdate';
-- Comment이(가) 생성되었습니다.

comment on column tbl_member.STATUS
is '가입상태, 기본은 1, 1은 가입중 0은 탈퇴, 체크제약(0,1)';
-- Comment이(가) 생성되었습니다.

select column_name, comments
from user_col_comments
where table_name = 'TBL_MEMBER';

select column_name, comments
from user_col_comments
where table_name = 'TBL_BOARD';


select *
from tbl_member
order by userseq asc;


-----------------------------------------------------------------------------------------
/*
   Transaction(트랜잭션) 처리 실습을 위해서
   tbl_member 테이블의 point 컬럼의 값은 최대 30을 넘지 못하도록 check 제약을 걸도록 하겠습니다.
*/
-----------------------------------------------------------------------------------------
alter table tbl_member
add constraint CK_tbl_member_point check( point between 0 and 30 );
-- Table TBL_MEMBER이(가) 변경되었습니다.

select *
from tbl_member
order by userseq desc;

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.

update tbl_member set point = point + 10
where userid = 'leess';
-- 1 행 이(가) 업데이트되었습니다.
/*
  오류 보고 -
  ORA-02290: 체크 제약조건(JDBC_USER.CK_TBL_MEMBER_POINT)이 위배되었습니다
*/

select *
from tbl_member
order by userseq desc;

rollback;
-- 롤백 완료.

select *
from user_constraints
where table_name = 'TBL_MEMBER';

select *
from user_cons_columns
where table_name = 'TBL_MEMBER';

select A.constraint_name, constraint_type, search_condition, status, column_name
from user_constraints A JOIN user_cons_columns B
ON A.constraint_name = B.constraint_name
WHERE A.table_name = 'TBL_MEMBER';

select *
from tbl_board
order by boardno desc;


---- *** 글목록 보기 *** -----
select *
from tbl_board;

select *
from tbl_member;

SELECT boardno, subject, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, viewcount
FROM tbl_board B JOIN tbl_member M
ON B.fk_userid = M.userid
ORDER BY boardno DESC;


SELECT boardno, 
       case when length(subject) > 12 then substr(subject, 1, 10) || '..' else subject end AS subject, 
       name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, viewcount
FROM tbl_board B JOIN tbl_member M
ON B.fk_userid = M.userid
ORDER BY boardno DESC;


SELECT subject, contents
FROM tbl_board
WHERE boardno = to_number('6');


SELECT subject, contents, name, viewcount, fk_userid
FROM 
( SELECT subject, contents, viewcount, fk_userid
  FROM tbl_board
  WHERE boardno = to_number('6') ) B JOIN tbl_member M
ON B.fk_userid = M.userid;  


select *
from tbl_board
order by boardno desc;

select *
from tbl_comment
order by commentno desc;

select *
from tbl_member;


SELECT boardno 
     , case when length(subject) > 12 then substr(subject, 1, 10) || '..' else subject end AS subject  
	 , name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, viewcount   
FROM tbl_board B JOIN tbl_member M 
ON B.fk_userid = M.userid 
ORDER BY boardno DESC;

SELECT fk_boardno, COUNT(*) AS cmtcnt
FROM tbl_comment
GROUP BY fk_boardno;


SELECT boardno
     , CASE WHEN cmtcnt IS null THEN subject ELSE subject || '['|| cmtcnt ||']' END AS subject
     , name, writeday, viewcount
FROM
(
    SELECT boardno 
         , case when length(subject) > 12 then substr(subject, 1, 10) || '..' else subject end AS subject  
         , name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, viewcount   
    FROM tbl_board B JOIN tbl_member M 
    ON B.fk_userid = M.userid 
) V1
LEFT JOIN
(
    SELECT fk_boardno, COUNT(*) AS cmtcnt
    FROM tbl_comment
    GROUP BY fk_boardno
) V2
ON V1.boardno = V2.fk_boardno
ORDER BY boardno DESC;


--- *** 아래와 같이 하면 안된다. !!!! *** ---
SELECT B.contents AS 원글내용
     , C.contents AS 댓글내용
FROM ( select *
       from tbl_board
       where boardno = 3 ) B JOIN tbl_comment C
ON B.boardno = C.fk_boardno;


select contents AS 원글내용
from tbl_board
where boardno = 3;

select contents AS 댓글내용
from tbl_comment
where fk_boardno = 3;

select contents AS 댓글내용
from tbl_comment
where fk_boardno = 1;


-- 댓글내용, 작성자명, 작성일자 
SELECT contents, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
FROM 
(SELECT *
 FROM tbl_comment
 WHERE fk_boardno = 3) C 
JOIN tbl_member M 
ON C.fk_userid = M.userid
ORDER BY commentno DESC;



------------------------------------------------------ 
show user;
-- USER이(가) "HR"입니다.

select department_id AS 부서번호
     , count(*) AS 인원수
from employees
group by rollup(department_id);

-----------------------------------------------------------------
전체   10   20   30   40    50   60  70   80   90  100  110  부서없음
-----------------------------------------------------------------
107   1    2     6    1    45   5    1    34   3    6    2     1

desc employees;

SELECT COUNT(NVL(department_id, -9999)) AS 전체인원수
     , SUM( DECODE(NVL(department_id, -9999), 10, 1, 0) ) AS "10"
     , SUM( DECODE(NVL(department_id, -9999), 20, 1, 0) ) AS "20"
     , SUM( DECODE(NVL(department_id, -9999), 30, 1, 0) ) AS "30"
     , SUM( DECODE(NVL(department_id, -9999), 40, 1, 0) ) AS "40"
     , SUM( DECODE(NVL(department_id, -9999), 50, 1, 0) ) AS "50"
     , SUM( DECODE(NVL(department_id, -9999), 60, 1, 0) ) AS "60"
     , SUM( DECODE(NVL(department_id, -9999), 70, 1, 0) ) AS "70"
     , SUM( DECODE(NVL(department_id, -9999), 80, 1, 0) ) AS "80"
     , SUM( DECODE(NVL(department_id, -9999), 90, 1, 0) ) AS "90"
     , SUM( DECODE(NVL(department_id, -9999), 100, 1, 0) ) AS "100"
     , SUM( DECODE(NVL(department_id, -9999), 110, 1, 0) ) AS "110"
     , SUM( DECODE(NVL(department_id, -9999), -9999, 1, 0) ) AS "부서없음"
FROM employees;


--- 최근 1주일간 일자별 게시글 작성건수 ---
show user;
-- USER이(가) "JDBC_USER"입니다.
-------------------------------------------------------------------------------------------
 전체   2025-04-22   2025-04-23  2025-04-24  2025-04-25  2025-04-26  2025-04-27  2025-04-28
-------------------------------------------------------------------------------------------
  4        0             0           3           1           0           0           0

SELECT *
FROM tbl_board;

SELECT boardno, subject, writeday, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss')
     , sysdate - writeday
     , to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd')
FROM tbl_board;


SELECT COUNT(*) AS 전체   -- 전체
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 6, 1, 0) ) AS PREVIOUS6 -- 2025-04-22
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 5, 1, 0) ) AS PREVIOUS5 -- 2025-04-23
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 4, 1, 0) ) AS PREVIOUS4 -- 2025-04-24
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 3, 1, 0) ) AS PREVIOUS3 -- 2025-04-25
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 2, 1, 0) ) AS PREVIOUS2 -- 2025-04-26
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 1, 1, 0) ) AS PREVIOUS1 -- 2025-04-27  
     , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 0, 1, 0) ) AS TODAY -- 2025-04-28
FROM tbl_board
WHERE to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd') < 7; -- 작성일자(2025-04-28)가 오늘 포함 최근 1주일간




--- 이번달 일자별 게시글 작성건수 ---
SELECT *
FROM tbl_board;

/*
  ------------------------
   작성일자      작성건수
  ------------------------ 
  2025-04-24      3
  2025-04-25      1
*/

SELECT boardno, writeday, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss')
FROM tbl_board;


SELECT DECODE(grouping(to_char(writeday, 'yyyy-mm-dd')), 0, to_char(writeday, 'yyyy-mm-dd'), '전체') AS 작성일자
     , count(*) AS 작성건수
     , TRUNC( count(*) / ( select count(*)
                           from tbl_board
                           where to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm') ) * 100 ) AS "백분율(%)" 
FROM tbl_board
WHERE to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm')
GROUP BY ROLLUP(to_char(writeday, 'yyyy-mm-dd'));




















