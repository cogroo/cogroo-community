CoGrOO Comunidade
=================

This is the source code of CoGrOO Comunidade, which runs at http://comunidade.cogroo.org

Development Environment
=======================

The development environment depends on:

- Maven 3
- Java 6

The following instructions will start the website using HSQL and Jetty:

* Copy the file `comunidade.properties` to the parent folder, i.e. the same level of the project. This holds sensitive information, and this procedure would prevents us from  accidentally commit passwords. Only the build team own the file with passwords.

* Copy the contents of `src/main/resources/META-INF/persistence.xml_test` to `src/main/resources/META-INF/persistence.xml`. The `persistence.xml` is for production, and use MySQL. The `persistence.xml_test` is for development and will use HSQL. *Please don't commit this change*.

* Open a terminal, go to the project directory and execute `mvn clean install -Dmaven.test.skip` to build the project. Sorry we need to skip tests. It is on our TODO to fix it and any help is welcome.

* In the same terminal execute `sh scripts/dbStart.sh` to start HSQLDB.

* Open another terminal, go to the project directory and execute `sh scripts/jettyStart.sh` to start Jetty.

* If everything is OK you will be able to open Cogroo Comunidade at http://localhost:8080


Other info
==========
Eclipse plugins
- Subversive: Subversion (SVN) integration for Eclipse
  site: http://www.eclipse.org/subversive/
  
- m2eclipse: Maven integration for Eclipse
  site: http://m2eclipse.sonatype.org
  
- EclEmma: code coverage tool
  site: http://www.eclemma.org/
  

Configuring Apache2 + Tomcat
Follow http://borort.wordpress.com/2010/09/23/apache2-tomcat-5-5-mod_jk-ubuntu-8-04/
substitute solr by "cogrooWeb"

Uncomment in /etc/tomcat6/server.xml :
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

  
