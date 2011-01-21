<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />
<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.dataTables.sort.js' />" type="text/javascript" ></script>

<script type="text/javascript">
	if (${justReported})
	{
		_gaq.push(['_trackEvent', 'Problems', 'succeeded report', '${login}']);
	}
</script>

<script type="text/javascript" charset="utf-8">
	var oTable;
	
	var remove_error = function(currentId) {

		var r=confirm("Você deseja remover este erro?");
		if (r==true) {
			
			$.post("errorEntryDelete", $("#form_remove_error" + currentId).serialize(),
		   		function(data){
		   });
			//location.reload();
			var nTr = $('#tr_errorEntry' + currentId).get(0);
			oTable.fnClose( nTr );
			oTable.fnDeleteRow(nTr);
			
		};
	};

	/* Formating function for row details */
	var fnFormatDetails = function ( nTr )
	{
		var iIndex = oTable.fnGetPosition( nTr );
		var aData = oTable.fnSettings().aoData[iIndex]._aData;
		
		return '<div class="reportlist_details">'+aData[8]+'</div>';
	};
	
	$(document).ready(function() {
		
		$('.remove_error').click(remove_error);
		
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
			"aaSorting": [[ 4, 'desc' ]],
			"iDisplayLength": 10,
			"aoColumns": [
				{ "bSortable": false }, 	//0
				{ "sType": "num-html" }, 	//1
				null,						//2
				null,  						//3
				{ "sType": "title-string" },//4
				null,						//5
				null,  						//6
				null,						//7
				{ "bVisible": false }		//8
			]
		} );
		
		/* Add click event handler for user interaction */
		$('td img', oTable.fnGetNodes() ).each( function () {
			$(this).click( function () {
				var nTr = this.parentNode.parentNode;
				if ( this.src.match('details_close') )
				{
					/* This row is already open - close it */
					this.src = "./images/details_open.png";
					oTable.fnClose( nTr );
				}
				else
				{
					/* Open this row */
					this.src = "./images/details_close.png";
					oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
				}
			} );
		} );

	} );
</script>

<c:if test="${justReported}">
	<h3>Erro reportado com sucesso!</h3>
	<br />
</c:if>

<h2>Problemas reportados</h2>
	<p>Exibe todos os problemas reportados através da página e do plug-in CoGrOO para BrOffice.org.</p>
	<br />
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="errorList">
		<thead>
			<tr>
			  <th></th> 			<!-- 0 -->
			  <th title="ID">#</th>			<!-- 1 -->
			  <th title="Tipo">Tipo</th>			<!-- 2 -->
			  <th title="Texto">Texto</th>		<!-- 3 -->
			  <th title="Alterado em">Alterado em</th>	<!-- 4 -->
			  <th title="Comentários">Comentários</th>	<!-- 5 -->
			  <th title="Versão">Versão</th>		<!-- 6 -->
			  <th title="Login">Login</th>		<!-- 7 -->
			  <th>Detalhes</th>		<!-- 8 -->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${errorEntryList}" var="errorEntry" varStatus="i">

				<c:if test="${errorEntry.isNew}">
					<tr id="tr_errorEntry_${ i.count }" class="highlighted">
				</c:if>
				<c:if test="${not errorEntry.isNew}">
					<tr id="tr_errorEntry_${ i.count }">
				</c:if>
			
					<td valign="middle"><img src="./images/details_open.png"></td>
					<td><a href="<c:url value="/errorEntry?errorEntry.id=${errorEntry.id}"/>">${errorEntry.id}</a>
					</td>
					<c:choose>
						<c:when test="${empty errorEntry.omission}">
				    		<td>Intervenção indevida</td>
				  		</c:when>
				  		<c:otherwise>
				    		<td>Omissão</td>
				  		</c:otherwise>
					</c:choose>
					<td>${errorEntry.markedText}</td>
					<td><span title="${errorEntry.modified}"></span><fmt:formatDate type="both" dateStyle="long" value="${errorEntry.modified}" /></td>
					<td>${errorEntry.commentCount}</td>
					<td>${errorEntry.version.version}</td>
					<td>${errorEntry.submitter.login}</td>
	  			  	<td>
  					<c:if test="${(errorEntry.submitter.login == loggedUser.user.login) || (loggedUser.user.login == 'admin') }"> 
						<a onclick="remove_error('_${ i.count }'); return false;" id="_${ i.count }" href="about:blank" class="remove_error">excluir</a>
						<form action="/errorEntryDelete" method="post" id="form_remove_error_${ i.count }">
						    <input name="errorEntry.id" value="${errorEntry.id}" type="hidden" />
						</form>
					</c:if>
	  			  	<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">
		  			  	<c:choose>
							<c:when test="${empty errorEntry.omission}">
					    		<tr><td>Tipo:</td><td><fmt:message key="${errorEntry.badIntervention.classification}" /></td></tr>
					    		<tr><td>Regra:</td><td>${errorEntry.badIntervention.rule}</td></tr>
					  		</c:when>
					  		<c:otherwise>
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
