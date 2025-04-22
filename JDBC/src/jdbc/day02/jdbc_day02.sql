show user;

-- 1) 학급테이블 생성
create table tbl_class
(classno        number(3)
,classname      varchar2(100)
,teachername    varchar2(20)
,constraint PK_tbl_class_classno primary key(classno)
);
-- Table TBL_CLASS이(가) 생성되었습니다.

create sequence seq_classno
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_CLASSNO이(가) 생성되었습니다.

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍A', '김샘'); 

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍B', '이샘');

insert into tbl_class(classno, classname, teachername) 
values(seq_classno.nextval, '자바웹프로그래밍C', '서샘');

commit;

select *
from tbl_class;

-- 2) 학생테이블 생성 
drop table tbl_student purge;

create table tbl_student
(stno           number(8)               -- 학번
,name           Nvarchar2(20) not null   -- 학생명
,tel            Nvarchar2(15) not null   -- 연락처
,addr           Nvarchar2(100)           -- 주소
,registerdate   date default sysdate    -- 입학일자
,fk_classno     number(3) not null      -- 학급번호
,constraint PK_tbl_student_stno primary key(stno)
,constraint FK_tbl_student_classno foreign key(fk_classno) references tbl_class(classno)
);    
-- Table TBL_STUDENT이(가) 생성되었습니다.

-- 학번에 사용할 시퀀스 생성
drop sequence seq_stno;

create sequence seq_stno
start with 9001
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_STNO이(가) 생성되었습니다.

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '이순신', '02-234-5678', '서울시 강남구 역삼동', default, 1);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '김유신', '031-345-8876', '경기도 군포시', default, 2);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '안중근', '02-567-1234', '서울시 강서구 화곡동', default, 2);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '엄정화', '032-777-7878', '인천시 송도구', default, 3);

insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno)
values(seq_stno.nextval, '박순신', '02-888-9999', '서울시 마포구 서교동', default, 3);

commit;

select *
from tbl_student;

insert into tbl_student(stno, name, tel, addr, fk_classno)
values('구천육', '강감찬', '010-3456-7890', '서울시 관악구 낙성대', 3);
/*
    오류 보고 -
    SQL 오류: ORA-01722: 수치가 부적합합니다
*/

insert into tbl_student(stno, name, tel, addr, fk_classno)
values(9001, '강감찬', '010-3456-7890', '서울시 관악구 낙성대', 3);
/*
    오류 보고 -
    ORA-00001: 무결성 제약 조건(JDBC_USER.PK_TBL_STUDENT_STNO)에 위배됩니다
*/

insert into tbl_student(stno, name, tel, addr, fk_classno)
values(9006, '강감찬', '010-3456-7890', '서울시 관악구 낙성대', 4);
/*
    오류 보고 -
    ORA-02291: 무결성 제약조건(JDBC_USER.FK_TBL_STUDENT_CLASSNO)이 위배되었습니다- 부모 키가 없습니다
*/

insert into tbl_student(stno, name, tel, addr, fk_classno)
values(9006, '강감찬', '010-3456-7890789078907890', '서울시 관악구 낙성대', 3);
/*
    오류 보고 -
    SQL 오류: ORA-12899: "JDBC_USER"."TBL_STUDENT"."TEL" 열에 대한 값이 너무 큼(실제: 25, 최대값: 15)
*/

insert into tbl_student(stno, name, tel, addr, fk_classno)
values(9006, null, '010-3456-7890', '서울시 관악구 낙성대', 3);
/*
    오류 보고 -
    SQL 오류: ORA-01400: NULL을 ("JDBC_USER"."TBL_STUDENT"."NAME") 안에 삽입할 수 없습니다
*/

insert into tbl_student(stno, name, tel, addr, fk_classno)
values(9006, '', '010-3456-7890', '서울시 관악구 낙성대', 3);
/*
    오류 보고 -
    SQL 오류: ORA-01400: NULL을 ("JDBC_USER"."TBL_STUDENT"."NAME") 안에 삽입할 수 없습니다
*/
select stno, name , tel, addr , to_char(registerdate, 'yyyy-mm-dd') as registerdate , classno,classname ,teachername
from tbl_student s join tbl_class c
on s.fk_classno = c.classno
where stno = 9005;



