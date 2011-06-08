<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

	<h2>Bem-vindo ao CoGrOO Comunidade</h2>
	
	<p>Para completar seu cadastro, por favor confirme seus dados e aceite o termo de uso:</p>
	
	<form action="<c:url value='/register/oauth'/>" method="post">
        <div style="margin-top: 1em">
            <input type="hidden" value="${ user.service }" maxlength="40" style="width: 300px;" class="inputtxt" name="provider" id="provider">
            <input type="hidden" value="${ user.login }" maxlength="40" style="width: 300px;" class="inputtxt" name="login" id="login">
            <p>
            	<label for="name" class="loginlabel">Nome:
           		<span class="required">*</span></label>
            	<input type="text" value="${ user.name }" maxlength="40" style="width: 300px;" class="inputtxt" name="name" id="name">
            </p>
            <p>
            	<label for="email" class="loginlabel">E-mail:
            	<span class="required">*</span></label>
            	<input type="text" value="${ user.email }" maxlength="60" style="width: 300px;" class="inputtxt" name="email" id="email"> 
            </p>
           
        </div>
        
        <br />
        <div class="specialframe">
            <h3 style="margin-top: 0px;">Termo de Uso</h3>
            <p>Todo o conteúdo do CoGrOO e do CoGrOO Comunidade são publicados sob licenças de <i><a target="_blank" href="http://www.opensource.org/docs/osd">código aberto</a></i>. Inclusões, alterações e propostas para melhorar os recursos estarão sujeitas ao licenciamento dos mesmos.
            Eu entendo os princípios do código aberto e os aceito. Aceito também que os dicionários podem ser liberados sob outras licenças "open source", como definido pela OSI (<a  target="_blank" href="http://www.opensource.org/">Open Source Initiative</a>).</p>


            <p style="text-align: right;"><label><img alt="" src="<c:url value='/images/tag_integrated.png' />"><b> Eu aceito! </b><input type="checkbox" ${ iAgree ? "checked" :"" } name="iAgree"></label></p>
        </div>
        
        <br />
        <p style="text-align: right;"><input type="submit" value=" Inscrever-se &raquo; " class="button"></p>
    </form>