# dbsclient
A small Java client application to connect to DBS.

This is an Eclipse-project that reuires the Maven plugin. Only useful for customers of SIDN's DBS-service.

## Background information
* https://www.sidn.nl/a/sidn-services/domain-name-surveillance-service
* http://maven.apache.org/

## Install:
    mvn clean compile assembly:single
    java -jar dbsRestClient-1.0.null-jar-with-dependencies.jar

 ![Screenshot](https://github.com/SIDN/dbsclient/blob/master/screenshots/dbs-client.png "Screenshot")