create or replace procedure pcd_student_select_one
(p_stno         in tbl_student.stno%type
,o_stno         out tbl_student.stno%type
,o_name         out tbl_student.name%type
,o_tel          out tbl_student.tel%type
,o_addr         out tbl_student.addr%type
,o_registerdate out VARCHAR2
,o_classno      out tbl_class.classno%type
,o_classname    out tbl_class.calssname%type
,o_teachername  out tbl_class.teachername%type)

is
v_cnt number(1);


begin 
        select count(*) into v_cnt
        from tbl_student 
        where stno = p_stno;
        
        if v_cnt = 0 then 
            o_stno := 0;
            o_name := null;
            o_tel := null;
            o_addr  := null;
            o_registerdate:= null;
            o_classno := null;
            o_classname  := null;
            o_teachername := null;
        else 
            select stno, name , tel, addr , to_char(registerdate, 'yyyy-mm-dd') as registerdate , classno,classname ,teachername
           into o_stno,o_name,o_tel,o_addr,o_registerdate, o_classno,o_classname, o_teachername
            from tbl_student s join tbl_class c
            on s.fk_classno = c.classno
            where stno = p_stno;

           end if 
            
        

end pcd_student_select_one;

create or replace procedure pcd_student_select_one
(p_stno         IN   tbl_student.stno%type
,o_stno         OUT  tbl_student.stno%type
,o_name         OUT  tbl_student.name%type
,o_tel          OUT  tbl_student.tel%type
,o_addr         OUT  tbl_student.addr%type
,o_registerdate OUT  varchar2
,o_classno      OUT  tbl_class.classno%type
,o_classname    OUT  tbl_class.classname%type
,o_teachername  OUT  tbl_class.teachername%type
)
is
   v_cnt  number(1);
begin
     select count(*) into v_cnt
     from tbl_student
     where stno = p_stno;
     
     if v_cnt = 0 then
        o_stno := 0;
        o_name := null;
        o_tel := null;
        o_addr := null;
        o_registerdate := null;
        o_classno := null;
        o_classname := null;
        o_teachername := null;
     else
         SELECT stno, name, tel, addr, to_char(registerdate, 'yyyy-mm-dd') AS registerdate 
              , classno, classname, teachername
              INTO
                o_stno, o_name, o_tel, o_addr, o_registerdate, o_classno, o_classname, o_teachername
         FROM tbl_student S JOIN tbl_class C
         ON S.fk_classno = C.classno
         WHERE stno = p_stno;
         
     end if;

end pcd_student_select_one;

select stno, name , tel, addr , to_char(registerdate, 'yyyy-mm-dd') as registerdate , classno,classname ,teachername
from    (select *
        from tbl_student 
        where addr like '%'|| '서울' ||'%')s join tbl_class c
on s.fk_classno = c.classno;





create or replace procedure pcd_student_select_many
(p_addr in varchar2
,o_data out SYS_REFCURSOR

)
is
begin
    open o_date for 
    select stno, name , tel, addr , to_char(registerdate, 'yyyy-mm-dd') as registerdate , classno,classname ,teachername
    from    (select *
             from tbl_student 
             where addr like '%'|| p_addr ||'%') s join tbl_class c
            on s.fk_classno = c.classno;

end pcd_student_select_many;

create or replace procedure pcd_student_select_many
	(p_addr  IN   varchar2
	,o_data  OUT  SYS_REFCURSOR
	)
	is
	begin
	    OPEN o_data FOR
	    SELECT stno, name, tel, addr, to_char(registerdate, 'yyyy-mm-dd') AS registerdate 
	         , classno, classname, teachername
	    FROM ( select * 
	           from tbl_student
	           where addr like '%'|| p_addr ||'%' ) S JOIN tbl_class C
	    ON S.fk_classno = C.classno;
	    
	end pcd_student_select_many;