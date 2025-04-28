package jdbc.day03;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MemberCtrl {

	// field
	MemberDAO mdao = new MemberDAO_imple();
	
	
	// method
	// ==== *** 시작메뉴를 보여주는 메소드 *** ==== //
	public void menu_Start(Scanner sc) {
		
		boolean isSuccess_login = false;
		MemberDTO member = null;
		
		do {
			
			if(isSuccess_login == false) { // 로그인 하기 전
			
				// ======================================================== //
				System.out.println("\n>>> ---- 시작메뉴 ---- <<<\n"
						         + "1.회원가입   2.로그인   3.프로그램종료 \n"
						         + "---------------------------------- \n");
				
				System.out.print("▷ 메뉴번호 선택 : ");
				String s_Choice = sc.nextLine();
				
				switch (s_Choice) {
					case "1":  // 회원가입
						memberRegister(sc);
						break;
						
					case "2":  // 로그인
						member = login(sc); // 로그인 시도하기
						
						if(member != null) { // 로그인 성공이라면 
							isSuccess_login = true;
						}
						
						break;
						
					case "3":  // 프로그램종료
						return;	// 	menu_Start(Scanner sc) 메소드 종료함.			
		
					default:
						System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
						break;
				}// end of switch (s_Choice)---------------------
				// ======================================================== //
			
			} // end of if(isSuccess_login == false)------------------
			
			
			if(isSuccess_login == true) { // 로그인 한 후 
				
				String add_menu = ("admin".equals(member.getUserid()))?"4.모든회원조회":"4.내정보수정하기"; 
				String bar = "-".repeat(60)+"\n";
				
				System.out.println("\n>>> ---- 시작메뉴 ["+ member.getName() +"님 로그인중..] ---- <<<\n"
						         + "1.로그아웃   2.회원탈퇴하기   3.나의정보보기   " + add_menu + "\n"
						         + bar);
				
				System.out.print("▷ 메뉴번호 선택 : ");
				String s_Choice = sc.nextLine();
				
				switch (s_Choice) {
					case "1":   // 로그아웃
						member = null;
						isSuccess_login = false;
						System.out.println(">>> 로그아웃 되었습니다. <<<\n");
						break;
						
					case "2":   // 회원탈퇴하기
						
						String yn = "";
						
						do {
							System.out.print("▷ 정말로 탈퇴하시겠습니까? [Y/N] : ");
							yn = sc.nextLine();
							
							if("y".equalsIgnoreCase(yn)) { // 탈퇴처리
								int n = mdao.memberDelete(member.getUserseq()); 
								if(n == 1) {
									member = null;
									isSuccess_login = false;
									System.out.println(">>> 회원탈퇴가 성공되었습니다. <<< \n");
								}
							}
							
							else if("n".equalsIgnoreCase(yn)) { // 탈퇴취소
								System.out.println(">>> 회원탈퇴를 취소하셨습니다. <<< \n");
							}
							
							else {
								System.out.println(">>> Y 또는 N 만 입력하세요!! <<< \n");
							}
						
						} while(!("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)));
						
						break;	
						
					case "3":   // 나의정보보기
						
					 //	System.out.println(member.toString());
					 // 또는
						System.out.println(member);
						
						break;						
	
					case "4":  // admin 으로 로그인시 모든회원조회, 일반회원으로 로그인시 내정보수정하기 	
						if("admin".equals(member.getUserid())) {
							showAllMember(); // 모든회원조회
						}
						else {
							updateMyinfo(member, sc); // 내정보수정하기
						}
						
						break;
						
					default:
						System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
						break;
				}// end of switch (s_Choice)------------------------
				
			}// end of if(isSuccess_login == true)------------------
			
		} while(true);
		
	}// end of public void menu_Start(Scanner sc)----------------------	

	

	// **** 회원가입 **** //
	   private void memberRegister(Scanner sc) {
	      
	      System.out.println("\n >>> ---- 회원가입 ---- <<< ");
	      
	      System.out.print("1. 아이디 : ");
	      String userid = sc.nextLine();
	      
	      System.out.print("2. 비밀번호 : ");
	      String passwd = sc.nextLine();
	      
	      System.out.print("3. 회원명 : ");
	      String name = sc.nextLine();
	      
	      System.out.print("4. 연락처(휴대폰) : ");
	      String mobile = sc.nextLine();
	      
	      MemberDTO mbrdto = new MemberDTO();
	      mbrdto.setUserid(userid);
	      mbrdto.setPasswd(passwd);
	      mbrdto.setName(name);
	      mbrdto.setMobile(mobile);
	      
	      int n = mdao.memberRegister(mbrdto);
	      
	      if(n==1) {
	         System.out.println("\n >>> 회원가입을 축하드립니다. <<<");
	      }
	      else {
	         System.out.println(">>> 회원가입이 실패되었습니다. <<<");
	      }
	      
	   }// end of private void memberRegister(Scanner sc)---------------
	
	
	
	// ==== 로그인 시도하기 ==== 
	private MemberDTO login(Scanner sc) {
		
		System.out.println("\n >>> --- 로그인 --- <<<");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("▷ 비밀번호 : ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		MemberDTO member = mdao.login(paraMap);
		
		if(member != null) {
			System.out.println("\n >>> 로그인 성공!! <<< \n");
		}
		
		else {
			System.out.println("\n >>> 로그인 실패!! <<< \n");
		}
		
		return member;
	}// end of private MemberDTO login(Scanner sc)-----------------
	
	
	// ==== 모든회원조회 ====
	private void showAllMember() {
		
			List<MemberDTO> memberlist = mdao.showAllMember();
		if(memberlist != null) {
			StringBuilder sb =new StringBuilder();
			
			sb.append("-".repeat(50)+ "\n");
			sb.append("회원번호 	아이디 	회원명 	연락처 	포인트 	가입일자 	가입상태\n");
			sb.append("-".repeat(50) + "\n");
			
			for(int i=0; i<memberlist.size(); i++) {
				String status =(memberlist.get(i).getStatus() == 1)?"가입중":"탈퇴";
				sb.append(memberlist.get(i).getUserseq()+ "  " + 
						memberlist.get(i).getUserid()+ "  " + 
						memberlist.get(i).getName()+ "  " + 
						memberlist.get(i).getMobile()+ "  " + 
						memberlist.get(i).getPoint()+ "  " + 
						memberlist.get(i).getRegisterday().substring(0, 10)+ "  " +
						status + "\n" );
				
				
			}//end for 
		
		//	System.out.println(sb.toString());
			System.out.println(sb);
		}
		else {
			System.out.println(">> 가입된 회원이 1명도 없습니다 << ");
		}
		
	}// end of private void showAllMember()---------------
	
	
	// ==== 내정보수정하기 ====
	private void updateMyinfo(MemberDTO member, Scanner sc) {
		//System.out.println(member.toString());
		//또는
			System.out.println(member);
			
			System.out.println("== [주의사항] 변경하지 않으려면 그냥 엔터 하세요 ==");
			System.out.print("> 성명 : ");
			String new_name = sc.nextLine();
			
			System.out.print("> 연락처 : ");
			String new_mobile =sc.nextLine();
			
			if(!new_name.isBlank()) {
				member.setName(new_name);
			}
			if(!new_mobile.isBlank()) {
				member.setMobile(new_mobile);
			}
			
			int n = mdao.updateMyinfo(member);
			
			if(n==1) {
				System.out.println( "\n>>> 수정완료 <<< \n");
				
			}
			
			System.out.println(member);
	}// end of private void updateMyinfo(MemberDTO member, Scanner sc)-------
	
	
	
}





	
	
	
	
	
	
	
	
	

