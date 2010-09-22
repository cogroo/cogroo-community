<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Erros cadastrados - CoGrOO Web</title>
</head>
<body>
	<br/>
	<p>Erros enviados através do plug-in CoGrOO para BrOffice.org</p>
	<br/>
	<table border="1">
		<tr>
		  <th>Autor</th>
		  <th>Texto exemplo</th>
		  <th>Comentários</th>
		  <th>Versão</th>
		  <th>Ações</th>
		</tr>
		<c:forEach items="${errorReportList}" var="errorReport">
			<tr>
			  <td>${errorReport.submitter.name}</td>
  			  <td>${errorReport.sampleText}</td>
  			  <td>${fn:length(errorReport.comments)}</td>
  			  <td>${errorReport.version}</td>
  			  <td><form action="<c:url value="errorReport"/>" method="post" >
  			  		<input type="hidden" name="errorReportID" value="${errorReport.id}"/>
					<input type="submit" value="abrir" />
				</form></td>
		    </tr>
		</c:forEach>
	</table>
</body>
</html>