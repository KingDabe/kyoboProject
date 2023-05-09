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
<link rel="icon" type="image/png" href="<%=cp %>/kyobo/image/파비콘.png">
<title>교보문고</title>

<script type="text/javascript">

	function check() {
		
		var f = document.myForm;
		f.action = "<%=cp%>/book/idcheck2.do";
		f.submit();
		
	}
	
</script>
</head>
<body>
<form name="myForm" method="post" action="">
<table width="372" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="372" colspan="2" height="437" valign="top" align="center">
    <p>아이디를 입력해주세요.</p>
    <input type="text" name="cusId" placeholder="아이디를 입력해주세요."/>
    <input type="button" class="green press" style="font-size: 15px;"
    value=" 중복검사하기 " onclick="check();">
    </td>
  </tr>
</table>
</form>
</body>
</html>