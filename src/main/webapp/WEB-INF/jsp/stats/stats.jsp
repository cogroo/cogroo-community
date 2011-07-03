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
      data.addColumn('string', 'title1');
      data.addColumn('number', 'Visitas');
      data.addColumn('string', 'title2');
      data.addColumn('number', 'Impressões');
      data.addColumn('string', 'title3');
      
      var str = "${appData.temporalData}";
      var entries = str.split(";")
      data.addRows(entries.length);  
      
      for (i=0;i<entries.length;i++) {
    	  var metrics = entries[i].split(",");
    	  var date = metrics[0].split("-");
    	  data.setValue(i, 0, new Date(date[0], date[1] - 1, date[2]));
    	  for (j=1,k=1;j<metrics.length;j++,k+=2)
    		  data.setValue(i, k, Number(metrics[j]));
      }
      
 	  // 26/11/10
      data.setValue(16, 6, 'Lançamento da versão 1.1.0');
      // 30/11/10
      data.setValue(20, 6, 'Notícia no <a href="http://br-linux.org/2010/broffice-nova-versao-do-corretor-gramatical-cogroo/">BR-Linux.org</a>');
      // 12/04/11
      data.setValue(153, 6, 'Lançamento da versão 1.2.0');
      // 26/04/11
      data.setValue(167, 4, 'Post no Twitter @<a href="http://twitter.com/vendenafarmacia/status/62998635268407296">vendenafarmacia</a>');
      // 04/06/11
      data.setValue(206, 2, 'Véspera deadline <a href="http://www.textolivre.pro.br/blog/?p=1107">UEADSL</a>');
      // 17/06/11
      data.setValue(219, 6, 'Lançamento da versão 1.3.0');
      
      var annotatedtimeline = new google.visualization.AnnotatedTimeLine(
          document.getElementById('visualization'));
      annotatedtimeline.draw(data, {'displayAnnotations': true,
    	  							'annotationsWidth': 15,
    	  							'allowHtml': true,
    	  							'dateFormat': "dd 'de' MMMM 'de' yyyy"});
    }
    
    google.setOnLoadCallback(drawVisualization);
  </script>

<h2>Estatísticas</h2>

<h3>Globais</h3>

<p>Eventos: ${appData.events}</p>
<p>Visitas: ${appData.visits}</p>
<p>Impressões: ${appData.pageviews}</p>
<br />
<p>Erros reportados: ${appData.reportedErrors}</p>
<p>Palavras no dicionário: ${appData.dictionaryEntries}</p>
<p>Membros: ${appData.registeredMembers}</p>
<br />
<p>Usuários online: ${appData.onlineUsers}</p>
<p>Visitantes: ${appData.onlineVisits}</p>
<p>Membros online: ${appData.onlineMembers}</p>
<br />

<h3>Temporais</h3>
<div id="visualization" style="width: 964px; height: 400px;"></div>	
<p class="left"><a href="<c:url value="/stats/EstatisticasCogrooComunidade.csv"/>" onclick="_gaq.push(['_trackEvent', 'Link', 'clicked link', '<c:url value="/stats/EstatisticasCogrooComunidade.csv"/>']);"><b>download</b></a></p>
<br />

<h3>Relatórios</h3>

<h4>Membros online</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="online_members">
	<thead>
		<tr>
			<th>Nome</th>
			<th>Último login</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${appData.loggedUsers}">
			<tr>
				<td><a href="<c:url value="/users/${user.service}/${user.login}"/>">${user.name}</a></td>
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

<h4>Últimos usuários online</h4>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="last_online_users">
	<thead>
		<tr>
			<th>Nome</th>
			<th>Último login</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="user" items="${appData.topUsers}">
			<tr>
				<td><a href="<c:url value="/users/${user.service}/${user.login}"/>">${user.name}</a></td>
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
