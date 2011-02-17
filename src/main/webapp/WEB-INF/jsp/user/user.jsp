<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>

<h3>Detalhes do usuário <strong>${user.login}</strong>:</h3>
<div class="rule_details">
	
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Login:</th><td>${user.login}</td>
			</tr>
			<tr>
			    <th>Nome:</th><td>${user.name}</td>
			</tr>
			<tr>
			    <th>Último login:</th><td>${user.lastLogin}</td>
			</tr>
			<tr>
			    <th>Papel:</th><td>${user.role}</td>
			</tr>
			<c:if test="${(user.roleName == 'admin') || (loggedUser.user.login == 'admin') }"> 
				<tr>
			    	<th>Email:</th><td>${user.email}</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>

<c:if test="${(user.roleName == 'admin') || (loggedUser.user.login == 'admin') }">
	<form id="setUserRole"  action="<c:url value="/userRole"/>" method="post" >
		Escolha um novo papel para o usuário: 
		<select name="role">
			<c:forEach items="${roleList}" var="role">
				<option value="${role.roleName}">${role.roleName}</option>
			</c:forEach>
		</select>
		<input name="user.id" value="${user.id}" type="hidden" />
	    <input type="submit" value=" Alterar &raquo; " id="setUserRole"/>
	</form>
</c:if>