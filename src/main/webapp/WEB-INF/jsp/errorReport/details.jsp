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

	<h2>Entrada #${errorEntry.id}</h2>
			<span style="FLOAT: right; POSITION: static">
			<c:if test="${loggedUser.user.role.canEditErrorReport}">
				<a href="<c:url value="/editErrorEntry/${errorEntry.id}"/>">editar</a>
			</c:if>
		</span>
	<div class="report_details">
	
		<div class="report_details_table">
		<table >
			<tbody>
			<tr>
			    <th>Enviado por:</th><td><a href="<c:url value="/user/${errorEntry.submitter.id}"/>">${errorEntry.submitter.name}</a></td>
			</tr>
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
						<th>Regra:</th><td><a href="<c:url value="/rule/${errorEntry.badIntervention.rule}"/>">${errorEntry.badIntervention.rule}</a></td>
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
			<tr>
				<c:choose>
					<c:when test="${loggedUser.user.role.canSetErrorReportState}">
						<th>Situação:</th><td>
					      	<div style="display: none;" id="editstate">
					            <form action="<c:url value='/errorEntrySetState'/>" method="post">
					            	<select name="state">
										<c:forEach items="${states}" var="s">
											<option value="${s}"><fmt:message key="${s}" /></option>
										</c:forEach>
									</select>
									<input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
				                    <input type="submit" style="font-size: 11px;" value=" Alterar " class="button"/> | 
				                    <a onclick="off('editstate'); on('state'); return false" href="#"><b>voltar</b></a>
					            </form>
					        </div>
					      	<div id="state">
			    				<fmt:message key="${errorEntry.state}" /> <a onclick="on('editstate'); off('state'); return false" href="#">(alterar)</a>
					        </div>
			    		</td>
			    		<th>Prioridade:</th><td>
					      	<div style="display: none;" id="editpriority">
					            <form action="<c:url value='/errorEntrySetPriority'/>" method="post">
					            	<select name="priority">
										<c:forEach items="${priorities}" var="p">
											<option value="${p}"><fmt:message key="${p}" /></option>
										</c:forEach>
									</select>
									<input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
				                    <input type="submit" style="font-size: 11px;" value=" Alterar " class="button"/> | 
				                    <a onclick="off('editpriority'); on('priority'); return false" href="#"><b>voltar</b></a>
					            </form>
					        </div>
					      	<div id="priority">
			    				<fmt:message key="${errorEntry.priority}" /> <a onclick="on('editpriority'); off('priority'); return false" href="#">(alterar)</a>
					        </div>
			    		</td>
			  		</c:when>
			  		<c:otherwise>
			  			<th>Situação:</th><td><fmt:message key="${errorEntry.state}" /></td>
			    		<th>Prioridade:</th><td><fmt:message key="${errorEntry.priority}" /></td>
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
		
		                              
		<h3><strong>Análise gramatical</strong></h3>
		
		
		<c:set var="processResultList" scope="request" value="${processResultList}" />    
		<c:set var="id" scope="request" value="c${i.count}" />
		<jsp:include page="/analysisdetails.jspf" />
	</div>
	
	<div class="report_disscussion">
		<h2>Discussão</h2>
		<c:forEach items="${errorEntry.errorEntryComments}" var="comment" varStatus="i">
			<div id="comment_${ i.count }">
				<h4 class="undeline">Por <a href="<c:url value="/user/${comment.user.id}"/>">${comment.user.name}</a> em <fmt:formatDate type="both" dateStyle="long" value="${comment.date}" />
				<c:if test="${((comment.user.login == loggedUser.user.login) && loggedUser.user.role.canDeleteOwnCommment) || (loggedUser.user.role.canDeleteOtherUserCommment) }"> 
					<a id="_${ i.count }" href="about:blank" class="comment_remove">excluir</a>
				</c:if>
				</h4>
				<form action="/errorEntryDeleteComment" method="post" id="form_comment_remove_${ i.count }">
				    <input name="comment.id" value="${comment.id}" type="hidden" />
				</form>
				<div>${comment.processedComment}</div>
				<div class="report_answer">
					<c:if test="${not empty comment.answers}">
						<b>Respostas</b>
						<table class="answer">
							<c:forEach items="${comment.answers}" var="answer"  varStatus="j">
								<tr id="tr_answer_${ i.count }_${ j.count }">
									<td>${answer.processedComment} <i> -- <a href="<c:url value="/user/${answer.user.id}"/>">${answer.user.name}</a> em <fmt:formatDate type="both" dateStyle="long" value="${answer.date}" /></i>
									<c:if test="${((answer.user.login == loggedUser.user.login) && loggedUser.user.role.canDeleteOwnCommment) || (loggedUser.user.role.canDeleteOtherUserCommment) }">
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
								<legend>Responder a esta discussão:</legend><br/>
							    <textarea class="answerText" id="answerText${comment.id}" name="answer" cols="80" rows="4"></textarea>
							    <input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
							    <input name="comment.id" value="${comment.id}" type="hidden" />
							    <span class="count" id="count${comment.id}">700</span> caracteres restantes<br>
							    <input type="submit" id="go" value=" Responder &raquo; ">
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
			    <textarea id="newCommentText" name="newComment" cols="80" rows="4"></textarea>
			    <input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
			    <span id="newCommentTextCount">700</span> caracteres restantes<br>
			    <input type="submit" id="go" value=" Enviar &raquo; ">
			</form>
		</c:if>
	</div>

	<div class="report_disscussion">
		<h2>Histórico</h2>
		<c:forEach items="${errorEntry.historyEntries}" var="historyEntry">
			<h4 class="undeline">Por <a href="<c:url value="/user/${historyEntry.user.id}"/>">${historyEntry.user.name}</a> em <fmt:formatDate type="both" dateStyle="long" value="${historyEntry.creation}" /></h4>
			<ul>
				<c:forEach items="${historyEntry.historyEntryField}" var="historyEntryField">
					<c:choose>
						<c:when test="${historyEntryField.isFormatted}">
				    		<li><b><fmt:message key="${historyEntryField.fieldName}" /></b>: [<i><del><fmt:message key="${historyEntryField.before}" /></del></i>] &rarr; [<i><fmt:message key="${historyEntryField.after}" /></i>]</li>
				  		</c:when>
				  		<c:otherwise>
				    		<li><b><fmt:message key="${historyEntryField.fieldName}" /></b>: [<i><del>${historyEntryField.before}</i></del>] &rarr; [<i>${historyEntryField.after}</i>]</li>
				  		</c:otherwise>
					</c:choose>
					
				</c:forEach>
			</ul>		
		</c:forEach>
	</div>