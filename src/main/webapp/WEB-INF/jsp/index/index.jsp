<script src="<c:url value='/js/jquery.twitter.search.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.zrssfeed.js' />" type="text/javascript" ></script>

<script type="text/javascript">
	if (${justRegistered})
	{
		_gaq.push(['_trackEvent', 'Register', 'succeeded register', '${login}']);
	}
</script>

<script type="text/javascript">
	if (${justLogged})
	{
		_gaq.push(['_trackEvent', 'Login', 'succeeded login', '${login}']);
	}
</script>

<script type="text/javascript"><!--//--><![CDATA[//><!--

	$(document).ready(function(){

		$('#twitter').twitterSearch({
			term: 'cogroo',
			title: '@cogroo',
			avatar:  false, 
			titleLink: 'http://twitter.com/cogroo',
			birdLink: 'http://twitter.com/cogroo',
			css: {
				img: { width: '30px', height: '30px' }
				}
			}); 
		
		$('#news').rssfeed('http://ccsl.ime.usp.br/cogroo/pt-br/rss.xml', {
		    limit: 5,
		    date: false,
		    content: true,
		    snippet: true,
		    errormsg: 'Não foi possível abrir notícias.'
		  });
	});
	
//--><!]]>

</script>
		
		
	
		<h2>Sobre o projeto</h2>
		
		<p>O CoGrOO Comunidade é um portal colaborativo para aprimorar o CoGrOO, o corretor gramatical em português para OpenOffice. <a href="<c:url value="/about"/>">Mais informações...</a></p>
		<br />
		<p><a href="<c:url value="/grammar"/>">Busque</a> erros gramaticais em uma frase. Algo estranho? <a href="<c:url value="/reportNewError"/>">Conte</a> para nós!</p>
		<br />
		
		
	
		<h2>Notícias</h2>
		
		<div id="twitter" class="twitter"></div>
		
		<div id="newswrapper">
			<div id="news"></div>
			<a href="http://ccsl.ime.usp.br/cogroo/pt-br/rss.xml"><img width="16" height="16" title="Divulgar conteúdo" alt="Divulgar conteúdo" src="./images/feed.png"/></a>
		</div> 