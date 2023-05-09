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
		
		f.action = "<%=cp%>/book/login.do";
		f.submit();
		
	}
	

</script>

</head>
<body>
<!-- 아이디/비번찾기 결과창 더미페이지 -->

<form class="findInfo"  method="post" action="" name="myForm">
<!-- if else문 사용할려면 c:choose로 감싸야 하며
		if otherwise가 아니라
		when otherwise로 사용해야 한다 -->
		
<c:choose>
<c:when test="${!empty err_msg }">
	<div>
		${err_msg } <br/>
		<input type="button" class="green press" value=" 로그인 " 
				onclick="login();">
	</div>
</c:when>

<c:when test="${!empty pwdInfo }">
	<div>
		<!-- ${cusId }님 -->
		비밀번호는 ${pwdInfo } 입니다 <br/>
		<input type="button" class="green press" value=" 로그인 " 
				onclick="login();">
	</div>
</c:when>

<c:otherwise>
	<div>
		<!-- ${cusId }님 -->
		아이디는 ${cusIdInfo } 입니다 <br/>
		<input type="button" class="green press" value=" 로그인 " 
				onclick="login();">
	</div>
</c:otherwise>
</c:choose>

</form>
</body>
</html>














