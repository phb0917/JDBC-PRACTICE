package jdbc.day04.board.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;
import jdbc.day04.dbconnection.MyDBConnection;
import jdbc.day04.member.domain.MemberDTO;

public class BoardDAO_imple implements BoardDAO {

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


	// === 게시판 글쓰기 ===
	// === Transaction 처리 ===
	//    (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
	//     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
	//     insert 와 update 가 모두 성공해야만 commit 할 것이다.)
	
	// 게시판 글쓰기 Transaction 처리하여 성공되어지면 1 을 리턴시켜 줄 것이고,
	// 장애(오류)가 발생되어 실패하면 -1 을 리턴시켜 줄 것이다.
	@Override
	public int write(BoardDTO bdto) {
		
		int result = 0;
		
		try {
			conn.setAutoCommit(false);
			// Transaction 처리를 위해서 수동 commit 으로 전환 시킨다.
			
			String sql = " insert into tbl_board(boardno, fk_userid, subject, contents, boardpasswd) " 
					   + " values(seq_board.nextval, ?, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bdto.getFk_userid());
			pstmt.setString(2, bdto.getSubject());
			pstmt.setString(3, bdto.getContents());
			pstmt.setString(4, bdto.getBoardpasswd());
			
			int n1 = pstmt.executeUpdate(); // sql문 실행 
			
			if(n1 == 1) { // tbl_board 테이블에 insert 가 성공되었다라면 
				sql = " update tbl_member set point = point + 10 "
					+ " where userid = ? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, bdto.getFk_userid());
				
				int n2 = pstmt.executeUpdate(); // sql문 실행 
				
				if(n2 == 1) { // tbl_member 테이블에 update 가 성공되었다라면
					conn.commit();  // 커밋을 해준다.
					result = 1;
				}
				
			}
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 2290) {
				System.out.println(">> 아이디 "+ bdto.getFk_userid() +" 님의 포인트는 30을 초과할 수 없기 때문에 오류발생 하였습니다. << \n"); 
			}
			else {
			   e.printStackTrace();
			}
			
			try {
				conn.rollback(); // 롤백을 해준다.
				result = -1;
			} catch (SQLException e1) {	} 
			
		} finally {
			try {
				conn.setAutoCommit(true); // 자동 commit 으로 복원시킨다.
			} catch (SQLException e2) {	}
			
			close(); // 자원반납하기 
		}
		
