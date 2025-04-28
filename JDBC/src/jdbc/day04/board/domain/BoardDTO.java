package jdbc.day04.board.domain;

import jdbc.day04.member.domain.MemberDTO;

public class BoardDTO { // BoardDTO 는
                        // 오라클의 tbl_board 테이블에 해당함.
                        // tbl_board 테이블은 오라클의 tbl_member 테이블을 부모테이블로 하고 있는 자식테이블이다.
	                    // tbl_board 테이블의 자식테이블은 tbl_comment 테이블이다.

	// field
	private int boardno;        // 글번호
	private String fk_userid;   // 작성자아이디
	private String subject;     // 글제목
	private String contents;    // 글내용
	private String writeday;    // 작성일자 
	private int viewcount;      // 조회수 
	private String boardpasswd; // 글암호
	
	private MemberDTO mbrdto;   // !!! JOIN 해서 select 하는 용도 !!!

	// method 
	public int getBoardno() {
		return boardno;
	}

	public void setBoardno(int boardno) {
		this.boardno = boardno;
	}

	public String getFk_userid() {
		return fk_userid;
	}

	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getWriteday() {
		return writeday;
	}

	public void setWriteday(String writeday) {
		this.writeday = writeday;
	}

	public int getViewcount() {
		return viewcount;
	}

	public void setViewcount(int viewcount) {
		this.viewcount = viewcount;
	}

	public String getBoardpasswd() {
		return boardpasswd;
	}

	public void setBoardpasswd(String boardpasswd) {
		this.boardpasswd = boardpasswd;
	}

	public MemberDTO getMbrdto() {
		return mbrdto;
	}

	public void setMbrdto(MemberDTO mbrdto) {
		this.mbrdto = mbrdto;
	}

	
	// ====================================================== //
	public String boardInfo() {
		    // 글번호\t글제목\t\t작성자\t작성일자\t\t조회수
		return boardno+"\t"+subject+"\t\t"+mbrdto.getName()+"\t"+writeday+"\t\t"+viewcount;
	}
	
	
	
	
	
}
