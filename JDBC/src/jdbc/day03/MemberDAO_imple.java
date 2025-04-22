package jdbc.day03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//DAO(Database Access Object)란?
//-- Database 에 연결하여 DB와 관련된 업무(SQL)를 실행시켜주는 객체이다

public class MemberDAO_imple implements MemberDAO {

	//field
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	
	
	//method
	
	// === 자원반납을 해주는 메소드 ===
	private void close() {
		try {
			if(rs != null) {rs.close(); rs = null;}
			
			if(pstmt != null) {pstmt.close(); pstmt = null;}
			
			if(conn != null) {conn.close(); conn = null;}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//end void 
	
	
	
	@Override
	public int memberRegister(MemberDTO member) {
		
		int result = 0;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven"); 
		
			String sql = " insert into tbl_member(userseq, userid, passwd, name, mobile) "
					+ " values(userseq.nextval, ?, ?, ?, ?) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, member.getPasswd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMobile());
			
			result = pstmt.executeUpdate(); // sql 문 실행 
			
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close() ;
			
		}
		return result;
	}//end public int memberRegister(MemberDTO member)

}
