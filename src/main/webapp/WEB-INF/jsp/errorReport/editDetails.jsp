<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>
<script src="<c:url value='/js/analysisdetails.js' />" type="text/javascript" ></script>

	<script type="text/javascript"><!--//--><![CDATA[//><!--

		$(document).ready(function(){

			// install the event handler
			$('.answer_remove').click(answer_remove);
			$('.comment_remove').click(comment_remove);

		});

		function answer_remove(e) {

			var r=confirm("Você deseja remover esta resposta?");
			if (r==true) {
				var currentId = e.target.id;
				
				e.preventDefault();
				var $this = $(this);
				
				$.post("errorEntryDeleteAnswer", $("#form_answer_remove" + currentId).serialize(),
			   		function(data){
			   });
				
				//tr_answer_
				$('#tr_answer' + currentId).remove();

			}

		}
		
		function comment_remove(e) {

			var r=confirm("Você deseja remover este comentários e todas suas respostas?");
			if (r==true) {
				var currentId = e.target.id;
				
				e.preventDefault();
				var $this = $(this);
				
				$.post("errorEntryDeleteComment", $("#form_comment_remove" + currentId).serialize(),
			   		function(data){
			   });
				
				//tr_answer_
				$('#comment' + currentId).remove();

			}

		}

		//--><!]]>

	</script>
<script src="<c:url value='/js/jquery.NobleCount.min.js' />" type="text/javascript" ></script>
<script type="text/javascript"><!--//--><![CDATA[//><!--

	$(document).ready(function(){
		
		
		$.each($('.answerText'), function(key) {
			
			var id = key + 1;
			 
			$('#answerText' + id).NobleCount('#count' + id,{
				on_negative: 'go_red',
				on_positive: 'go_green',
				max_chars: 700
			});
		});
		
		
		

		
		$('#newCommentText').NobleCount('#newCommentTextCount',{
			on_negative: 'go_red',
			on_positive: 'go_green',
			max_chars: 700
		});
	});
	
//--><!]]>

</script>

<style type="text/css">
table.answer {
	border-width: 0px;
	border-spacing: 0px;
	border-style: hidden;
	border-color: gray;
	border-collapse: collapse;
	background-color: white;
}
table.answer th {
	border-width: 1px;
	padding: 1px;
	border-style: dotted;
	border-color: gray;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
table.answer td {
	border-width: 1px;
	padding: 1px;
	border-style: dotted;
	border-color: gray;
	background-color: white;
	-moz-border-radius: 0px 0px 0px 0px;
}
</style>

	<h2>Editar entrada #${errorEntry.id}</h2>
	<div class="report_details">
		<div class="report_details_table">
		<table >
			<tbody>
			
			<tr>
			    <th>Tipo:</th><c:choose>
						<c:when test="${hasError}">
						   <td><select name="priority">
								<option value="OMISSION" <c:if test="${empty errorEntry.badIntervention}">selected="selected"</c:if>>Omissão</option>
								<option value="BADINT" <c:if test="${empty errorEntry.omission}">selected="selected"</c:if>>Intervenção Indevida</option>
							</select></td>
				  		</c:when>
				  		<c:otherwise>
							<td>Omissão</td>
				  		</c:otherwise>
					</c:choose>
			</tr>
			
			<c:choose>
				<c:when test="${empty errorEntry.omission}">
					<tr>
						<th>Regra:</th><td><a href="<c:url value="/rule/${errorEntry.badIntervention.rule}"/>">${errorEntry.badIntervention.rule}</a></td>
					</tr>
					<tr>
						<th>Erro:</th>
		            	<td><select name="badint">
								<option value="FALSE_ERROR" <c:if test="${errorEntry.badIntervention.classification eq 'FALSE_ERROR'}">selected="selected"</c:if>>Falso erro, a oração está correta.</option>
								<option value="INAPPROPRIATE_DESCRIPTION" <c:if test="${errorEntry.badIntervention.classification eq 'INAPPROPRIATE_DESCRIPTION'}">selected="selected"</c:if>>Erro existe, mas a descrição é inapropriada.</option>
								<option value="INAPPROPRIATE_SUGGESTION" <c:if test="${errorEntry.badIntervention.classification eq 'INAPPROPRIATE_SUGGESTION'}">selected="selected"</c:if>>Erro existe, mas a sugestão é inapropriada.</option>
						</select></td>
					</tr>
		  		</c:when>
		  		<c:otherwise>
		    		<tr><th>Categoria:</th>
						<td><select name="dummyName">
							<c:forEach items="${omissionCategoriesList}" var="omissionCategories">
								<option value="${omissionCategories}" <c:if test="${errorEntry.omission.category eq omissionCategories}">selected="selected"</c:if>>${omissionCategories}</option>
							</c:forEach>
							<option value="custom" <c:if test="${empty errorEntry.omission.category}">selected="selected"</c:if>>Personalizada</option>
						</select></td>
<!--		    		${errorEntry.omission.category}-->
		    		</tr>
		    		<tr><th>Categoria (personalizada):</th><td><textarea rows="1" cols="70" id="omissionComment" name="dummyOmissionComment">${errorEntry.omission.customCategory}</textarea></td></tr>
					<tr>
		    			<th>Substituir por:</th><td>${errorEntry.omission.replaceBy}</td>
		    		</tr>
		  		</c:otherwise>
			</c:choose>
			</tr>
			</tbody>
		</table>
		</div>
		<hr>	
		<div class="analise_text">
			<p><b>${errorEntry.markedText}</b></p>
		</div>
		<hr>
		
		                              
		<p><strong>Análise gramatical</strong></p>
		
		
		<c:set var="processResultList" scope="request" value="${processResultList}" />    
		<c:set var="id" scope="request" value="c${i.count}" />
		<jsp:include page="/analysisdetails.jspf" />
	</div>