package com.kyobo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.board.BoardDTO;
import com.imageTest.ImageTestDTO;
import com.kyobo.DTO.BasketDTO;
import com.kyobo.DTO.BookDTO;
import com.kyobo.DTO.MemberDTO;
import com.kyobo.DTO.OrdersDTO;

public class MemberDAO {
	
	private Connection conn;
	
	//conn 연결을 위한 오버로딩
	public MemberDAO(Connection conn) {
		this.conn = conn;
	}
	
	
	
	
	//회원가입
	public int insertData(MemberDTO dto) {

		int result = 0;
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "insert into member (cusId,cusPwd,cusName,cusGender,cusBirth, ";
			sql+= "cusTel,postcode,address,detailAddress,cusEmail,cusPoint) ";
			sql+= "values(?,?,?,?,?,?,?,?,?,?,0)";
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getCusId());
			pstmt.setString(2, dto.getCusPwd());
			pstmt.setString(3, dto.getCusName());
			pstmt.setString(4, dto.getCusGender());
			pstmt.setString(5, dto.getCusBirth());
			pstmt.setString(6, dto.getCusTel());
			pstmt.setString(7, dto.getPostcode());
			pstmt.setString(8, dto.getAddress());
			pstmt.setString(9, dto.getDetailAddress());
			pstmt.setString(10, dto.getCusEmail());

