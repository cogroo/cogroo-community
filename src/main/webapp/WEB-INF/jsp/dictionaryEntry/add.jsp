<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  

<style type="text/css" media="all">

legend {
	font-weight:bold;
	border:1px solid #ccc;
	padding:1px 4px; 
}
form div {
	margin:10px 0 0 0;
}
.typeField {
	width:100%;
	overflow:auto;
}
.fieldOptions {
	width:100%;
	overflow:auto;
}
label {
	width:245px;
	display:block;
	float:left;
}
label.typeField {
	font-weight:bold;
	margin:0;
}
.grupo, .typeField {
	width:100%;
	overflow:auto;
}
.typeField {
	background:#add6ef;
	padding:5px 0;
}
.grupo{
	background:#d6e2e5;
}
.fieldset {border-style:dotted;}


</style>
<script type="text/javascript">
//<![CDATA[
	$(document).ready(function() {
		$('.grupo').children('label:not(.typeField)').hide();
		$(':radio').parents('label').click(function(){
			$(this).siblings().fadeIn(1500); 
			$(this).parent().siblings('.grupo')
			.children('label:not(.typeField)')
			.slideUp().find(':checked').each(function(){
				$(this).removeAttr('checked');
			});
		});		
	});
   // ]]>
</script>

<h2>Inserir verbete.</h2>

<c:forEach var="error" items="${errors}">
    <span style="color: red">[<fmt:message  key="${error.category}"/>] <i><fmt:message  key="${error.message}"/></i></span><br />
</c:forEach>

<form action="<c:url value="/dictionaryEntry"/>"  method="post" >
	<fieldset>
		<legend>Inserir um novo verbete:</legend>
		
		   	Escolha o lema do verbete:      <input type="text" name="dictionaryEntry.lemma.word" value="${dictionaryEntry.lemma.word}  maxlength="128""/><br/>
		  	Palavra:      <input type="text" name="dictionaryEntry.word.word" value="${dictionaryEntry.word.word}" maxlength="128"/><br/>
		  	Etiqueta morfológica:  <br />
		  	
		<%! int i=0; %>
		<!-- itera sobre lista de objetos TypeField -->
		<c:forEach items="${typeFieldList}" var="typeField" > 
		
		<div class="grupo">
		<label class="typeField"><input type="radio" name="tagClass" value="${typeField.type} " /> 
			<fmt:message key="${typeField.type}"/>
		</label>
			
		<!-- itera sobre a lista de campos para um type -->
		<c:forEach items="${typeField.fieldOptions}" var="fieldOptions">
		<label>
			<select name="fieldList[<%= i++ %>]" id="${fieldOptions.field}">
			<option value="Selecione o ${fieldOptions.field}" selected>-Selecione ${fieldOptions.field}-</option>
				<!-- itera sobre a lista de opções para um campo --> 
				<c:forEach items="${fieldOptions.options}" var="option">
					<option value="${typeField.type}-${fieldOptions.field}-${option}"><fmt:message key="${option}"/></option>
				</c:forEach>
			</select>
		</label>
		</c:forEach>
				
		</div>
			 
		</c:forEach>
		<input type="submit" value=" Salvar &raquo; " />
	</fieldset>
</form>
