<%@ page contentType="text/html; charset=UTF-8"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.dataTables.sort.js' />" type="text/javascript" ></script>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		$('#table_id').dataTable({
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
				null,	//id
				null,	//service
				null, 	//login
				null, 	//name
				null,  	//role
				null,	//errors
				null,	// comments
				{ "sType": "title-string" },	//last login
			],

			"fnDrawCallback": function ( oSettings ) {
				$('#table_id tbody tr').each( function () {
					var title = $(this).attr('title');
					$(this).click( function () {
						window.location = title;
					} );
				} );
				$(this).hover(function() {
		            $(this).css('cursor', 'pointer');
		        }, function() {
		            $(this).css('cursor', 'auto');
		        });
			}

		} );

	} );
</script>

<div>
	<h2>Lista de usuários</h2>
</div>

<div class="userList">
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="table_id">
				<thead>
					<tr>
						<th>id</th>
						<th>Serviço</th>
						<th>Login</th>
						<th>Nome</th>
						<th>Papel</th>
						<th># Erros</th>
						<th># Comentários</th>
						<th>Último login</th>
					</tr>
				</thead>
				<%!int i = 0;%>
				<c:forEach items="${userList}" var="user">
					<tr title="<c:url value="/users/${user.service}/${user.login}"/>">
						<td>${user.id}</td>
						<td>${user.service}</td>
						<td><a href="<c:url value="/users/${user.service}/${user.login}"/>">${user.login}</a></td>
						<td>${user.name}</td>
						<td><fmt:message key="${user.role}" /></td>
						<td>${user.reportedErrorsCount}</td>
						<td>${user.commentsCount}</td>
						<td><c:choose>
							<c:when test="${not empty user.lastLogin}">
								<span title="${user.lastLoginAsLong}"></span><fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${user.lastLogin}" />
							</c:when>
							<c:otherwise>
								<span title="0"></span>nunca
							</c:otherwise>
						</c:choose></td>
					</tr>
				</c:forEach>
			</table>
</div>
