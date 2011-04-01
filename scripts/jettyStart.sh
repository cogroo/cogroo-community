echo Executando... 

export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"

echo mvn jetty:run -Danalytics.usr="cogroo" -Danalytics.pwd="$1" -Demail.system.usr="$2" -Demail.system.pwd="$3" -Dbase.url="$4"
echo . 
mvn jetty:run -Danalytics.usr="cogroo" -Danalytics.pwd="$1" -Demail.system.usr="$2" -Demail.system.pwd="$3" -Dbase.url="$4"
