
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>

<span style="FLOAT: right; POSITION: static">
<c:if test="${previousRule != NULL}">
[<a href="<c:url value="/rule?rule.id=${previousRule}"/>">anterior</a>]
</c:if>
<c:if test="${nextRule != NULL}">
[<a href="<c:url value="/rule?rule.id=${nextRule}"/>">próxima</a>]
</c:if>
</span>

<div class="rule_details">
	<h3>Detalhes da regra #${rule.id}</h3>
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Tipo:</th><td>${rule.type}</td>
			</tr>
			<tr>
			    <th>Grupo:</th><td>${rule.group}</td>
			</tr>
			<tr>
			    <th>Mensagem curta:</th><td>${rule.shortMessage}</td>
			</tr>
			<tr>
			    <th>Mensagem:</th><td>${rule.message}</td>
			</tr>
			<tr>
			    <th>Ativa:</th><td><fmt:message key="yn${rule.active}"/></td>
			</tr>
			<tr>
			    <th>Padrão da regra:</th><td></td>
			</tr>
			<tr>
			    <td align="center" colspan="2">${pattern}</td>
			</tr>
			<tr>
			    <th>Padrão da sugestão:</th><td>${replacePattern}</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="rule_examples">
	<h3>Exemplos de erros</h3>
		    			<c:forEach items="${exampleList}" var="example" varStatus="i">
		    				<div style="border:1px dashed #808080; margin-right: 12px; padding-left: 12px">
								<b>incorreto:</b> <br />
									<div class="analise_text_incorrect">
										<p>${example.a.a}</p>
									</div>
									<c:set var="processResultList" scope="request" value="${example.a.b}" />    
									<c:set var="id" scope="request" value="i${i.count}" />
									<c:set var="hidden" scope="request" value="hidden" />
									<jsp:include page="/analysisdetails.jspf" /><br />
								<b>correto:</b> <br />
									<div class="analise_text_correct">
										<p>${example.b.a}</p>
									</div>
									<c:set var="processResultList" scope="request" value="${example.b.b}" />    
									<c:set var="id" scope="request" value="c${i.count}" />
									<c:set var="hidden" scope="request" value="hidden" />
									<jsp:include page="/analysisdetails.jspf" />
							</div>
							<br/>
						</c:forEach> 
					</ol>
			
	<br />
</div>