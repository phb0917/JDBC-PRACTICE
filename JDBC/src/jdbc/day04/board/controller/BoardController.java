package jdbc.day04.board.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jdbc.day04.board.domain.BoardDTO;
import jdbc.day04.board.domain.CommentDTO;
import jdbc.day04.member.domain.MemberDTO;
import jdbc.day04.board.model.*;

public class BoardController {

	// field
	BoardDAO bdao = new BoardDAO_imple();
	
	
	// method
	
	// **** 게시판 메뉴를 보여주는 메소드 **** //
	public void menu_Board(MemberDTO mbrdto, Scanner sc) {
		
		boolean isExit = false;
		
		do {
			System.out.println("\n>>> ---- 게시판메뉴 ["+ mbrdto.getName() +"님 로그인중..]---- <<<\n"
			         + "1.글목록보기   2.글내용보기    3.글쓰기    4.댓글쓰기 \n"
			         + "5.글수정하기   6.글삭제하기    7.최근1주일간 일자별 게시글 작성건수 \n"
			         + "8.이번달 일자별 게시글 작성건수  9.나가기 ");
			
			System.out.print("▷ 메뉴번호 선택 : ");
			String s_menuNo = sc.nextLine();
			
			switch (s_menuNo) {
				case "1":  // 글목록보기
					boardList();
					break;
					
				case "2":  // 글내용보기
					viewContents(mbrdto.getUserid(), sc);
					break;
					
				case "3":  // 글쓰기
					int n = write(mbrdto, sc);
					
					if(n == 1) {
						System.out.println(">> 글쓰기 성공!! <<");
					}
					
					else if(n == 0) {
						System.out.println(">> 글쓰기 취소!! <<");
					}
					
					else if(n == -1) {
						System.out.println(">> 글쓰기 실패!! <<");
					}
					
					break;
					
				case "4":  // 댓글쓰기 
					n = writeComment(mbrdto, sc);
					
					if(n == 1) {
						System.out.println(">> 댓글쓰기 성공!! <<");
					}
					
					else if(n == 0) {
						System.out.println(">> 댓글쓰기 취소!! <<");
					}
					
					else if(n == -1) {
						System.out.println(">> 댓글쓰기 실패!! <<");
					}
					
					break;
					
				case "5":  // 글수정하기 
					n = updateBoard(mbrdto.getUserid(), sc);
					
					if(n == 1) {
						System.out.println(">> 글수정 성공!! <<");
					}
					
					else if(n == 0) {
						System.out.println(">> 글수정 취소!! <<");
					}
					
					else if(n == -1) {
						System.out.println(">> 글수정 실패!! <<");
					}
					
					break;
					
				case "6":  // 글삭제하기 
					n = deleteBoard(mbrdto.getUserid(), sc);
					
					if(n == 1) {
						System.out.println(">> 글삭제 성공!! <<");
					}
					
					else if(n == 0) {
						System.out.println(">> 글삭제 취소!! <<");
					}
					
					else if(n == -1) {
						System.out.println(">> 글삭제 실패!! <<");
					}
					
					break;
										
				case "7":  // 최근1주일간 일자별 게시글 작성건수 
					statistics_by_week();
					break;
					
				case "8":  // 이번달 일자별 게시글 작성건수
					statistics_by_currentMonth();
					break;
					
				case "9":  // 나가기
					isExit = true;
					break;					
	
				default:
					System.out.println(">> 메뉴에 없는 번호 입니다. << \n");
					break;
			}// end of switch (s_menuNo)------------------
			
		} while(!isExit);
		
	}// end of public void menu_Board()--------------------



	// *** 글목록보기 *** //
	private void boardList() {
		
		List<BoardDTO> boardList = bdao.boardList();
		
		if(boardList.size() > 0) { 
			// 게시글이 존재하는 경우
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("\n"+"-".repeat(30)+" [게시글 목록] "+"-".repeat(30)+"\n");
			sb.append("글번호\t글제목\t\t작성자\t작성일자\t\t조회수\n");
			sb.append("-".repeat(75)+"\n");
			
			for(int i=0; i<boardList.size(); i++) {
				sb.append(boardList.get(i).boardInfo()+"\n");
				       // boardList.get(i) 은 BoardDTO 이다.
			}// end of for-----------------------------------
			
		//	System.out.println(sb.toString());
		//  또는	
			System.out.println(sb);
		}
		
		else {
			// 게시글이 존재하지 않는 경우
			System.out.println(">> 글목록이 없습니다 << \n");
		}
		
	}// end of private void boardList()----------------------

	
	
