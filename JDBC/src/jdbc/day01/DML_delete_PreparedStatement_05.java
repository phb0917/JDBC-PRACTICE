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
				int no = rs.getInt("no"); 	
				String name = rs.getString("name"); 	
				String msg = rs.getString("msg");  
				String writeday = rs.getString("writeday"); 
				
				sb.append(no);
				sb.append("\t"+name);
				sb.append("\t"+msg);
				sb.append("\t"+writeday+"\n");
			}// end of while(rs.next())-------------------------
			
			System.out.println(sb.toString());
			
			// ================================================ //
			
			String no = "";
			
			do {
				System.out.print("▷ 삭제할 글번호 : "); 
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
			
			
			if(rs.next()) { // 삭제해야할 글번호가 존재하는 경우 
				
				System.out.println("\n===== 삭제하기 전의 내용 =====");
				System.out.println("\n□ 글쓴이 : " + rs.getString("name"));
				System.out.println("\n□ 글내용 : " + rs.getString("msg"));
				
				String yn = "";
				do {
					System.out.print("▷ 정말로 삭제하시겠습니까?[Y/N] : ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						sql = " delete from tbl_memo "
							+ " where no = to_number(?) ";  
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, no);
						pstmt.executeUpdate(); // sql문을 실행
						
						System.out.println(">> 데이터 삭제 성공!! <<\n");
						
						// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
						
						sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
						    + " from tbl_memo "
						    + " order by no desc ";
						
						pstmt = conn.prepareStatement(sql);
						
						rs = pstmt.executeQuery(); // sql문 실행
												
						System.out.println("-".repeat(70));
						System.out.println("글번호\t글쓴이\t글내용\t작성일자");
						System.out.println("-".repeat(70));
						
						sb = new StringBuilder();
						
						while(rs.next()) {
							int num = rs.getInt("no"); 	
							String name = rs.getString("name"); 
							String msg = rs.getString("msg");  	
							String writeday = rs.getString("writeday"); 
							
							sb.append(num);
							sb.append("\t"+name);
							sb.append("\t"+msg);
							sb.append("\t"+writeday+"\n");
							
						}// end of while(rs.next())-------------------------
						
						System.out.println(sb.toString());
						
					}
					else if("n".equalsIgnoreCase(yn)){
						System.out.println(">> 데이터 삭제 취소!! <<");
					}
					else {
						System.out.println(">> Y 또는 N 만 입력하세요!! <<\n");
					}
					
				} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
				
			}
			
			else { // 삭제해야할 글번호가 DB에 존재하지 않는 경우 
				System.out.println(">>> 삭제할 글번호 "+ no +"는(은) 존재하지 않습니다. <<<");
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			
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
