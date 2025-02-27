#!/bin/bash

#########################################################################################
#                                                                                       #
#                                    Variables                                          #
#                                                                                       #
#########################################################################################

SCRIPT=$(realpath "$0")
ROOT_PATH="$(dirname "$SCRIPT")/.."
REPO_PATH="$ROOT_PATH"/repo
CSV_PATH="$ROOT_PATH"/config/alpha.csv
MODULE_PATH=""

#########################################################################################
#                                                                                       #
#                                    Reading CSV                                        #
#                                                                                       #
#########################################################################################

while IFS=: read -r groupId artifactId version
do
    if [ "$groupId" == "" ];then
	continue
    fi
    #Read the split words into an array based on space delimiter
    IFS=. read -a strarr <<< "$groupId"
    # Print each value of the array by using the loop
    path=''
    for val in "${strarr[@]}";
    do
	path="$path"/"$val"
    done
    MODULE_PATH=${MODULE_PATH}:"$REPO_PATH$path/$artifactId/$version/$artifactId-$version.jar"
done < $CSV_PATH

#########################################################################################
#                                                                                       #
#                                    Starting JVM                                       #
#                                                                                       #
#########################################################################################

java -Xrunjdwp:transport=dt_socket,address=7700,server=y,suspend=n \
    -Dspring.profiles.active=production \
    -Dorg.jboss.logging.provider=log4j \
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
    -Dlog4j.configurationFile="$ROOT_PATH/config/log4j2.xml" \
    -Dcom.techsenger.alpha.core.log.memory=false \
    -Dcom.techsenger.alpha.core.root.path="$ROOT_PATH" \
    -Dcom.techsenger.alpha.core.executor="alpha-executor:${project.version}" \
    -Dcom.techsenger.alpha.core.executor.alias="executor" \
    -Dcom.techsenger.alpha.core.script="installation.script" \
    -Djava.net.preferIPv4Stack=true \
    -Djava.io.tmpdir="$ROOT_PATH/temp" \
    -Djna.tmpdir="$ROOT_PATH/temp" \
    -Dfile.encoding=UTF-8 \
    --add-modules ALL-DEFAULT \
    --add-modules org.apache.logging.log4j,org.apache.logging.log4j.jul \
    --add-modules com.techsenger.alpha.repo.maven.resolver \
    --add-opens java.base/java.time=com.techsenger.alpha.core \
    --add-opens java.base/java.lang=com.techsenger.alpha.core \
    --module-path "$MODULE_PATH" \
    -m com.techsenger.alpha.core/com.techsenger.alpha.core.launcher.Launcher install

