<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript" charset="utf-8">
	var oTable;
	
	$(document).ready(function() {
		
		oTable = $('#resultsTable').dataTable( {
			"oLanguage": {
				"sLengthMenu": "Exibir _MENU_ entradas por página",
				"sSearch": "Filtrar entradas:",
				"sFirst": "Primeira página",
				"sLast": "Última página",
				"sNext": "Próxima página",
				"sPrevious": "Página anterior",
				"sZeroRecords": "Desculpe, nada encontrado.",
				"sInfo": "Exibindo de _START_ até _END_ de um total de _TOTAL_ entradas",
				"sInfoEmpty": "Exibindo de 0 até 0 de um total de 0 entradas",
				"sInfoFiltered": "(filtrados de um total de _MAX_ entradas)"
			}
		} );
		
	} );
</script>

<div>
	<h1>Consultar palavras</h1>
</div>


<div>
	<p>Digite a palavra que deseja procurar</p>
	<form action="<c:url value="/dictionaryEntrySearch"/>" method="post">
		<input type="text" id="word" value="${word}" name="word"  maxlength="128"/> 
		<input type="submit" value="Procurar" id="go" />
	</form>
</div>

<div class="search">
	<c:choose>
		<c:when	test="${dictionaryEntryList ne null and empty dictionaryEntryList}">
			<p>Não foi possível encontrar a palavra <i>${word}</i></p>
		</c:when>
		<c:when test="${not empty dictionaryEntryList}">
			<form action="<c:url value="/dictionaryEntryDelete"/>" method="post">
			<table id="resultsTable">
				<caption>
					<p>Resultado da busca pela palavra: "<b>${word}</b>"</p>
				</caption>
				<thead>
					<tr>
<!--						<th></th>-->
						<th>Palavra</th>
						<th>Lemma</th>
						<th>Etiqueta</th>
					</tr>					
				</thead>
				<tr>
				<%!int i = 0;%>
				<c:forEach items="${dictionaryEntryList}" var="dictionaryEntry">
					<tr>
<!--						<td>-->
<!--							<c:if test="${loggedUser.logged}">-->
<!--								<input type="checkbox" name="listaWords[<%=i++%>]"-->
<!--									value="${dictionaryEntry.word.word}-${dictionaryEntry.lemma.word}-${dictionaryEntry.posTag.posTag}">-->
<!--							</c:if>-->
<!--						</td>-->
						<td>${dictionaryEntry.word.word}</td>
						<td>${dictionaryEntry.lemma.word}</td>
						<td>
							<c:forEach items="${dictionaryEntry.tagParts}" var="tagPart">
								<fmt:message key="${tagPart}" />, 
							</c:forEach>
						</td>
					</tr>
				</c:forEach>
			</table>
<!--			<c:if test="${loggedUser.logged}">-->
<!--				<button type="submit" name="_method" value="DELETE">Apagar</button>-->
<!--			</c:if>-->
			</form>
			<br/>
			<br/>
			<br/>
		</c:when>
	</c:choose>
</div>
