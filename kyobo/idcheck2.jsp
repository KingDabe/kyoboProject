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
</head>
<body>

<c:choose>
	<c:when test="${message==null }">
		<table width="372" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="372" colspan="2" height="437" valign="top" align="center">
					${message2}<br/>
					<input type='BUTTON' value=" 창닫기" onClick='self.close()'>
				</td>
			</tr>
		</table>
	</c:when>
	<c:otherwise>
		<table width="372" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="372" colspan="2" height="437" valign="top" align="center">
					${message}<br/>
					<input type='BUTTON' value=" 창닫기" onClick='self.close()'>
				</td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>


</body>
</html>