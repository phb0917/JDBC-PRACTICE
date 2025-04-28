package jdbc.day03;

import java.util.List;
import java.util.Map;

//DAO(Database Access Object)란?
//-- Database 에 연결하여 DB와 관련된 업무(SQL)를 실행시켜주는 객체이다. 
public interface MemberDAO {
	
	//회원가입(insert) 메소드 
	int memberRegister(MemberDTO member);

	// 로그인처리(select) 메소드 
	MemberDTO login(Map<String, String> paramap);

	// 회원탈퇴
	int memberDelete(int userseq);

	// ==== 모든회원조회(select) 메소드  ====
	List<MemberDTO> showAllMember();

	// ==== 내정보 수정 ( 메소드  ===
	int updateMyinfo(MemberDTO member);

	

}
