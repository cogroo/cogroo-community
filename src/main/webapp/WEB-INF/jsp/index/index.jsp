<script src="<c:url value='/js/jquery.twitter.search.js' />" type="text/javascript" ></script>
<script src="<c:url value='/js/jquery.zrssfeed.js' />" type="text/javascript" ></script>

<script type="text/javascript"><!--//--><![CDATA[//><!--

	$(document).ready(function(){

		$('#news').rssfeed('http://ccsl.ime.usp.br/cogroo/rss.xml', {
		    limit: 7,
		    date: false,
		    content: true,
		    snippet: true,
		    errormsg: 'N�o foi poss�vel abrir not�cias.'
		  });
	});

//--><!]]>

</script>

	<h2>Sobre o projeto</h2>
	<div>
		<div id="socialNetworks" class="socialNetworks">
			<iframe src="http://www.facebook.com/plugins/likebox.php?href=http%3A%2F%2Fwww.facebook.com%2Fpages%2FCoGrOO%2F191205774239878&amp;width=240&amp;colorscheme=light&amp;show_faces=true&amp;stream=false&amp;header=false&amp;height=200" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:240px; height:200px;" allowTransparency="true"></iframe>
			<br /><br />
			<div id="twitter" class="twitter">
<a class="twitter-timeline" href="https://twitter.com/CoGrCom" data-widget-id="445833775591587840">Tweets by @CoGrCom</a>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>


			</div>

		</div>

		<p>O CoGrOO Comunidade � um portal colaborativo para aprimorar o CoGrOO, o corretor gramatical em portugu�s para BrOffice. <a href="<c:url value="/about"/>">Mais informa��es...</a></p>
		<br />
		<p><a href="<c:url value="/grammar"/>">Busque</a> erros gramaticais em uma frase. Algo estranho? Conte para n�s!</p>
		<br />


		<h2>Not�cias</h2>

		<div id="newswrapper">
			<div id="news"></div>
		</div>
	</div>