	// *** 글내용보기 *** //
	// == 현재 로그인 사용자가 자신이 쓴 글을 볼때는 조회수 증가가 없지만
	//    다른 사용자가 쓴 글을 볼때는 조회수를 1증가 해주어야 한다.
	private void viewContents(String login_userid, Scanner sc) {
		
		System.out.println("\n>>> 글내용 보기 <<<");
		
		do {
			System.out.print("▷ 글번호 : ");
			String boardno = sc.nextLine();
			
			try {
				Integer.parseInt(boardno);
			} catch(NumberFormatException e) {
				System.out.println(">> [경고] 글번호는 숫자만 가능합니다.!! <<");
				continue;
			}
			
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("login_userid", login_userid);
			paraMap.put("boardno", boardno);
			
			BoardDTO bdto = bdao.viewContents(paraMap);
			
			if(bdto != null) {
				// 존재하는 글번호를 입력한 경우
			
				System.out.println("[글제목] "+ bdto.getSubject() +"\n"
						         + "[글내용] "+ bdto.getContents() +"\n"
						         + "[작성자] "+ bdto.getMbrdto().getName() +"\n"
						         + "[조회수] "+ bdto.getViewcount() );
				
				// ~~~~~~~~~~~~~~~~~~~~~ 댓글 보여주기 시작 ~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 
				System.out.println("[댓글]\n"+"-".repeat(50));
				
				List<CommentDTO> commentList = bdao.commentList(boardno);
				// 원글에 대한 댓글을 가져오는 것 
				
				if(commentList.size() > 0) {
					// 원글에 대한 댓글이 존재하는 경우
					
					StringBuilder sb = new StringBuilder();
					
					sb.append("댓글내용\t\t작성자명\t작성일자\n");
					sb.append("-".repeat(50)+"\n");
					
					for(CommentDTO cmtdto : commentList) {
						sb.append(cmtdto.getContents()+"\t\t"+cmtdto.getMbrdto().getName()+"\t"+cmtdto.getWriteday()+"\n"); 
					}// end of for----------------------
					
					System.out.println(sb);
				}
				else {
					// 원글에 대한 댓글이 존재하지 않는 경우
					System.out.println(">> 댓글 내용 없음 << \n");
				}
				// ~~~~~~~~~~~~~~~~~~~~~ 댓글 보여주기 끝 ~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
			}
			
			else {
				// 존재하지 않는 글번호를 입력한 경우
				System.out.println(">> 입력하신 글번호 "+boardno+" 는 글목록에 존재하지 않습니다. << \n");
			}
			
			break;
			
		} while(true);
		
	}// end of private void viewContents(Scanner sc)-------------
	
	

