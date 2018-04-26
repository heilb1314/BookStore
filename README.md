# BookStore
EECS4413 Project

## Installation
1. Install [MySQL](https://dev.mysql.com/downloads/mysql/)
   <br>Make sure you setup MySQL user 'root' without any password, and remain the default port: 3306
   #### Create book_store database.
       mysql -u root -p
       create database book_store; 
       exit
   #### Import db/db.sql to book_store database (Under the project folder, run following command)
       mysql -u root book_store -p < db/db.sql
2. Install [Apache Tomcat 8.5](https://tomcat.apache.org/tomcat-8.5-doc/index.html)
3. The following jar files should be in the WebContent/WEB-INF/lib directory
   <br>• javax.json-1.0.jar
   <br>• jersey-bundle-1.19.1.jar
   <br>• mysql-connector-java-5.1.45-bin.jar
4. Configure Eclipse with Tomcat (in Java EE perspective, explore the Servers tab and add the Tomcat directory)
5. Right click on the project name -> Build Path -> Configure Build path... -> Libraries -> Add Library
    <br>• EAR Libraries
    <br>• JRE System Library
    <br>• Web App Libraries
    <br>• Server Runtime -> Apache Tomcat v8.5

## SSL Configuration

1. Add this to your tomcat directory's `conf/server.xml`, where "<pathToRepo>" is the location of the Keystore,
or alternatively move the Keystore somewhere else. The keystore is the one at the root of this repository

```$xslt
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS"
               keyAlias="selfsigned_tomcat" keystoreFile="<pathToKeystore>/KeyStore.jks"
               keystorePass="123456" />
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
```

Add the following to your tomcat directory's `conf/web.xml`:

```$xslt
      <security-constraint>
              <web-resource-collection>
                      <web-resource-name>Entire Application</web-resource-name>
                      <url-pattern>/*</url-pattern>
              </web-resource-collection>
              <!-- auth-constraint goes here if you requre authentication -->
              <user-data-constraint>
                      <transport-guarantee>CONFIDENTIAL</transport-guarantee>
              </user-data-constraint>
      </security-constraint>
```

This will rewrite all requests to port 8080 via Secure socket layer.

