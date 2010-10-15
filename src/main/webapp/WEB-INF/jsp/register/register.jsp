<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

	<h1>Cadastro no CoGrOO Comunidade</h1>
	
	<p>O cadastro é necessário para evitar abusos como spam. Seus dados nunca serão divulgados.</p>
	
	<form action="<c:url value='/register'/>" method="post">
        <div >
            <p>
            	<label for="login" style="width: 150px;" class="defaultlbl">Login</label>
            	<input type="text" value="${ login }" maxlength="20" style="width: 300px;" class="inputtxt" name="login" id="login">
            	<span>*</span>
            </p>
            <p>
            	<label for="email" style="width: 150px;" class="defaultlbl">Endereço e-mail</label>
            	<input type="text" value="${ email }" maxlength="60" style="width: 300px;" class="inputtxt" name="email" id="email"> 
            	<span>*</span>
            </p>
            <p>
            	<label class="defaultlbl" style="width: 150px" for="pw">Senha</label>
            	<input type="password"  id="pw" name="password" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            	<span>*</span>
            </p>
            <p>
            	<label class="defaultlbl" style="width: 150px" for="pw">Repita a Senha</label>
            	<input type="password" id="pw" name="passwordRepeat" class="inputtxt" style="width: 300px;" maxlength="20" /> 
            	<span>*</span>
            </p>
            <p>
            	<label for="name" style="width: 150px;" class="defaultlbl">Nome</label>
            	<input type="text"  value="${ name }" maxlength="40" style="width: 300px;" class="inputtxt" name="name" id="name">
            </p>
            <p>
            	<span>* Campos obrigatórios</span>
            </p>
        </div>
        
        <div class="specialframe">
            <h3 style="margin-top: 0px;">Contrato de Licença</h3>
            <p>Todo o conteúdo do CoGrOO e do CoGrOO Comunidade são publicados sob licenças de <i><a target="_blank" href="http://www.opensource.org/docs/osd">código aberto</a></i>. Inclusões, alterações e propostas para melhorar os recursos estarão sujeitas ao licenciamento dos mesmos.</p>
            
			<p>Eu entendo os princípios do código aberto e os aceito. Aceito também que os dicionários podem ser liberados sob outras licenças "open source", como definido pela OSI (<a  target="_blank" href="http://www.opensource.org/">Open Source Initiative</a>).</p>


            <p style="text-align: right;"><label><img alt="" src="./images/tag_integrated.png"> Eu aceito <input type="checkbox" ${ iAgree ? "checked" :"" } name="iAgree"></label></p>
        </div>
        <p style="text-align: right;"><input type="submit" value="Se inscrever" class="button"></p>
    </form>