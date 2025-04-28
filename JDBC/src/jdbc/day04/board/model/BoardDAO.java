package jdbc.day04.board.model;

import java.util.List;
import java.util.Map;

import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;

public interface BoardDAO {

	// 게시글 쓰기
	int write(BoardDTO bdto);
	// === Transaction 처리 ===
	//    (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
	//     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
	//     insert 와 update 가 모두 성공해야만 commit 할 것이다.)

	// 글목록 보기 
	List<BoardDTO> boardList();

	// 글 1개 내용보기
	// == 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	//    다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.
	BoardDTO viewContents(Map<String, String> paraMap);

	// 글 1개 내용보기(조회수 증가는 없고 단순히 글내용만 보여주기) 
	BoardDTO viewContents(String boardno); // 메소드의 오버로딩(over loading)

	// 글수정
	int updateBoard(Map<String, String> paraMap);

	// 글삭제
	int deleteBoard(String boardno);

	// 댓글쓰기
	int writeComment(CommentDTO cmtdto);

	// 댓글쓰기 시 입력한 원글번호가 존재하는지 유무 판단하기 
	boolean isExist_boardno(int fk_boardno);

	// 댓글 보여주기
	List<CommentDTO> commentList(String boardno);

	// 최근 1주일내에 작성된 게시글만 DB에서 가져오기 
	Map<String, Integer> statistics_by_week();
	
	// 이번달 일자별 게시글 작성건수 
	List<Map<String, String>> statistics_by_currentMonth();

}







