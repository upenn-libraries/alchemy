#!/bin/bash

jar="elements-tools-0.1.jar"
jarpath=$jar

if [ -f "target/$jar" ]
then
    jarpath="target/$jar"
fi

cmd=java
if [ -n "$JAVA_HOME" ]
then
    cmd=$JAVA_HOME/bin/java
fi

$cmd -jar $jarpath $@
