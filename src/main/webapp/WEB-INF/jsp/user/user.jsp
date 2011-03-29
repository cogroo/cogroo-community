<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>

<h3>Detalhes do usuário <strong>${user.login}</strong>:</h3>
<div class="rule_details">

	<c:set var="canEdit" value="${loggedUser.user.id == user.id || loggedUser.user.role.canEditSensitiveUserDetails}"></c:set>
	<c:set var="canView" value="${loggedUser.user.id == user.id || loggedUser.user.role.canViewSensitiveUserDetails}"></c:set>
	
	<form id="editUser" action="<c:url value="/editUser"/>" method="post" >
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Login:</th>
			    <td><input type="text" name="login-noedit" value="${user.login}" disabled="disabled" /></td>
			</tr>
			<tr>
			    <th>Nome:</th>
			    <c:choose>
					<c:when test="${canEdit}">
			    		<td><input type="text" name="name" value="${user.name}" /></td>
			  		</c:when>
			  		<c:otherwise>
			    		<td><input type="text" name="name-noedit" value="${user.name}" disabled="disabled" /></td>
			  		</c:otherwise>
				</c:choose>
				
			</tr>
			<c:if test="${canView}"> 
				<tr>
			    	<th>Email:</th>
				    <c:choose>
						<c:when test="${canEdit}">
				    		<td><input type="text" name="email" value="${user.email}" /></td>
				  		</c:when>
				  		<c:otherwise>
				    		<td><input type="text" name="email-noedit" value="${user.email}" disabled="disabled" /></td>
				  		</c:otherwise>
					</c:choose>
				</tr>
			</c:if>
			<tr>
			    <th>Último login:</th><td><fmt:formatDate type="both" dateStyle="long" value="${user.lastLogin}" /></td>
			</tr>
			<tr>
			    <th>Papel:</th><td><fmt:message key="${user.role}" /></td>
			</tr>
			<c:if test="${canView}">
				<tr>
				    <th>Notificações e-mail:</th>
				    <c:choose>
						<c:when test="${user.isReceiveEmail}">
				    		<c:set var="checked" value="checked='CHECKED'"></c:set>
				  		</c:when>
				  		<c:otherwise>
				    		<c:set var="checked" value=""></c:set>
				  		</c:otherwise>
					</c:choose>
				    
				    <td><c:choose>
						<c:when test="${canEdit}">
				    		<input type="checkbox" name="isReceiveEmail" value="true" ${checked} />
				  		</c:when>
				  		<c:otherwise>
				    		<input type="checkbox" name="isReceiveEmail-noedit" value="true" disabled="disabled"/>
				  		</c:otherwise>
					</c:choose>Enviar e-mail quando houver alterações ou novos comentários em um problemas reportados que eu tenha iteragido.</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	<c:if test="${canEdit}">
		<input name="user.id" value="${user.id}" type="hidden" />
		<input type="submit" value=" Atualizar &raquo; " id="updateUser"/>  
	</c:if>
	</form>
</div>

<c:if test="${(loggedUser.user.role.canSetUserRole) || (loggedUser.user.login == 'admin') }">
	<form id="setUserRole"  action="<c:url value="/userRole"/>" method="post" >
		Escolha um novo papel para o usuário: 
		<select name="role">
			<c:forEach items="${roleList}" var="role">
				<option value="${role.roleName}"><fmt:message key="${role.roleName}" /></option>
			</c:forEach>
		</select>
		<input name="user.id" value="${user.id}" type="hidden" />
	    <input type="submit" value=" Alterar &raquo; " id="setUserRole"/>
	</form>
</c:if>