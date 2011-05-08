<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	
	// set viewing
	viewing();
	
	$('#edit').click(function(e) {
		e.preventDefault();
		editing();
	 });
	
	$('#reset').click(function(e) {
		// let it reset, don't prevent devault
		viewing();
	 });
});

function viewing() {
	$('.viewing').show();
	$('.editing').hide();
};

function editing() {
	$('.viewing').hide();
	$('.editing').show();
};

	
</script>

<h3>Usuário <strong>${user.login}</strong>:</h3>
<div class="rule_details">

	<c:set var="canEdit" value="${loggedUser.user.id == user.id || loggedUser.user.role.canEditSensitiveUserDetails}"></c:set>
	<c:set var="canView" value="${loggedUser.user.id == user.id || loggedUser.user.role.canViewSensitiveUserDetails}"></c:set>
	
	<form id="editUser" action="<c:url value="/users/${user.login}"/>" method="post" >
	<table class="attributes">
		<tbody>
			<tr>
			    <th>Login:</th>
			    <td>${user.login}</td>
			</tr>
			<tr>
			    <th>Nome:</th>
			    <c:choose>
					<c:when test="${canEdit}">
			    		<td class="editing"><input type="text" name="name" value="${user.name}" /></td>
			    		<td class="viewing">${user.name}</td>
			  		</c:when>
			  		<c:otherwise>
			    		<td>${user.name}</td>
			  		</c:otherwise>
				</c:choose>
				
			</tr>
			<c:if test="${canView}"> 
				<tr>
			    	<th>Email:</th>
				    <c:choose>
						<c:when test="${canEdit}">
				    		<td class="editing"><input type="text" name="email" value="${user.email}" /></td>
				    		<td class="viewing">${user.email}</td>
				  		</c:when>
				  		<c:otherwise>
				    		<td>${user.email}</td>
				  		</c:otherwise>
					</c:choose>
				</tr>
			</c:if>
			<tr>
			    <th>Twitter:</th>
			    <c:choose>
					<c:when test="${canEdit}">
			    		<td class="editing"><input type="text" name="twitter" value="${user.twitter}" /></td>
			    		<td class="viewing">${user.twitter}</td>
			  		</c:when>
			  		<c:otherwise>
			    		<td><a target="_blank" href="http://twitter.com/#!/${user.twitter}">${user.twitter}</a></td>
			  		</c:otherwise>
				</c:choose>
				
			</tr>
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
				    		<input class="viewing" type="checkbox" name="isReceiveEmail" value="true" ${checked}  disabled="disabled"/>
				    		<input class="editing" type="checkbox" name="isReceiveEmail" value="true" ${checked} />
				  		</c:when>
				  		<c:otherwise>
				    		<input type="checkbox" name="isReceiveEmail-noedit" value="true" disabled="disabled"/>
				  		</c:otherwise>
					</c:choose>Enviar e-mail quando houver alterações ou novos comentários em algum problema que eu tenha interagido.</td>
				</tr>
			</c:if>
		</tbody>
	</table>
	<c:if test="${canEdit}">
		<div class="editing">
			<input type="hidden" name="_method" value="PUT"/>
			<input name="user.id" value="${user.id}" type="hidden" />
			<input type="reset" value=" &laquo; Limpar " onClick="window.location.reload()">
			<input type="submit" value=" Atualizar &raquo; " id="updateUser"/>
		</div>
		<div class="viewing">
			<input type="submit" value=" Editar &raquo; " id="edit"/>
		</div> 
	</c:if>
	</form>
</div>

<c:if test="${(loggedUser.user.role.canSetUserRole) || (loggedUser.user.login == 'admin') }">
	<form id="setUserRole"  action="<c:url value="/users/${user.login}/role"/>" method="post" >
		Escolha um novo papel para o usuário: 
		<select name="role">
			<c:forEach items="${roleList}" var="role">
				<option value="${role.roleName}"><fmt:message key="${role.roleName}" /></option>
			</c:forEach>
		</select>
		<input name="user.id" value="${user.id}" type="hidden" />
		<input type="hidden" name="_method" value="PUT"/>
	    <input type="submit" value=" Alterar &raquo; " id="setUserRole"/>
	</form>
</c:if>