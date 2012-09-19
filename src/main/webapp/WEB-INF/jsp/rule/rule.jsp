<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>

<h2>Regra ${rule.id}</h2>

<span style="FLOAT: right; POSITION: static">
<c:if test="${previousRule != NULL}">
[<a href="<c:url value="/rules/${previousRule}"/>">anterior</a>]
</c:if>
<c:if test="${nextRule != NULL}">
[<a href="<c:url value="/rules/${nextRule}"/>">próxima</a>]
</c:if>
</span>

<div class="rule_details">
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Categoria:</th><td>${rule.category}</td>
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
			
			<c:if test="${active != NULL }">
			<tr>
			    <th>Ativa:</th><td><fmt:message key="yn${active}"/></td>
			</tr>
			</c:if>
			
			<c:if test="${method == \"PHRASE_LOCAL\" }">
			<tr>
			    <th>Tipo de regra:</th><td>Dentro de um sintagma nominal</td>
			</tr>
			</c:if>
			
			<c:if test="${pattern != NULL}">
				<tr>
				    <th>Padrão da regra:</th><td></td>
				</tr>
				<tr>
				    <td align="center" colspan="2">${pattern}</td>
				</tr>
			</c:if>
			
			<c:if test="${replacePattern != NULL}">
				<tr>
				    <th>Padrão da sugestão:</th><td>${replacePattern}</td>
				</tr>
			</c:if>
			
		</tbody>
	</table>
	</div>
	<div class="rule_examples">
	<h3>Exemplos de erros</h3>
		    			<c:forEach items="${exampleList}" var="examples" varStatus="i">
		    				<div class="dashed_white">
								<b>Incorreto:</b> <br />
									<div class="analise_text_incorrect">
										<p>${examples.a.a}</p>
									</div>
									<c:set var="processResultList" scope="request" value="${examples.a.b}" />    
									<c:set var="id" scope="request" value="i${i.count}" />
									<c:set var="hidden" scope="request" value="hidden" />
									<jsp:include page="/analysisdetails.jspf" /><br />
								<b>Correto:</b> <br />
									<div class="analise_text_correct">
										<p>${examples.b.a}</p>
									</div>
									<c:set var="processResultList" scope="request" value="${examples.b.b}" />    
									<c:set var="id" scope="request" value="c${i.count}" />
									<c:set var="hidden" scope="request" value="hidden" />
									<jsp:include page="/analysisdetails.jspf" />
							</div>
							<br/>
						</c:forEach> 
					</ol>
	
			<form id="formOwnExample"  action="<c:url value="/grammar"/>" method="post" >
			    <input type="hidden" name="text" value="${rule.examples[0].incorrect}" />
			    <input type="submit" value=" Teste seu próprio exemplo &raquo; " id="submitOwnExample"/>
			</form>
	<br />
</div>