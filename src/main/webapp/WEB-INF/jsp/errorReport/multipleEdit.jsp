<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />
<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.dataTables.sort.js' />" type="text/javascript" ></script>

<script src="<c:url value='/js/jquery.NobleCount.min.js' />" type="text/javascript" ></script>

<c:if test="${gaEventErrorReported}">
	<script type="text/javascript">
	_gaq.push(['_trackEvent', 'Problems', 'succeeded report', '${loggedUser.user.service}']);
	</script>
</c:if>

<script type="text/javascript" charset="utf-8">
	var oTable;
	
	var apply = function(e) {
		//e.preventDefault();
		var r=confirm("Deseja mesmo continuar edição multipla?");
		if(r==false) {
			e.preventDefault();
		}
		if (r==true) {
			
			_gaq.push(['_trackEvent', 'Problems', 'multiple edit', '${loggedUser.user.service}']);
			
		};
		
	}
	
/* 	var remove_error = function(currentId) {

		var r=confirm("Você deseja remover este erro?");
		if (r==true) {
			
			var form = $("#form_apply");
			var url = form.attr('action');

			
			$.post(url, form.serialize(),
		   		function(data){
		   });
			//location.reload();
			_gaq.push(['_trackEvent', 'Problems', 'removed report', '${loggedUser.user.service}']);
			var nTr = $('#tr_errorEntry_' + currentId).get(0);
			oTable.fnClose( nTr );
			oTable.fnDeleteRow(nTr);
			
		};
	}; */

	/* Formating function for row details */
	var fnFormatDetails = function ( nTr )
	{
		var iIndex = oTable.fnGetPosition( nTr );
		var aData = oTable.fnSettings().aoData[iIndex]._aData;
		
		return '<div class="reportlist_details">'+aData[9]+'</div>';
	};
	
	function fnGetSelected( oTableLocal )
	{
		var aReturn = new Array();
		var aTrs = oTableLocal.fnGetNodes();
		
		for ( var i=0 ; i<aTrs.length ; i++ )
		{
			if ( $(aTrs[i]).hasClass('row_selected') )
			{
				aReturn.push( aTrs[i] );
			}
		}
		return aReturn;
	}
	
	$(document).ready(function() {
		

		$('#bt_apply').click(apply);
		
		$('#newCommentText').NobleCount('#newCommentTextCount',{
			on_negative: 'go_red',
			on_positive: 'go_green',
			max_chars: 700
		});
		
 		$('#errorList tr td input').click( function(e) {
 			var tr = $(this).parent().parent();
			if ( $(this).is(':checked') )
				tr.addClass('row_selected');
			else
				tr.removeClass('row_selected');
		} );
		
		//$('.remove_error').click(remove_error);
		
		oTable = $('#errorList').dataTable( {
			"oLanguage": {
				"sLengthMenu": "Exibir _MENU_ entradas por página",
				"sSearch": "Filtrar entradas:",
				"sFirst": "Primeira página",
				"sLast": "Última página",
				"sNext": "Próxima página",
				"sPrevious": "Página anterior",
				"sZeroRecords": "Desculpe, nada encontrado.",
				"sInfo": "Exibindo de _START_ até _END_ de um total de _TOTAL_ entradas",
				"sInfoEmpty": "Exibindo de 0 até 0 de um total de 0 entradas",
				"sInfoFiltered": "(filtrados de um total de _MAX_ entradas)"
			},
			"aLengthMenu": [20,50,100,200],
			"aaSorting": [[ 7, 'desc' ]],
			"iDisplayLength": 20,
			"aoColumns": [
				{ "bSortable": false }, 	//0
				{ "bSortable": false }, 	//1
				{ "sType": "num-html" }, 	//2
				{ "sType": "title-string" },
				null,  						//3
				null,						//4
				null,  						//5
				{ "sType": "title-string" },//6
				null,						//7
				{ "bVisible": false }		//8
			]
		} );
		
		/* Add click event handler for user interaction */
		$('td img', oTable.fnGetNodes() ).each( function () {
			$(this).click( function (e) {
				e.stopPropagation();
				var nTr = this.parentNode.parentNode;
				if ( this.src.match('details_close') )
				{
					/* This row is already open - close it */
					this.src = "../../images/details_open.png";
					oTable.fnClose( nTr );
				}
				else
				{
					/* Open this row */
					this.src = "../../images/details_close.png";
					oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
				}
			} );
		} );

	} );
