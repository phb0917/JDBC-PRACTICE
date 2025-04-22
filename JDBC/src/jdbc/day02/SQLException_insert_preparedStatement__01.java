package jdbc.day02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SQLException_insert_preparedStatement__01 {

	public static void main(String[] args) {
			
		Connection conn = null;
		
		PreparedStatement pstmt = null;
		
		ResultSet rs =null;
		
		Scanner sc = new Scanner(System.in);
		
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");
			
			// ============================================================================================
			String stno = "";
			String name = "";
			String tel  = "";
			String addr = "";
			String fk_classno = "";
			
			do {
				System.out.print("> 학번 : ");
				stno =sc.nextLine();
				
				try {
					Integer.parseInt(stno);
					 // 입력받은 학번 중복검사하기 시작 
					String sql =" select * "
							+ " from tbl_student "
							+ " where stno = to_number(?) ";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, stno);
					rs = pstmt.executeQuery();
					
					if (rs.next()) {
						System.out.println(">> 입력하신 학번 " + stno + "는 이미 사용중입니다 <<\n");
					}
					else {
						System.out.println(">> 입력하신 학번 " + stno + "는 사용가능 합니다 ");
						break;
					}
					
					// 입력받은 학번 중복검사하기 끝 
				} catch (NumberFormatException e) {
				     System.out.println(">> [경고] 학번은 숫자로만 입력하세요 <<< \n");
				}
				
			 } while(true);
			

			do {
				System.out.print("> 성명 : ");
			name =sc.nextLine();
			if( name.isBlank()) { 
				System.out.println(">> [경고] 성명은 필수 입력 사항입니다 ");
			}
			else if(name.length() > 20 ) {
				System.out.println(">> [경고] 성명은 최대 20글자 이내이여야합니다 ");
			}
			else 
				break;
			
			}while(true);
			
			do {
				System.out.print("> 연락처 : ");
			tel =sc.nextLine();
			if( tel.isBlank()) { 
				System.out.println(">> [경고] 연락처는 필수 입력 사항입니다 ");
			}
			else if(tel.length() > 15 ) {
				System.out.println(">> [경고] 연락처는 최대 15글자 이내이여야합니다 ");
			}
			else 
				break;
			
			}while(true);
			
			do {
				System.out.print("> 주소 : ");
			addr =sc.nextLine();
			if (!addr.isBlank() ) {
				if(addr.length() > 100) {
					System.out.println(">> [경고] 주소를 입력할 경우엔 최대 100글자 이내여야합니다 << \n");
					continue;
				}
			}
			break;
			
			}while(true);
			
			do {
				System.out.print("> 학급번호 : ");
			fk_classno =sc.nextLine();
			try {
				Integer.parseInt(fk_classno);
				String sql = " insert into tbl_student(stno, name, tel, addr, registerdate, fk_classno) "
						+ " values(to_number(?), ?, ?,  ? , ?, to_number(?)) ";
				
			pstmt =	conn.prepareStatement(sql);
			pstmt.setString(1, stno);
			pstmt.setString(2, name);
			pstmt.setString(3, tel);
			pstmt.setString(4, addr);
			pstmt.setString(5, fk_classno);
			
			int n = pstmt.executeUpdate();
			
			if(n==1) {
				System.out.println(">> 학생데이터 입력 성공 <<");
				// 방금 입력한 학생 데이터 보여주기 ( 조회) 
				sql = " select stno, name , tel, addr , to_char(registerdate, 'yyyy-mm-dd') as registerdate , classno,classname ,teachername "
						+ " from tbl_student s join tbl_class  "
						+ " on s.fk_classno = c.classno "
						+ " where stno = to_number(?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, stno);
				
				rs = pstmt.executeQuery();
				
				rs.next();
				
				
				
				System.out.println("\n===== 입력한 결과 조회 =====");
                System.out.println("▷ 학번 : "+ rs.getInt("stno"));
                System.out.println("▷ 학생명 : "+rs.getString("name"));
                System.out.println("▷ 연락처 : "+rs.getString("tel") );
                System.out.println("▷ 주소 : " +rs.getString("addr"));
                System.out.println("▷ 입력일자 : " +rs.getString("registerdate"));
                System.out.println("▷ 학급번호 : " + rs.getInt("fk_classno"));
                System.out.println("▷ 학급명 : " +rs.getString("classname"));
                System.out.println("▷ 담임교사명 : " +rs.getString("teachername"));
				
				
				break;
				
			}
			
			}catch (NumberFormatException e) {
				System.out.println(">>[경고] 학급번호는 숫자로만 입력하십시오 << \n");
			}catch(SQLException e ) {
				if(e.getErrorCode() == 2291) {
					 System.out.println(">>[경고] 입력하신 학급번호 " + fk_classno + "는 학급테이블에 존재 하지 않습니다 \n");
					 System.out.println(">> 입력가능한 학급번호는 아래와 같습니다 ");
					 /*
					    -----------------------------------------------------
					    학급번호 학급명 
					    ------------------------------------------------------
					    1	자바웹프로그래밍A
						2	자바웹프로그래밍B
						3	자바웹프로그래밍C
					  */
					 StringBuilder sb =new StringBuilder();
					 sb.append("-".repeat(25)+ "\n");
					 sb.append("학급번호\t학급명\n");
					 sb.append("-".repeat(25)+ "\n");
					 String sql = " select classno, calssname "
					 		+ " from tbl_clss "
					 		+ " order by 1 ";
					pstmt = conn.prepareStatement(sql);
					rs =pstmt.executeQuery(); 
					while (rs.next()) {
					 sb.append( rs.getInt("classno") + "\t" + rs.getString("classname") + "\n");	
					}//end while 
					System.out.println(sb.toString());
				
				}
			}
			 //--------------------------------------------------------------------
			}while(true);
			
			
				
			
			
		}catch ( Exception e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			
		}finally {
			try {
	            if(rs != null) {
	               rs.close();
	               rs = null;
	            }
	            
	            if(pstmt != null) {
	               pstmt.close();
	               pstmt = null;
	            }
	            
	            if(conn != null) {
	               conn.close();
	               conn = null;
	            }
	         } catch (SQLException e) {
	            e.printStackTrace();
	         }   
		}

		sc.close();
		System.out.println("프로그램 종료");
		
	}//end void 

}
