package jdbc.day04.member.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdbc.day04.dbconnection.MyDBConnection;
import jdbc.day04.member.domain.MemberDTO;

public class MemberDAO_imple implements MemberDAO {

	// field
	private Connection conn = MyDBConnection.getConn();
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	
	// method
	// ==== 자원반납을 해주는 메소드 ==== //
	private void close() {
		try {
			if(rs != null)    {rs.close();    rs = null;}
			if(pstmt != null) {pstmt.close(); pstmt = null;}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}// end of private void close()---------------------

	
	// === 회원가입(insert) === //
	@Override
	public int memberRegister(MemberDTO mbrdto) {

		int result = 0;
		
		try {
			String sql = " insert into tbl_member(userseq, userid, passwd, name, mobile) "
					   + " values(userseq.nextval, ?, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mbrdto.getUserid());
			pstmt.setString(2, mbrdto.getPasswd());
			pstmt.setString(3, mbrdto.getName());
			pstmt.setString(4, mbrdto.getMobile());
			
			result = pstmt.executeUpdate();  // sql문 실행
			
		} catch(SQLException e) {
			if(e.getErrorCode() == 1) {
				System.out.println(">> 아이디가 중복되었습니다. 새로운 아이디를 입력하세요!! <<");
			}
		} finally {
			close();
		}	
			
		return result;
	}// end of public int memberRegister(MemberDTO mbrdto)---------------------
	
	
	// === 로그인처리(select) === //
	@Override
	public MemberDTO login(Map<String, String> paraMap) {
		
		MemberDTO mbrdto = null;
		
		try {
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') AS registerday  "   
					   + " from tbl_member "
					   + " where status = 1 and userid = ? and passwd = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, paraMap.get("passwd"));
			
			rs = pstmt.executeQuery(); // sql문 실행 
			
			if(rs.next()) {
				
				mbrdto = new MemberDTO();
				
				mbrdto.setUserseq(rs.getInt("userseq"));
				mbrdto.setUserid(rs.getString("userid"));
				mbrdto.setName(rs.getString("name"));
				mbrdto.setMobile(rs.getString("mobile"));
				mbrdto.setPoint(rs.getInt("point"));
				mbrdto.setRegisterday(rs.getString("registerday"));
				
			}// end of if(rs.next())------------------
					
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return mbrdto;
	}// end of public MemberDTO login(Map<String, String> paraMap)------------


	// === 정렬방식에 따른 모든 회원을 조회(select) === //
	@Override
	public List<MemberDTO> showAllMember(String sortChoice) {

		List<MemberDTO> memberList = null;
		
		try {
			String sql = " select userseq, userid, name, mobile, point, to_char(registerday, 'yyyy-mm-dd hh24:mi:ss') as registerday "
					   + "      , status "  
					   + " from tbl_member "
					   + " where userid != 'admin' ";
					   
			switch (sortChoice) {
			    case "":
			    case "1":  // 회원명의 오름차순
					sql += " order by name asc ";
					break;
					
				case "2":  // 회원명의 내림차순
					sql += " order by name desc ";
					break;
					
				case "3":  // 가입일자의 오름차순 
					sql += " order by registerday asc ";
					break;
					
				case "4":  // 가입일자의 내림차순
					sql += " order by registerday desc ";
					break;					

			}// end of switch (sortChoice)-------------- 
			
			
			pstmt = conn.prepareStatement(sql);
						
			rs = pstmt.executeQuery(); // sql문 실행
			
			int cnt = 0;
			while(rs.next()) {
				cnt++;
				if(cnt == 1) {
					memberList = new ArrayList<>();
				}
				
				MemberDTO member = new MemberDTO();
				
				member.setUserseq(rs.getInt("userseq"));
				member.setUserid(rs.getString("userid"));
				member.setName(rs.getString("name"));
				member.setMobile(rs.getString("mobile"));
				member.setPoint(rs.getInt("point"));
				member.setRegisterday(rs.getString("registerday"));
				member.setStatus(rs.getInt("status")); 
								
				memberList.add(member);
				
			}// end of while(rs.next())-----------
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}		
		
		return memberList;
	}// end of public List<MemberDTO> showAllMember(String sortChoice)------------
	

}
