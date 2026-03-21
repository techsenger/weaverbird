#!/bin/bash
set -e

#########################################################################################
#                                                                                       #
#                                    Variables                                          #
#                                                                                       #
#########################################################################################

SCRIPT=$(realpath "$0")
ROOT_PATH="$(realpath "$(dirname "$SCRIPT")/..")"
REPO_PATH="$ROOT_PATH"/repo
${modulepath}

#########################################################################################
#                                                                                       #
#                                    Starting JVM                                       #
#                                                                                       #
#########################################################################################

java -agentlib:jdwp=transport=dt_socket,address=7700,server=y,suspend=n \
    -Dorg.jboss.logging.provider=log4j \
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
    -Dlog4j.configurationFile="$ROOT_PATH/config/log4j2.xml" \
    -Dcom.techsenger.alpha.core.root.path="$ROOT_PATH" \
    -Djava.net.preferIPv4Stack=true \
    -Djava.io.tmpdir="$ROOT_PATH/temp" \
    -Djna.tmpdir="$ROOT_PATH/temp" \
    -Dfile.encoding=UTF-8 \
    --add-modules ALL-DEFAULT \
    --add-modules org.apache.logging.log4j,org.apache.logging.log4j.jul \
    --add-opens java.base/java.time=com.techsenger.alpha.core \
    --add-opens java.base/java.lang=com.techsenger.alpha.core \
    --module-path "$MODULE_PATH" \
    -m ${mainClass}
