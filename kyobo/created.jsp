<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
	
	Cookie[] ck = request.getCookies();

	String popUrl = "";
	String strUrl, str;

	strUrl = "idcheck1.do";
	str = "window.open('" + strUrl + "', 'Think', ";
	str = str + "'left=610, ";
	str = str + "top=10, ";
	str = str + "width=372, ";
	str = str + "height=466, ";
	str = str + "toolbar=no, ";
	str = str + "menubar=no, ";
	str = str + "status=no, ";
	str = str + "scrollbars=no, ";
	str = str + "resizable=no')";

	popUrl = str;
	
	
	
	
	String popUrl2 = "";
	String strUrl2, str2;
	
	strUrl = "epostTest.do";
	str2 = "window.open('" + strUrl + "', '주소검색', ";
	str2 = str2 + "'left=610, ";
	str2 = str2 + "top=10, ";
	str2 = str2 + "width=372, ";
	str2 = str2 + "height=466, ";
	str2 = str2 + "toolbar=no, ";
	str2 = str2 + "menubar=no, ";
	str2 = str2 + "status=no, ";
	str2 = str2 + "scrollbars=no, ";
	str2 = str2 + "resizable=no')";
	
	popUrl2 = str2;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>교보문고</title>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<link rel="stylesheet" type="text/css" href="<%=cp%>/kyobo/data/created.css"/>
<link rel="stylesheet" type="text/css" href="<%=cp%>/board/css/style.css"/>
<link rel="stylesheet" type="text/css" href="<%=cp%>/board/css/list.css"/>
<link rel="icon" type="image/png" href="<%=cp %>/kyobo/image/파비콘.png">

<script type="text/javascript">

</script>

<script type="text/javascript">

	function login() {
		
		var f = document.myForm;
		
		if(!f.cusId.value){
			alert("아이디를 입력하세요.");
			f.cusId.focus();
			return;
		}
		
		if(!f.cusPwd.value){
			alert("패스워드를 입력하세요.");
			f.cusPwd.focus();
			return;
		}
		
		if(!f.cusName.value){
			alert("이름을 입력하세요.");
			f.cusName.focus();
			return;
		}
		
		if(f.cusGender.selectedIndex==0){
			alert("성별을 선택하세요.");
			f.cusGender.focus();
			return;
		}
		
		if(!f.cusBirth.value){
			alert("생일을 입력하세요.");
			f.cusBirth.focus();
			return;
		}
		
		if(!f.cusTel.value){
			alert("전화번호를 입력하세요.");
			f.cusTel.focus();
			return;
		}
		
		if(!f.postcode.value){
			alert("주소를 입력하세요.");
			f.postcode.focus();
			return;
		}
		
		
		if(!f.address.value){
			alert("주소를 입력하세요.");
			f.address.focus();
			return;
		}
		
		if(!f.detailAddress.value){
			alert("주소를 입력하세요.");
			f.detailAddress.focus();
			return;
		}
		
		if(!f.cusEmail.value){
			alert("이메일을 입력하세요.");
			f.cusEmail.focus();
			return;
		}
		
		alert("회원가입이 완료되었습니다!")
		
		f.action = "<%=cp%>/book/created_ok.do";
		f.submit();
		
	}
	
	//주소 찾기 메소드
    function Postcode() {
        new daum.Postcode({
            oncomplete: function(data) {
               
                var addr = ''; 
                var extraAddr = ''; 

                if (data.userSelectedType === 'R') {
                    addr = data.roadAddress;
                } else { 
                    addr = data.jibunAddress;
                }
               
                document.getElementById('postcode').value = data.zonecode;
                document.getElementById("address").value = addr;
                document.getElementById("detailAddress").focus();
            }
        
        }).open();
        
    }

</script>

</head>
<body>
<div class="login-page">
  <div class="form">
  <div id="imogaplogo"><a href="<%=cp%>/book/list.do"><img src="<%=cp%>/kyobo/image/kyobologo.PNG"></a></div>
  <div id="imogaplogo"><h3>회원가입</h3></div>
  <br/><br/>
  
<form class="login-form"  method="post" action="" name="myForm">
    <p style="float: left;"><b>* 아이디</b></p>
    <br/>
    <div style="float: left; width: 65%;">
 		<input type="text" name="cusId" placeholder="아이디를 입력해주세요">
	</div>

	<div style="float: right; width: 30%;">
		<input type="button" class="green press" name="Check" value="중복체크"
		onclick="<%=popUrl%>">
	</div>
	<br/><br/><br/><br/><br/>
	 
	<p style="float: left;"><b>* 비밀번호</b></p>
		<input type="password" name="cusPwd" placeholder="비밀번호를 입력해주세요"/>
    <br/><br/>
    
    <p style="float: left;"><b>* 이름</b></p>
		<input type="text" name="cusName" placeholder="이름을 입력해주세요"/>
    <br/><br/>
    
    <p style="float: left;"><b>* 성별</b></p>
    <select name="cusGender" class="selectField" style="width: 100%; height: 45px;">
		<option value="Gender">성별을 선택해주세요</option>
		<option value="남자">남자</option>
		<option value="여자">여자</option>
	</select>
	<br/><br/><br/>
	
	<p style="float: left;"><b>* 생일</b></p>
		<input type="text" name="cusBirth" placeholder="생일을 입력해주세요"/>
    <br/><br/>
    
    <p style="float: left;"><b>* 전화번호</b></p>
    	<input type="text" name="cusTel" placeholder="전화번호를 입력해주세요"/>
    <br/><br/>
    
    <p style="float: left;"><b>* 주소</b></p><br/>
    <div style="float: left; width: 60%;">
    	<input onclick="Postcode()" type="text" name="postcode"
    	id="postcode"  placeholder="우편번호">
    </div>
		<input type="text" name="address" id="address" placeholder="주소"><br>
		<input type="text" name="detailAddress" id="detailAddress" placeholder="상세주소">
    	
    <br/><br/>
    
    <p style="float: left;"><b>* Email</b></p>
    	<input type="text" name="cusEmail" placeholder="이메일을 입력해주세요"/>
    <br/><br/>
    
    <input type="button" class="green press" style="font-size: 15px;" value=" 회원가입 " onclick="login();">
    <p class="message"><a href="<%=cp%>/book/login.do">취소하기</a></p>
</form>
</div>
</div> 

</body>
</html>