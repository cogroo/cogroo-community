<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

	<h2>Recuperação de Senha</h2>
	
	<p>Por favor, informe seu e-mail de cadastro no CoGrOO Comunidade.</p>
	
	<form action="<c:url value='/recover'/>" method="post">
        <div >
            <p>
            	<label for="email" style="width: 100px;" class="defaultlbl">E-mail:</label>
            	<input type="text" value="${ email }" maxlength="60" style="width: 300px;" class="inputtxt" name="email" id="email"> 
            </p>
        </div>
        
        <br />
        <div class="specialframe">
            <h3 style="margin-top: 0px;">Instruções</h3>
            <ul>
            	<li>1. Enviaremos uma mensagem para seu e-mail com um link.<b> <-- Estamos aqui!</b></li>
    			<li>2. Abra seu e-mail e clique no link indicado no corpo do email.</li>
    			<li>3. O link te redirecionará para um formulário que solicitará a nova senha.</li>
    			<li>4. Tudo pronto! Você já pode entrar no CoGrOO Comunidade com a sua nova senha.</li>
            </ul>
        </div>
        
        <br />
        <p style="text-align: right;"><input type="submit" value=" Enviar &raquo; " class="button"></p>
    </form>