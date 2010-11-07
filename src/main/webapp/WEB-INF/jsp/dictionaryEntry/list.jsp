<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

<script type="text/javascript">
//<![CDATA[
	$(document).ready(function() {
		$('.relatorio tbody tr:odd').addClass('odd');
		$('.relatorio tbody tr:even').addClass('even');
	});
// ]]>
</script>

<div>
	<h2>Entradas cadastradas por <i>${loggedUser.user.name}</i>:</h2>
</div>

<div class="relatorio">
	<table border="1">
		<caption>
			<p>Palavras:</p>
		</caption>	
		<thead>
		<tr>
		  <th>Palavra</th>
		  <th>Lemma</th>
		  <th>Etiqueta</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${dictionaryEntryList}" var="dictionaryEntry">
			<tr>
			  <td>${dictionaryEntry.word.word}</td>
  			  <td>${dictionaryEntry.lemma.word}</td>
  			  <td><c:forEach items="${dictionaryEntry.tagParts}" var="tagPart"><fmt:message key="${tagPart}"/>, </c:forEach></td>
		    </tr>
		</c:forEach>
		</tbody>
	</table>
</div>
	
<form action="<c:url value="/dictionaryEntry"/>"  method="get" >
    <input type="submit" value="Incluir" />
</form>

