<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Entrar - CogrOO Web</title>
</head>
<body>
	<a href="<c:url value="/"/>">Página principal</a>
	<c:forEach var="error" items="${errors}">
	    <span style="color: red">[<fmt:message  key="${error.category}"/>] <i><fmt:message  key="${error.message}"/></i></span><br />
	</c:forEach>
	<p>Login:</p>
		<form action="<c:url value="/login"/>"  method="post" >
		    Nome do usuário:      <input type="text" id="username" name="user.name" value="${user.name}"/><br/>

		    <input name="login" type="submit" value="Entrar"  />
		</form>
</body>
</html>