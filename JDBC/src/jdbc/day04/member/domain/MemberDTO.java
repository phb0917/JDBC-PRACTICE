package jdbc.day04.member.domain;

public class MemberDTO { // MemberDTO 는
                         // 오라클의 tbl_member 테이블에 해당함.
                         // tbl_member 테이블은 오라클의 tbl_board 테이블과 tbl_comment 테이블을 자식테이블로 두고 있는 부모테이블이다.  

	// field
	private int userseq;         // 회원번호   
	private String userid;       // 회원아이디
	private String passwd;       // 회원비밀번호
	private String name;         // 회원명
	private String mobile;       // 연락처
	private int point;           // 포인트
	private String registerday;  // 가입일자
	private int status;          // status 컬럼의 값이 1 이면 가입된 상태, 0 이면 탈퇴	
	
	
	// method
	public int getUserseq() {
		return userseq;
	}
	
	public void setUserseq(int userseq) {
		this.userseq = userseq;
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public int getPoint() {
		return point;
	}
	
	public void setPoint(int point) {
		this.point = point;
	}
	
	public String getRegisterday() {
		return registerday;
	}
	
	public void setRegisterday(String registerday) {
		this.registerday = registerday;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	// ======================================= //
	
	@Override
	public String toString() {
		
		return "=== "+ name +"님의 정보 ===\n"
			  + "◇ 성명 : " + name + "\n"
			  + "◇ 연락처 : "+ mobile + "\n"
			  + "◇ 포인트 : "+ point + "\n"
			  + "◇ 가입일자 : "+ registerday.substring(0, 10) + "\n"; 
	}
	
}
