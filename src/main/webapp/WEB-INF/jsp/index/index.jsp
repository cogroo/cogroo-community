<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8" %>  

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Projeto Cogroo Web</title>
		
		<link rel="stylesheet" href="<c:url value='/js/jquery.tooltip.css' />" />
		<script src="<c:url value='/js/jquery.tooltip.js' />" type="text/javascript" ></script>
		
		<script src="<c:url value='/js/jquery.parseSyntaxTree.js' />" type="text/javascript" ></script>
		<script src="<c:url value='/js/jquery.drawTree.js' />" type="text/javascript" ></script>
		<script type="text/javascript">
		//<![CDATA[
		$(document).ready(function() {
	
			$('.syntaxTree').each(function() {
				$(this).next().drawTree(  $(this).parseSyntaxTree(), {border:'none'}  );
				$(this).hide();
				//$(this).drawTree(  $(this).parseSyntaxTree(), {border:'none'}  );
			});
			//$('.drawTree').hide();
	
		});
		   // ]]>
		</script>
		
	</head>
	<body>
		<h1>Analisador Gramatical.</h1>
		<p>Digite o texto em português para verificar sua análise:</p>
		<form action="<c:url value="/"/>"  method="post" >
		    <textarea rows="4" cols="70" name="texto">${texto}</textarea>
		    <input type="submit" value="Processar" id="go"/>
		</form>
	
		<c:if test="${not empty processResultList}">
			<div id="resposta">
			
				<p>Resutado do processamento:</p>
				<ul>
				<c:forEach items="${processResultList}" var="processResult">
					<div class="syntaxTree">${processResult.syntaxTree}</div>
					<div class="drawTree"></div>
				</c:forEach>
				</ul>		 
			</div>
		</c:if>
</html>