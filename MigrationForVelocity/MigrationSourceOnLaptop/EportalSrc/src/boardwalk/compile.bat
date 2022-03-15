SET CLASSPATH=.;%TOMCAT_HOME%\common\lib\servlet.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\mailapi.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\activation.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\jdbcext.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\jtds-0.5.1.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\mssqlserver.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\msbase.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib\msutil.jar;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\classes;%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\lib
SET TARGET_DIR=%DEPLOY_TOMCAT_HOME%\webapps\bw\WEB-INF\classes
%JAVA_HOME%\bin\javac connection\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac table\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac collaboration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac neighborhood\*.java -classpath %CLASSPATH% -d %TARGET_DIR%





