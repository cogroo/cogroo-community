		
		
		<h1>Analisador Gramatical.</h1>
		<p>Digite o texto em português para verificar sua análise:</p>
		<form action="<c:url value="/"/>"  method="post" >
		    <textarea rows="4" cols="70" name="texto">${texto}</textarea>
		    <input type="submit" value="Processar" id="go"/>
		</form>


<jsp:include page="/analysisdetails.jspf" />