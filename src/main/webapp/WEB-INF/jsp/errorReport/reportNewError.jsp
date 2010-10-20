<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<p>
	Bem-vindo ao formulário de avaliação do corretor gramatical CoGrOO. Obrigado por sua colaboração, que é importante para o aprimoramento da ferramenta.
</p>
<p>
	Ao submeter textos, sempre tome cuidado para não enviar conteúdo confidencial. Você deve concordar em licenciar o texto submetido sob os termos da <a target="_blank" href="http://www.gnu.org/licenses/lgpl-3.0-standalone.html">LGPL</a>, tornando o publico.
</p>

<br/>

<c:if test="${empty text}">
	<legend>Digite um texto com problema:</legend>
	<form id="formSendErrorText"  action="<c:url value="/reportNewErrorAddText"/>" method="post" >
	    <textarea rows="4" cols="70" name="text">${text}</textarea>
	    <input type="submit" value="Enviar" id="sendErrorText"/>
	</form>
</c:if>
<c:if test="${not empty text}">
	<legend>Texto digitado:</legend>
	<div class="analise_text">
		<p><b>${annotatedText}</b></p>
	</div>
	
	<form id="report"  action="<c:url value="/reportNewError"/>" method="post" >
	
		<h2>Intervenções indevidas</h2>
		
		<p>Algumas vezes o CoGrOO pode errar ao apontar um texto como errado, chamamos isto de intervenção indevida. Aqui você pode classificar uma intervenção do CoGrOO como:</p>
		
		<DL>
			<DT><STRONG>Falso erro</STRONG></DT>
				<DD>Não existe erro, o verificador apontou este erro inapropriadamente.</DD>
			<DT><STRONG>Classificação inapropriada</STRONG></DT>
				<DD>O erro existe, mas está classificado de forma errada.</DD>
			<DT><STRONG>Sugestão inapropriada</STRONG></DT>
				<DD>O erro existe e está classificado corretamente, mas nenhuma das sugestões indicadas está correta.</DD>
		</DL>
		
		<c:if test="${empty singleGrammarErrorList}">
			<p><strong>Não foram emcontrados erros no texto.</strong></p>
		</c:if>
		<c:if test="${not empty singleGrammarErrorList}">
			<c:forEach items="${singleGrammarErrorList}" var="singleGrammarError" varStatus="i">
				
				<h3>Intervenção ${ i.count }:</h3>
					<div class="analise_text">
						<p><b>${singleGrammarError.annotatedText}</b></p>
					</div>
					
					<DL>
						<DT><STRONG>Sugestões:</STRONG></DT>
							<DD>
								<c:forEach items="${singleGrammarError.mistake.suggestions}" var="suggestion">
									${suggestion};&nbsp;
								</c:forEach>
							</DD>
						<DT><STRONG>Mensagem curta:</STRONG></DT>
							<DD>${singleGrammarError.mistake.shortMessage}</DD>
						<DT><STRONG>Mensagem longa:</STRONG></DT>
							<DD>${singleGrammarError.mistake.longMessage}</DD>
					</DL>
					
					<legend>Classifique esta intervenção:</legend>
					<select name="badint[${ i.count }]">
						<option value="ok">Intervenção correta.</option>
						<option value="FALSE_ERROR">Falso erro, a frase está correta.</option>
						<option value="INAPPROPRIATE_DESCRIPTION">Erro existe, mas sua descrição foi inapropriada.</option>
						<option value="INAPPROPRIATE_SUGGESTION">Erro existe, mas a sugestão é inapropriada.</option>
					</select>
	
					<div id="comments_${ i.count }">
						<legend>Comentários:</legend>
						<textarea rows="4" cols="70" name="comments[${ i.count }]"></textarea>
					</div>
					
					
			</c:forEach>
		
		</c:if>
		<br/>
		<input type="submit" value="Enviar relatório" id="sendError"/>
	</form>
</c:if>