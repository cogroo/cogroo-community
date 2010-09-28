<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui/jquery-ui-1.8.5.custom.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery-ui-1.8.5.custom.min.js' />" type="text/javascript" ></script>
<script type="text/javascript" charset="utf-8">
	var oTable;

	/* Formating function for row details */
	function fnFormatDetails ( nTr )
	{
		var iIndex = oTable.fnGetPosition( nTr );
		var aData = oTable.fnSettings().aoData[iIndex]._aData;
		
		return '<div class="reportlist_details">'+aData[4]+'</div>';
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
				"sInfoFiltered": "(filtados de um total de _MAX_ entradas)"
			},
			"bFilter": false,
			"bInfo": false,
			"bPaginate": false,
			"aoColumns": [
				{ "bSortable": false },
				{ "bSortable": false }, 
				{ "bSortable": false },
				{ "bSortable": false },
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
					this.src = "/images/details_open.png";
					oTable.fnClose( nTr );
				}
				else
				{
					/* Open this row */
					this.src = "/images/details_close.png";
					oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
				}
			} );
		} );

	} );

	
	
</script>
	<script type="text/javascript">
	$(function() {
		$('.iframe').click(function(e) {
			var currentId = e.target.id;
			
			e.preventDefault();
			var $this = $(this);
			var horizontalPadding = 30;
			var verticalPadding = 30;
			/*$("#form" + currentId ).submit();*/

			 
	        $('<iframe id="externalSite" class="externalSite"/>').dialog({
	            title: ($this.attr('title')) ? $this.attr('title') : 'External Site',
	            autoOpen: true,
	            width: 800,
	            height: 500,
	            modal: true,
	            resizable: true,
				autoResize: true,
	            overlay: {
	                opacity: 0.5,
	                background: "black"
	            }
	        }).width(800 - horizontalPadding).height(500 - verticalPadding);
			$.post('myurl', function(data) {
			    $('#externalSite').html(data);
			}, 'html');
		});
	});
	</script>


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
	
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
		<thead>
			<tr>
			  <th></th>
			  <th>#</th>
			  <th>Elementos</th>
			  <th>Árvore</th>
			  <th>Detalhes</th>
			</tr>
		</thead>
		<tbody>
			<%! int i=1; %>
			<c:forEach items="${processResults}" var="processResult">
				<tr>
					<td valign="middle"><img src="/images/details_open.png"></td>
					<td><%= i %></td>
					<td>${processResult.textAnnotatedWithErrors}</td>
					<td>
							<form id="form_<%= i %>" action="http://201.52.96.104:8081/~colen/phpsyntaxtree/?" method="post" target="externalSite">
								<input  type="hidden" name="antialias" value="on" />
<!--								antialias	on-->
								<input  type="hidden" name="autosub" value="on" />
<!--								autosub	on-->
								<input  type="hidden" name="autosub" value="on" />
<!--								closedcount	5-->
								<input  type="hidden" name="closedcount" value="5" />
<!--								color	on-->
								<input  type="hidden" name="color" value="on" />
<!--								data	[S [NP phpSyntaxTree][VP [V creates][NP nice syntax trees]]]-->
								<input  type="hidden" name="data" value="${processResult.syntaxTree}" />
<!--								font	vera_sans-->
								<input  type="hidden" name="font" value="era_sans" />
<!--								fontsize	8-->
								<input  type="hidden" name="fontsize" value="8" />
<!--								opencount	5-->
								<input  type="hidden" name="opencount" value="5" />
<!--								triangles	on-->
								<input  type="hidden" name="triangles" value="on" />
								<a id="_<%= i %>" class="iframe" href="http://www.google.com" title="Google Dialog">Google</a>
								<!-- <button class="iframe" name="drawbtn" type="submit"> Draw </button> --> 
							</form>
					</td>
					<td>
						
						<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
							<thead>
								<tr>
								  <th>#</th>
								  <th>Elemento</th>
								  <th>Primitiva</th>
								  <th>Classificação</th>
								</tr>
							</thead>
							<tbody>
								<%! int j=1; %>
								<c:forEach items="${processResult.sentence.tokens}" var="token">
									<tr>
										<td><%= j++ %></td>
										<td>${token.lexeme}</td>
										<td>${token.primitive}</td>
										<td>${token.morphologicalTag}</td>
								    </tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
			    </tr>
				<%= i++ %>
			</c:forEach>
		</tbody>
	</table>
	
	
	
	
	dddd
	
	</div>

