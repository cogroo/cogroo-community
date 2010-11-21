<%@ page contentType="text/html; charset=UTF-8" %>  
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table_jui.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/dataTables_table.css"/>" />

<script type="text/javascript" src="http://www.google.com/jsapi"></script>
  <script type="text/javascript">
    google.load('visualization', '1', {packages: ['annotatedtimeline']});
    function drawVisualization() {
      var data = new google.visualization.DataTable();
      data.addColumn('date', 'Date');
      data.addColumn('number', 'Eventos');
      data.addColumn('number', 'Visitas');
      data.addColumn('number', 'Impressões');
      
      var str = "${metrics}";
      var entries = str.split(";")
      data.addRows(entries.length);   
      
      for (i=0;i<entries.length;i++) {
    	  var metrics = entries[i].split(",");
    	  var date = metrics[0].split("-");
    	  data.setValue(i, 0, new Date(date[0], date[1] - 1, date[2]));
    	  for (j=1;j<metrics.length;j++)
    		  data.setValue(i, j, Number(metrics[j]));
      }
      
      var annotatedtimeline = new google.visualization.AnnotatedTimeLine(
          document.getElementById('visualization'));
      annotatedtimeline.draw(data, {'displayAnnotations': false});
    }
    
    google.setOnLoadCallback(drawVisualization);
  </script>

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

<h3>Temporais</h3>
<div id="visualization" style="width: 750px; height: 400px;"></div>	
<br />

<h3>Relatórios</h3>

<h4>Membros online</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="online_members">
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
	id="inactive_users">
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

<h4>Últimos usuários online (por ordem de login)</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="last_online_users">
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