</script>

<h2>Edição multipla de problemas reportados <span class="help"><a onclick="onOff('helpErrorList'); return false" href="#"><img src="<c:url value='/images/help.png' />" /></a></span></h2>
	<div id="helpErrorList" style="display: none;" class="help">
		<p>Exibe todos os problemas reportados através da página e do plug-in CoGrOO para BrOffice.</p>
		<p>Clique no número do problema reportado para detalhes.</p>
		<p>Clique nas setas encontradas em cada coluna para ordenar os resultados em ordem alfabética.</p>
	</div>
	<p>Selecione os erros que deseja editar e use o formulário no fim da página.</p>
	<form id="form_apply" action="<c:url value='/reports/edit'/>" method="post">
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="errorList">
		<thead>
			<tr>
			  <th></th> 
			  <th></th> 			<!-- 1 -->
			  <th title="Exibe o número do problema reportado.">Nº.</th>			<!-- 2 -->
			  <th title="Exibe o status do problema">Status</th>
			  <th title="Indica a situação (aberta, em andamento, resolvida, aguardando resposta, fechada ou rejeitada) do problema.">Situação</th>				<!-- 3 -->
			  <th title="Indica a prioridade (baixa, normal, alta, urgente ou imediata) do problema.">Prioridade</th>			<!-- 4 -->
			  <th title="Exibe a sentença relacionada ao problema reportado.">Sentença com problema</th>		<!-- 5 -->
			  <th title="Exibe a data da última alteração realizada no problema.">Data</th>	<!-- 6 -->
			  <th title="Exibe o número de comentários feitos sobre o problema.">Comentários</th>	<!-- 7 -->
			  <th>Detalhes</th>		<!-- 8 -->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${errorEntryList}" var="errorEntry" varStatus="i">
				
				<c:if test="${errorEntry.isNew}">
					<tr id="tr_errorEntry_${ i.count }" class="highlighted" title="<c:url value="/reports/${errorEntry.id}"/>">
				</c:if>
				<c:if test="${not errorEntry.isNew}">
					<tr id="tr_errorEntry_${ i.count }" title="<c:url value="/reports/${errorEntry.id}"/>">
				</c:if>
					<td><input type="checkbox" name="errorEntryID[${errorEntry.id}]" value="${errorEntry.id}"/></td>
					<td valign="middle"><img src="<c:url value="/images/details_open.png"/>"></td>		<!-- 0 -->
					<td><a href="<c:url value="/reports/${errorEntry.id}"/>">${errorEntry.id}</a></td>		<!-- 1 -->
					
				<c:choose>  
					    <c:when test="${errorEntry.statusFlag == 'OK'}">  
					        <td valign="middle"><img title="Corrigido" src="<c:url value="/images/icons/status-green.png"/>"></td>  
					    </c:when>
					    <c:when test="${errorEntry.statusFlag == 'NOT'}">  
					        <td valign="middle"><img title="Não corrigido" src="<c:url value="/images/icons/status-red.png"/>"></td>  
					    </c:when>
					    <c:when test="${errorEntry.statusFlag == 'WARN'}">  
					        <td valign="middle"><img title="Parcialmente corrigido" src="<c:url value="/images/icons/status-yellow.png"/>"></td>  
					    </c:when>
					    <c:otherwise>
				        	<td valign="middle"><img title="Rejeitado" src="<c:url value="/images/icons/status-grey.png"/>"></td>
				    	</c:otherwise>
					</c:choose>
					
					<td><fmt:message key="${errorEntry.state}" /></td>					<!-- 2 -->
					<td><fmt:message key="${errorEntry.priority}" /></td>				<!-- 3 -->
					<td>${errorEntry.markedText}</td>									<!-- 4 -->
					<td><span title="${errorEntry.modified}"></span><fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${errorEntry.modified}" /></td>		<!-- 5 -->
					<td>${errorEntry.commentCount}</td>									<!-- 6 -->
	  			  	<td>																<!-- 7 -->
	  			  	<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">
		  			  	<c:choose>
							<c:when test="${empty errorEntry.omission}">
								<tr><td>Tipo:</td><td>Intervenção indevida</td></tr>
					    		<tr><td>Classificação:</td><td><fmt:message key="${errorEntry.badIntervention.classification}" /></td></tr>
					    		<tr><td>Regra:</td><td>${errorEntry.badIntervention.rule}</td></tr>
					  		</c:when>
					  		<c:otherwise>
					    		<tr><td>Tipo:</td><td>Omissão</td></tr>
					    		<tr>
						    		<c:choose>
										<c:when test="${empty errorEntry.omission.category}">
								    		<td>Categoria personalizada:</td><td>${errorEntry.omission.customCategory}</td>
								  		</c:when>
								  		<c:otherwise>
								    		<td>Categoria:</td><td>${errorEntry.omission.category}</td>
								  		</c:otherwise>
									</c:choose>
								</tr>
					    		<tr><td>Substituir por:</td><td>${errorEntry.omission.replaceBy}</td></tr>
					  		</c:otherwise>
						</c:choose>
						<tr><td>Enviado por:</td><td><a href="<c:url value="/users/${errorEntry.submitter.service}/${errorEntry.submitter.login}"/>">${errorEntry.submitter.name}</a></td></td></tr>
						<tr><td>Versão:</td><td>${errorEntry.version.version}</td></tr>
	  			  		
	  			  	</table>
					</td>
	  			  <!-- <td><form action="<c:url value="errorEntry"/>" method="post" >
	  			  		<input type="hidden" name="errorEntryID" value="${errorEntry.id}"/>
						<input type="submit" value="abrir" />
					</form></td> -->
			    </tr>
			</c:forEach>
		</tbody>
	</table>
	<br>
         
         	<input id="cb_applystate" type="checkbox" name="applystate"/>
         	<label for="cb_applystate">Aplicar novo estado</label>
         	<select name="state">
				<c:forEach items="${states}" var="s">
					<option value="${s}"
						<c:if test="${s eq errorEntry.state}">selected="selected"
						</c:if>
					><fmt:message key="${s}" /></option>
				</c:forEach>
			</select>
			<br>
         	<input id="cb_applypriority" type="checkbox" name="applypriority"/>
         	<label for="cb_applypriority">Aplicar nova prioridade</label>			
           	<select name="priority">
				<c:forEach items="${priorities}" var="p">
					<option value="${p}"
						<c:if test="${p eq errorEntry.priority}">selected="selected"
						</c:if>
					><fmt:message key="${p}" /></option>
				</c:forEach>
			</select>
			<br>
			<input id="tb_applycomment" type="checkbox" name="applycomment"/>
         	<label for="tb_applycomment">Inserir comentário</label>	<br>
         	<textarea id="newCommentText" name="newComment" cols="80" rows="4"></textarea>
         	<span id="newCommentTextCount">700</span> caracteres restantes<br>
<!-- 			<input name="selectedErrors[]" value="fnGetSelected('#errorList');" type="hidden" />
 -->			<input type="hidden" name="_method" value="PUT"/>
			<input id="bt_apply" type="submit" style="font-size: 11px;" value=" Aplicar " class="button"/>
		</form>