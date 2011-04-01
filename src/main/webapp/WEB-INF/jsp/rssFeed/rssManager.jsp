<%@ page contentType="text/html; charset=UTF-8" %>
<script type="text/javascript">
	var path = '${pageContext.servletContext.contextPath}';
</script>

<h3>RSS Manager:</h3>

<form id="delete" action="<c:url value="/rss/delete"/>" method="post" >
	Limpar RSS Feed <input type="submit" value="Limpar"/>
</form>
