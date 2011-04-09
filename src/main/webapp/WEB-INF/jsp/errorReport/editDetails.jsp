<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>
<script src="<c:url value='/js/jquery-fieldselection.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	
    // Webkit bug: can't use select if textarea is readonly
  	if ($.browser.webkit) {
  		$("#selector").removeAttr( "readonly" )
	 };
	 
	 // set select text handler for omission
	 $('#addNewOmission').click(function() {
		 omissionChanged();
	 });
	 
	// Type: omission or badint
	 $('#typeSelection').change(function() {
		 typeChanged();
	 });
	 typeChanged();
	 
	 // Custom Omission
    customChanged();
    $('#omissionCategory').change(function() {
    	customChanged();
    });

    
    // BadInt
    $('#badintSelec').change(function() {
    	badintChanged();
	 });
});

function customChanged() {
    if( $('#omissionCategory').val() == 'custom' )   $('#customOmissionText').show();
    else $('#customOmissionText').hide();
}

function typeChanged() {
	
	if (document.getElementById("typeSelection").value == 'OMISSION') {
		$(".omission").show();
		$(".badint").hide();
		restoreOmission();
	} else {
		$(".omission").hide();
		$(".badint").show();
		badintChanged();
	};
};

function badintChanged() {
	
	var badint = document.getElementById("badintSelec").value;
	var text = $("#selector").text();
	
	var s = document.getElementById("badintStart_" + badint).value;
	var e = document.getElementById("badintEnd_" + badint).value;
	
	if(e > 0 && s != e) {
		var selection = text.substr(s, e - s);
		var before = text.substr(0, s);
		var after = text.substr(e);
	    
		$('#selectedOmission').html( before + '<span class="badint">' + selection + '</span>' + after);
	} else {
		$('#selectedOmission').html(text);
	}
};

function restoreOmission() {
	
	var badint = document.getElementById("badintSelec").value;
	var text = $("#selector").text();
	
	var s = document.getElementById("omissionStart").value;
	var e = document.getElementById("omissionEnd").value;
	
	if(e > 0 && s != e) {
		var selection = text.substr(s, e - s);
		var before = text.substr(0, s);
		var after = text.substr(e);
	    
		$('#selectedOmission').html( before + '<span class="omission">' + selection + '</span>' + after);
	} else {
		$('#selectedOmission').html(text);
	}
};

function omissionChanged() {
	 var input = $("#selector");
	    
    var range = input.getSelection();
	    
     if(range.end > 0 && range.start != range.end) {
    	var text = input.text();
        
        var selection = text.substr(range.start, range.end - range.start);
        var before = text.substr(0, range.start);
        var after = text.substr(range.end);
        
        // var html = $('#toCopy').children('div').clone();
        // Formatando novo input
        $('#selectedOmission').html( before + '<span class="omission">' + selection + '</span>' + after);
        $('#omissionStart').attr('value',range.start);
        $('#omissionEnd').attr('value',range.end);
        
        off('updateOmission'); on('displayOmission');
     }
};

