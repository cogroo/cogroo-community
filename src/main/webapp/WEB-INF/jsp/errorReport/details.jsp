<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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

	<h2>Entrada #${errorEntry.id}</h2>
	<div class="report_details">
		<p>Enviado por ${errorEntry.submitter.name}</p>

		<table class="attributes">
			<tbody>
			<tr>
			    <th>Versão:</th><td>${errorEntry.version.version}</td>
			    <th>Criado em:</th><td><fmt:formatDate type="both" dateStyle="long" value="${errorEntry.creation}" /></td>
			</tr>
			<tr>
			    <th>Tipo:</th><c:choose>
						<c:when test="${empty errorEntry.omission}">
				    		<td>Intervenção indevida</td>
				  		</c:when>
				  		<c:otherwise>
				    		<td>Omissão</td>
				  		</c:otherwise>
					</c:choose>
			    <th>Modificada em:</th><td><fmt:formatDate type="both" dateStyle="long" value="${errorEntry.modified}" /></td>
			</tr>
			<tr>
				<c:choose>
					<c:when test="${empty errorEntry.omission}">
						<th>Regra:</th><td><a href="<c:url value="/rule?rule.id=${errorEntry.badIntervention.rule}"/>">${errorEntry.badIntervention.rule}</a></td>
			    		<th>Erro:</th><td><fmt:message key="${errorEntry.badIntervention.classification}" /></td>
			  		</c:when>
			  		<c:otherwise>
			  			<c:choose>
							<c:when test="${empty errorEntry.omission.category}">
					    		<th>Categoria (personalizada):</th><td>${errorEntry.omission.customCategory}</td>
					  		</c:when>
					  		<c:otherwise>
					    		<th>Categoria:</th><td>${errorEntry.omission.category}</td>
					  		</c:otherwise>
						</c:choose>
			    		<th>Substituir por:</th><td>${errorEntry.omission.replaceBy}</td>
			  		</c:otherwise>
				</c:choose>
			</tr>
			</tbody>
		</table>
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
	<div class="report_disscussion">
		<h2>Discussão</h2>
		<c:forEach items="${errorEntry.errorEntryComments}" var="comment" varStatus="i">
			<div id="comment_${ i.count }">
				<h4>Por ${comment.user.name} em <fmt:formatDate type="both" dateStyle="long" value="${comment.date}" />
				<c:if test="${(comment.user.login == loggedUser.user.login) || (loggedUser.user.login == 'admin') }"> 
					<a id="_${ i.count }" href="about:blank" class="comment_remove">excluir</a>
				</c:if>
				</h4>
				<form action="/errorEntryDeleteComment" method="post" id="form_comment_remove_${ i.count }">
				    <input name="comment.id" value="${comment.id}" type="hidden" />
				</form>
				<div>${comment.errorEntryComment}</div>
				<div class="report_answer">
					<c:if test="${not empty comment.answers}">
						<b>Respostas</b>
						<table class="answer">
							<c:forEach items="${comment.answers}" var="answer"  varStatus="j">
								<tr id="tr_answer_${ i.count }_${ j.count }">
									<td>${answer.errorEntryComment} <i> -- ${answer.user.name} em <fmt:formatDate type="both" dateStyle="long" value="${answer.date}" /></i>
									<c:if test="${(answer.user.login == loggedUser.user.login) || (loggedUser.user.login == 'admin') }">
										<a id="_${ i.count }_${ j.count }" href="about:blank" class="answer_remove">excluir</a>
										<form action="/errorEntryAnswerToComment" method="post" id="form_answer_remove_${ i.count }_${ j.count }">
										    <input name="answer.id" value="${answer.id}" type="hidden" />
										    <input name="comment.id" value="${comment.id}" type="hidden" />
										</form>
									</c:if>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					<c:if test="${loggedUser.logged}">
						<div class="disscussion_actions">
							<a href="#" onclick="onOff('reply_${ i.count }'); return false">responder</a>
						</div>
						<div style="display: none;" class="disscussion_reply_form" id="reply_${ i.count }">
							<form method="post" action="<c:url value="/errorEntryAddAnswerToComment"/>">
								<legend>Responder esta discussão:</legend><br/>
							    <textarea name="answer" cols="80" rows="4"></textarea>
							    <input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
							    <input name="comment.id" value="${comment.id}" type="hidden" />
							    <input type="submit" id="go" value="Responder">
							</form>
						</div>
					</c:if>
				</div>
				<hr/>
			</div>
		</c:forEach>
		<c:if test="${loggedUser.logged}">
			<form method="post" action="<c:url value="/errorEntryAddComment"/>">
				<legend>Novo comentário:</legend><br/>
			    <textarea name="newComment" cols="80" rows="4"></textarea>
			    <input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
			    <input type="submit" id="go" value="Enviar">
			</form>
		</c:if>
	</div>