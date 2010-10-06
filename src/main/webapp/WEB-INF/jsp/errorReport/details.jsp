<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



	<h2>Entrada #${errorEntry.id}</h2>
	<div class="report_details">
		<p>Enviado por ${errorEntry.submitter.name}</p>

		<table class="attributes">
			<tbody>
			<tr>
			    <th>Versão:</th><td>${errorEntry.version.version}</td>
			    <th>Criado em:</th><td>${errorEntry.creation}</td>
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
			    <th>Modificada em:</th><td>${errorEntry.modified}</td>
			</tr>
			<tr>
				<c:choose>
					<c:when test="${empty errorEntry.omission}">
						<th>Regra:</th><td>${errorEntry.badIntervention.rule}</td>
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
			</tbody></table>
	<hr>	
	<div class="analise_text">
		<p><b>${errorEntry.markedText}</b></p>
	</div>
	<hr>
	
	                              
	<p><strong>Análise gramatical</strong></p>
	
	<jsp:include page="/analysisdetails.jspf" />
	
	
	
	dddd
	
	</div>