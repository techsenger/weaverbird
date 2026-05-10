@echo off

::#########################################################################################
::#                                                                                       #
::#                                     Variables                                         #
::#                                                                                       #
::#########################################################################################

set ROOT_PATH=%~dp0..
set REPO_PATH=%ROOT_PATH%\repo
setLocal EnableDelayedExpansion
${modulepath}

::#########################################################################################
::#                                                                                       #
::#                                     Starting JVM                                      #
::#                                                                                       #
::#########################################################################################

java ^
    -Dcom.techsenger.weaverbird.core.root.path=%ROOT_PATH% ^
    -Dlog4j.configurationFile=%ROOT_PATH%\config\log4j2.xml ^
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager ^
    -Dorg.jboss.logging.provider=log4j ^
    -Djava.io.tmpdir=%ROOT_PATH%\temp ^
    -Dfile.encoding=UTF-8 ^${jvmArgs}
    --add-modules ALL-DEFAULT ^
    --add-modules org.apache.logging.log4j,org.apache.logging.log4j.jul ^
    --add-opens java.base/java.time=com.techsenger.weaverbird.core ^
    --add-opens java.base/java.lang=com.techsenger.weaverbird.core ^
    --enable-native-access=com.techsenger.weaverbird.core ^
    --module-path !MODULE_PATH! ^
    -m ${mainClass}
