<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

	<h2>Recuperação de Senha</h2>
	<br/>
	<p>Informe uma nova senha.</p>
	<br/>
	<form action="<c:url value='/recover/${ user.id }/${ codeRecover }'/>" method="post">
        <div >
            <p>
            	<label class="defaultlbl" style="width: 100px" for="pw">Senha:</label>
            	<input type="password"  id="pw" name="password" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            </p>
            <p>
            	<label class="defaultlbl" style="width: 100px" for="pw">Confirme a senha:</label>
            	<input type="password" id="pw" name="passwordRepeat" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            </p>
        </div>
        <p style="text-align: right;"><input type="submit" value=" Enviar &raquo; " class="button"></p>
    </form>
    
