<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

	<h2>Recuperação de Senha</h2>
	
	<p>Por favor, informe uma senha a ser utilizada no CoGrOO Comunidade.</p>
	
	<form action="<c:url value='/recover/${ user.id }/${ codeRecover }'/>" method="post">
        <div >
            <p>
            	<label class="defaultlbl" style="width: 100px" for="pw">Senha:</label>
            	<input type="password"  id="pw" name="password" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            </p>
            <p>
            	<label class="defaultlbl" style="width: 100px" for="pw">Repita a senha:</label>
            	<input type="password" id="pw" name="passwordRepeat" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            </p>
        </div>
        <br/>
		<div class="specialframe">
		    <h3 style="margin-top: 0px;">Instruções</h3>
		    <ul>
		    	<li>1. Enviaremos uma mensagem para seu e-mail com um link.</li>
				<li>2. Abra seu e-mail e clique no link indicado no corpo do email.</li>
		  		<li>3. O link te redirecionará para um formulário que solicitará a nova senha.<b> <-- Estamos aqui!</b></li>
		  		<li>4. Tudo pronto! Você já pode entrar no CoGrOO Comunidade com a sua nova senha.</li>
		    </ul>
		</div>
		          
        <br />        
        <br />
        <p style="text-align: right;"><input type="submit" value=" Enviar &raquo; " class="button"></p>
    </form>
    
