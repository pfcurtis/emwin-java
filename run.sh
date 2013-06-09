#!/bin/bash

java -cp .:\
/opt/storm-0.8.2/storm-0.8.2.jar:\
/usr/share/maven-repo/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:\
/opt/storm-0.8.2/lib/libthrift7-0.7.0.jar:\
/opt/storm-0.8.2/lib/log4j-1.2.16.jar:\
/opt/storm-0.8.2/lib/commons-lang-2.5.jar:\
/opt/storm-0.8.2/lib/clojure-1.4.0.jar:\
/opt/storm-0.8.2/lib/slf4j-api-1.5.8.jar:\
/opt/storm-0.8.2/lib/slf4j-log4j12-1.5.8.jar:\
/opt/storm-0.8.2/lib/curator-client-1.0.1.jar:\
/opt/storm-0.8.2/lib/curator-framework-1.0.1.jar:\
/opt/storm-0.8.2/lib/commons-exec-1.1.jar:\
/opt/storm-0.8.2/lib/commons-io-1.4.jar:\
/opt/storm-0.8.2/lib/json-simple-1.1.jar:\
/opt/storm-0.8.2/lib/disruptor-2.10.1.jar:\
/opt/storm-0.8.2/lib/zookeeper-3.3.3.jar:\
/opt/storm-0.8.2/lib/snakeyaml-1.9.jar:\
/opt/storm-0.8.2/lib/guava-13.0.jar:\
/opt/storm-0.8.2/lib/kryo-2.17.jar:\
/opt/storm-0.8.2/lib/objenesis-1.2.jar:\
/opt/storm-0.8.2/lib/minlog-1.2.jar:\
/opt/storm-0.8.2/lib/carbonite-1.5.0.jar \
com/terrapin/emwin/storm/EMWINTopology
