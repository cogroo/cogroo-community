<%@ page contentType="text/html; charset=UTF-8" %>  
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.NobleCount.min.js' />" type="text/javascript" ></script>
<script type="text/javascript"><!--//--><![CDATA[//><!--

	$(document).ready(function(){
		$('#text').NobleCount('#count',{
			on_negative: 'go_red',
			on_positive: 'go_green',
			max_chars: 255
		});
	});
	
//--><!]]>

</script>

<script type="text/javascript">
	if (${justAnalyzed})
	{
		_gaq.push(['_trackEvent', 'Grammatical Analysis', 'succeeded analysis', '${login}']);
	}
</script>		
		
		<h2>Analisador Gramatical</h2>
		<p>Digite um texto para buscar erros gramaticais com o CoGrOO:</p>
		<form action="<c:url value="/grammar"/>"  method="post" >
		    <textarea rows="4" cols="70" name="text" id="text">${text}</textarea>
		    <br/>
		    <span id="count">1024</span> <input type="submit" value="Analisar" id="go"/>
		</form>
		<c:if test="${not empty text}">
			<form id="formSendErrorText"  action="<c:url value="/reportNewErrorAddText"/>" method="post" >
			    <input type="hidden" name="text" value="${text}" />
			    <input type="submit" value="Reportar erro" id="sendErrorText"/>
			</form>
		</c:if>
		<c:set var="processResultList" scope="request" value="${processResultList}" />    
		<c:set var="id" scope="request" value="id" />
		<jsp:include page="/analysisdetails.jspf" />