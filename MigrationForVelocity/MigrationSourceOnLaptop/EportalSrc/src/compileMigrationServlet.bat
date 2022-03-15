@ECHO OFF
SET JAVA_HOME=d:\jdk1.8
SET CATALINA_HOME=d:\tomcat8.5

SET CLASSPATH=.;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\commons-io-1.1.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\commons-fileupload-1.1.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\activation.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\mailapi.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\jdbcext.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\jtds-0.5.1.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\jtds-1.2.4.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\msbase.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\msutil.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\classes;%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\xerces.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\xercesImpl.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\xmlParserAPIs.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\jsch.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\log4j-1.2.15.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\commons-codec-1.4.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\mssqlserver.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\gson-2.2.2.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\java-json.jar;%CATALINA_HOME%\webapps\EPortal\WEB-INF\lib\json-simple-1.1.jar;

SET TARGET_DIR=%CATALINA_HOME%\webapps\EPortal\WEB-INF\classes

 %JAVA_HOME%\bin\javac servlets\xlMigrationServiceExt.java -classpath %CLASSPATH%  -d  %TARGET_DIR%



@ECHO ON