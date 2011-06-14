<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.dataTables.sort.js' />" type="text/javascript" ></script>
<script type="text/javascript" charset="utf-8">
	var oTable;
	
	/* Formating function for row details */
	function fnFormatDetails ( nTr )
	{
		var iIndex = oTable.fnGetPosition( nTr );
		var aData = oTable.fnSettings().aoData[iIndex]._aData;
		
		return '<div class="reportlist_details">'+aData[5]+'</div>';
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
			"aLengthMenu": [20,50,100,200],
			"aaSorting": [[ 1, "asc" ]],
			"iDisplayLength": 20,
			"aoColumns": [
				{ "bSortable": false },
				{ "sType": "num-html" }, 
				null,
				null,  
				null,
				{ "bVisible": false }
			],
			
			"fnDrawCallback": function ( oSettings ) {
				$('#table_id tbody tr').each( function () {
					var title = $(this).attr('title');
					$(this).click( function () {
						window.location = title;
					} );
					$(this).hover(function() {
			            $(this).css('cursor', 'pointer');
			        }, function() {
			            $(this).css('cursor', 'auto');
			        });
				} );
			}
			
		} );
		
		/* Add click event handler for user interaction */
		$('td img', oTable.fnGetNodes() ).each( function () {
			$(this).click( function (e) {
				e.stopPropagation();
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


	<h2>Regras <span class="help"><a onclick="onOff('helpRuleList'); return false" href="#"><img src="<c:url value='/images/help.png' />" /></a></span></h2>
		<div id="helpRuleList" style="display: none;" class="help">
			<p>Exibe as regras utilizadas pelo corretor gramatical CoGrOO para identificar erros.</p>
			<p>Clique no número da regra para detalhes. Regras com identificador tachado estão desabilitadas.</p>
			<p>Clique nas setas encontradas em cada coluna para ordenar os resultados em ordem alfabética.</p>
		</div>
		 
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
		<thead>
			<tr>
			  <th></th>
			  <th title="Exibe o número da regra utilizada pelo CoGrOO.">Nº.</th>
			  <th title="Indica a categoria de erros gramaticais coberta pela regra.">Categoria</th>
			  <th title="Indica o grupo interno da categoria coberto pela regra.">Grupo</th>
			  <th title="Exibe uma mensagem curta descritiva do erro gramatical coberto pela regra.">Mensagem</th>
			  <th>Detalhes</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${ruleList}" var="rule">
				<tr title="<c:url value="/rules/${rule.id}"/>" id="${rule.id}">
					<td valign="middle"><img src="./images/details_open.png"></td>
					<c:choose> 
					  <c:when test="${rule.active == true}" > 
					    <td>
					  </c:when> 
					  <c:otherwise> 
					    <td style="text-decoration: line-through;">
					  </c:otherwise> 
					</c:choose> 
						<a href="<c:url value="/rules/${rule.id}"/>">${rule.id}</a>
					</td>
					<td>${rule.type}</td>
					<td>${rule.group}</td>
					<td>${rule.shortMessage}</td>
	  			  	<td>
	  			  	<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">
			    		<tr><td>Mensagem longa:</td><td>${rule.message}</td></tr>
			    		<tr><td>Exemplos:</td><td>
			    			<ol>
				    			<c:forEach items="${rule.example}" var="example">
									<li>${ i.count }
										<ul>
											<c:forEach items="${rule.example}" var="example">
												<li><b>incorreto:</b> ${example.incorrect}</li>
												<li><b>correto:</b> ${example.correct}</li>
											</c:forEach>
										</ul> 
									</li>
								</c:forEach> 
							</ol>
			    			</td>
			    		</tr>
 			  			
	  			  	</table>
					</td>

			    </tr>
			</c:forEach>
		</tbody>
	</table>
