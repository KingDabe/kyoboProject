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
		
		//���� ���� ��� (file����-test3 ����)
		String root = getServletContext().getRealPath("/");

		String path = root + "pds" + File.separator + "bookImage";
		
		//���� ��ü ����
		File f = new File(path);

		//�ش� ���� ��ΰ� ������ ����
		if(!f.exists()) {
			f.mkdirs();
		}
		
		// ȸ������ ������ ����
		// ȸ������ �������� ����
		if(uri.indexOf("created.do")!=-1) {

			url = "/kyobo/created.jsp";
			forward(req, resp, url);

			
		// ȸ������ �ϱ�
		// ȸ������ ó���� �α��� �������� ����
		} else if(uri.indexOf("created_ok.do")!=-1) {

			//�Է°��� �޾ƿ� dto�� ������
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

			//DB�� ȸ������ ���� �߰�
			dao.insertData(dto);

			url = cp + "/book/login.do";
			resp.sendRedirect(url);


		// �α��� ������ ����
		}else if(uri.indexOf("login.do")!=-1) {
			
			url = "/kyobo/login.jsp";
			forward(req, resp, url);
		
			
		// �α��� ó���ϱ�
		// �α����ϸ� ������������ ����
		}else if(uri.indexOf("login_ok.do")!=-1) {
			
			//id,pwd �Է°� �޾ƿ���
			String cusId = req.getParameter("cusId");
			String cusPwd = req.getParameter("cusPwd");
			
			//�Էµ� ������ ȸ������ �б�
			MemberDTO dto = dao.getReadData(cusId);
			
			
			//id,pwd�� ���� ������ �ٽ� �Է��ϰԲ�
			if(dto==null || (!dto.getCusPwd().equals(cusPwd))) {
			
			req.setAttribute("message",
					"���̵� �Ǵ� �н����带 Ȯ�����ּ���.");
			
				url = "/kyobo/login.jsp";
				forward(req, resp, url);
				return;
			
			}
			
			//id,pwd�� ������ ���� �ø� �غ�
			
			//�������� ���� info ��ü ����
			CustomInfo info = new CustomInfo();
			
			//info�� �Է¹��� id,pwd ����
			info.setCusId(dto.getCusId());
			info.setCusName(dto.getCusName());
			
			//���� ��ü ����
			//�������� ���ǰ��� ���ų�, ������ �ø��ų� ������ �ݵ�� ���� ����
			HttpSession session = req.getSession();
			
			//custonInfo��� ���ǿ� info�� ������ ��� �ø���
			//������ �ø��� ���� ���� ���ο��� �� �� �ִ�..!!
			session.setAttribute("customInfo", info);
			
			url = cp + "/book/list.do";
			
			resp.sendRedirect(url);
			
			
		// �α׾ƿ� �ϱ�
		// �α׾ƿ� �ϸ� ������������ ����
		}else if(uri.indexOf("logout.do")!=-1) {
			
			//�������� ������ ���־� �ϴ� ���� ����
			HttpSession session = req.getSession();
			
			session.removeAttribute("customInfo"); //���� ���� ���� ����
			session.invalidate(); //���� ��ü�� ����
			
			url = cp + "/book/list.do";
			
			resp.sendRedirect(url);
			
			
		// ���̵� �ߺ�üũ 1��° �˾�â ���� (�ߺ�üũ�� �Է¹ޱ�)
		}else if(uri.indexOf("idcheck1.do")!=-1) {
		
			url = "/kyobo/idcheck1.jsp";
			forward(req, resp, url);
		
		// ���̵� �ߺ�üũ 2��° �˾�â ���� (�ߺ�üũ�� �Ǻ��ϱ�)
		// �ߺ�üũ �ϰ� Ȯ�� ������ â ����
		}else if(uri.indexOf("idcheck2.do")!=-1) {
			
			String cusId = req.getParameter("cusId");
			
			MemberDTO dto = dao.getReadData(cusId);
			
			//�ߺ��� ���̵� �������
			if(dto==null) {
				req.setAttribute("message",
						cusId + "�� ��밡���� ���̵��Դϴ�.");
				req.setAttribute("cusId",cusId);
				
				url = "/kyobo/idcheck2.jsp";
				forward(req, resp, url);
				return;
			}
			
			//�ߺ��� ���̵� �������
			req.setAttribute("message2",
					cusId + "�� �̹� ������� ���̵��Դϴ�..");
			url = "/kyobo/idcheck2.jsp";
			forward(req, resp, url);
			return;
		
			
		// ���̵� ã�� ��������
		} else if(uri.indexOf("searchCusId.do")!=-1) {
			url = "/kyobo/searchCusId.jsp";
			forward(req, resp, url);


		// ��й�ȣ ã�� ��������
		}else if(uri.indexOf("searchCusPwd.do")!=-1) {
			url = "/kyobo/searchCusPwd.jsp";
			forward(req, resp, url);

			
		// ���̵� ã�� ó��
		// ó���Ǹ� ���� ���������� ���̵� ǥ������
		} else if(uri.indexOf("searchCusId_ok.do")!=-1) {

			//TODO �̸�, ��ȭ��ȣ �Է��ϸ� ������ �����ͼ� ���ϰ�
			//���ؼ� ���� ID�� �ش� ������ �ϴܿ� ����ϱ�

			//�Է¹��� id,pwd ����
			String cusName = req.getParameter("cusName");
			String cusTel = req.getParameter("cusTel");

			//ȸ������ �о����
			MemberDTO dto = dao.getDataForId(cusName, cusTel);
			
			//���̵� ��й�ȣ�� ��ġ���� ������
			if(dto == null ){

				req.setAttribute("err_msg", "ȸ�������� �������� �ʽ��ϴ�");

				url = "/kyobo/findCusInfo.jsp";
				forward(req, resp, url);
				return;
			}
			
			//��ġ�Ѵٸ� ���̵� ����
			req.setAttribute("cusIdInfo", dto.getCusId());

			// TODO �α��� ������ �Ʒ��� ��й�ȣ ��� ��

			url = "/kyobo/findCusInfo.jsp";
			forward(req, resp, url);
			return;



		// ��й�ȣ ã��
		// ó���Ǹ� ���� ���������� ��й�ȣ ǥ������
		} else if(uri.indexOf("searchCusPwd_ok.do")!=-1) {

			String cusId = req.getParameter("cusId");
			String cusName = req.getParameter("cusName");
			String cusTel = req.getParameter("cusTel");

			MemberDTO dto = dao.getDataForPwd(cusId, cusName, cusTel);


			// TODO ���� ������ ���� ������ �Ѿ�� ���� �˾� â ��� ��
			if(dto == null ){

				req.setAttribute("err_msg", "ȸ�������� �������� �ʽ��ϴ�");

				url = "/kyobo/findCusInfo.jsp";
				forward(req, resp, url);
				return;
			}

			//req.setAttribute("cusName", dto.getCusName());
			req.setAttribute("pwdInfo", dto.getCusPwd());

			// TODO �α��� ������ �Ʒ��� ��й�ȣ ��� ��

			url = "/kyobo/findCusInfo.jsp";
			forward(req, resp, url);
			return;
			
			
		// ȸ������ ���� ��������
		}else if(uri.indexOf("updated.do")!=-1) {

			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//Ȥ�ö� ���ǰ��� ������
			if(info==null) {
				url = cp + "/book/login.do";
				resp.sendRedirect(url);
				return;
			}
			
			//������ �ڵ� �Է��� ���� ������ �о����
			MemberDTO dto = dao.getReadData(info.getCusId());
			
			req.setAttribute("dto", dto);

			url = "/kyobo/updated.jsp";
			forward(req, resp, url);

		
		// ȸ������ �����ϱ�
		// ȸ������ ���� ó���ϸ� ���� ȭ������
		} else if(uri.indexOf("updated_ok.do")!=-1) {
			
			//�Է°� �޾ƿͼ� ȸ������ update
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
			
			
		// ȸ�� Ż���ϱ�
		}else if(uri.indexOf("deleted_ok.do")!=-1) {
			
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			if(info==null) {
				url = cp + "/book/login.do";
				resp.sendRedirect(url);
				return;
			}
			
			//ȸ�� Ż�� DAO
			dao.deleteData(info.getCusId());
			
			
			url = "/kyobo/login.jsp";
			forward(req, resp, url);

			
		// ���� ȭ�� ����
		}else if(uri.indexOf("list.do")!=-1) {
			
			url = "/kyobo/list.jsp";
			forward(req, resp, url);
		
			
		// ���� ��� ȭ�� ���� + ����¡ ó��
		}else if(uri.indexOf("purchased.do")!=-1) {
	    	  
	    	  HttpSession session = req.getSession();
	    	  CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
	    	  
	    	  String cusId = info.getCusId();
	    	  
	    	  String imagePath = cp + "/pds/bookImage";
	    	  
	    	  
	    	  //���Ÿ�� ����¡ó��
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
	    	  
	    	  //String imagePath = cp + "/pds/ ";//�������
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
	    	  
	    	  
	    // �Ϲ� �˻��� �˻� ��� ���� + ����¡/�˻�ó��
	    }else if(uri.indexOf("bookList.do")!=-1) {
			
	    	
	    	//����¡,�˻� ó��
			String pageNum = req.getParameter("pageNum");
			
			int currentPage = 1;
			
			if(pageNum!=null){ //�Ѿ���� ������ ��ȣ�� �ִٸ�
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
			
			//�˻����� �ִٸ� param���� �˻��� �ѱ� �غ�
			if(!searchValue.equals("")&&searchValue!=null) {
				param = "searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
			}
			
			String listUrl = cp + "/book/bookList.do";
			
			//�˻����� ������ listUrl�ڿ� ?�� ���̰� �ڿ� �˻����� �ִ´�
			if(!param.equals("")) {
				listUrl += "?" + param;
			}
			
			String pageIndexList = 
					myUtil.pageIndexList(currentPage, totalPage, listUrl);
			
			//�̹��� ��� ����
			String imagePath = cp + "/pds/bookImage";
			
			//�Ϲ� �˻��ÿ��� ���Ÿ�� �˻�â�� �˻��� �ڵ� �Է��� ����
			String judge = "yes";
			
			req.setAttribute("currentPage", currentPage);
			req.setAttribute("numPerPage", numPerPage);
			req.setAttribute("imagePath", imagePath);
			req.setAttribute("pageIndexList", pageIndexList);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("lists", lists);
			//������ ������ ����¡,�˻� ������ ���� ���� 
			req.setAttribute("searchKeyData", searchKey);
			req.setAttribute("searchValueData", searchValue);
			req.setAttribute("pageNumData", pageNum);
			req.setAttribute("judge", judge);
			
			url = "/kyobo/bookList.jsp";
			forward(req, resp, url);
			

			
			
		// ī�װ�1 �˻� ������ ���� + ����¡/�˻�ó��
	    }else if(uri.indexOf("bookList1.do")!=-1) {
				
	    	String pageNum = req.getParameter("pageNum");

	    	int currentPage = 1;

	    	if(pageNum!=null){ //�Ѿ���� ������ ��ȣ�� �ִٸ�
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

	    	//�˻����� �ִٸ� param���� �˻��� �ѱ� �غ�
	    	if(!searchValue.equals("")&&searchValue!=null) {
	    		param = "searchKey=" + searchKey;
	    		param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
	    	}

	    	String listUrl = cp + "/book/bookList.do";

	    	//�˻����� ������ listUrl�ڿ� ?�� ���̰� �ڿ� �˻����� �ִ´�
	    	if(!param.equals("")) {
	    		listUrl += "?" + param;
	    	}

	    	String pageIndexList = 
	    			myUtil.pageIndexList(currentPage, totalPage, listUrl);

	    	//�̹��� ��� ����
	    	String imagePath = cp + "/pds/bookImage";

	    	req.setAttribute("currentPage", currentPage);
	    	req.setAttribute("numPerPage", numPerPage);
	    	req.setAttribute("imagePath", imagePath);
	    	req.setAttribute("pageIndexList", pageIndexList);
	    	req.setAttribute("dataCount", dataCount);
	    	req.setAttribute("lists", lists);


	    	url = "/kyobo/bookList.jsp";
	    	forward(req, resp, url);

				
	    // ī�װ�2 �˻� ������ ���� + ����¡/�˻�ó��
	    }else if(uri.indexOf("bookList2.do")!=-1) {

	    	String pageNum = req.getParameter("pageNum");

	    	int currentPage = 1;

	    	if(pageNum!=null){ //�Ѿ���� ������ ��ȣ�� �ִٸ�
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

	    	//�˻����� �ִٸ� param���� �˻��� �ѱ� �غ�
	    	if(!searchValue.equals("")&&searchValue!=null) {
	    		param = "searchKey=" + searchKey;
	    		param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
	    	}

	    	String listUrl = cp + "/book/bookList.do";

	    	//�˻����� ������ listUrl�ڿ� ?�� ���̰� �ڿ� �˻����� �ִ´�
	    	if(!param.equals("")) {
	    		listUrl += "?" + param;
	    	}

	    	String pageIndexList = 
	    			myUtil.pageIndexList(currentPage, totalPage, listUrl);

	    	//�̹��� ��� ����
	    	String imagePath = cp + "/pds/bookImage";

	    	req.setAttribute("currentPage", currentPage);
	    	req.setAttribute("numPerPage", numPerPage);
	    	req.setAttribute("imagePath", imagePath);
	    	req.setAttribute("pageIndexList", pageIndexList);
	    	req.setAttribute("dataCount", dataCount);
	    	req.setAttribute("lists", lists);


	    	url = "/kyobo/bookList.jsp";
	    	forward(req, resp, url);


	    // ������ å ���̺� ���� + ����¡/�˻�ó�� �����ϸ鼭
	    // ���� ó���ϸ� �ٽ� �˻� ��������
	    }else if(uri.indexOf("deleteBookData.do")!=-1) {
				  
			//����¡,�˻��� ������ ���� get������� �� �޾ƿ���
			int bookNumber = Integer.parseInt(req.getParameter("bookNumber"));
			String pageNum = req.getParameter("pageNumData");
			String searchKey = req.getParameter("searchKeyData");
			String searchValue = req.getParameter("searchValueData");
			
			//�ش� å ����
			dao.deleteBookData(bookNumber);
			 
			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//������ �̵� ���� å ������ ù �������� ���� ����
			if(pageNum.equals("")) {
				pageNum = "1";
			}
			
			//����¡,�˻��� �ʿ��� ���� �ּҿ� �ֱ�
			String param = "pageNum=" + pageNum;
			if(searchValue!=null&&!searchValue.equals("")) {
				param += "&searchKey=" + searchKey;
				param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
			}
			
			url = cp + "/book/bookList.do?" + param;
			resp.sendRedirect(url);

			
		//���ǰԽ��� �۾��� ��������
		}else if(uri.indexOf("write.do")!=-1) {

			url = "/kyobo/write.jsp";
			forward(req, resp, url);

			
		//���ǰԽ��ǿ� �۾���
		}else if(uri.indexOf("write_ok.do")!=-1) {
			
			BoardDTO dto = new BoardDTO();
			
			//���ǰԽ��� ��ü �� ���� ���ϱ�
			int maxNum = dao1.getMaxNum();
			
			dto.setInqNumber(maxNum + 1);
			dto.setInqType(req.getParameter("inqType"));
			dto.setSubject(req.getParameter("subject"));
			dto.setCusId(req.getParameter("cusId"));
			dto.setCusName(req.getParameter("cusName"));
			dto.setContent(req.getParameter("content"));
			dto.setInqPwd(req.getParameter("inqPwd"));
			
			//�Է��� ���� update
			dao1.insertBoard(dto);
			
			url = cp + "/book/boardList.do";
			resp.sendRedirect(url);
			
			
		// ���ǰԽ��� ���� ����
		} else if(uri.indexOf("boardList.do")!=-1) {

			url = "/kyobo/boardList.jsp"; 
			forward(req, resp, url);

			
		// ���ǰԽ��� ���������� ����
		} else if(uri.indexOf("boardUpdate.do")!=-1) {
			
			//������ ���Ǳ� ��ȣ �ޱ�
			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			
			//����¡,�˻�ó�� ���� �ޱ�
			String pageNum = req.getParameter("pageNum");
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//��ĭ�� �������� �ڵ� �Է��ϱ� ���� ������ �ҷ�����
			BoardDTO dto = dao1.getReadData(inqNumber);

			if(dto==null) {
				url = cp + "/book/boardList.do";
				resp.sendRedirect(url);
			}
			
			//param�� ����¡/�˻��� �ֱ�
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

		
		// �Խñ� ����ó��
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
			
			
		// �Խñ� ���� ����
		}else if(uri.indexOf("boardArticle.do")!=-1) {

			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			String pageNum = req.getParameter("pageNum");

			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");

			if(searchValue!=null) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			//�Խñ� ���� �ҷ�����
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
			
			
		// ���ǰԽñ� ����
		}else if(uri.indexOf("boardDelete_ok.do")!=-1){

			int inqNumber = Integer.parseInt(req.getParameter("inqNumber"));
			dao1.boardDeleteData(inqNumber);


			url = cp + "/book/boardList.do";
			resp.sendRedirect(url);
			
			
		// ��ǰ ���� ������ ����
		}else if(uri.indexOf("article.do")!=-1) {
			
			//������ å �̸� �޾ƿ���
			String bookName = req.getParameter("bookName");
			
			String imagePath = cp + "/pds/bookImage";

			//������ å ���� �о��
			BookDTO dto = dao.getReadDataBook(bookName);
			
			req.setAttribute("imagePath", imagePath);
			req.setAttribute("dto", dto);
			
			url = "/kyobo/article.jsp"; 
			forward(req, resp, url);
			
		
		//��ٱ��Ͽ� �߰��ϱ�
		//��ٱ��� �߰��� ���� �������� ���ư�
		}else if(uri.indexOf("basketInsertData.do")!=-1) {
			
			//��ٱ��ϴ� ������ �̿��ϱ� ������
			//�������� ������ ���� �غ� ���� �Ѵ�..
			
			//��ٱ��Ͽ� ���� å �̸� �ޱ�
			String bookName = req.getParameter("bookName");
			
			//��ٱ��Ͽ� ����ID���� �޾ƿ;� �ϹǷ� ���ǰ� ���� �غ�
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//��ٱ��Ͽ� �ִµ� �ʿ��� �� �о����
			String cusId = info.getCusId();
			BookDTO dto = dao.getReadDataBook(bookName);
			int basNumber = dao.basketGetMaxNum()+1;
			
			
			//��ٱ��Ͽ� �߰�
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

			
			//������ �������� �˸�â ���� ���� ȭ������ ���ư���
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter w = resp.getWriter();
			w.write("<script>alert('��ٱ��Ͽ� �߰��Ǿ����ϴ�.');history.go(-1);</script>");
			w.flush();
			w.close();

	    
		// ��ٱ��� ������ ����
		} else if(uri.indexOf("basket.do")!=-1) {
			
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//��ٱ��� �����͸� �о�� list�� ����
			List<BasketDTO> lists = dao.getBasketList(info.getCusId());

			String imagePath = cp + "/pds/bookImage";


			req.setAttribute("imagePath", imagePath);
			req.setAttribute("lists", lists);

			url = "/kyobo/basket.jsp";
			forward(req, resp, url);
			return;

				
		// ��ٱ��� ������ ����
		// ���� �Ҷ����� ��ٱ��� �������� �ٽ� ���
		} else if(uri.indexOf("deleteData_ok.do")!=-1) {
				
			int basNumber = Integer.parseInt(req.getParameter("basNumber"));

			dao.deleteBasData(basNumber);

			url = cp + "/book/basket.do";
			resp.sendRedirect(url);
				
			
		// ��ٱ��Ͽ��� ������ ��ǰ���� ���� �������� ����
		} else if(uri.indexOf("orderPage.do")!=-1) {
			
			//checkbox���� üũ�� ��ǰ�� value���� Name�迭�� ����
			//�������� ��ǰ�� �޾ƿ;� �ϱ� ������ �迭�� ����
			//basket.jsp���� checkbox �̸��� check�̱� ������ check�� �޴� ��
			//getParameterValues�� ���� checkbox���� ���ƿ�
			//�������� ������ �������� ���� �� �ִ�
			String[] Name = req.getParameterValues("check");
			List<BookDTO> lists = new ArrayList<BookDTO>();
			
			//�� ���űݾ��� ǥ���ϱ� ���� totalPrice�� �ʱⰪ ����
			int totalPrice = 0;
			
			//�迭�� �޾ƿ� ������ Ȯ��for������ �����͸� �о�´�
			//�� ���űݾ��� ������ �޾ƿö����� �����ش�
			for(String data : Name) {

				String bookName = data;

				BookDTO dto = dao.getReadDataBook(bookName);
				totalPrice += dto.getBookPrice();
				
				//�޾ƿ� �������� �ϳ��� lists�� ����
				lists.add(dto);

			}

			String imagePath = cp + "/pds/bookImage";

			req.setAttribute("imagePath", imagePath);
			req.setAttribute("lists", lists);
			req.setAttribute("totalPrice", totalPrice);

			url = "/kyobo/basketBuy.jsp";
			forward(req, resp, url);
			return;
				
		
		// ��ǰ���� â���� �����ϱ� Ŭ����
		// ���� �������� ����
		}else if(uri.indexOf("buy.do")!=-1) {
	        
			String bookName = req.getParameter("bookName");
			String imagePath = cp + "/pds/bookImage";
			
			BookDTO dto = dao.getReadDataBook(bookName);


			req.setAttribute("imagePath", imagePath);
			req.setAttribute("dto", dto);

			url = "/kyobo/buy.jsp"; 
			forward(req, resp, url);

			
		// ���� ���������� ���� Ȯ���� orders ���̺� �߰��ϱ�
		// ���� ó���� ���� �Ϸ� �������� ����
		}else if(uri.indexOf("buy_ok")!=-1) {
			
			//�ֹ� ���̺� ������ id�� �־�� �ϹǷ� ������ ���� �غ�
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//�ֹ� ���̺� ���� ��(id,å�̸�) ����
			String cusId = info.getCusId();
			String bookName = req.getParameter("book");
			
			//�ֹ���ȣ�� �ֹ���¥ ���� �ʱⰪ ����
			int OrderNumber = 0;
			String date = null;
			
			//�ֹ��� å ������ book���̺��� �о��
			BookDTO dto = dao.getReadDataBook(bookName);
			
			//�ֹ���ȣ�� �Է��ϱ� ���� orders���̺��� ��ü ������ �о�� +1
			int maxNum = dao.ordersGetMaxNum()+1;
			
			
			//orders���̺� �������� insert
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

			
			//���ſϷ� ���������� �ֹ���ȣ�� �ֹ���¥�� ǥ���ϱ� ����
			//���� ������ ������ buyComplete.jsp�� �����غ�
			OrderNumber = maxNum;
			date = dao.getReadDataOrderDate(maxNum);


			req.setAttribute("OrderNumber", OrderNumber);
			req.setAttribute("BookName", bookName);
			req.setAttribute("BookPrice", dto.getBookPrice());
			req.setAttribute("date", date);

			url = "/kyobo/buyComplete.jsp";
			forward(req, resp, url);
			return;
				
		
		//��ٱ���->�������������� ���� Ȯ����
		// ���� ó���� ���� �Ϸ� �������� ����
		} else if(uri.indexOf("basketBuy_ok")!=-1) {
			
			//�ֹ� ���̺� ������ id�� �־�� �ϹǷ� ������ ���� �غ�
			HttpSession session = req.getSession();
			CustomInfo info = (CustomInfo)session.getAttribute("customInfo");
			
			//�ֹ� ���̺� ���� ��(id,å�̸�) ����
			String cusId = info.getCusId();
			
			//���⼱ �������� ���� �޾ƾ� �ϹǷ� �迭�� getParameterValues�� �޾��ش�
			String[] book = req.getParameterValues("book");
			
			//��ٱ��� ���Ŵ� �������� �����ϱ� ������ �Ѿ��� ���� �غ� 
			String totalPrice = req.getParameter("totalPrice");
			
			//������ ��ǰ�� ������ �ֹ���ȣ�� ǥ���ϱ� ���� ������ �ʱⰪ ����
			int count = 0;
			int maxNum = 0;

			//�ֹ���ȣ�� �ֹ���¥ å�̸� ���� �ʱⰪ ����
			int OrderNumber = 0;
			String BookName = null;
			String date = null;

			//�迭�� ���� �޾����Ƿ� Ȯ��for������ �ϳ��� orders���̺� �����Ѵ�
			for(String data : book) {

				String bookName = data;

				BookDTO dto = dao.getReadDataBook(bookName);
				
				//�ֹ���ȣ�� �Է��ϱ� ���� orders���̺��� ��ü ������ �о�� +1
				maxNum = dao.ordersGetMaxNum()+1;
				
				
				//������ ��ǰ ���������� orders���̺� insert
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
				
				//������ ��ǰ�� ��ٱ��Ͽ��� ����
				dao.deleteBasDataOrder(cusId,bookName);

				count++;

				//���ſϷ� ���������� @�� 2�� �̷������� ǥ���� �����̹Ƿ�
				//ó������ �ִ� å�� ������ ������ ������ jsp�� ���� �غ� �Ѵ�
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
