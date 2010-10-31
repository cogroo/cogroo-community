<script src="<c:url value='/js/jquery.NobleCount.min.js' />" type="text/javascript" ></script>
<script type="text/javascript"><!--//--><![CDATA[//><!--

	$(document).ready(function(){
		$('#text').NobleCount('#count',{
			on_negative: 'go_red',
			on_positive: 'go_green',
			max_chars: 1024
		});
	});
	
//--><!]]>

</script>		
		
		<h1>Analisador Gramatical.</h1>
		<p>Digite o texto em português para verificar sua análise:</p>
		<form action="<c:url value="/grammar"/>"  method="post" >
		    <textarea rows="4" cols="70" name="text" id="text">${text}</textarea>
		    <br/>
		    <span id="count">1024</span> <input type="submit" value="Processar" id="go"/>
		</form>
		<c:if test="${loggedUser.logged}">
			<c:if test="${not empty text}">
				<form id="formSendErrorText"  action="<c:url value="/reportNewErrorAddText"/>" method="post" >
				    <input type="hidden" name="text" value="${text}" />
				    <input type="submit" value="Reportar erro" id="sendErrorText"/>
				</form>
			</c:if>
		</c:if>
<jsp:include page="/analysisdetails.jspf" />