		return result;
	}// end of public int write(BoardDTO bdto)------------------------


	// === 게시글 목록보기 ===
	@Override
	public List<BoardDTO> boardList() {
		
		List<BoardDTO> boardList = new ArrayList<>();
		
		try {
			String sql =  " SELECT boardno "
						+ "      , CASE WHEN cmtcnt IS null THEN subject ELSE subject || '['|| cmtcnt ||']' END AS subject "
						+ "      , name, writeday, viewcount "
						+ " FROM "
						+ " ( "
						+ "    SELECT boardno "
						+ "         , case when length(subject) > 12 then substr(subject, 1, 10) || '..' else subject end AS subject " 
						+ "         , name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday, viewcount "
						+ "    FROM tbl_board B JOIN tbl_member M "
						+ "    ON B.fk_userid = M.userid "
						+ " ) V1 "
						+ " LEFT JOIN "
						+ " ( "
						+ "    SELECT fk_boardno, COUNT(*) AS cmtcnt "
						+ "    FROM tbl_comment "
						+ "    GROUP BY fk_boardno "
						+ " ) V2 "
						+ " ON V1.boardno = V2.fk_boardno "
						+ " ORDER BY boardno DESC ";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery(); // sql문 실행
			
			while(rs.next()) {
				
				BoardDTO bdto = new BoardDTO();
				
				bdto.setBoardno(rs.getInt("boardno"));
				
			/*	
				String subject = rs.getString("subject");
				if(subject.length() > 12) {
					subject = subject.substring(0, 10)+"..";
				}
				bdto.setSubject(subject);
			*/	
				bdto.setSubject(rs.getString("subject"));
				
				MemberDTO mbrdto = new MemberDTO();
				mbrdto.setName(rs.getString("name"));
				bdto.setMbrdto(mbrdto);
				
				bdto.setWriteday(rs.getString("writeday"));
				bdto.setViewcount(rs.getInt("viewcount"));
				
				boardList.add(bdto);
			}// end of while(rs.next())--------------------------
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return boardList;
	}// end of public List<BoardDTO> boardList()----------------------


	// === 글 1개 내용보기 ===
	// == 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	//    다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.
	@Override
	public BoardDTO viewContents(Map<String, String> paraMap) {
		
		BoardDTO bdto = null;
		
		try {
			String sql = " SELECT subject, contents, name, viewcount, fk_userid "
					   + " FROM "
					   + " ( SELECT subject, contents, viewcount, fk_userid "
					   + "   FROM tbl_board "
					   + "   WHERE boardno = to_number(?) ) B JOIN tbl_member M "
					   + " ON B.fk_userid = M.userid ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("boardno") );
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bdto = new BoardDTO();
				
				bdto.setSubject(rs.getString("subject"));
				bdto.setContents(rs.getString("contents"));
				
				MemberDTO mbrdto = new MemberDTO();
				mbrdto.setName(rs.getString("name"));
				bdto.setMbrdto(mbrdto);
				
				bdto.setViewcount(rs.getInt("viewcount"));
				
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
				
				// 로그인한 사용자가 다른 사용자가 쓴 글을 조회할 경우에만 글조회수 1증가 시켜야 한다.
				if( !paraMap.get("login_userid").equals(rs.getString("fk_userid") ) ) {
					
					sql = " update tbl_board set viewcount = viewcount + 1 "
						+ " where boardno = to_number(?) ";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("boardno"));
					
					pstmt.executeUpdate();
					
					bdto.setViewcount(bdto.getViewcount() + 1);
				}
				
			}// end of if(rs.next())-----------------------
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return bdto;
	}// end of public BoardDTO viewContents(Map<String, String> paraMap)--------------


	// === 글 1개 내용보기(조회수 증가는 없고 단순히 글내용만 보여주기) === 
	@Override
	public BoardDTO viewContents(String boardno) {

		BoardDTO bdto = null;
		
		try {
			String sql = " SELECT subject, contents, fk_userid, boardpasswd "
					   + " FROM tbl_board "
					   + " WHERE boardno = to_number(?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bdto = new BoardDTO();
				
				bdto.setSubject(rs.getString("subject"));
				bdto.setContents(rs.getString("contents"));
				bdto.setFk_userid(rs.getString("fk_userid"));
				bdto.setBoardpasswd(rs.getString("boardpasswd"));
				
			}// end of if(rs.next())-----------------------
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return bdto;		
		
	}// end of public BoardDTO viewContents(String boardno)--------------------------


	// === 글수정 ===
	@Override
	public int updateBoard(Map<String, String> paraMap) {

		int result = 0;
		
		try {
			String sql = " update tbl_board set subject = ? , contents = ?  " 
					   + " where boardno = to_number(?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("new_subject"));
			pstmt.setString(2, paraMap.get("new_contents"));
			pstmt.setString(3, paraMap.get("boardno"));
						
			result = pstmt.executeUpdate(); // sql문 실행 
			
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;			
		} finally {
			close(); // 자원반납하기 
		}
		
		return result;		
		
	}// end of public int updateBoard(Map<String, String> paraMap)-------------------

    
	// === 글삭제 ===
	@Override
	public int deleteBoard(String boardno) {

		int result = 0;
		
		try {
			String sql = " delete from tbl_board " 
					   + " where boardno = to_number(?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
									
			result = pstmt.executeUpdate(); // sql문 실행 
			
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;			
		} finally {
			close(); // 자원반납하기 
		}
		
		return result;				
		
	}// end of public int deleteBoard(String boardno)---------------------------------


	// === 댓글쓰기 ===
	@Override
	public int writeComment(CommentDTO cmtdto) {
		
		int result = 0;
		
		try {
			String sql = " insert into tbl_comment(commentno, fk_boardno, fk_userid, contents) " 
					   + " values(seq_comment.nextval, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, cmtdto.getFk_boardno());
			pstmt.setString(2, cmtdto.getFk_userid());
			pstmt.setString(3, cmtdto.getContents());
						
			result = pstmt.executeUpdate(); // sql문 실행 
			
		} catch (SQLException e) {
			
			if(e.getErrorCode() == 2291) {
				/*
				    ORA-02291: 무결성 제약조건(JDBC_USER.FK_TBL_COMMENT_FK_BOARDNO)이 위배되었습니다- 부모 키가 없습니다
				*/
				System.out.println(">> 입력하신 원글번호 "+ cmtdto.getFk_boardno() +"는(은) 게시글에 존재하지 않습니다. << \n");
			}
			
			else {
				e.printStackTrace();
			}
			
			result = -1;			
		} finally {
			close(); // 자원반납하기 
		}		
		
		return result;
		
	}// end of public int writeComment(CommentDTO cmtdto)------------------------


	// === 댓글쓰기 시 입력한 원글번호가 존재하는지 유무 판단하기 ===
	@Override
	public boolean isExist_boardno(int fk_boardno) {
		
		boolean bool = false;
		
		try {
			String sql = " select * "
					   + " from tbl_board "
					   + " where boardno = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fk_boardno);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				bool = true;
			}
			else {
				bool = false;
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			close(); // 자원반납하기 
		}
		
		return bool;
		
	}// end of public boolean isExist_boardno(int fk_boardno)--------------------


	// === 댓글 보여주기 === //
	@Override
	public List<CommentDTO> commentList(String boardno) {
		
		List<CommentDTO> commentList = new ArrayList<>();
		
		try {
			String sql = " SELECT contents, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "  
					   + " FROM "
					   + " (SELECT * "
					   + "  FROM tbl_comment "
					   + "  WHERE fk_boardno = to_number(?) ) C "
					   + " JOIN tbl_member M "
					   + " ON C.fk_userid = M.userid "
					   + " ORDER BY commentno DESC ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardno);
			
			rs = pstmt.executeQuery(); // sql문 실행
			
			while(rs.next()) {
				
				CommentDTO cmtdto = new CommentDTO();
				cmtdto.setContents(rs.getString("contents"));
				
				MemberDTO mbrdto = new MemberDTO();
				mbrdto.setName(rs.getString("name")); 
				
				cmtdto.setMbrdto(mbrdto);
				
				cmtdto.setWriteday(rs.getString("writeday"));
				
				commentList.add(cmtdto);
			}// end of while(rs.next())-----------------
			
		} catch (SQLException e) {
			
		} finally {
			close();
		}
		
		return commentList;
	}// end of public List<CommentDTO> commentList(String boardno)----------------

	
	
	// 최근 1주일내에 작성된 게시글만 DB에서 가져오기 
	@Override
	public Map<String, Integer> statistics_by_week() {
		
		Map<String, Integer> map = new HashMap<>();
		
		try {
			String sql = " SELECT COUNT(*) AS TOTAL "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 6, 1, 0) ) AS PREVIOUS6 "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 5, 1, 0) ) AS PREVIOUS5 "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 4, 1, 0) ) AS PREVIOUS4 "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 3, 1, 0) ) AS PREVIOUS3 "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 2, 1, 0) ) AS PREVIOUS2 "
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 1, 1, 0) ) AS PREVIOUS1 "  
					   + "      , SUM( DECODE( to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd'), 0, 1, 0) ) AS TODAY " 
					   + " FROM tbl_board "
					   + " WHERE to_date( to_char(sysdate, 'yyyymmdd'), 'yyyymmdd') - to_date( to_char(writeday, 'yyyymmdd'), 'yyyymmdd') < 7 "; 
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery(); // sql문 실행
			
			rs.next();
			
			map.put("TOTAL", rs.getInt("TOTAL"));
			map.put("PREVIOUS6", rs.getInt("PREVIOUS6"));
			map.put("PREVIOUS5", rs.getInt("PREVIOUS5"));
			map.put("PREVIOUS4", rs.getInt("PREVIOUS4"));
			map.put("PREVIOUS3", rs.getInt("PREVIOUS3"));
			map.put("PREVIOUS2", rs.getInt("PREVIOUS2"));
			map.put("PREVIOUS1", rs.getInt("PREVIOUS1"));
			map.put("TODAY", rs.getInt("TODAY"));
			
		} catch (SQLException e) {
			
		} finally {
			close();
		}
		
		return map;
	}// end of public Map<String, Integer> statistics_by_week()------------------------	
	

	// === 이번달 일자별 게시글 작성건수 ===
	@Override
	public List<Map<String, String>> statistics_by_currentMonth() {
		
		List<Map<String, String>> mapList = new ArrayList<>();
		
		try {
			String sql = " SELECT DECODE(grouping(to_char(writeday, 'yyyy-mm-dd')), 0, to_char(writeday, 'yyyy-mm-dd'), '전체') AS writeday "       
					   + "      , count(*) AS cnt"
					   + "      , TRUNC( count(*) / ( select count(*) "
					   + "                            from tbl_board "
					   + "                            where to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm') ) * 100 ) AS percentage "
					   + " FROM tbl_board "
					   + " WHERE to_char(writeday, 'yyyymm') = to_char(sysdate, 'yyyymm') "
					   + " GROUP BY ROLLUP(to_char(writeday, 'yyyy-mm-dd')) ";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery(); // sql문 실행
			
			while(rs.next()) {
				
				Map<String, String> map = new HashMap<>();
				map.put("writeday", rs.getString("writeday"));
				map.put("cnt", String.valueOf(rs.getInt("cnt")));
				map.put("percentage", String.valueOf(rs.getInt("percentage")));
				
				mapList.add(map);
			}// end of while(rs.next())----------------------------
			
		} catch (SQLException e) {
			
		} finally {
			close();
		}
		
		return mapList;
	}// end of public List<Map<String, String>> statistics_by_currentMonth()----------

	
}
