<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<script type="text/javascript">
	if (${justAddedDictionaryEntry})
	{
		_gaq.push(['_trackEvent', 'Dictionary', 'add entry', '${login}']);
	}
</script>

<h2>Cadastro Realizado com sucesso !</h2>
<h3>Favor efetuar login.</h3>