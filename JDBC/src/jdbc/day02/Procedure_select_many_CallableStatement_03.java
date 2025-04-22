package jdbc.day02;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
 	== 먼저 jdbc_day02.sql 파일을 열어서 tbl_class 테이블 및 tbl_student 테이블을 생성한다. 
    == 그리고 아래와 같이 오라클에서 프로시저를 생성해야 한다. ==
 
 
 */

public class Procedure_select_many_CallableStatement_03 {

	public static void main(String[] args) {
		
		Connection conn = null;
	      // Connection conn 은 데이터베이스 서버와 연결을 맺어주는 자바의 객체이다.
		CallableStatement cstmt = null;
	      // CallableStatement cstmt 은 Connection conn(연결한 DB 서버)에 존재하는 Procedure 를 호출해주는 객체(우편배달부)이다.
		
		ResultSet rs = null;
		
		try {
	         // >>> 1. 오라클 드라이버 로딩 <<< 
	         Class.forName("oracle.jdbc.driver.OracleDriver");
	         
	         // >>> 2. 어떤 오라클 서버에 연결을 할래? <<< 
	         conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");
		
		
	         // >>> 3. Connection conn 객체를 사용하여 prepareCall() 메소드를 호출함으로써
	         //        CallableStatement cstmt 객체를 생성한다.
	         //        즉, 우편배달부(택배기사) 객체 만들기
	         cstmt = conn.prepareCall("{call pcd_student_select_many(?,?)}");
	         /*
	            오라클 서버에 생성한 프로시저 pcd_student_select_many 의 
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
	         System.out.println("> 주소 : ");
	         String searchAddr = sc.nextLine(); 
	         
	         cstmt.setString(1, searchAddr); // 숫자 1 은 프로시저 파라미터중 첫번째 파라미터인 IN 모드의 ? 를 말한다.
				cstmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR); // 숫자 2는 프로시저 파라미터중  두번째 파라미터인 OUT 모드의 ? 를 말한다.
				
				

	         
	      // >>> 4. CallableStatement cstmt 객체를 사용하여 오라클의 프로시저 실행하기  <<< 
	         cstmt.execute(); // 오라클 서버에게 해당 프로시저를 실행해라는 것이다. 즉, 프로시저의 실행하기 
	      //  또는   
	      //   cstmt.executeUpdate(); // 오라클 서버에게 해당 프로시저를 실행해라는 것이다. 즉, 프로시저의 실행하기
	         rs  =  (ResultSet)cstmt.getObject(2);
	         // 여기서 숫자 2는 프로시저의 파라미터 순서를 말한다.
	         // 즉, 2번째 파라미터에 저장되어진 정보를 꺼내오는데 리턴타입이 Object 이다.
	         // 여기서 2번째 파라미터는 CURSOR로 되어진 OUT 모드이며 select 되어진 결과물이다.
	         // 그러므로 Object 타입으로 리턴된 것을 ResultSet 타입으로 casting(강제형변환)시켜야 한다. 
	         
	         StringBuilder sb =new StringBuilder(); 
	         int cnt = 0;
	         while(rs.next()) {
	        	 cnt ++ ;
	        	 if(cnt == 1) { 
	        	 sb.append("-".repeat(80)+"\n");
	             sb.append("학번 \t성명 \t연락처 \t\t주소 \t\t입력일자 \t\t학급번호 \t학급명 \t\t담임명\n");
	             sb.append("-".repeat(80)+"\n");
	        	 }
	        	 
	        	 sb.append(rs.getInt("stno") + "\t"
	        			 + rs.getString("name")+ "\t"
	        			 + rs.getString("tel")+ "\t"
	        			 + rs.getString("addr")+ "\t"
	        			 + rs.getString("registerdate")+ "\t"
	        			 + rs.getInt("classno")+ "\t"
	        			 + rs.getString("classname")+ "\t"
	        			 + rs.getString("teachername")+ "\n");
	        	 
	        	 
	         }//end while 
	         if(cnt == 0) {
	        	 System.out.println(">>> 검색하신 주소 " + searchAddr + "에 거주하는 학생은 없습니다 ");
	         }
	         else {
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
		        	  if(rs!= null) {
			                rs.close();
			                rs = null;
			           }
			             
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
