package jdbc.day02;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/*
 	== 먼저 jdbc_day02.sql 파일을 열어서 tbl_class 테이블 및 tbl_student 테이블을 생성한다. 
    == 그리고 아래와 같이 오라클에서 프로시저를 생성해야 한다. ==
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
 
 */

public class Procedure_select_one_CallableStatement_02 {

	public static void main(String[] args) {
		
		Connection conn = null;
	      // Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바의 객체이다.
		CallableStatement cstmt = null;
	      // CallableStatement cstmt 은 Connection conn(연결한 DB 서버)에 존재하는 Procedure 를 호출해주는 객체(우편배달부)이다.
		
		try {
	         // >>> 1. 오라클 드라이버 로딩 <<< 
	         Class.forName("oracle.jdbc.driver.OracleDriver");
	         
	         // >>> 2. 어떤 오라클 서버에 연결을 할래? <<< 
	         conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");
		
		
	         // >>> 3. Connection conn 객체를 사용하여 prepareCall() 메소드를 호출함으로써
	         //        CallableStatement cstmt 객체를 생성한다.
	         //        즉, 우편배달부(택배기사) 객체 만들기
	         cstmt = conn.prepareCall("{call pcd_student_select_one(?,?,?,?,?,?,?,?,?)}");
	         /*
	            오라클 서버에 생성한 프로시저 pcd_student_select_one 의 
	            매개변수 갯수가 9개 이므로 ? 를 9개 준다.
	            
	            다음으로 오라클의 프로시저를 수행( executeUpdate() 또는 execute() ) 하기에 앞서서  
	            반드시 해야할 일은 IN mode 로 되어진 파라미터에 값을 넣어주고,
	            OUT mode 로 설정된 곳에 그 결과값을 받아오도록 아래와 같이 설정해야 한다.
	            
	            프로시저의 IN mode 로 되어진 파라미터에 값을 넣어줄때는 
	            cstmt.setXXX() 메소드를 사용한다. 
	            
	            프로시저의 OUT mode 로 되어진 파라미터에 저장되어진 값을 자바에서 꺼내 오려면 
	            cstmt.registerOutParameter() 메소드를 사용한다.
	            
	            ※ registerOutParameter() 메소드는?
	            ==> public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException 
	                : 프로시저를 실행하여 받아온 값을 JDBC타입(자바에서 인식하는 타입)으로 등록시켜주는 메소드이다.
	             
	             자바에서는 오라클의 OUT mode 변수에 오라클 데이터타입으로 저장되어 있는 값들을 읽어와서
	             JDBC타입(자바에서 인식하는 타입)으로 변경하는 과정을 거쳐야만 한다.
	             대표적인 sqlType을 알아보면 NULL, FLOAT, INTEGER, VARCHAR, DATE, CLOB, BLOB 등이 있다.
	         */
	         Scanner sc = new Scanner(System.in);
	         System.out.println("> 학번 : ");
	         String stno = sc.nextLine();
	         
	         cstmt.setInt(1, Integer.parseInt(stno)); // 숫자 1 은 프로시저 파라미터중 첫번째 파라미터인 IN 모드의 ? 를 말한다.
				cstmt.registerOutParameter(2, java.sql.Types.INTEGER); // 숫자 2는 프로시저 파라미터중  두번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(3, java.sql.Types.VARCHAR); // 숫자 3는 프로시저 파라미터중  세번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(4, java.sql.Types.VARCHAR); // 숫자 4는 프로시저 파라미터중  네번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(5, java.sql.Types.VARCHAR); // 숫자 5는 프로시저 파라미터중  다섯번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(6, java.sql.Types.VARCHAR); // 숫자 6는 프로시저 파라미터중  여섯번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(7, java.sql.Types.INTEGER); // 숫자 7는 프로시저 파라미터중  일곱번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(8, java.sql.Types.VARCHAR); // 숫자 8는 프로시저 파라미터중  여덟번째 파라미터인 OUT 모드의 ? 를 말한다.
				cstmt.registerOutParameter(9, java.sql.Types.VARCHAR); // 숫자 9는 프로시저 파라미터중  아홉번째 파라미터인 OUT 모드의 ? 를 말한다. 
				

	         
	      // >>> 4. CallableStatement cstmt 객체를 사용하여 오라클의 프로시저 실행하기  <<< 
	         cstmt.execute(); // 오라클 서버에게 해당 프로시저를 실행해라는 것이다. 즉, 프로시저의 실행하기 
	      //  또는   
	      //   cstmt.executeUpdate(); // 오라클 서버에게 해당 프로시저를 실행해라는 것이다. 즉, 프로시저의 실행하기
	         
	         if(cstmt.getString(3) == null) {
	        	 System.out.println(">>> 입력하신 학번 " + stno + "은 존재하지 않습니다 ");
	         }
	         else {
	        	 StringBuilder sb = new StringBuilder();
	        	 
	        	 sb.append("-".repeat(30) + "\n");
	        	 sb.append("> 학번 : " + cstmt.getInt(2) + "\n");
	        	 sb.append("> 학생명 : " + cstmt.getString(3) + "\n" );
	        	 sb.append("> 연락처 : " + cstmt.getString(4) + "\n");
	        	 sb.append("> 주소 : " +cstmt.getString(5) + "\n");
	        	 sb.append("> 입력일자 : " +cstmt.getString(6) + "\n");
	        	 sb.append("> 학급번호 : " +cstmt.getInt(7) + "\n");
	        	 sb.append("> 학급명 : " +cstmt.getString(8) + "\n");
	        	 sb.append("> 선생님이름 : " +cstmt.getString(9) + "\n");
	        	 sb.append("-".repeat(30) + "\n");
	        	// 위의 cstmt.getInt(2) 에서 숫자 2는 프로시저 파라미터중 두번째 파라미터인 OUT 모드의 결과값을 말한다.
	             // 나머지 3 부터 9 까지도 동일하다. 
	        	 System.out.println(sb.toString());
	         }
	         sc.close();
	         
	         
			} catch (ClassNotFoundException e) {
		         System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		      } catch(SQLException e) {
		         e.printStackTrace();
		      }catch (Exception e) {
		    	  e.printStackTrace();
		      }finally {
		    	// >>> 5. 사용하였던 자원을 반납하기 <<< 
		          // 반납의 순서는 생성순의 역순으로 한다.
		          
		          try {
		             if(cstmt != null) {
		                cstmt.close();
		                cstmt = null;
		             }
		             
		             if(conn != null) {
		                conn.close();
		                conn = null;
		             }
		          } catch (SQLException e) {
		             e.printStackTrace();
		          }   
		      }
		
		System.out.println("\n 프로그램종료");
		
	}//end void 

}
