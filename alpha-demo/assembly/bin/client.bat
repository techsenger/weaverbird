@echo off

::#########################################################################################
::#                                                                                       #
::#                                     Variables                                         #
::#                                                                                       #
::#########################################################################################

set ROOT_PATH=%~dp0..
set REPO_PATH=%ROOT_PATH%\repo
set CSV_PATH=%ROOT_PATH%\config\alpha.csv
set "MODULE_PATH="

::#########################################################################################
::#                                                                                       #
::#                                     Reading CSV                                       #
::#                                                                                       #
::#########################################################################################

setLocal EnableDelayedExpansion
for /f "usebackq tokens=1-3 delims=:" %%a in ("%CSV_PATH%") do (
    set g=%%a
    set "p="
    for %%A in (!g:.^= !) do (
	set "p=!p!\%%A"
    )
    set "MODULE_PATH=!MODULE_PATH!%REPO_PATH%!p!\%%b\%%c\%%b-%%c.jar;"
)

::#########################################################################################
::#                                                                                       #
::#                                     Starting JVM                                      #
::#                                                                                       #
::#########################################################################################

java -Xrunjdwp:transport=dt_socket,address=7800,server=y,suspend=n ^
    -Dspring.profiles.active=production ^
    -Dorg.jboss.logging.provider=log4j ^
    -Dlog4j.configurationFile=%ROOT_PATH%\config\log4j2.xml ^
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager ^
    -Dcom.techsenger.alpha.core.mode="client" ^
    -Dcom.techsenger.alpha.core.log.memory=false ^
    -Dcom.techsenger.alpha.core.root.path=%ROOT_PATH% ^
    -Dcom.techsenger.alpha.core.executor="alpha-executor:${project.version}" ^
    -Dcom.techsenger.alpha.core.executor.alias="executor" ^
    -Dcom.techsenger.alpha.core.script="client-boot.script" ^
    -Djava.net.preferIPv4Stack=true ^
    -Djava.io.tmpdir=%ROOT_PATH%\temp ^
    -Djna.tmpdir=%ROOT_PATH%\temp ^
    -Dfile.encoding=UTF-8 ^
    -Djavax.net.ssl.keyStore=%ROOT_PATH%\bin\${client.keystore.file} ^
    -Djavax.net.ssl.trustStore=%ROOT_PATH%\bin\${client.truststore.file} ^
    -Djavax.net.ssl.keyStorePassword="${client.keystore.password}" ^
    -Djavax.net.ssl.trustStorePassword="${client.truststore.password}" ^
    --add-modules ALL-DEFAULT ^
    --add-modules org.apache.logging.log4j,org.apache.logging.log4j.jul ^
    --add-modules com.techsenger.alpha.repo.maven.resolver ^
    --add-opens java.base/java.time=com.techsenger.alpha.core ^
    --add-opens java.base/java.lang=com.techsenger.alpha.core ^
    --module-path !MODULE_PATH! ^
    -m com.techsenger.alpha.core/com.techsenger.alpha.core.launcher.Launcher

