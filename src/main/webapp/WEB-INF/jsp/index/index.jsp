
		
		
		<h1>Analisador Gramatical.</h1>
		<p>Digite o texto em português para verificar sua análise:</p>
		<form action="<c:url value="/"/>"  method="post" >
		    <textarea rows="4" cols="70" name="texto">${texto}</textarea>
		    <input type="submit" value="Processar" id="go"/>
		</form>
	
<!--		<c:if test="${not empty processResultList}">-->
<!--			<div id="resposta">-->
<!--			-->
<!--				<p>Resutado do processamento:</p>-->
<!--				<ul>-->
<!--				<c:forEach items="${processResultList}" var="processResult">-->
<!--					<div class="syntaxTree">${processResult.syntaxTree}</div>-->
<!--					<div class="drawTree"></div>-->
<!--				</c:forEach>-->
<!--				</ul>		 -->
<!--			</div>-->
<!--		</c:if>-->

	<jsp:include page="/analysisdetails.jspf" />


