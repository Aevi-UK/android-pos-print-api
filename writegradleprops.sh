#!/bin/bash

GRADLE_PROPERTIES_FILE="gradle.properties"
OUTPUT_PROPS=${GRADLE_PROPERTIES_FILE}

echo "Create gradle properties file"
echo "org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8" > ${OUTPUT_PROPS}
echo "bintrayUser=$BINTRAY_USER" > ${OUTPUT_PROPS}
echo "bintrayKey=$BINTRAY_KEY" >> ${OUTPUT_PROPS}
