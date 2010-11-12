<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>
<script src="<c:url value='/js/jquery-fieldselection.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.NobleCount.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	
	$('#text').NobleCount('#count',{
		on_negative: 'go_red',
		on_positive: 'go_green',
		max_chars: 255
	});
    
  var count=0;
  
    // Webkit bug: can't use select if textarea is readonly
  	if ($.browser.webkit) {
  		$("#selector").removeAttr( "readonly" )
	 }
  
  $('#addNewOmission').click(function() {
	var input = $("#selector");
    
    var range = input.getSelection();
    
     if(range.end > 0 && range.start != range.end) {
    	 count++;
    	var text = input.text();
        
        var selection = text.substr(range.start, range.end - range.start);
        var before = text.substr(0, range.start);
        var after = text.substr(range.end);
        
        
        
        var html = $('#toCopy').children('div').clone();
        // Formatando novo input
        $(html).find('div.#omission').html( before + '<span class="omission">' + selection + '</span>' + after);
        $(html).find('h3').text( 'Omissão ' + count + ':');
        $(html).find('select').attr('name','omissionClassification[' + count +']').
        	attr('ONCHANGE',"if( 'custom' == this.options[this.selectedIndex].value ) {on('customOmissionTextDiv_" + count + "');} else {off('customOmissionTextDiv_" + count + "');} ;");
        $(html).find('#customOmissionText').attr('name','customOmissionText[' + count +']');
        
        $(html).find('#omissionStart').attr('name','omissionStart[' + count +']');
        $(html).find('#omissionEnd').attr('name','omissionEnd[' + count +']');
        $(html).find('#omissionStart').attr('value',range.start);
        $(html).find('#omissionEnd').attr('value',range.end);
        $(html).find('#omissionComment').attr('name','omissionComment[' + count +']');
        $(html).find('#omissionReplaceBy').attr('name','omissionReplaceBy[' + count +']');
        $(html).find('#customOmissionTextDiv').attr('id','customOmissionTextDiv_' + count);
        
        $('#deleteIfHaveOmission').remove();
        $('#omissionList').append(html);
    } 
    
    
  });
  
	$('#b').click(function() {     
	  alert(getSelection());
	});

	
});
</script>

<c:if test="${not loggedUser.logged}">
	<p>Por favor, conecte-se no canto superior direito para enviar novo erro.</p>
	<p>Não é cadastrado? <a href="<c:url value="/register"/>">Clique aqui</a> e cadastre-se! É rápido e gratuito!</p>
