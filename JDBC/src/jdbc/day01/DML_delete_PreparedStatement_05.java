package jdbc.day01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DML_delete_PreparedStatement_05 {

	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Scanner sc = new Scanner(System.in);
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.print("▷ 연결할 오라클 서버의 IP 주소 : ");
			String ip = sc.nextLine(); // 127.0.0.1  // 192.168.10.210
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe", "JDBC_USER", "seven");
		
			String sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					   + " from tbl_memo "
					   + " order by no desc ";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery(); // sql문 실행
			System.out.println("-".repeat(70));
			System.out.println("글번호\t글쓴이\t글내용\t작성일자");
			System.out.println("-".repeat(70));
		
			StringBuilder sb = new StringBuilder();
			
			while(rs.next()) {
			
				int no = rs.getInt("no");  // "no" 은 select 해온 컬럼명이다.	
				String name = rs.getString("name");  // "name" 은 select 해온 컬럼명이다.
				String msg = rs.getString("msg");  // "msg" 은 select 해온 컬럼명이다.
				String writeday = rs.getString("writeday");  // "writeday" 은 select 해온 컬럼명이다.
				
				sb.append(no);
				sb.append("\t"+name);
				sb.append("\t"+msg);
				sb.append("\t"+writeday+"\n");
				
			}// end of while(rs.next())-------------------------
			
			System.out.println(sb.toString());
			String no = "";
			
			do {
				System.out.print("▷ 수정할 글번호 : "); 
				no = sc.nextLine(); // 3
				                           // sdfsdfsdf 
				                           // 92384
				
				sql = " select name, msg "
					+ " from tbl_memo "
					+ " where no = to_number(?) ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, no);
				
				try {
					rs = pstmt.executeQuery(); // sql 문을 실행
					
					break;
				} catch(SQLException e) {
					if(e.getErrorCode() == 1722) {
						System.out.println(">> [경고] 수정할 글번호는 숫자만 가능합니다.!! << \n");
					}
				}
			
			} while(true);
			
			
			if(rs.next()) { // 수정해야할 글번호가 존재하는 경우 
				
				String before_name = rs.getString("name");
				String before_msg = rs.getString("msg");
				
				System.out.println("\n===== 수정하기 전의 내용 =====");
				System.out.println("\n□ 글쓴이 : " + before_name);
				System.out.println("\n□ 글내용 : " + before_msg);
				
				System.out.println("\n===== 글 수정하기 =====");
				System.out.print("▷ 글쓴이(변경하지 않으려면 그냥 엔터) : ");
				String new_name = sc.nextLine();
				
				if(new_name.isBlank()) { // 그냥 엔터 또는 공백 이라면 
					new_name = before_name;
				}
				
				System.out.print("▷ 글내용(변경하지 않으려면 그냥 엔터) : ");
				String new_msg = sc.nextLine();
				
				if(new_msg.isBlank()) { // 그냥 엔터 또는 공백 이라면 
					new_msg = before_msg;
				}
				
				String yn = "";
				do {
					System.out.print("▷ 정말로 수정하시겠습니까?[Y/N] : ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						sql = " update tbl_memo set name = ?, msg = ?, writeday = sysdate "
							+ " where no = to_number(?) ";  
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, new_name);
						pstmt.setString(2, new_msg);
						pstmt.setString(3, no);
						
						int n = pstmt.executeUpdate(); // sql문을 실행
						
						if(n==1) {
							// n==1 이라는 것은 update 구문이 성공되었다는 말이다.
							
							System.out.println(">> 데이터 수정 성공!! <<");
							
							System.out.println("\n=== 수정한 후 내용 ===");
							
							sql = " select name, msg "
								+ " from tbl_memo "
								+ " where no = to_number(?) ";
							
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, no);
							
							rs = pstmt.executeQuery(); // sql문 실행
							
							// !!!!!! 중요 !!!!!! //
							rs.next();
							
							String name = rs.getString("name");
							String msg = rs.getString("msg");
							
							System.out.println("□ 글쓴이 : " + name);
							System.out.println("□ 글내용 : " + msg);
						}
						
					}
					else if("n".equalsIgnoreCase(yn)){
						System.out.println(">> 데이터 수정 취소!! <<");
					}
					else {
						System.out.println(">> Y 또는 N 만 입력하세요!! <<\n");
					}
					
				} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
				
			}
			
			else { // 수정해야할 글번호가 DB에 존재하지 않는 경우 
				System.out.println(">>> 수정할 글번호 "+ no +"는(은) 존재하지 않습니다. <<<");
			}
			
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			// >>> 6. 사용하였던 자원을 반납하기 <<<
			// 반납의 순서는 생성순의 역순으로 한다.
			
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
		System.out.println("~~~ 프로그램 종료 ~~~");

	}// end main(String[] args)---------------------

}

