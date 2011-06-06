<%@ page contentType="text/html; charset=UTF-8" %>  

<c:if test="${justRecovered}">
	<script type="text/javascript">
	_gaq.push(['_trackEvent', 'Password Recover', 'succeeded recover', '${login}']);
	</script>
</c:if>	

<h2>Recuperação de Senha</h2>
<br/>
<p>Parabéns, você alterou sua senha! Se precisar, você já pode efetuar o login com a nova senha.</p>
<br/>
<br/>
<div class="specialframe">
    <h3 style="margin-top: 0px;">Instruções</h3>
    <ul>
    	<li>1. Enviaremos uma mensagem para seu e-mail com um link.</li>
    	<li>2. Abra seu e-mail e clique no link indicado no corpo do e-mail.</li>
    	<li>3. O link te redirecionará para um formulário que solicitará a nova senha.</li>
    	<li>4. Tudo pronto! Você já pode entrar no CoGrOO Comunidade com a sua nova senha.<b> <-- Estamos aqui!</b></li>
    </ul>
</div>