	// *** 글쓰기를 해주는 메소드 *** //
	// === Transaction 처리 ===
	//    (tbl_board 테이블에 insert 가 성공되어지면 tbl_member 테이블의 point 컬럼에 10씩 증가 update 를 할 것이다.
	//     그런데 insert 또는 update 가 하나라도 실패하면 모두 rollback 할 것이고,
	//     insert 와 update 가 모두 성공해야만 commit 할 것이다.)
	private int write(MemberDTO mbrdto, Scanner sc) {
		
		int result = 0;
		
		System.out.println(">>> 글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + mbrdto.getName());
		
		System.out.print("2. 글제목[최대 100글자] : "); 
		String subject = sc.nextLine();
		
		System.out.print("3. 글내용[최대 200글자] : ");
		String contents = sc.nextLine();
		
		System.out.print("4. 글암호[최대 20글자] : ");
		String boardpasswd = sc.nextLine();
		
		BoardDTO bdto = new BoardDTO();
		bdto.setFk_userid(mbrdto.getUserid());
		bdto.setSubject(subject);
		bdto.setContents(contents);
		bdto.setBoardpasswd(boardpasswd);
		
		do {
			// --------------------------------------------------- //
			System.out.print(">> 정말로 글쓰기를 하시겠습니까?[Y/N] => ");
			String yn = sc.nextLine();
			
			if("y".equalsIgnoreCase(yn)) {
				
				if(subject.length() > 100) {
					System.out.println(">> [경고] 글제목은 100글자 이하이어야 합니다.");
					return -1;
				}
				
				if(contents.length() > 200) {
					System.out.println(">> [경고] 글내용은 200글자 이하이어야 합니다.");
					return -1;
				}
				
				if(boardpasswd.length() > 20) {
					System.out.println(">> [경고] 글암호는 20글자 이하이어야 합니다.");
					return -1;
				}
				
				result = bdao.write(bdto); // 게시글 쓰기
				
				break;
			}
			
			else if("n".equalsIgnoreCase(yn)) {
			
				break;
			}
			
			else {
				System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
			}
			// --------------------------------------------------- //
		} while(true);
		
		
		return result;
	}// end of private int write(MemberDTO mbrdto, Scanner sc)---------------------
	
	
	
	// *** 댓글쓰기 *** // 
	private int writeComment(MemberDTO mbrdto, Scanner sc) {
		
		int result = 0;
		
		System.out.println("\n>>> 댓글쓰기 <<<");
		
		System.out.println("1. 작성자명 : " + mbrdto.getName());
		
		int fk_boardno = 0;
		do {
			// ================================================= //
			System.out.print("2. 원글의 글번호 : ");
			String s_fk_boardno = sc.nextLine(); // "강아지" 와 같은 문자가 들어오면 안된다.!!!
			
			try {
				fk_boardno = Integer.parseInt(s_fk_boardno);
				
				if(fk_boardno < 1) {
					System.out.println(">> [경고] 원글의 글번호는 1이상인 정수로만 입력하셔야 합니다.!! << \n");
				}
				else {
					
					if(!bdao.isExist_boardno(fk_boardno)) { 
						// 입력한 원글번호가 게시글에 존재하지 않는 경우라면 isExist_boardno(fk_boardno) 이 false 이다. 
						System.out.println(">> [경고] 입력하신 "+ fk_boardno +" 는 존재하지 않는 원글번호 입니다. << \n");
					}
					else {
						// 입력한 원글번호가 게시글에 존재하는 경우라면
						break;
					}
					
				}
				
			} catch(NumberFormatException e) {
				System.out.println(">> [경고] 원글의 글번호는 정수로만 입력하셔야 합니다.!! << \n");
			}
		// ================================================= //
		} while(true);
		
		
		String contents = "";
		do {
			// ================================================= //
			System.out.print("3. 댓글내용 : ");
			contents = sc.nextLine(); // 그냥 엔터나 공백이나 
			                          // tbl_comment 테이블의 contents 컬럼의 크기(최대 100글자)보다 더 많은 문자가 들어오면 안된다.!!! 
				
			if(contents.isBlank()) {
				System.out.println(">> [경고] 댓글내용은 필수로 입력하셔야 합니다.!! << \n");
			}
			else if( contents.length() > 100 ) {
				System.out.println(">> [경고] 댓글내용은 최대 100글자 이내로 입력하셔야 합니다.!! << \n");
			}
			else {
				break;
			}
		// ================================================= //
		} while(true);
		
		
		do {
			System.out.print("▷ 정말로 댓글쓰기를 하시겠습니까?[Y/N] : ");
			String yn = sc.nextLine();
		
			if("y".equalsIgnoreCase(yn)) {
				
				CommentDTO cmtdto = new CommentDTO();
				cmtdto.setFk_boardno(fk_boardno);
				cmtdto.setFk_userid(mbrdto.getUserid());
				cmtdto.setContents(contents);
				
				result = bdao.writeComment(cmtdto);
				//       1 또는 -1 
				
				break;
			}
			
			else if("n".equalsIgnoreCase(yn)) {
				break;
			}
			
			else {
				System.out.println(">> [경고] Y 또는 N 만 입력하세요!! << \n");
			}
			
		} while(true);
		
		return result;
	}// end of private int writeComment(MemberDTO mbrdto, Scanner sc)-------
	
		

	// *** 글수정하기 **** // 
	private int updateBoard(String login_userid, Scanner sc) {
		
		int result = 0;
		
		System.out.println("\n>>> 글 수정하기 <<<");
		
		do {
			System.out.print("▷ 수정할 글번호 : ");
			String boardno = sc.nextLine();
			
			try {
				Integer.parseInt(boardno);
			
				BoardDTO bdto = bdao.viewContents(boardno);
				// 조회수 증가는 없고 단순히 글내용만 보여주기
				
				if(bdto == null) {
					// 수정할 글번호가 글목록에 존재하지 않는 경우
					System.out.println(">> 글번호 "+ boardno +"은(는) 글목록에 존재하지 않습니다. << \n"); 
				}
				
				else {
					// 수정할 글번호가 글목록에 존재하는 경우
					
					if( !login_userid.equals(bdto.getFk_userid()) ) {
						// 수정할 글번호가 다른 사용자가 쓴 글인 경우라면 
						System.out.println(">> [경고] 다른 사용자의 글은 수정 불가합니다.!! \n");
					}
					else {
						// 수정할 글번호가 자신이 쓴 글인 경우라면
						System.out.print("▷ 글암호 : ");
						String boardpasswd = sc.nextLine();
						
						if( !boardpasswd.equals(bdto.getBoardpasswd()) ) {
							// 글암호가 일치하지 않는 경우 
							System.out.println(">> [경고] 입력하신 글암호가 작성시 입력한 글암호와 일치하지 않으므로 수정 불가합니다.!! \n"); 
						}
						
						else {
							// 글암호가 일치하는 경우
							
							System.out.println("-------------------------------------");
							System.out.println("[수정전 글제목] " + bdto.getSubject() );
							System.out.println("[수정전 글내용] " + bdto.getContents() );
							System.out.println("-------------------------------------");
							
							System.out.print("▷ 글제목[최대 100글자, 변경하지 않으려면 그냥 엔터] : ");
							String new_subject = sc.nextLine();
							if(new_subject.isBlank()) {
								new_subject = bdto.getSubject();
							}
							
							System.out.print("▷ 글내용[최대 200글자, 변경하지 않으려면 그냥 엔터] : ");
							String new_contents = sc.nextLine();
							if(new_contents.isBlank()) {
								new_contents = bdto.getContents();
							}
							
							if( new_subject.length() > 100 || new_contents.length() > 200 ) {
								System.out.println(">> [경고] 글제목은 최대 100 글자이며, 글내용은 최대 200 글자 이내이어야 합니다. \n"); 
							}
							else {
								// 글제목의 길이가 100 이하이고, 글내용의 길이가 200 이하인 경우
								
								String yn = "";
								do {
									// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
									System.out.print("▷ 정말로 글수정 하시겠습니까?[Y/N] : ");
									yn = sc.nextLine();
									
									if("y".equalsIgnoreCase(yn)) {
										// 글수정에 들어간다.
										
										Map<String, String> paraMap = new HashMap<>();
										
										paraMap.put("boardno", boardno); // 수정해야할 글번호
										paraMap.put("new_subject", new_subject); // 새 글제목
										paraMap.put("new_contents", new_contents); // 새 글내용 
										
										result = bdao.updateBoard(paraMap); // 글수정
										// result ==>  1 이면 글수정 성공
										// result ==> -1 이면 글수정 실패(SQL구문 오류)
									}
									
									else if("n".equalsIgnoreCase(yn)) {
										// 글수정을 취소한 경우 
										
									}
									
									else {
										System.out.println(">> [경고] Y 또는 N 만 입력하세요!! ");
									}
									// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
								} while( !("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)) );
								
							}
							
						}
						
					}
					
				}
				
				break;
				
			} catch(NumberFormatException e) {
				System.out.println(">> [경고] 글번호는 숫자만 가능합니다.!! <<");
				continue;
			}
			
		} while(true);
		
		return result;
	}// end of private int updateBoard(String login_userid, Scanner sc)------------	
	

	
	// *** 글삭제하기 *** // 
	private int deleteBoard(String login_userid, Scanner sc) {
		
		int result = 0;
		
		System.out.println("\n>>> 글 삭제하기 <<<");
		
		do {
			System.out.print("▷ 삭제할 글번호 : ");
			String boardno = sc.nextLine();
			
			try {
				Integer.parseInt(boardno);
			
				BoardDTO bdto = bdao.viewContents(boardno);
				// 조회수 증가는 없고 단순히 글내용만 보여주기
				
				if(bdto == null) {
					// 삭제할 글번호가 글목록에 존재하지 않는 경우
					System.out.println(">> 글번호 "+ boardno +"은(는) 글목록에 존재하지 않습니다. << \n"); 
				}
				
				else {
					// 삭제할 글번호가 글목록에 존재하는 경우
					
					if( !login_userid.equals(bdto.getFk_userid()) ) {
						// 삭제할 글번호가 다른 사용자가 쓴 글인 경우라면 
						System.out.println(">> [경고] 다른 사용자의 글은 삭제 불가합니다.!! \n");
					}
					else {
						// 삭제할 글번호가 자신이 쓴 글인 경우라면
						System.out.print("▷ 글암호 : ");
						String boardpasswd = sc.nextLine();
						
						if( !boardpasswd.equals(bdto.getBoardpasswd()) ) {
							// 글암호가 일치하지 않는 경우 
							System.out.println(">> [경고] 입력하신 글암호가 작성시 입력한 글암호와 일치하지 않으므로 삭제 불가합니다.!! \n"); 
						}
						
						else {
							// 글암호가 일치하는 경우
							
							System.out.println("-------------------------------------");
							System.out.println("[글제목] " + bdto.getSubject() );
							System.out.println("[글내용] " + bdto.getContents() );
							System.out.println("-------------------------------------");
							
							String yn = "";
							do {
								// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
								System.out.print("▷ 정말로 글삭제 하시겠습니까?[Y/N] : ");
								yn = sc.nextLine();
									
								if("y".equalsIgnoreCase(yn)) {
									// 글삭제에 들어간다.
										
									result = bdao.deleteBoard(boardno); // 글삭제 
									// result ==>  1 이면 글삭제 성공
									// result ==> -1 이면 글삭제 실패(SQL구문 오류)
								}
									
								else if("n".equalsIgnoreCase(yn)) {
									// 글삭제를 취소한 경우 
										
								}
									
								else {
									System.out.println(">> [경고] Y 또는 N 만 입력하세요!! ");
								}
									// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
							} while( !("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn)) );
							
						}
						
					}
					
				}
				
				break;
				
			} catch(NumberFormatException e) {
				System.out.println(">> [경고] 글번호는 숫자만 가능합니다.!! <<");
				continue;
			}
			
		} while(true);
		
		return result;		
		
	}// end of private int deleteBoard(String login_userid, Scanner sc)---------------	
	

	
	// *** 최근1주일간 일자별 게시글 작성건수 *** //
	private void statistics_by_week() {
		
		System.out.println("\n"+"-".repeat(30)+" [최근 1주일간 일자별 게시글 작성건수] "+"-".repeat(30));  
		// 만약 오늘이 2025-04-28 이라면
		/*
		    ------------------------------ [최근 1주일간 일자별 게시글 작성건수] -----------------------------------
             전체    2025-04-22   2025-04-23   2025-04-24   2025-04-25   2025-04-26   2025-04-27   2025-04-28
            ------------------------------------------------------------------------------------------------- 
               4	    0	         0	           3	        1	         0         	  0	          0
		*/
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("전체\t");
		
		for(int i=0; i<7; i++) {
			sb.append( myAddDay(i-6)+"  "); // -6  -5  -4  -3  -2  -1  0
		}// end of for-----------------------------------------------------
		
		sb.append("\n"+"-".repeat(90));
		
		System.out.println(sb.toString());
		
		
		// 최근 1주일내에 작성된 게시글만 DB에서 가져온 결과물
		Map<String, Integer> map = bdao.statistics_by_week();  // select
		// Map 은 1개 행으로 보면 된다.
		
		String result = map.get("TOTAL") + "\t" +
				        map.get("PREVIOUS6") + "\t" +
				        map.get("PREVIOUS5") + "\t" +
				        map.get("PREVIOUS4") + "\t" +
				        map.get("PREVIOUS3") + "\t" +
				        map.get("PREVIOUS2") + "\t" +
				        map.get("PREVIOUS1") + "\t" +
				        map.get("TODAY");
		
		System.out.println(result);
		
	}// end of private void statistics_by_week()---------------------	
	
		
	// ==== 현재일로 부터 일수만큼 더하거나 빼주어서 날짜를 리턴시켜주는 메소드 ==== //
	private String myAddDay(int n) {
		
		Calendar current_now = Calendar.getInstance();
		// 현재날짜와 시간을 얻어온다.
		
		current_now.add(Calendar.DATE, n);
		
		SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
		
		return sdfmt.format(current_now.getTime());
	}// end of private String myAddDay(int n)--------------------------



	// *** 이번달 일자별 게시글 작성건수 *** //
	private void statistics_by_currentMonth() {
		
	/*	
		>>> [2025년 04월 일자별 게시글 작성건수] <<<
		--------------------
		 작성일자      작성건수
		--------------------
		2025-04-24	3
		2025-04-25	1
		전체	        4
		
		
		>>> [2025년 04월 일자별 게시글 작성건수] <<<
		게시된 글이 없습니다.
	*/	
		Calendar currentDate = Calendar.getInstance();
		// 현재날짜와 시간을 얻어온다. 
		
		SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy년 MM월");
		
		String currentMonth = sdfmt.format(currentDate.getTime());
		
		System.out.println("\n>>> ["+currentMonth+" 일자별 게시글 작성건수] <<<");
		
		List<Map<String, String>> mapList = bdao.statistics_by_currentMonth();
		
		if(mapList.size() > 0) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("-".repeat(30)+"\n");
			sb.append(" 작성일자\t  작성건수\t백분율(%)\n");
			sb.append("-".repeat(30)+"\n");
			
			for(Map<String, String> map : mapList) {
				sb.append(map.get("writeday")+"\t"+map.get("cnt")+"\t"+map.get("percentage")+"\n");  
			}// end of for-----------------------
			
			System.out.println(sb.toString());
		}
		else {
			System.out.println(" 게시된 글이 없습니다. \n");
		}
		
	}// end of private void statistics_by_currentMonth()-------------------	
	
	
}
