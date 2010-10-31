<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
//<![CDATA[
	$(document).ready(function() {
		$('#resposta tbody tr:odd').addClass('odd');
		$('#resposta tbody tr:even').addClass('even');
		var word = "palavra";
		$('#word').val(word).css('color', '#999')
			.focus(function(){
				if ($(this).val() == word){
					$(this).css('color', '').val('');
				}
			}).blur(function(){
				if ($(this).val() == '' ){
					$(this).val(word).css('color', '#999');
				}
			});
			
	});
   // ]]>
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

<div class="relatorio">
	<c:choose>
		<c:when	test="${dictionaryEntryList ne null and empty dictionaryEntryList}">
			<p>Não foi possível encontrar a palavra <i>${word}</i></p>
		</c:when>
		<c:when test="${not empty dictionaryEntryList}">
			<form action="<c:url value="/dictionaryEntryDelete"/>" method="post">
			<table id="resposta" border="1">
				<caption>
					<p>Resultado da busca pela palavra: "<b>${word}</b>"</p>
				</caption>
				<thead>
					<tr>
						<th></th>
						<th>Palavra</th>
						<th>Lemma</th>
						<th>Etiqueta</th>
					</tr>					
				</thead>
				<%!int i = 0;%>
				<c:forEach items="${dictionaryEntryList}" var="dictionaryEntry">
					<tr>
						<td>
							<c:if test="${loggedUser.logged}">
								<input type="checkbox" name="listaWords[<%=i++%>]"
									value="${dictionaryEntry.word.word}-${dictionaryEntry.lemma.word}-${dictionaryEntry.posTag.posTag}">
							</c:if>
						</td>
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
			<c:if test="${loggedUser.logged}">
				<button type="submit" name="_method" value="DELETE">Apagar</button>
			</c:if>
			</form>
			<br/>
			<br/>
			<br/>
		</c:when>
	</c:choose>
</div>
