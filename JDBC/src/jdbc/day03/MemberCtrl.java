package jdbc.day03;

import java.util.Scanner;

public class MemberCtrl {

	

	//field
	MemberDAO mdao = new MemberDAO_imple();
	
	
	
	
	//method
public void menu_Start(Scanner sc) {
		
	
		do {
			//==========================================================
			
			System.out.println("\n>>> ---- 시작메뉴 ---- <<<\n"
					            + "1.회원가입   2.로그인   3.프로그램종료 \n"
					            + "---------------------------------- \n");
		
			System.out.print("▷ 메뉴번호 선택 : ");
			String s_choice = sc.nextLine();
			
			switch (s_choice) {
			case "1": // 회원가입
				memberRegister(sc);
				break;
			case "2":// 로그인
				
				break;
			case "3": // 프로그램종료
				
				return; // public void menu_Start(Scanner sc) 메소드 종료

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. 다시 선택하세요 <<" );
				break;
			}//end switch
			//==========================================================
		}while(true);
	
	}//end of void 


	//회원가입
	private void memberRegister(Scanner sc) {
		System.out.println("\n>>> ~~~~~~회원가입~~~~~~ <<<");
		System.out.print("1. 아이디 : ");
		String userid = sc.nextLine();  //	db 와 연동이 목적이기 때문에 속성명과 똑같이 쓴다 
		
		System.out.print("2. 비밀번호 : ");
		String passwd = sc.nextLine();
		
		System.out.print("3. 회원명 : ");
		String name  = sc.nextLine();
		
		System.out.print("4. 연락처(휴대폰) : ");
		String mobile = sc.nextLine();
		
		
		MemberDTO member = new MemberDTO();
		member.setUserid(userid);
		member.setPasswd(passwd);
		member.setName(name);
		member.setMobile(mobile);
		
		
		int n = mdao.memberRegister(member);
		
		if(n == 1 ) {
			System.out.println("\n >>>> 회원가입을 축하드립니다 <<<<");
		}
		else {
			System.out.println(">>> 회원가입이 실패되었습니다 <<<");
						
		}
		
	}// end void 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
