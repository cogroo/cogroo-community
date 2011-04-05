<%@ page contentType="text/html; charset=UTF-8"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script src="<c:url value='/js/jquery.dataTables.min.js' />" type="text/javascript" ></script>

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
			"iDisplayLength": 20
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
						<th>Login</th>
						<th>Nome</th>
						<th>Papel</th>
						<th>Último login</th>
					</tr>					
				</thead>
				<%!int i = 0;%>
				<c:forEach items="${userList}" var="user">
					<tr>
						<td><a href="<c:url value="/users/${user.login}"/>">${user.login}</a></td>
						<td>${user.name}</td>
						<td><fmt:message key="${user.role}" /></td>
						<td><fmt:formatDate value="${user.lastLogin}"
							pattern="dd/MM/yyyy HH:mm" /></td>
					</tr>
				</c:forEach>
			</table>
</div>
