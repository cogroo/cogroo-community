<%@ page contentType="text/html; charset=UTF-8" %>  

<c:if test="${gaEventPasswordRecovered}">
	<script type="text/javascript">
	_gaq.push(['_trackEvent', 'User', 'recovered password', '${provider}']);
	</script>
</c:if>	

<div id="specialFrame">
<h2>Recuperação de Senha</h2>
<br/>
<p>Sua senha foi alterada com sucesso.</p>
<br/>
</div>