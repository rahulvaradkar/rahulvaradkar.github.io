@ECHO OFF

SET JAVA_HOME=d:\jdk1.8
SET CATALINA_HOME=d:\tomcat8.5

SET WEB_APP=%CATALINA_HOME%\webapps\BAE_4_6_1_TARGET

SET CLASSPATH=.;%WEB_APP%\WEB-INF\lib\activation-1.1.jar;%WEB_APP%\WEB-INF\lib\annotations-2.0.1.jar;%WEB_APP%\WEB-INF\lib\aopalliance-repackaged-2.5.0-b42.jar;%WEB_APP%\WEB-INF\lib\bsh-2.0b6.jar;%WEB_APP%\WEB-INF\lib\cdi-api-2.0.jar;%WEB_APP%\WEB-INF\lib\commons-codec-1.5.jar;%WEB_APP%\WEB-INF\lib\commons-fileupload-1.3.jar;%WEB_APP%\WEB-INF\lib\commons-io-2.2.jar;%WEB_APP%\WEB-INF\lib\commons-lang3-3.8.1.jar;%WEB_APP%\WEB-INF\lib\encoder-1.2.1.jar;%WEB_APP%\WEB-INF\lib\guava-20.0.jar;%WEB_APP%\WEB-INF\lib\hk2-api-2.5.0-b42.jar;%WEB_APP%\WEB-INF\lib\hk2-locator-2.5.0-b42.jar;%WEB_APP%\WEB-INF\lib\hk2-utils-2.5.0-b42.jar;%WEB_APP%\WEB-INF\lib\jackson-annotations-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-core-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-databind-2.8.5.jar;%WEB_APP%\WEB-INF\lib\jackson-dataformat-xml-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-dataformat-yaml-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-datatype-joda-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-jaxrs-base-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-jaxrs-json-provider-2.9.5.jar;%WEB_APP%\WEB-INF\lib\jackson-module-jaxb-annotations-2.8.10.jar;%WEB_APP%\WEB-INF\lib\javassist-3.19.0-GA.jar;%WEB_APP%\WEB-INF\lib\javax.annotation-api-1.2.jar;%WEB_APP%\WEB-INF\lib\javax.el-api-3.0.0.jar;%WEB_APP%\WEB-INF\lib\javax.inject-1.jar;%WEB_APP%\WEB-INF\lib\javax.inject-2.5.0-b42.jar;%WEB_APP%\WEB-INF\lib\javax.interceptor-api-1.2.jar;%WEB_APP%\WEB-INF\lib\javax.json.bind-api-1.0.jar;%WEB_APP%\WEB-INF\lib\javax.json-api-1.0.jar;%WEB_APP%\WEB-INF\lib\javax.ws.rs-api-2.1.jar;%WEB_APP%\WEB-INF\lib\jaxb-api-2.3.0.jar;%WEB_APP%\WEB-INF\lib\jaxb-core-2.3.0.jar;%WEB_APP%\WEB-INF\lib\jaxb-impl-2.3.0.jar;%WEB_APP%\WEB-INF\lib\jboss-interceptor-api-1.1.jar;%WEB_APP%\WEB-INF\lib\jcommander-1.72.jar;%WEB_APP%\WEB-INF\lib\jersey-client-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-common-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-container-servlet-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-container-servlet-core-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-entity-filtering-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-hk2-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-media-jaxb-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-media-json-jackson-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-media-multipart-2.27.jar;%WEB_APP%\WEB-INF\lib\jersey-server-2.27.jar;%WEB_APP%\WEB-INF\lib\joda-time-2.7.jar;%WEB_APP%\WEB-INF\lib\jsr311-api-1.1.1.jar;%WEB_APP%\WEB-INF\lib\jstl-1.2.jar;%WEB_APP%\WEB-INF\lib\junit-3.8.1.jar;%WEB_APP%\WEB-INF\lib\mail-1.4.7.jar;%WEB_APP%\WEB-INF\lib\mimepull-1.9.6.jar;%WEB_APP%\WEB-INF\lib\mockito-all-1.9.5.jar;%WEB_APP%\WEB-INF\lib\mssql-jdbc-7.2.2.jre8.jar;%WEB_APP%\WEB-INF\lib\osgi-resource-locator-1.0.1.jar;%WEB_APP%\WEB-INF\lib\reflections-0.9.10.jar;%WEB_APP%\WEB-INF\lib\servlet-api.jar;%WEB_APP%\WEB-INF\lib\slf4j-api-1.6.3.jar;%WEB_APP%\WEB-INF\lib\snakeyaml-1.18.jar;%WEB_APP%\WEB-INF\lib\stax2-api-3.1.4.jar;%WEB_APP%\WEB-INF\lib\swagger-annotations-1.5.0.jar;%WEB_APP%\WEB-INF\lib\swagger-core-1.5.20.jar;%WEB_APP%\WEB-INF\lib\swagger-jaxrs-1.5.13.jar;%WEB_APP%\WEB-INF\lib\swagger-models-1.5.3.jar;%WEB_APP%\WEB-INF\lib\testng-6.14.3.jar;%WEB_APP%\WEB-INF\lib\validation-api-1.0.0.GA.jar;%WEB_APP%\WEB-INF\lib\woodstox-core-5.0.3.jar;%WEB_APP%\WEB-INF\lib\xerces.jar;%WEB_APP%\WEB-INF\lib\xercesImpl-2.8.0.jar;%WEB_APP%\WEB-INF\lib\xml-apis-1.3.03.jar;%WEB_APP%\WEB-INF\lib\yasson-1.0.jar
SET TARGET_DIR=%WEB_APP%\WEB-INF\classes

%JAVA_HOME%\bin\javac servlets\*.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
REM PAUSE

%JAVA_HOME%\bin\javac com\boardwalk\neighborhood\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\collaboration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\exception\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\query\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\user\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\whiteboard\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\excel\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\distribution\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
REM %JAVA_HOME%\bin\javac com\boardwalk\integration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\database\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\member\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\dal\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\logic\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\model\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\table\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\wizard\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\util\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac com\boardwalk\transformers\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
REM PAUSE

%JAVA_HOME%\bin\javac boardwalk\connection\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac boardwalk\table\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac boardwalk\collaboration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac boardwalk\neighborhood\*.java -classpath %CLASSPATH% -d %TARGET_DIR% 
%JAVA_HOME%\bin\javac boardwalk\common\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac boardwalk\rest\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
REM PAUSE

%JAVA_HOME%\bin\javac io\swagger\model\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac io\swagger\api\factories\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac io\swagger\api\impl\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
%JAVA_HOME%\bin\javac io\swagger\api\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
PAUSE

@ECHO ON