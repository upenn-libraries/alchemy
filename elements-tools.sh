#!/bin/bash
#
# script to run elements-tools CLI, from either repo project directory
# in which the app was built, or the distributed .zip/.tar.gz package

lib="lib"

if [ -d "target" ]
then
    lib="./target"
    if compgen -G "$lib/*-all.jar" > /dev/null; then
        uberjar="$lib/*-all.jar"
    fi
fi

cmd="java"
if [ -n "$JAVA_HOME" ]
then
    cmd=$JAVA_HOME/bin/java
fi

if [ -n "$uberjar" ]; then
    $cmd -jar $uberjar $@
else
    $cmd -classpath "$lib/*" edu.upenn.library.elements.App $@
fi
