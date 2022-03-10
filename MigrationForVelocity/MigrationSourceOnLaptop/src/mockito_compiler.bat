@ECHO OFF
SET CATALINA_HOME=C:\Tomcat8
SET JAVA_HOME="C:\Program Files\Java\jdk1.8.0_171"
SET WEB_APP=%CATALINA_HOME%\webapps\BAE_4_5


SET CLASSPATH=.;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-container-servlet-core-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-entity-filtering-2.26.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-guava-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-media-jaxb-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-media-json-2.0-m05.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-media-json-jackson-2.26.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-media-multipart-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-server-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jettison-1.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\joda-time-2.7.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jsr311-api-1.1.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jstl-1.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\log4j-1.2.17.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\mail-1.4.7.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\mimepull-1.9.6.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\mockito-all-1.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\mssql-jdbc-7.2.2.jre8.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\nekohtml-1.9.16.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\osgi-resource-locator-1.0.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\reflections-0.9.10.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\servlet-api.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\slf4j-api-1.6.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\snakeyaml-1.18.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\stax2-api-3.1.4.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\stax-api-1.0-2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\swagger-annotations-1.5.0.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\swagger-core-1.5.20.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\swagger-jaxrs-1.5.13.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\swagger-models-1.5.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\testng-6.14.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\validation-api-1.0.0.GA.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\woodstox-core-5.0.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xalan-2.7.0.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xerces.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xercesImpl-2.8.0.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xml-apis-1.3.03.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xml-apis-ext-1.3.04.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\xom-1.2.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\activation-1.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\annotations-2.0.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\antisamy-1.5.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\aopalliance-repackaged-2.5.0-b30.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\batik-css-1.8.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\batik-ext-1.8.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\batik-util-1.8.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\bsh-2.0b6.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\bsh-core-2.0b4.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-beanutils-core-1.8.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-codec-1.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-collections-3.2.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-configuration-1.10.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-fileupload-1.3.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-httpclient-3.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-io-2.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-lang-2.6.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-lang3-3.8.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\commons-logging-1.1.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\encoder-1.2.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\esapi-2.1.0.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\guava-20.0.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\hk2-api-2.5.0-b30.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\hk2-locator-2.5.0-b30.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\hk2-utils-2.5.0-b30.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-annotations-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-core-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-core-asl-1.9.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-databind-2.8.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-dataformat-xml-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-dataformat-yaml-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-datatype-joda-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-jaxrs-1.9.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-jaxrs-base-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-jaxrs-json-provider-2.9.5.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-mapper-asl-1.9.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-module-jaxb-annotations-2.8.4.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jackson-xc-1.9.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javassist-3.19.0-GA.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javax.annotation-api-1.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javax.inject-1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javax.inject-2.5.0-b30.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javax.json-api-1.0.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\javax.ws.rs-api-2.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jaxb-api-2.2.2.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jaxb-impl-2.2.3-1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jboss-interceptor-api-1.1.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jcommander-1.72.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-client-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-common-2.25.jar;%CATALINA_HOME%\webapps\BAE_4_5\WEB-INF\lib\jersey-container-servlet-2.25.jar
SET TARGET_DIR=%WEB_APP%\WEB-INF\classes

%JAVA_HOME%\bin\javac C:\Tomcat8\webapps\BAE_4_5\src\test\*.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
PAUSE


@ECHO ON
