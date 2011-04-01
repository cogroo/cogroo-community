<div id="sitemap">
<h2>Mapa do site</h2>
<ul>
	<li class="outside_list"><a href="<c:url value="/grammar"/>">Análise Gramatical</a></li>
	<li class="outside_list"><a href="<c:url value="/errorEntries"/>">Problemas Reportados</a>
	<ul>
		<c:forEach items="${errorEntryList}" var="errorEntry">
			<li><a href="<c:url value="/errorEntry/${errorEntry.id}"/>">Problema #${errorEntry.id}: ${errorEntry.text}</a></li>
		</c:forEach>
	</ul>
	</li>
	<li class="outside_list"><a href="<c:url value="/ruleList"/>">Regras</a>
	<ul>
		<c:forEach items="${ruleList}" var="rule">
			<li><a href="<c:url value="/rule/${rule.id}"/>">Regra #${rule.id}: ${rule.shortMessage}</a></li>
		</c:forEach>
	</ul>
	</li>
	<li class="outside_list"><a href="<c:url value="/dictionaryEntrySearch"/>">Léxico</a></li>
	<li class="outside_list"><a href="<c:url value="/register"/>">Cadastro</a></li>
	<li class="outside_list"><a href="<c:url value="/stats"/>">Estatísticas</a></li>
	<li class="outside_list"><a href="<c:url value="/about"/>">Sobre</a></li>
</ul>
<br />
</div>