<%@ page contentType="text/html; charset=UTF-8" %>  
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>
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

<c:if test="${gaEventGrammarAnalyzed}">
	<script type="text/javascript">
	_gaq.push(['_trackEvent', 'Grammatical Analysis', 'succeeded analysis', '${provider}']);
	</script>
</c:if>		
		
		<h2>Análise Gramatical <span class="help"><a onclick="onOff('helpRuleList'); return false" href="#"><img src="<c:url value='/images/help.png' />" /></a></span></h2>
		<div id="helpRuleList" style="display: none;" class="help">
			<p>Analisa um texto fornecido pelo usuário em busca de erros gramaticais.</p>
			<p>Após a análise, é possível reportar qualquer problema do corretor através do botão "Reportar problema".</p>
		</div>
		<p>Digite um texto para buscar erros gramaticais com o CoGrOO:</p>
		<form action="<c:url value="/grammar"/>"  method="post" >
		    <textarea rows="4" cols="70" name="text" id="text">${text}</textarea>
		    <span id="count">255</span> caracteres restantes<br>
		    <input type="submit" value=" Analisar &raquo; " id="go"/>
		</form>
		<c:set var="processResultList" scope="request" value="${processResultList}" />    
		<c:set var="id" scope="request" value="id" />
		<jsp:include page="/analysisdetails.jspf" />
		<c:if test="${justAnalyzed && not empty text}">
			<c:if test="${empty processResultList}">
				<div class="report_disscussion" style="margin: 1em auto;">
					<b><font color=red>O CoGrOO falhou ao processar o texto. Geralmente você pode tentar novamente em alguns segundos. Se persistir, por favor reporte o problema.</font></b>
				</div>
			</c:if>
			<form id="formSendErrorText"  action="<c:url value="/reports/newtext"/>" method="post" >
			    <input type="hidden" name="text" value="${text}" />
			    <input type="submit" class=".red_button" value=" Reportar problema &raquo; " id="sendErrorText"/>
			</form>
		</c:if>