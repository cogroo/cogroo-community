<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script type="text/javascript" charset="utf-8">
	var oTable;

	/* Formating function for row details */
	function fnFormatDetails ( nTr )
	{
		var iIndex = oTable.fnGetPosition( nTr );
		var aData = oTable.fnSettings().aoData[iIndex]._aData;
		
		return '<div class="reportlist_details">'+aData[7]+'</div>';
	}


	$(document).ready(function() {
		oTable = $('#table_id').dataTable( {
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
			"aaSorting": [[ 4, "desc" ]],
			"iDisplayLength": 20,
			"aoColumns": [
				{ "bSortable": false },
				null, 
				null,
				null, 
				{"sType": "date"}, 
				null,
				null,
				{ "bVisible": false }
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


	<br/>
	<p>Erros enviados através do plug-in CoGrOO para BrOffice.org</p>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
		<thead>
			<tr>
			  <th></th>
			  <th>#</th>
			  <th>Tipo</th>
			  <th>Texto</th>
			  <th>Alterado em</th>
			  <th>Versão</th>
			  <th>Usuário</th>
			  <th>Detalhes</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${errorEntryList}" var="errorEntry">
				<tr>
					<td valign="middle"><img src="./images/details_open.png"></td>
					<td><a href="<c:url value="/errorEntry?errorEntry.id=${errorEntry.id}"/>">${errorEntry.id}</a></td>
					<c:choose>
						<c:when test="${empty errorEntry.omission}">
				    		<td>Intervenção indevida</td>
				  		</c:when>
				  		<c:otherwise>
				    		<td>Omissão</td>
				  		</c:otherwise>
					</c:choose>
					<td>${errorEntry.markedText}</td>
					<td><fmt:formatDate type="both" dateStyle="long" value="${errorEntry.modified}" /></td>
					<td>${errorEntry.version.version}</td>
					<td>${errorEntry.submitter.name}</td>
	  			  	<td>
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
