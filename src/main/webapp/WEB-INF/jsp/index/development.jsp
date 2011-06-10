
<div id="about">
	<div class="yellow_box">
		<h1>Desenvolvimento</h1>
		<p>
			Que tal colaborar no desenvolvimento do CoGrOO Comunidade? Conheça
			aqui como obter o <a href="#src">código fonte</a>, como <a
				href="#comp">compilar</a> e <a href="#begin">por onde começar</a>.
		</p>
	</div>

	<div class="white_box">
		<h2>
			<a name="src">Código Fonte</a>
		</h2>
		<div class="dashed_white">
			<h2>Acesso Web</h2>
			<p>
				Navegue pelo código fonte usando o <a
					href="http://cogroo.svn.sourceforge.net/viewvc/cogroo/cogroo-community">ViewVC</a>
				pelo navegador.
			</p>
		</div>
		<div class="dashed_white">
			<h2>Repositório</h2>
			<p>
				O repositório SVN do CoGrOO Comunidade está localizado em<br>
				<code>https://cogroo.svn.sourceforge.net/svnroot/cogroo/cogroo-community/trunk</code>
				.
			</p>
		</div>
		<div class="dashed_white">
			<h2>Checkout SVN</h2>
			<p>Para obter o código fonte mais recente:</p>
			<div class="white_box">
				<code>svn co
					https://cogroo.svn.sourceforge.net/svnroot/cogroo/cogroo-community/trunk
					cogroo-community</code>
			</div>
			<p>Para obter uma tag ou branch:</p>
			<div class="white_box">
				<code>svn co
					https://cogroo.svn.sourceforge.net/svnroot/cogroo/cogroo-community/tags/(nome
					da tag)</code>
				<code>svn co
					https://cogroo.svn.sourceforge.net/svnroot/cogroo/cogroo-community/branches/(nome
					do branch)</code>
			</div>
		</div>
	</div>


	<div class="white_box">
		<h2>
			<a name="comp">Compilando os fontes</a>
		</h2>
		<div class="dashed_white">
			<h2>Pré-requisitos</h2>
			<p>
				Pelo menos <a
					href="http://www.oracle.com/technetwork/java/javase/overview/index.html">JDK
					5</a> para compilar e executar o projeto.
			</p>
			<p>
				Pelo menos <a href="http://maven.apache.org/">Maven 2</a> para
				compilar os fontes e empacotar o projeto.
			</p>
		</div>
		<div class="dashed_white">
			<h2>Procedimento para compilação</h2>
			<p>O build do CoGrOO Comunidade pode ser feito por um checkout do
				SVN. Aqui mostraremos como fazer da versão mais atual do trunk.</p>
			<p>Depois do primeiro checkout é necessário fazer um build
				completo:</p>
			<div class="white_box">
				<code>cd cogroo-community</code>
				<br>
				<code>mvn install -Dmaven.test.skip</code>
				<br>
			</div>
			<p>
				* o argumento -Dmaven.test.skip é necessário porque os testes estão
				falhando (<a href="http://ccsl.ime.usp.br/redmine/issues/272">refs
					#272</a>).
			</p>
			<p>O build pode falhar. Neste caso relate no fórum do projeto.</p>
		</div>
		<div class="dashed_white">
			<h2>Criando uma instância local do Comunidade</h2>
			<p>O CoGrOO Comunidade requer um servidor web e um banco de
				dados. No ambiente de produção usamos Tomcat + MySQL, mas no de
				desenvolvimento podemos usar Jetty + HSQLDB.</p>
			<p>O SVN deve ficar preparado para rodar no ambiente de produção.
				No ambiente local precisamos fazer algumas modificações.</p>
			<h3>Banco de Dados</h3>
			<p>
				Alternar do MySQL para o HSQLDB copiando o conteúdo do arquivo
				<code>cogroo-community/src/main/resources/META-INF/persistence.xml_test</code>
				para
				<code>cogroo-community/src/main/resources/META-INF/persistence.xml</code>
				.
			<p>
			<h3>Configurando o cogroo.properties</h3>
			<p>
				Copie o arquivo
				<code>cogroo-community/cogroo.properties</code>
				para a pasta um nível acima, junto da pasta
				<code>cogroo-community</code>
				. Este arquivo armazena informações confidenciais do projeto, como
				as chaves OAuth. Como não é seguro divulgar estes dados o arquivo
				não está completo e durante o funcionamento do portal podem haver
				exceptions decorrentes da falta dessas chaves.
			</p>
			<h3>Iniciando os servidores</h3>
			<p>Inicie o banco de dados HSQLDB com o comando:</p>
			<div class="white_box">
				<code>scripts/dbStart.sh</code>
			</div>
			<p>
				Crie um novo terminal, siga até a pasta
				<code>cogroo-community</code>
				e inicie o Jetty:
			</p>
			<div class="white_box">
				<code>scripts/jettyStart.sh</code>
			</div>
			Finalmente você pode acessar a instância local do comunidade
			digitando a seguinte URL no seu navegador:

			<div class="white_box">
				<code>http://localhost:8080</code>
			</div>
		</div>
	</div>

	<div class="white_box">
		<h2>
			<a name="begin">Por onde começar</a>
		</h2>
		<div class="dashed_white">
			<h2>Redmine</h2>
			<p>
				Existem muitas tarefas em aberto no nosso <a
					href="http://ccsl.ime.usp.br/redmine/projects/cogroo-community/issues">repositório
					de tarefas</a>. Você pode escolher uma que te agrade e submeter um
				patch. Caso tenha dúvidas, queira sugestões ou acompanhamento em uma
				tarefa entre em contato pelo fórum.
			</p>
		</div>
	</div>
</div>