#!/bin/bash

STORM_PATH=/opt/storm-0.8.2

java -cp .:\
/usr/share/maven-repo/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:\
${STORM_PATH}/storm-0.8.2.jar:\
${STORM_PATH}/lib/libthrift7-0.7.0.jar:\
${STORM_PATH}/lib/log4j-1.2.16.jar:\
${STORM_PATH}/lib/commons-lang-2.5.jar:\
${STORM_PATH}/lib/clojure-1.4.0.jar:\
${STORM_PATH}/lib/slf4j-api-1.5.8.jar:\
${STORM_PATH}/lib/slf4j-log4j12-1.5.8.jar:\
${STORM_PATH}/lib/curator-client-1.0.1.jar:\
${STORM_PATH}/lib/curator-framework-1.0.1.jar:\
${STORM_PATH}/lib/commons-exec-1.1.jar:\
${STORM_PATH}/lib/commons-io-1.4.jar:\
${STORM_PATH}/lib/json-simple-1.1.jar:\
${STORM_PATH}/lib/disruptor-2.10.1.jar:\
${STORM_PATH}/lib/zookeeper-3.3.3.jar:\
${STORM_PATH}/lib/snakeyaml-1.9.jar:\
${STORM_PATH}/lib/guava-13.0.jar:\
${STORM_PATH}/lib/kryo-2.17.jar:\
${STORM_PATH}/lib/objenesis-1.2.jar:\
${STORM_PATH}/lib/minlog-1.2.jar:\
${STORM_PATH}/lib/carbonite-1.5.0.jar \
com/terrapin/emwin/storm/EMWINTopology
