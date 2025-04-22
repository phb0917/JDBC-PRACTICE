package jdbc.day03;
//DAO(Database Access Object)란?
//-- Database 에 연결하여 DB와 관련된 업무(SQL)를 실행시켜주는 객체이다. 
public interface MemberDAO {
	
	//회원가입(insert) 메소드 
	int memberRegister(MemberDTO member);

}
