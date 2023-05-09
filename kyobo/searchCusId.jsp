<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>교보문고</title>
<link rel="stylesheet" type="text/css" href="<%=cp%>/kyobo/data/login.css"/>
<link rel="icon" type="image/png" href="<%=cp %>/kyobo/image/파비콘.png">


<script type="text/javascript">

	function login() {
		
		var f = document.myForm;
		
		if(!f.cusName.value){
			alert("이름을 입력하세요.");
			f.cusName.focus();
			return;
		}
		
		if(!f.cusTel.value){
			alert("전화번호를 입력하세요.");
			f.cusTel.focus();
			return;
		}
		
		f.action = "<%=cp%>/book/searchCusId_ok.do";
		f.submit();
		
	}

</script>

</head>
<body>
<div class="login-page">
  <div class="form">
  <div id="imogaplogo"><a href="<%=cp%>/book/list.do"><img src="<%=cp%>/kyobo/image/kyobologo.PNG"></a></div>
    <form class="login-form"  method="post" action="" name="myForm">
      <input type="text" name="cusName" placeholder="이	름"/>
      <input type="text" name="cusTel" placeholder="전화번호"/>
      <input type="button" class="green press" value=" ID 찾기 " onclick="login();">
      <p class="message"><a href="<%=cp%>/book/created.do">회원가입 </a> |
      <a href="<%=cp%>/book/login.do">로그인</a> |
     <a href="<%=cp%>/book/searchCusPwd.do">비밀번호찾기</a></p>
     
    </form>
  </div>
</div> 

</body>
</html>










