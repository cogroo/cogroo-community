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
		
		return '<div class="reportlist_details">'+aData[6]+'</div>';
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
				{ "sType": "title-numeric" },
				{ "sType": "title-numeric" },
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
				else if(this.src.match('details_open'))
				{
					/* Open this row */
					this.src = "./images/details_close.png";
					oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
				}
			} );
		} );

	} );
</script>


	<h2>Regras
	<span class="help"><a onclick="onOff('helpRuleList'); return false" href="#"><img src="<c:url value='/images/help.png' />" /></a></span>
	</h2>
		<div id="helpRuleList" style="display: none;" class="help">
			<p>Exibe as regras utilizadas pelo corretor gramatical CoGrOO para identificar erros.</p>
			<p>Clique no número da regra para detalhes. Regras com identificador tachado estão desabilitadas.</p>
			<p>Clique nas setas encontradas em cada coluna para ordenar os resultados em ordem alfabética.</p>
			<br>
			<p>Estatísticas das regras:</p>
			<ul class="message">
				<li title="Verdadeiros Positivos">| <b>VP:</b> ${stats.tp} </li>
				<li title ="Falsos Positivos">| <b>FP:</b> ${stats.fp} </li>
				<li title ="Falsos Negativos">| <b>FN:</b> ${stats.fn} |</li>
			</ul>
			<ul class="message">
				<li>| <b>Precisão:</b> <fmt:formatNumber value="${stats.precision} " type="percent"/></li>
				<li>| <b>Cobertura:</b> <fmt:formatNumber value="${stats.recall} " type="percent"/></li>
				<li>| <b>Medida F:</b> <fmt:formatNumber value="${stats.FMeasure}" type="percent"/> |</li>
			</ul>
		</div>
		
		<c:if test="${loggedUser.user.role.canRefreshRuleStatus}">
			<a href="<c:url value='/rulesRefresh' />" >Refresh</a>
		</c:if>
		 
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
		<thead>
			<tr>
			  <th></th>
			  <th title="Exibe o identificador da regra utilizada pelo CoGrOO.">Id.</th>
			  <th title="Exibe o status da regra. Verde: todos exemplos OK; Amarelo: algum exemplo falhou; Vermelho: todos os exemplos falharam; Cinza: regra desabilitada">Status</th>
			  <th title="Indica a categoria de erros gramaticais coberta pela regra.">Categoria</th>
			  <th title="Indica o grupo interno da categoria coberto pela regra.">Grupo</th>
			  <th title="Exibe uma mensagem curta descritiva do erro gramatical coberto pela regra.">Mensagem</th>
			  <th>Detalhes</th>
			</tr>
		</thead>
		<tbody>
			<c:set var="count" value="0" scope="page" />
			<c:forEach items="${ruleStatusList}" var="ruleStatus">
				<c:set var="count" value="${count + 1}" scope="page"/>
				<tr title="<c:url value="/rules/${ruleStatus.rule.id}"/>" id="${ruleStatus.rule.id}">
					<td valign="middle"><img src="./images/details_open.png"></td>
					<td title="${count}">
						<a title="${count}" href="<c:url value="/rules/${ruleStatus.rule.id}"/>">${fn:replace(fn:toLowerCase(ruleStatus.rule.id), '_', ' ')}</a>
					</td>
					
					<c:choose>  
					    <c:when test="${ruleStatus.active == false}">  
					        <td valign="middle"><img title="-1" src="./images/icons/status-grey.png"></td>  
					    </c:when>
					    <c:otherwise>
					        <c:choose>
					        	<c:when test="${ruleStatus.FMeasure == 1.0}">
					        		<td valign="middle"><img title="1" src="./images/icons/status-green.png"></td> 
					        	</c:when>
					        	<c:when test="${ruleStatus.FMeasure == 0.0}">
					        		<td valign="middle"><img title="0" src="./images/icons/status-red.png"></td> 
					        	</c:when>
					        	<c:otherwise>
					        		<td valign="middle"><img title="${ruleStatus.FMeasure}" src="./images/icons/status-yellow.png"></td>
					        	</c:otherwise>
					        </c:choose>
					    </c:otherwise>
					</c:choose> 
					
					<td>${ruleStatus.rule.category}</td>
					<td>${ruleStatus.rule.group}</td>
					<td>${ruleStatus.rule.shortMessage}</td>
	  			  	<td>
	  			  	<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">
			    		<tr><td>Mensagem longa:</td><td>${ruleStatus.rule.message}</td></tr>
			    		<tr><td>Exemplos:</td><td>
			    			<ol>
				    			<c:forEach items="${ruleStatus.rule.examples}" var="example">
									<li>${ i.count }
										<ul>
											<c:forEach items="${ruleStatus.rule.examples}" var="example">
												<li><b>incorreto:</b> ${example.incorrect}</li>
												<li><b>correto:</b> ${example.correct}</li>
											</c:forEach>
										</ul> 
									</li>
								</c:forEach> 
							</ol>
			    			</td>
			    		</tr>
			    		<tr><td>Estatísticas:</td><td>
								<ul class="message">
									<li title="Verdadeiros Positivos">| <b>VP:</b> ${ruleStatus.tp} </li>
									<li title ="Falsos Positivos">| <b>FP:</b> ${ruleStatus.fp} </li>
									<li title ="Falsos Negativos">| <b>FN:</b> ${ruleStatus.fn} |</li>
								</ul>
								<ul class="message">
									<li>| <b>Precisão:</b> <fmt:formatNumber value="${ruleStatus.precision} " type="percent"/></li>
									<li>| <b>Cobertura:</b> <fmt:formatNumber value="${ruleStatus.recall} " type="percent"/></li>
									<li>| <b>Medida F:</b> <fmt:formatNumber value="${ruleStatus.FMeasure}" type="percent"/> |</li>
								</ul>
			    			</td>
			    		</tr>
 			  			
	  			  	</table>
					</td>

			    </tr>
			</c:forEach>
		</tbody>
	</table>
