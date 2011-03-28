<%@ page contentType="text/html; charset=UTF-8"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>

<script type="text/javascript" charset="utf-8">
	
	$(document).ready(function() {
		
		$('#table_id').dataTable({
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
			},
			"aLengthMenu": [20,50,100,200],
			"iDisplayLength": 20
		} );
		
	} );
</script>

<div>
	<h2>Consultar palavra <span class="help"><a onclick="onOff('helpRuleList'); return false" href="#"><img src="<c:url value='/images/help.png' />" /></a></span></h2>
		<div id="helpRuleList" style="display: none;" class="help">
			<p>Busca uma palavra no dicionário léxico do corretor CoGrOO.</p>
			<p>Para cada entrada encontrada, é exibido o radical e a etiqueta gramatical correspondente.</p>
		</div>
</div>


<div>
	<p>Digite a palavra que deseja procurar no léxico:</p>
	<form action="<c:url value="/dictionaryEntrySearch"/>" method="post">
		<input type="text" id="word" value="${word}" name="word"  maxlength="128"/><br>
		<input type="submit" value=" Procurar &raquo; " id="go" />
	</form>
</div>

<div class="search">
	<c:choose>
		<c:when	test="${empty word}">
			<p>É necessário digitar uma palavra.</i></p>
		</c:when>
		<c:when	test="${dictionaryEntryList ne null and empty dictionaryEntryList}">
			<p>Não foi possível encontrar a palavra <i>${word}</i>.</p>
		</c:when>
		<c:when test="${not empty dictionaryEntryList}">
			<form action="<c:url value="/dictionaryEntryDelete"/>" method="post">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
				<caption>
					<p>Resultado da busca pela palavra: "<b>${word}</b>"</p>
				</caption>
				<thead>
					<tr>
<!--						<th></th>-->
						  <th title="Exibe as ocorrências da palavra encontradas no léxico.">Palavra</th>
						  <th title="Exibe o radical (forma primitiva) de cada palavra.">Radical</th>
						  <th title="Exibe a etiqueta gramatical de cada palavra.">Etiqueta</th>
					</tr>					
				</thead>
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
		</c:when>
	</c:choose>
</div>