</script>

	<h2>Editar entrada #${errorEntry.id}</h2>
	<form id="updateError" action="<c:url value="/reports/${errorEntry.id}"/>" method="POST" >
	<div class="report_details">
		<div class="report_details_table">
			<table >
				<tbody>
					<tr>
					    <th>Tipo:</th>
						   <td><select id="typeSelection" name="type" style="width: 300px;" <c:if test="${not hasError}">disabled="disabled"</c:if>>
								<option value="OMISSION" <c:if test="${not empty errorEntry.omission}">selected="selected"</c:if>>Omissão</option>
								<option value="BADINT" <c:if test="${not empty errorEntry.badIntervention}">selected="selected"</c:if>>Intervenção Indevida</option>
							</select></td>
					</tr>
					
					<tr class="badint">
						<th>Regra:</th>
						<c:if test="${not empty singleGrammarErrorList}">
						<td><select id="badintSelec" name="badintIndex" style="width: 300px;">
							<c:forEach items="${singleGrammarErrorList}" var="singleGrammarError" varStatus="i">
								<option value="${ i.count }"
								<c:if test="${singleGrammarError.mistake.ruleIdentifier eq errorEntry.badIntervention.rule and
									singleGrammarError.mistake.start eq errorEntry.spanStart}">selected="selected"</c:if>
								>${singleGrammarError.mistake.ruleIdentifier} (Intervenção ${ i.count })</option>
							</c:forEach>
						</select></td>
						</c:if>
					</tr>
					<tr class="badint">
						<th>Erro:</th>
		            	<td><select name="badintType" style="width: 300px;">
								<option value="FALSE_ERROR" <c:if test="${errorEntry.badIntervention.classification eq 'FALSE_ERROR'}">selected="selected"</c:if>>Falso erro, a oração está correta.</option>
								<option value="INAPPROPRIATE_DESCRIPTION" <c:if test="${errorEntry.badIntervention.classification eq 'INAPPROPRIATE_DESCRIPTION'}">selected="selected"</c:if>>Erro existe, mas a descrição é inapropriada.</option>
								<option value="INAPPROPRIATE_SUGGESTION" <c:if test="${errorEntry.badIntervention.classification eq 'INAPPROPRIATE_SUGGESTION'}">selected="selected"</c:if>>Erro existe, mas a sugestão é inapropriada.</option>
						</select></td>
					</tr>
		    		<tr class="omission"><th>Categoria:</th>
						<td><select id="omissionCategory" name="omissionCategory" style="width: 300px;">
							<c:forEach items="${omissionCategoriesList}" var="omissionCategories">
								<option value="${omissionCategories}" <c:if test="${errorEntry.omission.category eq omissionCategories}">selected="selected"</c:if>>${omissionCategories}</option>
							</c:forEach>
							<option value="custom" <c:if test="${empty errorEntry.omission.category or not empty errorEntry.omission.customCategory}">selected="selected"</c:if>>Personalizada</option>
						</select></td>
		    		</tr>
		    		<tr class="omission" id="customOmissionText" style="display: none;">
		    			<th>Categoria (personalizada):</th>
		    			<td><input name="omissionCustom" style="width: 300px;" value="${errorEntry.omission.customCategory}"/></td>
		    		</tr>
					<tr class="omission">
		    			<th>Substituir por:</th><td><input name="omissionReplaceBy" style="width: 300px;" value="${errorEntry.omission.replaceBy}"/></td>
		    		</tr>
				</tbody>
			</table>
			<input type="hidden" id="reportId" name="reportId" value="${errorEntry.id}"/>
			<input type="hidden" id="omissionStart" name="omissionStart" value="${errorEntry.spanStart}"/>
   			<input type="hidden" id="omissionEnd" name="omissionEnd" value="${errorEntry.spanEnd}"/>
		</div>
		<hr>
		<div id="displayOmission">
			<div class="analise_text" >
				<p><b id="selectedOmission">${errorEntry.markedText}</b></p>
			</div>
			<a class="omission" onclick="on('updateOmission'); off('displayOmission'); return false" href="#">(alterar)</a>
		</div>
		<div class="analise_text" id="updateOmission" style="display: none;">
			<textarea rows="2" cols="70" readonly="readonly" id="selector" >${errorEntry.text}</textarea><br/>
			<button type="button" id="addNewOmission" class="a_button" > Alterar seleção &raquo; </button>
		</div>
		<hr>
		<div class="badint">
			<c:if test="${not empty singleGrammarErrorList}">
					<c:forEach items="${singleGrammarErrorList}" var="singleGrammarError" varStatus="i">
							<div class="dashed_white">
							<b>Intervenção ${ i.count }:</b>
							
								<div class="analise_text_incorrect">
									<p><a onclick="onOff('helpBadInt_${ i.count }'); openClose('openCloseBadInt_${ i.count }'); return false" href="#"><img id="openCloseBadInt_${ i.count }" src="<c:url value='/images/details_open.png' />"></a> <b>${singleGrammarError.annotatedText}</b></p>
								</div>
								
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
								
							</div>
							<input type="hidden" id="badintStart_${ i.count }" name="badintStart[${ i.count }]" value="${singleGrammarError.mistake.start}" />
							<input type="hidden" id="badintEnd_${ i.count }" name="badintEnd[${ i.count }]" value="${singleGrammarError.mistake.end}" />
							<input type="hidden" id="badintRule_${ i.count }" name="badintRule[${ i.count }]" value="${singleGrammarError.mistake.ruleIdentifier}" />
					</c:forEach>
				</c:if>
	     </div>
	    <button type="submit" name="_method" value="PUT">Enviar</button>                
	</div>
</form>