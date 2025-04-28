package jdbc.day04.member.model;

import java.util.List;
import java.util.Map;

import jdbc.day04.member.domain.MemberDTO;

public interface MemberDAO {

	// 회원가입(insert)
	int memberRegister(MemberDTO mbrdto);
	
	// 로그인처리(select) 메소드
	MemberDTO login(Map<String, String> paraMap);

	// 정렬방식에 따른 모든 회원을 조회(select)해주는 메소드
	List<MemberDTO> showAllMember(String sortChoice);

	

}
