<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

<c:if test="${justAddedDictionaryEntry}">
	<script type="text/javascript">_gaq.push(['_trackEvent', 'Dictionary', 'add entry', '${login}']);</script>
</c:if>

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
		  <th title="Exibe as ocorrências da palavra encontradas no léxico.">Palavra</th>
		  <th title="Exibe o radical (forma primitiva) de cada palavra.">Radical</th>
		  <th title="Exibe a etiqueta gramatical de cada palavra.">Etiqueta</th>
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
    <input type="submit" value=" Incluir &raquo; " />
</form>

