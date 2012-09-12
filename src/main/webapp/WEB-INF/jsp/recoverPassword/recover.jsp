<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
	
<div class="specialframe">
	<h2>Recuperação de Senha</h2>
	<br/>
	<p>Informe abaixo seu e-mail do CoGrOO Comunidade:</p>

	<form action"<c:url value='/recover'/>" method="post">
        <div>
            <p>
            	<label for="email" style="width: 100px;" class="defaultlbl"></label>
            	<input type="text" value="${email }" maxlength="60" style="width: 350px;" class="inputtxt" name="email"> 
            </p>
        </div>
        
        <p style="text-align: right;"><input type="submit" value=" Enviar &raquo; " class="button"></p>
    </form>
</div>