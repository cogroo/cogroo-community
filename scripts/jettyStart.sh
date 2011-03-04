echo Executando... 
echo mvn jetty:run -Danalytics.usr="cogroo" -Danalytics.pwd="$1" -Demail.system.usr="$2" -Demail.system.pwd="$3"
echo . 
mvn jetty:run -Danalytics.usr="cogroo" -Danalytics.pwd="$1" -Demail.system.usr="$2" -Demail.system.pwd="$3"
