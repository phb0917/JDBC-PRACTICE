package jdbc.day04.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jdbc.day04.board.controller.BoardController;
import jdbc.day04.dbconnection.MyDBConnection;
import jdbc.day04.member.domain.MemberDTO;
import jdbc.day04.member.model.*;

public class MemberController {

	// field
	MemberDAO mdao = new MemberDAO_imple();
	BoardController bdCtrl = new BoardController();
	
	
	// method
	// *** 시작메뉴 *** //
	public void menu_Start(Scanner sc) {
		
		boolean isSuccess_login = false;
		MemberDTO mbrdto = null;
		
		do {
		    if(isSuccess_login == false) { // 로그인 하기 전
			
				System.out.println("\n>>> ---- 시작메뉴 ---- <<<\n"
						         + "1.회원가입   2.로그인   3.프로그램종료 \n"
						         + "---------------------------------- \n");
				
				System.out.print("▷ 메뉴번호 선택 : ");
				String s_Choice = sc.nextLine();
				
				switch (s_Choice) {
					case "1": // 회원가입
						memberRegister(sc);
						break;
						
					case "2": // 로그인
						mbrdto = login(sc); // 로그인 시도하기 
						
						if(mbrdto != null) {
							isSuccess_login = true;
						}
						
						break;
						
					case "3": // 프로그램종료
						MyDBConnection.closeConnection(); // Connection 객체 자원반납
						return; // menu_Start(Scanner sc) 메소드 종료함.
						
					default:
						System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<"); 
						break;
						
				}// end of switch (s_Choice)------------------------
				
		    }// end of if(isSuccess_login == false)------------------------
			
			
			
			if(isSuccess_login == true) { // 로그인 한 후 
				
				String admin_menu = ("admin".equals(mbrdto.getUserid()))?"4.관리자자전용(모든회원조회)":""; 
				String bar = "admin".equals(mbrdto.getUserid())?"-".repeat(65)+"\n":"-".repeat(45)+"\n"; 
				
				System.out.println("\n>>> ---- 시작메뉴 ["+ mbrdto.getName() +"님 로그인중..]---- <<<\n"
						         + "1.로그아웃   2.나의정보보기   3.게시판가기   "+ admin_menu +"\n"
						         + bar);
				
				System.out.print("▷ 메뉴번호 선택 : ");
				String s_Choice = sc.nextLine();
				
				switch (s_Choice) {
					case "1":  // 로그아웃
						mbrdto = null;
						isSuccess_login = false;
						System.out.println(">>> 로그아웃 되었습니다. <<< \n");
						break;
						
					case "2":  // 나의정보보기
					//	System.out.println(mbrdto.toString());
					//  또는
						System.out.println(mbrdto);
						break;
						
					case "3":  // 게시판가기
						bdCtrl.menu_Board(mbrdto, sc);
						break;	
						
					case "4":  // admin 으로 로그인시 모든회원정보조회, 일반회원으로 로그인시 "메뉴에 없는 번호 입니다. 다시 선택하세요!!" 가 나오도록 한다.   
						if("admin".equals(mbrdto.getUserid())) {
							
							System.out.println("▷ 정렬 [1.회원명의 오름차순  /  2.회원명의 내림차순  / \n"
									         + "       3.가입일자의 오름차순 /  4.가입일자의 내림차순]"); 
							System.out.print("정렬번호 선택 : ");
							String sortChoice = sc.nextLine().trim();  // 그냥 엔터나 공백을 주면 1.회원명의 오름차순 으로 해주겠다. 
							
							showAllMember(sortChoice); // 정렬방식에 따른 모든회원조회
							break;
						}	
		
					default:
						System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요!! <<<");
						break;
				}// end of switch (s_Choice)-----------------
				
			}// end of if(isSuccess_login == true)--------------------------

		} while(true);
		
	}// end of public void menu_Start(Scanner sc)-------------------



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


	// **** 로그인 시도하기 **** //
	private MemberDTO login(Scanner sc) {
		
		System.out.println("\n >>> --- 로그인 --- <<<");
		
		System.out.print("▷ 아이디 : ");
		String userid = sc.nextLine();
		
		System.out.print("▷ 비밀번호 : ");
		String passwd = sc.nextLine();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("passwd", passwd);
		
		MemberDTO mbrdto = mdao.login(paraMap);
		
		if(mbrdto != null) {
			System.out.println("\n >>> 로그인 성공!! <<< \n");
		}
		else {
			System.out.println("\n >>> 로그인 실패!! <<< \n");
		}
		
		return mbrdto;
	}// end of private MemberDTO login(Scanner sc)-------------------

	
	
	private void showAllMember(String sortChoice) {

		if("1".equals(sortChoice) || "2".equals(sortChoice) || 
		   "3".equals(sortChoice) || "4".equals(sortChoice) ||
		   sortChoice.isBlank()) {
		
			List<MemberDTO> memberList = mdao.showAllMember(sortChoice);
			
			if(memberList != null) { 
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("-".repeat(60)+"\n");
				sb.append("회원번호  아이디  회원명  연락처  포인트  가입일자  가입상태\n");    
				sb.append("-".repeat(60)+"\n");
				
				for(int i=0; i<memberList.size(); i++) {
					
					String status = (memberList.get(i).getStatus() == 1)?"가입중":"탈퇴"; 
					
					sb.append(memberList.get(i).getUserseq()+"  "+
							  memberList.get(i).getUserid()+"  "+
							  memberList.get(i).getName()+"  "+
							  memberList.get(i).getMobile()+"  "+
							  memberList.get(i).getPoint()+"  "+
							  memberList.get(i).getRegisterday()+"  "+ 
							  status+"\n");
					
				}// end of for------------------
				
			//	System.out.println(sb.toString());
			//  또는 	
				System.out.println(sb);
			}
			
			else {
				System.out.println(">> 가입된 회원이 1명도 없습니다. ㅜㅜ <<");
			}
		
		}
		
		else {
			System.out.println(">> 정렬에 없는 번호 입니다.!! << \n");
		}		
		
	}// end of private void showAllMember(String s_sortChoice)----------------
	
	
}
