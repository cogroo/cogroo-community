<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script src="<c:url value='/js/jquery.caret-range-1.0.js' />" type="text/javascript" ></script>

<script type="text/javascript" charset="utf-8">

var omissionCount = 0;

$(function() {
	
	$('#addNewOmission').click(function(e) {
		
		var input = $("#selector");
		var range = input.caret();
		
		$('<div>	' +
			'	<h3>Omissão 1:</h3>	' +
			'	Texto selecionado:	' +
			'	<div class="analise_text">	' +
			'		<p><b>${singleGrammarError.annotatedText}</b></p>	' +
			'	</div>	' +
			'	<div id="comments_1">	' +
			'		Comentários:<br/>	' +
			'		<textarea rows="4" cols="70" name="comments[1]"></textarea>	' +
			'	</div>	' +
			'</div>').insertBefore('#addNewOmission');
		);
		
		
		var text = null;
		
		// Get selected text
		text = input.val().substr(range.start, range.end - 1);
		alert(omissionCount++);
		// Insert text at caret then restore caret
		var value = input.val();
		text = " New Text ";
		input.val(value.substr(0, range.start) + text + value.substr(range.end, value.length));
		input.caret(range.start + text.length);
		
		// Select first ten characters of text
		input.caret(0, 10);
	});
});
</script>

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
					
					<legend>Classifique esta intervenção:</legend><br/>
					<select name="badint[${ i.count }]">
						<option value="ok">Intervenção correta.</option>
						<option value="FALSE_ERROR">Falso erro, a frase está correta.</option>
						<option value="INAPPROPRIATE_DESCRIPTION">Erro existe, mas sua descrição foi inapropriada.</option>
						<option value="INAPPROPRIATE_SUGGESTION">Erro existe, mas a sugestão é inapropriada.</option>
					</select>
	
					<div id="comments_${ i.count }">
						Comentários:
						<br><textarea rows="4" cols="70" name="comments[${ i.count }]"></textarea>
					</div>
					

					
			</c:forEach>
		
		</c:if>
		<h2>Omissões</h2>
		<p>Aqui você pode indicar os erros gramaticais que foram ignorados pelo CoGrOO.</p>
		<p>Para indicar uma nova omissão, selecione com o cursor o texto com erro e clique "Indicar nova omissão".</p>
		
		Selecione a omissão:<br/>
		<textarea rows="4" cols="70" readonly="readonly" id="selector" >${text}</textarea>
		<a class="a_button" id="addNewOmission" >Indicar nova omissão</a>
		
		<br/>
		<input type="submit" value="Enviar relatório" id="sendError"/>
	</form>
</c:if>