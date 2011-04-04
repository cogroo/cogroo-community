<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  


<div class="loginframe">
	<form action="<c:url value='/login'/>" method="post">
		<table>
		<tbody><tr>
		    <td align="right"><label class="loginlabel" for="login">Usuário:</label></td>
		    <td align="left"><input type="text" tabindex="1" name="login"></td>
		</tr>
		<tr>
		    <td align="right"><label class="loginlabel" for="password">Senha:</label></td>
		    <td align="left"><input type="password" tabindex="2" name="password"></td>
		</tr>
		
		<tr>
		    <td> </td>
		    <td align="left">
		         
		    </td>
		</tr>
		
		<tr>
		    <td align="left">
		    	<a href="<c:url value="/recover"/>">Esqueci a senha</a> <br>
				<a href="<c:url value="/register"/>">Criar novo usuário</a>
		    </td>
		    <td align="right">
		        <input type="submit" class="button" value=" Entrar &raquo; ">
		    </td>
		</tr>
		</tbody></table>
	</form>
</div>