package com.kyobo.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kyobo.DTO.BasketDTO;
import com.kyobo.DTO.BoardDTO;
import com.kyobo.DTO.BookDTO;
import com.kyobo.DTO.CustomInfo;
import com.kyobo.DAO.BoardDAO;
import com.kyobo.DAO.MemberDAO;
import com.kyobo.DTO.MemberDTO;
import com.kyobo.DTO.OrdersDTO;
import com.util.DBConn;
import com.util.MyUtil;

public class KyoboServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher(url);
		rd.forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Connection conn = DBConn.getConnection();
		
		MemberDAO dao = new MemberDAO(conn);
		
		BoardDAO dao1 = new BoardDAO(conn);
		
		String cp = req.getContextPath();
		
		String uri = req.getRequestURI();
		
		String url;
		
		MyUtil myUtil = new MyUtil();
		
		//파일 저장 경로 (file폴더-test3 참조)
		String root = getServletContext().getRealPath("/");

		String path = root + "pds" + File.separator + "bookImage";
		
		//파일 객체 생성
		File f = new File(path);

		//해당 파일 경로가 없으면 생성
		if(!f.exists()) {
			f.mkdirs();
		}
		
		// 회원가입 페이지 띄우기
		// 회원가입 페이지로 연결
		if(uri.indexOf("created.do")!=-1) {

			url = "/kyobo/created.jsp";
			forward(req, resp, url);

			
		// 회원가입 하기
		// 회원가입 처리후 로그인 페이지로 연결
		} else if(uri.indexOf("created_ok.do")!=-1) {

			//입력값을 받아와 dto에 담은후
			MemberDTO dto = new MemberDTO();
			
			dto.setCusId(req.getParameter("cusId"));
			dto.setCusPwd(req.getParameter("cusPwd"));
			dto.setCusName(req.getParameter("cusName"));
			dto.setCusGender(req.getParameter("cusGender"));
			dto.setCusBirth(req.getParameter("cusBirth"));
			dto.setCusTel(req.getParameter("cusTel"));
			dto.setPostcode(req.getParameter("postcode"));
			dto.setAddress(req.getParameter("address"));
			dto.setDetailAddress(req.getParameter("detailAddress"));
			dto.setCusEmail(req.getParameter("cusEmail"));

			//DB에 회원가입 정보 추가
			dao.insertData(dto);

			url = cp + "/book/login.do";
			resp.sendRedirect(url);


		// 로그인 페이지 띄우기
		}else if(uri.indexOf("login.do")!=-1) {
			
			url = "/kyobo/login.jsp";
			forward(req, resp, url);
		
			
		// 로그인 처리하기
		// 로그인하면 메인페이지로 연결
		}else if(uri.indexOf("login_ok.do")!=-1) {
			
			//id,pwd 입력값 받아오기
			String cusId = req.getParameter("cusId");
			String cusPwd = req.getParameter("cusPwd");
			
			//입력된 계정의 회원정보 읽기
			MemberDTO dto = dao.getReadData(cusId);
			
			
			//id,pwd가 맞지 않으면 다시 입력하게끔
			if(dto==null || (!dto.getCusPwd().equals(cusPwd))) {
			
			req.setAttribute("message",
					"아이디 또는 패스워드를 확인해주세요.");
			
				url = "/kyobo/login.jsp";
				forward(req, resp, url);
				return;
			
			}
			
			//id,pwd이 맞으면 세션 올릴 준비
			
			//세션으로 담을 info 객체 생성
			CustomInfo info = new CustomInfo();
			
			//info에 입력받은 id,pwd 넣음
			info.setCusId(dto.getCusId());
			info.setCusName(dto.getCusName());
			
			//세션 객체 생성
			//서블릿에서 세션값을 쓰거나, 세션을 올리거나 내릴때 반드시 먼저 선언
			HttpSession session = req.getSession();
			
			//custonInfo라는 세션에 info의 정보를 담아 올린다
			//세션을 올리면 왼쪽 폴더 전부에서 쓸 수 있다..!!
			session.setAttribute("customInfo", info);
			
			url = cp + "/book/list.do";
			
			resp.sendRedirect(url);
			
			
		// 로그아웃 하기
		// 로그아웃 하면 메인페이지로 연결
		}else if(uri.indexOf("logout.do")!=-1) {
			
			//서블릿에서 세션을 없애야 하니 먼저 선언
			HttpSession session = req.getSession();
			
			session.removeAttribute("customInfo"); //세션 안의 값만 지움
			session.invalidate(); //세션 자체를 지움
			
			url = cp + "/book/list.do";
			
			resp.sendRedirect(url);
			
			
		// 아이디 중복체크 1번째 팝업창 띄우기 (중복체크값 입력받기)
		}else if(uri.indexOf("idcheck1.do")!=-1) {
		
			url = "/kyobo/idcheck1.jsp";
			forward(req, resp, url);
		
		// 아이디 중복체크 2번째 팝업창 띄우기 (중복체크값 판별하기)
		// 중복체크 하고 확인 누르면 창 꺼짐
		}else if(uri.indexOf("idcheck2.do")!=-1) {
			
			String cusId = req.getParameter("cusId");
			
			MemberDTO dto = dao.getReadData(cusId);
			
			//중복된 아이디가 없을경우
			if(dto==null) {
				req.setAttribute("message",
						cusId + "는 사용가능한 아이디입니다.");
				req.setAttribute("cusId",cusId);
				
				url = "/kyobo/idcheck2.jsp";
				forward(req, resp, url);
				return;
			}
			
			//중복된 아이디가 있을경우
			req.setAttribute("message2",
					cusId + "는 이미 사용중인 아이디입니다..");
			url = "/kyobo/idcheck2.jsp";
			forward(req, resp, url);
			return;
		
			
		// 아이디 찾기 페이지로
		} else if(uri.indexOf("searchCusId.do")!=-1) {
			url = "/kyobo/searchCusId.jsp";
			forward(req, resp, url);


		// 비밀번호 찾기 페이지로
		}else if(uri.indexOf("searchCusPwd.do")!=-1) {
			url = "/kyobo/searchCusPwd.jsp";
			forward(req, resp, url);

			
		// 아이디 찾기 처리
		// 처리되면 더미 페이지에서 아이디 표시해줌
		} else if(uri.indexOf("searchCusId_ok.do")!=-1) {

			//TODO 이름, 전화번호 입력하면 데이터 가져와서 비교하고
			//비교해서 나온 ID를 해당 페이지 하단에 출력하기

			//입력받은 id,pwd 저장
			String cusName = req.getParameter("cusName");
			String cusTel = req.getParameter("cusTel");

			//회원정보 읽어오기
			MemberDTO dto = dao.getDataForId(cusName, cusTel);
			
			//아이디나 비밀번호가 일치하지 않으면
			if(dto == null ){

				req.setAttribute("err_msg", "회원정보가 존재하지 않습니다");

				url = "/kyobo/findCusInfo.jsp";
				forward(req, resp, url);
				return;
			}
			
			//일치한다면 아이디 띄우기
			req.setAttribute("cusIdInfo", dto.getCusId());

			// TODO 로그인 페이지 아래에 비밀번호 띄울 것

			url = "/kyobo/findCusInfo.jsp";
			forward(req, resp, url);
			return;



		// 비밀번호 찾기
		// 처리되면 더미 페이지에서 비밀번호 표시해줌
		} else if(uri.indexOf("searchCusPwd_ok.do")!=-1) {

			String cusId = req.getParameter("cusId");
			String cusName = req.getParameter("cusName");
			String cusTel = req.getParameter("cusTel");

			MemberDTO dto = dao.getDataForPwd(cusId, cusName, cusTel);


			// TODO 정보 없으면 따로 페이지 넘어가지 말고 팝업 창 띄울 것
			if(dto == null ){

				req.setAttribute("err_msg", "회원정보가 존재하지 않습니다");

				url = "/kyobo/findCusInfo.jsp";
				forward(req, resp, url);
				return;
			}

			//req.setAttribute("cusName", dto.getCusName());
			req.setAttribute("pwdInfo", dto.getCusPwd());

			// TODO 로그인 페이지 아래에 비밀번호 띄울 것

			url = "/kyobo/findCusInfo.jsp";
			forward(req, resp, url);
			return;
			
			
		// 회원정보 수정 페이지로
		}else if(uri.indexOf("updated.do")!=-1) {

			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//혹시라도 세션값이 없으면
			if(info==null) {
				url = cp + "/book/login.do";
				resp.sendRedirect(url);
				return;
			}
			
			//기존값 자동 입력을 위해 데이터 읽어오기
			MemberDTO dto = dao.getReadData(info.getCusId());
			
			req.setAttribute("dto", dto);

			url = "/kyobo/updated.jsp";
			forward(req, resp, url);

		
		// 회원정보 수정하기
		// 회원정보 수정 처리하면 메인 화면으로
		} else if(uri.indexOf("updated_ok.do")!=-1) {
			
			//입력값 받아와서 회원정보 update
			MemberDTO dto = new MemberDTO();
			dto.setCusId(req.getParameter("cusId"));
			dto.setCusPwd(req.getParameter("cusPwd"));
			dto.setCusName(req.getParameter("cusName"));
			dto.setCusGender(req.getParameter("cusGender"));
			dto.setCusBirth(req.getParameter("cusBirth"));
			dto.setCusTel(req.getParameter("cusTel"));
			dto.setPostcode(req.getParameter("postcode"));
			dto.setAddress(req.getParameter("address"));
			dto.setDetailAddress(req.getParameter("detailAddress"));
			dto.setCusEmail(req.getParameter("cusEmail"));

			dao.updateData(dto);

			url = cp + "/book/list.do";
			resp.sendRedirect(url);
			
			
		// 회원 탈퇴하기
		}else if(uri.indexOf("deleted_ok.do")!=-1) {
			
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			if(info==null) {
				url = cp + "/book/login.do";
				resp.sendRedirect(url);
				return;
			}
			
			//회원 탈퇴 DAO
			dao.deleteData(info.getCusId());
			
			
			url = "/kyobo/login.jsp";
			forward(req, resp, url);

			
		// 메인 화면 띄우기
		}else if(uri.indexOf("list.do")!=-1) {
			
			url = "/kyobo/list.jsp";
			forward(req, resp, url);
		
			
		// 구매 목록 화면 띄우기 + 페이징 처리
		}else if(uri.indexOf("purchased.do")!=-1) {
	    	  
	    	  HttpSession session = req.getSession();
	    	  CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
	    	  
	    	  String cusId = info.getCusId();
	    	  
	    	  String imagePath = cp + "/pds/bookImage";
	    	  
	    	  
	    	  //구매목록 페이징처리
	    	  String pageNum = req.getParameter("pageNum");
	    	  
	    	  int currentPage = 1;
	    	  
	    	  if(pageNum!=null) {
	    		  currentPage = Integer.parseInt(pageNum);
	    	  }
	    	  
	    	  int dataCount = dao.countingData(cusId);
				
	    	  int numPerPage = 3;
	    	  
	    	  int totalPage = myUtil.getPageCount(numPerPage, dataCount);
	    	  
	    	  if(currentPage>totalPage) {
	    		  currentPage=totalPage;
	    	  }
	    	  
	    	  int start = (currentPage-1)*numPerPage+1;
	    	  int end = currentPage*numPerPage;
	    	  
	    	  List<OrdersDTO> lists = dao.getPurchasedItems(info.getCusId(), start, end);
	    	  
	    	  String listUrl = cp + "/book/purchased.do";
	    	  String pageIndexList = 
	    			  myUtil.pageIndexList(currentPage, totalPage, listUrl);
	    	  
	    	  //String imagePath = cp + "/pds/ ";//실제경로
	    	  //req.setAttribute("imagePath", imagePath);
	    	  
	    	  req.setAttribute("lists", lists);
	    	  req.setAttribute("pageIndexList", pageIndexList);
	    	  req.setAttribute("dataCount", dataCount);
	    	  req.setAttribute("currentPage", currentPage);
	    	  req.setAttribute("totalPage", totalPage);
	    	  req.setAttribute("numPerPage", numPerPage);
	    	  req.setAttribute("imagePath", imagePath);
	    	  
	    	  url = "/kyobo/purchasedList.jsp";
	    	  forward(req,resp,url);
	    	  
	    	  
	    // 일반 검색시 검색 목록 띄우기 + 페이징/검색처리
	    }else if(uri.indexOf("bookList.do")!=-1) {
			
	    	
	    	//페이징,검색 처리
			String pageNum = req.getParameter("pageNum");
			
			int currentPage = 1;
			
			if(pageNum!=null){ //넘어오는 페이지 번호가 있다면
				currentPage = Integer.parseInt(pageNum);
			}
			
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");
			
			if(searchValue==null || searchValue.equals("")) {
				searchKey = "bookName";
				searchValue = "";
			}else {
				if(req.getMethod().equalsIgnoreCase("GET")){
					searchValue = URLDecoder.decode(searchValue, "UTF-8");
				}
			}
			
			int dataCount = dao.getDataCountBook(searchKey, searchValue);
			int numPerPage = 3;
			int totalPage = myUtil.getPageCount(numPerPage, dataCount);
			
			if(currentPage>totalPage) {
				currentPage = totalPage;
			}
			
			int start = (currentPage-1)*numPerPage+1; 
			int end = currentPage*numPerPage;
			
			List<BookDTO> lists = dao.getLists(start,end,searchKey,searchValue);
			
			String param = "";
			
			//검색값이 있다면 param으로 검색값 넘길 준비
			if(!searchValue.equals("")&&searchValue!=null) {
				param = "searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
			
			String listUrl = cp + "/book/bookList.do";
			
			//검색값이 있으면 listUrl뒤에 ?를 붙이고 뒤에 검색값을 넣는다
			if(!param.equals("")) {
				listUrl += "?" + param;
			}
			
			String pageIndexList = 
					myUtil.pageIndexList(currentPage, totalPage, listUrl);
			
			//이미지 경로 저장
			String imagePath = cp + "/pds/bookImage";
			
			//일반 검색시에만 구매목록 검색창에 검색값 자동 입력을 위해
			String judge = "yes";
			
			req.setAttribute("currentPage", currentPage);
			req.setAttribute("numPerPage", numPerPage);
			req.setAttribute("imagePath", imagePath);
			req.setAttribute("pageIndexList", pageIndexList);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("lists", lists);
			//관리자 삭제시 페이징,검색 유지를 위해 전달 
			req.setAttribute("searchKeyData", searchKey);
			req.setAttribute("searchValueData", searchValue);
			req.setAttribute("pageNumData", pageNum);
			req.setAttribute("judge", judge);
			
			url = "/kyobo/bookList.jsp";
			forward(req, resp, url);
			

			
			
		// 카테고리1 검색 페이지 띄우기 + 페이징/검색처리
	    }else if(uri.indexOf("bookList1.do")!=-1) {
				
	    	String pageNum = req.getParameter("pageNum");

	    	int currentPage = 1;

	    	if(pageNum!=null){ //넘어오는 페이지 번호가 있다면
	    		currentPage = Integer.parseInt(pageNum);
	    	}

	    	String searchKey = "bookCate1";
	    	String searchValue = req.getParameter("searchValue");

	    	if(searchValue==null || searchValue.equals("")) {
	    		searchKey = "bookCate1";
	    		searchValue = "";
	    	}else {
	    		if(req.getMethod().equalsIgnoreCase("GET")){
	    			searchValue = URLDecoder.decode(searchValue, "UTF-8");
	    		}
	    	}

	    	int dataCount = dao.getDataCountBook(searchKey, searchValue);
	    	int numPerPage = 3;
	    	int totalPage = myUtil.getPageCount(numPerPage, dataCount);
	    	System.out.println(totalPage);

	    	if(currentPage>totalPage) {
	    		currentPage = totalPage;
	    	}

	    	int start = (currentPage-1)*numPerPage+1; 
	    	int end = currentPage*numPerPage;

	    	List<BookDTO> lists = dao.getLists(start,end,searchKey,searchValue);

	    	String param = "";

	    	//검색값이 있다면 param으로 검색값 넘길 준비
	    	if(!searchValue.equals("")&&searchValue!=null) {
	    		param = "searchKey=" + searchKey;
	    		param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
	    	}

	    	String listUrl = cp + "/book/bookList.do";

	    	//검색값이 있으면 listUrl뒤에 ?를 붙이고 뒤에 검색값을 넣는다
	    	if(!param.equals("")) {
	    		listUrl += "?" + param;
	    	}

	    	String pageIndexList = 
	    			myUtil.pageIndexList(currentPage, totalPage, listUrl);

	    	//이미지 경로 저장
	    	String imagePath = cp + "/pds/bookImage";

	    	req.setAttribute("currentPage", currentPage);
	    	req.setAttribute("numPerPage", numPerPage);
	    	req.setAttribute("imagePath", imagePath);
	    	req.setAttribute("pageIndexList", pageIndexList);
	    	req.setAttribute("dataCount", dataCount);
	    	req.setAttribute("lists", lists);


	    	url = "/kyobo/bookList.jsp";
	    	forward(req, resp, url);

				
	    // 카테고리2 검색 페이지 띄우기 + 페이징/검색처리
	    }else if(uri.indexOf("bookList2.do")!=-1) {

	    	String pageNum = req.getParameter("pageNum");

	    	int currentPage = 1;

	    	if(pageNum!=null){ //넘어오는 페이지 번호가 있다면
	    		currentPage = Integer.parseInt(pageNum);
	    	}

	    	String searchKey = "bookCate2";
	    	String searchValue = req.getParameter("searchValue");

	    	if(searchValue==null || searchValue.equals("")) {
	    		searchKey = "bookCate2";
	    		searchValue = "";
	    	}else {
	    		if(req.getMethod().equalsIgnoreCase("GET")){
	    			searchValue = URLDecoder.decode(searchValue, "UTF-8");
	    		}
	    	}

	    	int dataCount = dao.getDataCountBook(searchKey, searchValue);
	    	int numPerPage = 3;
	    	int totalPage = myUtil.getPageCount(numPerPage, dataCount);

	    	if(currentPage>totalPage) {
	    		currentPage = totalPage;
	    	}

	    	int start = (currentPage-1)*numPerPage+1; 
	    	int end = currentPage*numPerPage;

	    	List<BookDTO> lists = dao.getLists(start,end,searchKey,searchValue);

	    	String param = "";

	    	//검색값이 있다면 param으로 검색값 넘길 준비
	    	if(!searchValue.equals("")&&searchValue!=null) {
	    		param = "searchKey=" + searchKey;
	    		param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
	    	}

	    	String listUrl = cp + "/book/bookList.do";

	    	//검색값이 있으면 listUrl뒤에 ?를 붙이고 뒤에 검색값을 넣는다
	    	if(!param.equals("")) {
	    		listUrl += "?" + param;
	    	}

	    	String pageIndexList = 
	    			myUtil.pageIndexList(currentPage, totalPage, listUrl);

	    	//이미지 경로 저장
	    	String imagePath = cp + "/pds/bookImage";

	    	req.setAttribute("currentPage", currentPage);
	    	req.setAttribute("numPerPage", numPerPage);
	    	req.setAttribute("imagePath", imagePath);
	    	req.setAttribute("pageIndexList", pageIndexList);
	    	req.setAttribute("dataCount", dataCount);
	    	req.setAttribute("lists", lists);


	    	url = "/kyobo/bookList.jsp";
	    	forward(req, resp, url);


	    // 관리자 책 테이블 삭제 + 페이징/검색처리 유지하면서
	    // 삭제 처리하면 다시 검색 페이지로
	    }else if(uri.indexOf("deleteBookData.do")!=-1) {
				  
			//페이징,검색값 유지를 위해 get방식으로 값 받아오기
			int bookNumber = Integer.parseInt(req.getParameter("bookNumber"));
			String pageNum = req.getParameter("pageNumData");
			String searchKey = req.getParameter("searchKeyData");
			String searchValue = req.getParameter("searchValueData");
			
			//해당 책 삭제
			dao.deleteBookData(bookNumber);
			 
			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//페이지 이동 없이 책 삭제시 첫 페이지로 띄우기 위해
			if(pageNum.equals("")) {
				pageNum = "1";
			}
			
			//페이징,검색에 필요한 값을 주소에 넣기
			String param = "pageNum=" + pageNum;
			if(searchValue!=null&&!searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
			}
			
			url = cp + "/book/bookList.do?" + param;
			resp.sendRedirect(url);

			
		//문의게시판 글쓰기 페이지로
		}else if(uri.indexOf("write.do")!=-1) {

			url = "/kyobo/write.jsp";
			forward(req, resp, url);

			
		//문의게시판에 글쓰기
		}else if(uri.indexOf("write_ok.do")!=-1) {
			
			BoardDTO dto = new BoardDTO();
			
			//문의게시판 전체 글 갯수 구하기
			int maxNum = dao1.getMaxNum();
			
			dto.setInqNumber(maxNum + 1);
			dto.setInqType(req.getParameter("inqType"));
			dto.setSubject(req.getParameter("subject"));
			dto.setCusId(req.getParameter("cusId"));
			dto.setCusName(req.getParameter("cusName"));
			dto.setContent(req.getParameter("content"));
			dto.setInqPwd(req.getParameter("inqPwd"));
			
			//입력한 값을 update
			dao1.insertBoard(dto);
			
			url = cp + "/book/boardList.do";
			resp.sendRedirect(url);
			
			
		// 문의게시판 메인 띄우기
		} else if(uri.indexOf("boardList.do")!=-1) {

			url = "/kyobo/boardList.jsp"; 
			forward(req, resp, url);

			
		// 문의게시판 수정페이지 띄우기
		} else if(uri.indexOf("boardUpdate.do")!=-1) {
			
			//수정할 문의글 번호 받기
			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			
			//페이징,검색처리 정보 받기
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//빈칸에 기존값을 자동 입력하기 위해 데이터 불러오기
			BoardDTO dto = dao1.getReadData(inqNumber);

			if(dto==null) {
				url = cp + "/book/boardList.do";
				resp.sendRedirect(url);
			}
			
			//param에 페이징/검색값 넣기
			String param = "pageNum=" + pageNum;

			if(searchValue!=null&&!searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");          
			}

			req.setAttribute("dto", dto);
			req.setAttribute("pageNum", pageNum); 
			req.setAttribute("params", param); 
			req.setAttribute("searchKey",searchKey); 
			req.setAttribute("searchValue",searchValue); 

			url = "/kyobo/boardUpdate.jsp"; 
			forward(req, resp, url);

		
		// 게시글 수정처리
		}else if(uri.indexOf("boardUpdate_ok.do")!=-1) {
			
			BoardDTO dto = new BoardDTO();
			
			dto.setInqNumber(Integer.parseInt(req.getParameter("inqNumber")));
			dto.setCusId(req.getParameter("cusId"));
			dto.setInqType(req.getParameter("inqType"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setInqPwd(req.getParameter("inqPwd"));

			dao1.updateBoard(dto);

			url = cp + "/book/boardList.do";
			resp.sendRedirect(url);
			
			
		// 게시글 정보 보기
		}else if(uri.indexOf("boardArticle.do")!=-1) {

			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			String pageNum = req.getParameter("pageNum");

			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//게시글 정보 불러오기
			BoardDTO dto = dao1.getReadData(inqNumber);
			
			if(dto==null) {
				url = cp + "/book/boardList.do";
				resp.sendRedirect(url);
			}
			
			
			int lineSu = dto.getContent().split("\n").length;

			dto.setContent(dto.getContent().replaceAll("\r\n", "<br/>"));

			String param = "pageNum=" + pageNum;

			if(searchValue!=null&&!searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
			}

			req.setAttribute("dto", dto);
			req.setAttribute("params", param);
			req.setAttribute("lineSu", lineSu);
			req.setAttribute("pageNum", pageNum);

			url = "/kyobo/boardArticle.jsp";
			forward(req,resp,url);
			
			
		// 문의게시글 삭제
		}else if(uri.indexOf("boardDelete_ok.do")!=-1){

			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			dao1.boardDeleteData(inqNumber);


			url = cp + "/book/boardList.do";
			resp.sendRedirect(url);
			
			
		// 상품 정보 페이지 띄우기
		}else if(uri.indexOf("article.do")!=-1) {
			
			//선택한 책 이름 받아오기
			String bookName = req.getParameter("bookName");
			
			String imagePath = cp + "/pds/bookImage";

			//선택한 책 정보 읽어옴
			BookDTO dto = dao.getReadDataBook(bookName);
			
			req.setAttribute("imagePath", imagePath);
			req.setAttribute("dto", dto);
			
			url = "/kyobo/article.jsp"; 
			forward(req, resp, url);
			
		
		//장바구니에 추가하기
		//장바구니 추가후 이전 페이지로 돌아감
		}else if(uri.indexOf("basketInsertData.do")!=-1) {
			
			//장바구니는 세션을 이용하기 때문에
			//서블릿에서 세션을 받을 준비를 자주 한다..
			
			//장바구니에 넣을 책 이름 받기
			String bookName = req.getParameter("bookName");
			
			//장바구니에 세션ID값을 받아와야 하므로 세션값 받을 준비
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//장바구니에 넣는데 필요한 값 읽어오기
			String cusId = info.getCusId();
			BookDTO dto = dao.getReadDataBook(bookName);
			int basNumber = dao.basketGetMaxNum()+1;
			
			
			//장바구니에 추가
			BasketDTO dto1 = new BasketDTO();

			dto1.setBasNumber(basNumber);
			dto1.setCusId(cusId);
			dto1.setBookNumber(dto.getBookNumber());
			dto1.setBookName(dto.getBookName());
			dto1.setBookWriter(dto.getBookWriter());
			dto1.setBookPublisher(dto.getBookPublisher());
			dto1.setBookPrice(dto.getBookPrice());
			dto1.setSaveFileName(dto.getSaveFileName());

			dao.basketInsertData(dto1);

			
			//임의의 페이지에 알림창 띄우고 이전 화면으로 돌아가기
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter w = resp.getWriter();
			w.write("<script>alert('장바구니에 추가되었습니다.');history.go(-1);</script>");
			w.flush();
			w.close();

	    
		// 장바구니 페이지 띄우기
		} else if(uri.indexOf("basket.do")!=-1) {
			
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//장바구니 데이터를 읽어와 list로 저장
			List<BasketDTO> lists = dao.getBasketList(info.getCusId());

			String imagePath = cp + "/pds/bookImage";


			req.setAttribute("imagePath", imagePath);
			req.setAttribute("lists", lists);

			url = "/kyobo/basket.jsp";
			forward(req, resp, url);
			return;

				
		// 장바구니 데이터 삭제
		// 삭제 할때마다 장바구니 페이지를 다시 띄움
		} else if(uri.indexOf("deleteData_ok.do")!=-1) {
				
			int basNumber = Integer.parseInt(req.getParameter("basNumber"));

			dao.deleteBasData(basNumber);

			url = cp + "/book/basket.do";
			resp.sendRedirect(url);
				
			
		// 장바구니에서 선택한 상품들을 구매 페이지에 띄우기
		} else if(uri.indexOf("orderPage.do")!=-1) {
			
			//checkbox에서 체크된 상품의 value값을 Name배열에 저장
			//여러개의 상품을 받아와야 하기 때문에 배열을 쓴다
			//basket.jsp안의 checkbox 이름이 check이기 때문에 check로 받는 것
			//getParameterValues를 쓰면 checkbox에서 날아온
			//여러개의 정보를 서블릿에서 받을 수 있다
			String[] Name = req.getParameterValues("check");
			List<BookDTO> lists = new ArrayList<BookDTO>();
			
			//총 구매금액을 표시하기 위해 totalPrice로 초기값 설정
			int totalPrice = 0;
			
			//배열로 받아온 정보를 확장for문으로 데이터를 읽어온다
			//총 구매금액은 가격을 받아올때마다 더해준다
			for(String data : Name) {

				String bookName = data;

				BookDTO dto = dao.getReadDataBook(bookName);
				totalPrice += dto.getBookPrice();
				
				//받아온 정보들을 하나씩 lists에 저장
				lists.add(dto);

			}

			String imagePath = cp + "/pds/bookImage";

			req.setAttribute("imagePath", imagePath);
			req.setAttribute("lists", lists);
			req.setAttribute("totalPrice", totalPrice);

			url = "/kyobo/basketBuy.jsp";
			forward(req, resp, url);
			return;
				
		
		// 상품정보 창에서 구매하기 클릭시
		// 구매 페이지로 연결
		}else if(uri.indexOf("buy.do")!=-1) {
	        
			String bookName = req.getParameter("bookName");
			String imagePath = cp + "/pds/bookImage";
			
			BookDTO dto = dao.getReadDataBook(bookName);


			req.setAttribute("imagePath", imagePath);
			req.setAttribute("dto", dto);

			url = "/kyobo/buy.jsp"; 
			forward(req, resp, url);

			
		// 결제 페이지에서 구매 확정후 orders 테이블에 추가하기
		// 구매 처리후 구매 완료 페이지로 연결
		}else if(uri.indexOf("buy_ok")!=-1) {
			
			//주문 테이블에 세션의 id를 넣어야 하므로 세션을 받을 준비
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//주문 테이블에 넣을 값(id,책이름) 설정
			String cusId = info.getCusId();
			String bookName = req.getParameter("book");
			
			//주문번호와 주문날짜 변수 초기값 설정
			int OrderNumber = 0;
			String date = null;
			
			//주문한 책 정보를 book테이블에서 읽어옴
			BookDTO dto = dao.getReadDataBook(bookName);
			
			//주문번호를 입력하기 위해 orders테이블의 전체 개수를 읽어와 +1
			int maxNum = dao.ordersGetMaxNum()+1;
			
			
			//orders테이블에 구매정보 insert
			OrdersDTO dto1 = new OrdersDTO();

			dto1.setOrderNum(maxNum);
			dto1.setCusId(cusId);
			dto1.setBookNumber(dto.getBookNumber());
			dto1.setBookName(dto.getBookName());
			dto1.setBookWriter(dto.getBookWriter());
			dto1.setBookPublisher(dto.getBookPublisher());
			dto1.setBookDate(dto.getBookDate());
			dto1.setBookPrice(dto.getBookPrice());
			dto1.setSaveFileName(dto.getSaveFileName());

			dao.ordersInsertData(dto1);

			
			//구매완료 페이지에서 주문번호와 주문날짜를 표시하기 위해
			//각각 변수에 저장후 buyComplete.jsp로 보낼준비
			OrderNumber = maxNum;
			date = dao.getReadDataOrderDate(maxNum);


			req.setAttribute("OrderNumber", OrderNumber);
			req.setAttribute("BookName", bookName);
			req.setAttribute("BookPrice", dto.getBookPrice());
			req.setAttribute("date", date);

			url = "/kyobo/buyComplete.jsp";
			forward(req, resp, url);
			return;
				
		
		//장바구니->구매페이지에서 구매 확정시
		// 구매 처리후 구매 완료 페이지로 연결
		} else if(uri.indexOf("basketBuy_ok")!=-1) {
			
			//주문 테이블에 세션의 id를 넣어야 하므로 세션을 받을 준비
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//주문 테이블에 넣을 값(id,책이름) 설정
			String cusId = info.getCusId();
			
			//여기선 여러개의 값을 받아야 하므로 배열과 getParameterValues로 받아준다
			String[] book = req.getParameterValues("book");
			
			//장바구니 구매는 여러개를 구매하기 때문에 총액을 보낼 준비 
			String totalPrice = req.getParameter("totalPrice");
			
			//구매한 상품의 개수와 주문번호를 표시하기 위해 변수로 초기값 설정
			int count = 0;
			int maxNum = 0;

			//주문번호와 주문날짜 책이름 변수 초기값 설정
			int OrderNumber = 0;
			String BookName = null;
			String date = null;

			//배열로 값을 받았으므로 확장for문으로 하나씩 orders테이블에 저장한다
			for(String data : book) {

				String bookName = data;

				BookDTO dto = dao.getReadDataBook(bookName);
				
				//주문번호를 입력하기 위해 orders테이블의 전체 개수를 읽어와 +1
				maxNum = dao.ordersGetMaxNum()+1;
				
				
				//각각의 상품 구매정보를 orders테이블에 insert
				OrdersDTO dto1 = new OrdersDTO();

				dto1.setOrderNum(maxNum);
				dto1.setCusId(cusId);
				dto1.setBookNumber(dto.getBookNumber());
				dto1.setBookName(dto.getBookName());
				dto1.setBookWriter(dto.getBookWriter());
				dto1.setBookPublisher(dto.getBookPublisher());
				dto1.setBookDate(dto.getBookDate());
				dto1.setBookPrice(dto.getBookPrice());
				dto1.setSaveFileName(dto.getSaveFileName());

				dao.ordersInsertData(dto1);
				
				//구매한 상품은 장바구니에서 삭제
				dao.deleteBasDataOrder(cusId,bookName);

				count++;

				//구매완료 페이지에서 @외 2개 이런식으로 표시할 예정이므로
				//처음으로 넣는 책의 정보를 변수에 저장해 jsp로 보낼 준비를 한다
				if(count==1) {
					
					OrderNumber = maxNum;
					BookName = dto.getBookName();
					date = dao.getReadDataOrderDate(maxNum);

				}
			}

			req.setAttribute("OrderNumber", OrderNumber);
			req.setAttribute("BookName", BookName);
			req.setAttribute("totalPrice", totalPrice);
			req.setAttribute("date", date);
			req.setAttribute("count", count);

			url = "/kyobo/buyComplete.jsp";
			forward(req, resp, url);
			return;

		}
		
		
	}
	
	
	
}
