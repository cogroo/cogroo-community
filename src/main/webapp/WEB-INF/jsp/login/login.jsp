<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

<h2>TELA DE LOGIN</h2>

<form action="<c:url value='/login'/>" method="post">
	<p>
		<label>Login:</label>
		<input type="text" name="login" maxlength="20" class="inputtxt">
		<br/> 
		<label>Senha:</label>
		<input type="password" name="password" maxlength="20" size="20" class="inputtxt"> 
		
		<input type="submit" value=" Entrar &raquo; " class="button"> 
	</p>
</form>
	