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
<link rel="icon" type="image/png" sizes="16x16" href="<%=cp %>/kyobo/image/파비콘.png">

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
		
		f.action = "<%=cp%>/book/login_ok.do";
		f.submit();
		
	}


</script>

</head>
<body>
<div class="login-page">
  <div class="form">
  <div id="imogaplogo"><a href="<%=cp%>/book/list.do"><img src="<%=cp%>/kyobo/image/kyobologo.PNG"></a></div>
    <form class="login-form"  method="post" action="" name="myForm">
   	  <input type="text" name="cusId" placeholder="아이디" size="40"/>
      <input type="password" name="cusPwd" placeholder="비밀번호"/>
      <input type="button" class="green press" value=" 로그인 " onclick="login();">
      <p class="message"><a href="<%=cp%>/book/created.do">회원가입 </a> |
      <a href="<%=cp%>/book/searchCusId.do">아이디찾기</a> |
      <a href="<%=cp%>/book/searchCusPwd.do">비밀번호찾기</a>
      
      <!-- 아이디/비밀번호 틀릴시 경고문 -->
       <c:if test="${!empty message }">
		<div>
			<p style="font-size: 12px; color: #ff0000;">[ ${message } ]</p>
		</div>
      </c:if>
      
    </form>
    
  </div>
</div> 

</body>
</html>