			result = pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;

	}
	
	
	
		// 아이디 찾기
		public MemberDTO getDataForId(String cusName, String cusTel) {

			MemberDTO dto = null;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;


			try {
				sql = "select cusId, cusName, cusTel ";
				sql+= "from member where cusName=? and cusTel=?";

				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, cusName);
				pstmt.setString(2, cusTel);

				rs = pstmt.executeQuery();

				while(rs.next()) {

					dto = new MemberDTO();

					dto.setCusId(rs.getString("cusId"));
					dto.setCusName(rs.getString("cusName"));
					dto.setCusTel(rs.getString("cusTel"));
				}



				rs.close();
				pstmt.close();

			} catch (Exception e) {

			}

			return dto;

		}

		
		
		
		// 패스워드 찾기
		public MemberDTO getDataForPwd(String cusId, String cusName, String cusTel) {

			MemberDTO dto = null;

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;

			try {
				sql = "select cusId, cusPwd, cusName, cusTel ";
				sql+= "from member where cusId=? and cusName=? and cusTel=?";

				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, cusId);
				pstmt.setString(2, cusName);
				pstmt.setString(3, cusTel);

				rs = pstmt.executeQuery();

				while(rs.next()) {

					dto = new MemberDTO();

					dto.setCusId(rs.getString("cusId"));
					dto.setCusPwd(rs.getString("cusPwd"));
					dto.setCusName(rs.getString("cusName"));
					dto.setCusTel(rs.getString("cusTel"));
				}

				rs.close();
				pstmt.close();

			} catch (Exception e) {
				System.out.println(e.toString());
			}

			return dto;

		}

		
		
		
		// 회원정보 수정
		public int updateData(MemberDTO dto) {
			int result = 0;
			PreparedStatement pstmt = null;
			String sql;

			try {
				sql = "update member set cusPwd=?,cusName=?,cusGender=?, ";
				sql+= "cusBirth=?,cusTel=?,postcode=?,address=?,detailAddress=?, ";
				sql+= "cusEmail=? where cusId=?";

				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, dto.getCusPwd());
				pstmt.setString(2, dto.getCusName());
				pstmt.setString(3, dto.getCusGender());
				pstmt.setString(4, dto.getCusBirth());
				pstmt.setString(5, dto.getCusTel());
				pstmt.setString(6, dto.getPostcode());
				pstmt.setString(7, dto.getAddress());
				pstmt.setString(8, dto.getDetailAddress());
				pstmt.setString(9, dto.getCusEmail());
				pstmt.setString(10, dto.getCusId());
				
				result = pstmt.executeUpdate();
				pstmt.close();

			} catch (Exception e) {
				System.out.println(e.toString());
			}
			return result;
		}
		
		
		// 회원 탈퇴
		public int deleteData(String cusId) {

			int result = 0;
			PreparedStatement pstmt = null;
			String sql;

			try {

				sql = "delete member where cusId=?";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, cusId);

				result = pstmt.executeUpdate();
				pstmt.close();


			} catch (Exception e) {
				System.out.println(e.toString());
			}

			return result;

		}
		
		
		
		// 구매내역 조회하기
		public List<OrdersDTO> getPurchasedItems(String cusId, int start, int end) {

			List<OrdersDTO> lists = new ArrayList<OrdersDTO>();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;

			try {

				sql ="select * from ( ";
				sql+="select rownum rnum, data.* from ( ";
				sql+="select orderNum, bookNumber, bookName, bookWriter, ";
				sql+="bookPublisher, bookPrice, saveFileName, ";
				sql+="to_char(bookDate,'YYYY-MM-DD') bookDate, ";
				sql+="to_char(orderDate,'YYYY-MM-DD') orderDate, ";
				sql+="to_char(deliveryDate,'YYYY-MM-DD') deliveryDate ";
				sql+="from orders where cusId=? ";
				sql+="order by orderNum desc) data) ";
				sql+="where rnum>=? and rnum<=? ";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, cusId);
				pstmt.setInt(2, start);
				pstmt.setInt(3, end);

				rs = pstmt.executeQuery();

				while(rs.next()) {

					OrdersDTO dto = new OrdersDTO();

					dto.setOrderNum(rs.getInt("orderNum"));
					dto.setBookNumber(rs.getInt("bookNumber"));
					dto.setBookName(rs.getString("bookName"));
					dto.setBookWriter(rs.getString("bookWriter"));
					dto.setBookPublisher(rs.getString("bookPublisher"));
					dto.setBookDate(rs.getString("bookDate"));
					dto.setBookPrice(rs.getInt("bookPrice"));
					dto.setSaveFileName(rs.getString("saveFileName"));
					dto.setOrderDate(rs.getString("orderDate"));
					dto.setDeliveryDate(rs.getString("deliveryDate"));
					
					lists.add(dto);
				}

				rs.close();
				pstmt.close();

			} catch (Exception e) {
				System.out.println(e.toString());
			}

			return lists;

		}
		
		
		//해당 아이디의 주문 데이터 갯수 구하기
		public int countingData(String cusId) { 
			int totNum = 0;
			
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			String sql;

			try {

				sql = "select count(*) from orders where cusId=?";

				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, cusId);

				rs = pstmt.executeQuery();

				if(rs.next()) {
					totNum = rs.getInt(1);
				}

				rs.close();
				pstmt.close();

			} catch (Exception e) {
				System.out.println(e.toString());
			}
			return totNum;
		}
	
	
	
	//회원정보 수정을 위한 해당 아이디의 회원정보 가져오기
	public MemberDTO getReadData(String cusId) {
		
		MemberDTO dto = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			sql = "select cusId, cusPwd, cusName, cusGender, ";
			sql+= "to_char(cusBirth,'YYYY-MM-DD') cusBirth, ";
			sql+= "cusTel, postcode, address, detailAddress,";
			sql+= "cusEmail, cusPoint ";
			sql+= "from member where cusId=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, cusId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				dto = new MemberDTO();
				
				dto.setCusId(rs.getString("cusId"));
				dto.setCusPwd(rs.getString("cusPwd"));
				dto.setCusName(rs.getString("cusName"));
				dto.setCusGender(rs.getString("cusGender"));
				dto.setCusBirth(rs.getString("cusBirth"));
				dto.setCusTel(rs.getString("cusTel"));
				dto.setPostcode(rs.getString("postcode"));
				dto.setAddress(rs.getString("address"));
				dto.setDetailAddress(rs.getString("detailAddress"));
				dto.setCusEmail(rs.getString("cusEmail"));
				dto.setCusPoint(rs.getInt("cusPoint"));
				
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
		
	}
	
	
	//검색한 데이터의 개수 구하기
	public int getDataCountBook(String searchKey, String searchValue) {
		
		int totalCount = 0;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			searchValue = "%" + searchValue + "%";
			
			sql = "select nvl(count(*),0) from book ";
			sql+= "where " + searchKey + " like ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, searchValue);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				totalCount = rs.getInt(1);
				
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return totalCount;
	}
	
	

	//검색한 데이터들의 정보 읽어오기
	public List<BookDTO> getLists(int start, int end, String searchKey, String searchValue){
		
		List<BookDTO> lists = new ArrayList<BookDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			searchValue = "%" + searchValue + "%";
			
			sql = "select * from (";
			sql+= "select rownum rnum, data.* from (";
			sql+= "select bookNumber,bookName,bookPrice, ";
			sql+= "bookWriter,to_char(bookDate,'YYYY-MM-DD') bookDate, ";
			sql+= "bookPublisher, bookStock, bookDisCount, ";
			sql+= "bookCate1, bookCate2, saveFileName ";
			sql+= "from book where " + searchKey + " like ? ";
			sql+= "order by bookNumber desc) data) ";
			sql+= "where rnum>=? and rnum<=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				BookDTO dto = new BookDTO();
				
				dto.setBookNumber(rs.getInt("bookNumber"));
				dto.setBookName(rs.getString("bookName"));
				dto.setBookPrice(rs.getInt("bookPrice"));
				dto.setBookWriter(rs.getString("bookWriter"));
				dto.setBookDate(rs.getString("bookDate"));
				dto.setBookPublisher(rs.getString("bookPublisher"));
				dto.setBookStock(rs.getInt("bookStock"));
				dto.setBookDisCount(rs.getInt("bookDisCount"));
				dto.setBookCate1(rs.getString("bookCate1"));
				dto.setBookCate2(rs.getString("bookCate2"));
				dto.setSaveFileName(rs.getString("saveFileName"));
				
				lists.add(dto);
				
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return lists;
		
	}
	
	
	//검색한 데이터 1개의 정보 가져오기
	public BookDTO getReadDataBook(String bookName) {
		
		BookDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			sql = "select bookNumber,bookName,bookPrice, ";
			sql+= "bookWriter,to_char(bookDate,'YYYY-MM-DD') bookDate, ";
			sql+= "bookPublisher, bookStock, bookDisCount,";
			sql+= "bookCate1, bookCate2, saveFileName ";
			sql+= "from book where bookName=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, bookName);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				dto = new BookDTO();
				
				dto.setBookNumber(rs.getInt("bookNumber"));
				dto.setBookName(rs.getString("bookName"));
				dto.setBookPrice(rs.getInt("bookPrice"));
				dto.setBookWriter(rs.getString("bookWriter"));
				dto.setBookDate(rs.getString("bookDate"));
				dto.setBookPublisher(rs.getString("bookPublisher"));
				dto.setBookStock(rs.getInt("bookStock"));
				dto.setBookDisCount(rs.getInt("bookDisCount"));
				dto.setBookCate1(rs.getString("bookCate1"));
				dto.setBookCate2(rs.getString("bookCate2"));
				dto.setSaveFileName(rs.getString("saveFileName"));
				
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
		
	}
	
	

	// Book 테이블의 데이터 삭제하기
	public int deleteBookData(int bookNumber) {

		int result = 0;
		PreparedStatement pstmt = null;
		String sql;

		try {

			sql = "delete book where bookNumber=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bookNumber);

			result = pstmt.executeUpdate();
			pstmt.close();


		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
		
	}
	
	
	
	//장바구니의 전체 데이터 개수 구하기
	public int basketGetMaxNum() {
		
		int maxNum = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try {
			
			sql = "select nvl(max(basNumber),0) from basket";

			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				maxNum = rs.getInt(1); //무조건 값이 하나이므로..
			}
			
			rs.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return maxNum;
	}
	
	
	
	//장바구니에 데이터 넣기
	public int basketInsertData(BasketDTO dto) {

		int result = 0;
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "insert into basket (basNumber,cusId,bookNumber, ";
			sql+= "bookName,bookWriter,bookPublisher,bookPrice,bookCount,saveFileName) ";
			sql+= "values (?,?,?,?,?,?,?,1,?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, dto.getBasNumber());
			pstmt.setString(2, dto.getCusId());
			pstmt.setInt(3, dto.getBookNumber());
			pstmt.setString(4, dto.getBookName());
			pstmt.setString(5, dto.getBookWriter());
			pstmt.setString(6, dto.getBookPublisher());
			pstmt.setInt(7, dto.getBookPrice());
			pstmt.setString(8, dto.getSaveFileName());
		
			result = pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;

	}

	
	//해당 아이디의 장바구니 데이터 읽어오기
	public List<BasketDTO> getBasketList(String cusId) {
		
		List<BasketDTO> lists = new ArrayList<BasketDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			sql = "select basNumber,bookNumber, bookName, bookWriter, ";
			sql+= "bookPublisher ,bookPrice, bookCount, saveFileName ";
			sql+= "from basket where cusId = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cusId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				BasketDTO dto = new BasketDTO();
				
				dto.setBasNumber(rs.getInt("basNumber"));
				dto.setBookNumber(rs.getInt("bookNumber"));
				dto.setBookName(rs.getString("bookName"));
				dto.setBookPrice(rs.getInt("bookPrice"));
				dto.setBookCount(rs.getInt("bookCount"));
				dto.setBookWriter(rs.getString("bookWriter"));
				dto.setBookPublisher(rs.getString("bookPublisher"));
				dto.setSaveFileName(rs.getString("saveFileName"));
				
				lists.add(dto);
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return lists;
		
	}
	
	
	// 장바구니 데이터 삭제하기
	public int deleteBasData(int basNumber) {
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "delete basket where basNumber=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, basNumber);
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
		
		
		
	}
	
	
	//주문 테이블의 전체 데이터 갯수 구하기
	public int ordersGetMaxNum() {
		
		int maxNum = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try {
			
			sql = "select nvl(max(orderNum),0) from orders";

			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				maxNum = rs.getInt(1); //무조건 값이 하나이므로..
			}
			
			rs.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return maxNum;
	}
	
	
	
	//주문 테이블에 데이터 넣기
	public int ordersInsertData(OrdersDTO dto) {

		int result = 0;
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "insert into orders (orderNum,cusId,bookNumber,bookName, ";
			sql+= "bookWriter,bookPublisher,bookDate,bookPrice,saveFileName,";
			sql+= "orderDate, deliveryDate) ";
			sql+= "values (?,?,?,?,?,?,?,?,?,sysdate,sysdate+3)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, dto.getOrderNum());
			pstmt.setString(2, dto.getCusId());
			pstmt.setInt(3, dto.getBookNumber());
			pstmt.setString(4, dto.getBookName());
			pstmt.setString(5, dto.getBookWriter());
			pstmt.setString(6, dto.getBookPublisher());
			pstmt.setString(7, dto.getBookDate());
			pstmt.setInt(8, dto.getBookPrice());
			pstmt.setString(9, dto.getSaveFileName());
		
			result = pstmt.executeUpdate();
			pstmt.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;

	}
	
	
	//주문 테이블의 주문 날짜 읽어오기
	public String getReadDataOrderDate(int maxNum) {
		
		String date = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try {
			
			sql = "select to_char(orderDate,'YYYY-MM-DD') orderDate ";
			sql+= "from orders where orderNum=?";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, maxNum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				date = rs.getString("orderDate"); //무조건 값이 하나이므로..
			}
			
			rs.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return date;
	}
	
	
	//장바구니 데이터 지우기
	public int deleteBasDataOrder(String cusId, String bookName) {
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "delete basket where cusId=? and bookName=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cusId);
			pstmt.setString(2, bookName);
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
		
		
		
	}

	

	
}











