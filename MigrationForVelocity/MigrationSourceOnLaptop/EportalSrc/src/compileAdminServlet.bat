@ECHO OFF
SET JAVA_HOME=d:\jdk1.7
SET CATALINA_HOME=d:\tomcat7

SET CLASSPATH=.;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\commons-io-1.1.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\commons-fileupload-1.1.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\activation.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\mailapi.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\jdbcext.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\jtds-0.5.1.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\jtds-1.2.4.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\msbase.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\msutil.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\classes;%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\xerces.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\xercesImpl.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\xmlParserAPIs.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\jsch.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\log4j-1.2.15.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\commons-codec-1.4.jar;%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\lib\mssqlserver.jar
SET TARGET_DIR=%CATALINA_HOME%\webapps\universal_template_qa\WEB-INF\classes

rem  %JAVA_HOME%\bin\javac com\boardwalk\neighborhood\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\collaboration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\database\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\exception\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\member\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\table\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\query\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\user\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\whiteboard\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\excel\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\wizard\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\distribution\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\integration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\xlImportChangesService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac com\boardwalk\table\TableViewManager.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\MaintenanceModeFilter.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\*.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac boardwalk\connection\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac boardwalk\table\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac boardwalk\collaboration\*.java -classpath %CLASSPATH% -d %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac boardwalk\neighborhood\*.java -classpath %CLASSPATH% -d %TARGET_DIR% 
rem  %JAVA_HOME%\bin\javac boardwalk\common\*.java -classpath %CLASSPATH% -d %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\fileUploadService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac servlets\xlTemplateService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\AggregationService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac boardwalk\table\BoardwalkPeriodicColumnManager.java -classpath %CLASSPATH% -d %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\httpt_vb_Login.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

rem  %JAVA_HOME%\bin\javac servlets\xlExportChangesService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
 rem  %JAVA_HOME%\bin\javac servlets\xlImportChangesService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%
rem  %JAVA_HOME%\bin\javac servlets\xlService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

%JAVA_HOME%\bin\javac servlets\xlAdminService.java -classpath %CLASSPATH%  -d  %TARGET_DIR%

@ECHO ON