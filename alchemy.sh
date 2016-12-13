#!/bin/bash
#
# script to run alchemy CLI, from either repo project directory
# in which the app was built, or the distributed .zip/.tar.gz package

platform="$(uname)"

if [ "$platform" == "Darwin" ]
then
    script="$(python -c 'import os,sys;print(os.path.realpath(sys.argv[1]))' ${BASH_SOURCE[0]})"
else
    script="$(readlink -f ${BASH_SOURCE[0]})"
fi

script_dir="$(dirname $script)"

lib="$script_dir/lib"

if [ -d "$script_dir/target" ]
then
    lib="$script_dir/target"
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
