#!/bin/bash

jar="elements-tools-0.1-SNAPSHOT.jar"

if [ -f "target/elements-tools-0.1-SNAPSHOT.jar" ]
then
    jar="target/elements-tools-0.1-SNAPSHOT.jar"
fi

java -jar $jar $@
