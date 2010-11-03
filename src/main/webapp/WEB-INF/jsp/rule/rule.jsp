<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>

<span style="FLOAT: right; POSITION: static">
<c:if test="${previousRule != NULL}">
[<a href="<c:url value="/rule?rule.id=${previousRule}"/>">anterior</a>]
</c:if>
<c:if test="${nextRule != NULL}">
[<a href="<c:url value="/rule?rule.id=${nextRule}"/>">próxima</a>]
</c:if>
</span>
<div class="report_details">
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
			    <th>É ativa:</th><td><fmt:message  key="${rule.active}"/></td>
			</tr>
		</tbody>
	</table>
	<br/>
	
	Padrão da regra: <br />
	${pattern}<br /><br />
	${replacePattern}<br /><br />
	Exemplos: <br />
		    			<c:forEach items="${exampleList}" var="example" varStatus="i">
		    				<div style="border:1px dashed #808080;">
								<b>incorreto:</b> <br />
									<div class="analise_text">
										<p>${example.a.a}</p>
									</div>
									<c:set var="processResultList" scope="request" value="${example.a.b}" />    
									<c:set var="id" scope="request" value="i${i.count}" />
									<c:set var="hidden" scope="request" value="hidden" />
									<jsp:include page="/analysisdetails.jspf" /><br />
								<b>correto:</b> <br />
									<div class="analise_text">
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