</c:if>
<c:if test="${loggedUser.logged}">

	<div class="white_box">
	
		<h2>Reportar problema <span class="help"><a onclick="onOff('helpNewError'); return false" href="#">ajuda</a></span></h2>
		
		<div id="helpNewError" style="display: none;" class="help">
			<p>
				Bem-vindo ao relatório de erros do corretor gramatical CoGrOO. Obrigado por sua colaboração, ela é fundamental para o aprimoramento da ferramenta.
			</p>
			<p>
				Ao submeter textos, sempre tome cuidado para não enviar conteúdo confidencial. Você deve concordar em licenciar o texto submetido sob os termos da <a target="_blank" href="http://www.gnu.org/licenses/lgpl-3.0-standalone.html">LGPL</a>, tornando o público.
			</p>
		</div>

		<c:if test="${!analyzed}">
			Digite um texto para buscar erros gramaticais com o CoGrOO:
			<form id="formSendErrorText"  action="<c:url value="/reportNewErrorAddText"/>" method="post" >
			    <textarea rows="4" cols="70" name="text" id="text">${text}</textarea><br/>
			    <span id="count">255</span> <input type="submit" value=" Analisar &raquo; " id="sendErrorText"/>
			</form>
	</div>
		</c:if>
	
		<c:if test="${analyzed && 	not empty text}">
			<legend>Seu texto:</legend>
			<div class="analise_text_correct">
				<p><b>${annotatedText}</b></p>
			</div>
		
			<c:set var="processResultList" scope="request" value="${processResultList}" />    
			<c:set var="id" scope="request" value="id" />
			<c:set var="hidden" scope="request" value="hidden" />
			<jsp:include page="/analysisdetails.jspf" />
	</div>
		<form id="report"  action="<c:url value="/reportNewError"/>" method="post" >
		
			<input type="hidden" id="userText" name="text" value="${text}"/>
	
	<div class="white_box">	
		
			<h3>Intervenções indevidas <span class="help"><a onclick="onOff('helpBadInt'); return false" href="#">ajuda</a></span></h3>
			
			<div id="helpBadInt" style="display: none;" class="help">
				<p>Uma intervenção é considerada indevida quando o corretor identifica um erro onde ele não 
				existe, ou quando o erro existe, mas houve algum erro na sua classificação.</p>
				<p>Tipos de intervenções indevidas:</p>
			
				<DL>
					<DT>Falso erro</DT>
						<DD>Não existe erro, o verificador identificou erroneamente.</DD>
					<DT>Classificação inapropriada</DT>
						<DD>O erro existe, mas foi classificado de forma errada.</DD>
					<DT>Sugestão inapropriada</DT>
						<DD>O erro existe e está classificado corretamente, mas nenhuma das sugestões indicadas é correta.</DD>
				</DL>
			</div>
			
			<c:if test="${empty singleGrammarErrorList}">
				<div class="dashed_white">
					<p>Não foram encontrados erros no texto.</p>
				</div>
			</c:if>
			<c:if test="${not empty singleGrammarErrorList}">
				<c:forEach items="${singleGrammarErrorList}" var="singleGrammarError" varStatus="i">
						<div class="dashed_white">
						<h3>Intervenção ${ i.count }:</h3>
						
							<div class="analise_text_incorrect">
								<p><b>${singleGrammarError.annotatedText}</b></p>
							</div>
							
							<a onclick="onOff('helpBadInt_${ i.count }'); return false" href="#">detalhes</a> <br/>
							<div id="helpBadInt_${ i.count }" style="display: none;" class="help">
								<table border="0">
									<tr>
										<th width="120px" align="right"><b>Sugestões:</b></th>
										<td>
											<c:forEach items="${singleGrammarError.mistake.suggestions}" var="suggestion">
												${suggestion};&nbsp;
											</c:forEach>
										</td>
									</tr>
									<tr>
										<th align="right"><b>Mensagem curta:</b></th>
										<td>${singleGrammarError.mistake.shortMessage}</td>
									</tr>
									<tr>
										<th align="right"><b>Mensagem longa:</b></th>
										<td>${singleGrammarError.mistake.longMessage}</td>
									</tr>
								</table>
							</div>
							Classifique essa intervenção:<br/>
							<select name="badint[${ i.count }]" 
								ONCHANGE="if( this.selectedIndex == 0 ) {off('comments_${ i.count }');} else {on('comments_${ i.count }');} ;">
								<option value="ok">Intervenção correta.</option>
								<option value="falseError">Falso erro, a frase está correta.</option>
								<option value="inappropriateDescription">Erro existe, mas a descrição é inapropriada.</option>
								<option value="inappropriateSuggestion">Erro existe, mas a sugestão é inapropriada.</option>
							</select>
			
							<div style="display: none;" id="comments_${ i.count }">
								Comentários:
								<br><textarea rows="4" cols="70" name="comments[${ i.count }]"></textarea>
							</div>
							<input type="hidden" id="badintStart" name="badintStart[${ i.count }]" value="${singleGrammarError.mistake.start}" />
							<input type="hidden" id="badintEnd" name="badintEnd[${ i.count }]" value="${singleGrammarError.mistake.end}" />
							<input type="hidden" id="badintRule" name="badintRule[${ i.count }]" value="${singleGrammarError.mistake.ruleIdentifier}" />
						</div>
				</c:forEach>
				
			</c:if>
		</div>
		
		<div class="white_box">	
			<h3>Omissões<span class="help"><a onclick="onOff('helpOmission'); return false" href="#">ajuda</a></span></h3>
			
			<div id="helpOmission" style="display: none;" class="help">
				<p>Indique os erros gramaticais que foram ignorados pelo CoGrOO.</p>
			</div>
			<div id="omissionList">
				<div class="dashed_white" id="deleteIfHaveOmission">
					<p>Você ainda não reportou nenhuma omissão.</p>
				</div>
			</div>
			<div class="red_border_box">
				<h5>Nova omissão<span class="help"><a onclick="onOff('helpAddOmission'); return false" href="#">ajuda</a></span></h5>
				<div id="helpAddOmission" style="display: none;" class="help">
					<p>Para adicionar uma omissão, selecione o trecho omisso na área abaixo e clique no botão "Adicionar omissão".</p>
					<p>Em seguida, preencha os dados da omissão na caixa acima.</p>
					<p>Repita o procedimento para cada omissão.</p>
				</div>
				Selecione com o mouse o trecho que contém uma omissão, se houver:<br/>
				<textarea rows="2" cols="70" readonly="readonly" id="selector" >${text}</textarea><br/>
				<button type="button" id="addNewOmission" class="a_button"> Adicionar omissão &raquo; </button>
			</div>
			<div id="toCopy" style="display:none;">
			 	<div class="dashed_white">
					<h3 id="omissionHeader">Omissão X:</h3>
					<div class="analise_text_incorrect" id="omission">
						
					</div>
					
					<label style="width: 170px; text-align: left; padding-right: 10px"" class="defaultlbl">Classifique esta omissão:</label>
					<select style="width: 300px;" name="dummyName" ONCHANGE="dummy">
						<c:forEach items="${omissionCategoriesList}" var="omissionCategories">
							<option value="${omissionCategories}">${omissionCategories}</option>
						</c:forEach>
						<option value="custom">Personalizada</option>
					</select><br/>
					
					<div style="display: none;" id="customOmissionTextDiv">
						<label style="width: 170px; text-align: left; padding-right: 10px"" class="defaultlbl">Classificação personalizada:</label>
				      	<input style="width: 300px;" id="customOmissionText" name="dummyOmissionText"></input><br/>
			      	</div>
			      	<label style="width: 170px; text-align: left; padding-right: 10px"" class="defaultlbl">Substituir por:</label>
			      	<input style="width: 300px;" id="omissionReplaceBy" name="dummyOmissionReplaceBy"></input><br/>
			      	Comentários:<br/>
			      	<textarea rows="1" cols="70" id="omissionComment" name="dummyOmissionComment"></textarea>
			      	<input type="hidden" id="omissionStart" name="dummyOmissionStart" value=""/>
			      	<input type="hidden" id="omissionEnd" name="dummyOmissionStart" value=""/>
			    </div>
			</div>
			
		</div>
			<input type="submit" value=" Reportar problema &raquo; " id="sendError"/>
		</form>
	</c:if>
</c:if>
