<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Detalhes de erro - CoGrOO Web</title>
</head>
<body>
	<br/>
	<p>Erro enviado através do plug-in CoGrOO para BrOffice.org</p>
	<br/>
	<h3>Detalhes</h3>
	<table border="1">
		<tr>
			<td>Autor</td><td>${errorReport.submitter.name}</td>
		</tr>
		<tr>
			<td>Data de submissão</td><td>${errorReport.creation}</td>
		</tr>
		<tr>
			<td>Texto exemplo</td><td>${errorReport.sampleText}</td>
		</tr>
		<tr>
			<td>Versão do CoGrOO</td><td>${errorReport.version}</td>
		</tr>
	</table>
	<h3>Comentários</h3>
	<c:forEach items="${errorReport.comments}" var="comment">
		<p>Usuário ${comment.user.name} em ${comment.date}: <br/>
		<i>${comment.comment}</i>
		</p>
		<hr />
	</c:forEach>
	<c:if test="${loggedUser.logged}">
		<form action="<c:url value="/errorReportAddComment"/>"  method="post" >
		 	<TEXTAREA NAME=newComment ROWS=4 COLS=80></TEXTAREA> <br />
		 	<input type="hidden" name="errorReportID" value="${errorReport.id}"/>
			<input type="submit" value="Enviar" />
		</form>
	</c:if>
</body>
</html>