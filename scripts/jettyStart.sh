echo Executando... 

export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"

echo mvn jetty:run
echo . 
mvn jetty:run
