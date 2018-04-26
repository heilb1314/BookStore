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
4. Install [MySQL Connector/J Platform Independent](https://dev.mysql.com/downloads/connector/j/)
   <br> Unzip MySQL Connector/J and put <b><i>mysql-connector-java-5.1.45-bin.jar</i></b> into tomcat/lib directory
5. Configure Eclipse with Tomcat (in Java EE perspective, explore the Servers tab and add the Tomcat directory)
6. Right click on the project name -> Build Path -> Configure Build path... -> Libraries -> Add Library
    <br>• EAR Libraries
    <br>• JRE System Library
    <br>• Web App Libraries
    <br>• Server Runtime -> Apache Tomcat v8.5
