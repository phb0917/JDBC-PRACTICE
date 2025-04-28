package jdbc.day03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// DAO(Database Access Object)란?
// -- Database 에 연결하여 DB와 관련된 업무(SQL)를 실행시켜주는 객체이다. 

public class MemberDAO_imple implements MemberDAO {

	// field
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	
	// method 
	
	// ==== 자원반납을 해주는 메소드 ==== //
	private void close() {
		try {
			if(rs != null)    {rs.close();    rs = null;}
			if(pstmt != null) {pstmt.close(); pstmt = null;}
			if(conn != null)  {conn.close();  conn = null;}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}// end of private void close()---------------------
	
	
	
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
			
			result = pstmt.executeUpdate();  // sql문 실행
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			if(e.getErrorCode() == 1) {
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
		} finally {
			close();
		}	
			
		return result;
	}// end of public int memberRegister(MemberDTO member)-------------


	// === 로그인처리(select) 메소드 ===
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO member = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");     
		
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') AS registerday  "   
					   + " from tbl_member "
					   + " where status = 1 and userid = ? and passwd = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("passwd"));
			
			rs = pstmt.executeQuery();  // sql문 실행
			
			if(rs.next()) {
				member = new MemberDTO();
				
				member.setUserseq(rs.getInt("userseq"));
				member.setUserid(rs.getString("userid"));
				member.setName(rs.getString("name"));
				member.setMobile(rs.getString("mobile"));
				member.setPoint(rs.getInt("point"));
				member.setRegisterday(rs.getString("registerday"));
			}
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			if(e.getErrorCode() == 1) {
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
		} finally {
			close();
		}		
		
		return member;
	}// end of public MemberDTO login(Map<String, String> paraMap)-------------


	// === 회원탈퇴(update) 메소드 ===
	@Override
	public int memberDelete(int userseq) {
		
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");     
		
			String sql = " update tbl_member set status = 0 "
					   + " where userseq = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userseq);
			
			result = pstmt.executeUpdate();  // sql문 실행
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}// end of public int memberDelete(int userseq)-----------------------------


	// ==== 모든회원조회(select) 메소드 ====
	@Override
	public List<MemberDTO> showAllMember() {
		 List<MemberDTO> memberlist = null; 
		 
		 try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				
				conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");     
			
				String sql = " select userseq, userid, name, mobile, point, "
						+ " to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') AS registerday ,status "
						+ " from tbl_member "
						+ " where userid != 'admin' "
						+ " ORDER by userseq asc ";
				pstmt = conn.prepareStatement(sql);
			
				
				rs = pstmt.executeQuery();  // sql문 실행
				int cnt = 0;
				while(rs.next()) {
					cnt++;
					if(cnt == 1) {
					memberlist = new ArrayList<MemberDTO>();
					}
					
					MemberDTO member = new MemberDTO();
					
					member.setUserseq(rs.getInt("userseq"));
					member.setUserid(rs.getString("userid"));
					member.setName(rs.getString("name"));
					member.setMobile(rs.getString("mobile"));
					member.setPoint(rs.getInt("point"));
					member.setRegisterday(rs.getString("registerday"));
					member.setStatus(rs.getInt("status"));
					
					memberlist.add(member);
					
					
					
					}//end while 
				
			} catch(ClassNotFoundException e) {
				System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
			} catch(SQLException e) {
				if(e.getErrorCode() == 1) {
					System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
				}
			} finally {
				close();
			}		
		return memberlist;
	}//end public List<MemberDTO> showAllMember()



	@Override
	public int updateMyinfo(MemberDTO member) {
		
		int result = 0;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "JDBC_USER", "seven");     
		
			String sql = " update tbl_member "
					+ " set name = ? , mobile = ? "
					+ " where userid = ? " ;
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getMobile());
			pstmt.setString(3, member.getUserid());
			
			result = pstmt.executeUpdate();  // sql문 실행
			
		} catch(ClassNotFoundException e) {
			System.out.println(">>> ojdbc8.jar 파일이 없습니다. <<<");
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		
		return result;
	}
	
	

}
