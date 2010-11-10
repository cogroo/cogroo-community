<%@ page contentType="text/html; charset=UTF-8" %>  
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css"
	href="<c:url value="/css/dataTables_table.css"/>" />

<h2>Estatísticas</h2>

<h3>Globais</h3>

<p>Erros reportados: ${appData.reportedErrors}</p>
<p>Palavras no dicionário: ${appData.dictionaryEntries}</p>
<p>Membros cadastrados: ${appData.registeredMembers}</p>
<br />
<p>Usuários online: ${appData.onlineUsers}</p>
<p>Visitantes: ${appData.onlineVisits}</p>
<p>Membros online: ${appData.onlineMembers}</p>
<br />

<!--<h3>Temporais</h3>
<img
	src="http://chart.apis.google.com/chart?chf=bg,s,67676700&chxr=0,1,18|1,0,20&chxs=0,676767,11.5,0,lt,676767|1,676767,11.5,0,lt,676767&chxt=x,y&chs=440x220&cht=lxy&chco=3072F3,FF9900&chds=1,18,0,20,1,18,0,20&chd=t:1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18|0,0,0,0,0,0,0,0,0,0,4,1,7,3,17,5,2,10|1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18|0,0,0,0,0,0,0,0,0,0,2,0,3,5,2,1,4,8&chdl=Visitas|Erros+reportados&chdlp=b&chg=0,10&chls=2,4,1|1&chma=5,5,5,25|5&chtt=Outubro"
	width="440" height="220" alt="Outubro" />

<br />

--><h3>Relatórios</h3>

<h4>Membros online</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="table_id">
	<thead>
		<tr>
			<th>Login</th>
			<th>Último login</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${appData.loggedUsers}">
			<tr>
				<td>${user.login}</td>
				<td><c:choose>
					<c:when test="${not empty user.lastLogin}">
						<fmt:formatDate value="${user.lastLogin}"
							pattern="dd/MM/yyyy HH:mm" />
					</c:when>
					<c:otherwise>
					nunca
					</c:otherwise>
				</c:choose></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br />

<h4>Usuários inativos (sem login há mais de um mês)</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="table_id">
	<thead>
		<tr>
			<th>Login</th>
			<th>Último login</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${appData.idleUsers}">
			<tr>
				<td>${user.login}</td>
				<td><c:choose>
					<c:when test="${not empty user.lastLogin}">
						<fmt:formatDate value="${user.lastLogin}"
							pattern="dd/MM/yyyy HH:mm" />
					</c:when>
					<c:otherwise>
					nunca
					</c:otherwise>
				</c:choose></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br />
<br />

<h4>Usuários mais ativos (por ordem de login)</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="table_id">
	<thead>
		<tr>
			<th>Login</th>
			<th>Último login</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${appData.topUsers}">
			<tr>
				<td>${user.login}</td>
				<td><c:choose>
					<c:when test="${not empty user.lastLogin}">
						<fmt:formatDate value="${user.lastLogin}"
							pattern="dd/MM/yyyy HH:mm" />
					</c:when>
					<c:otherwise>
					nunca
					</c:otherwise>
				</c:choose></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br />
