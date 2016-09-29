#!/bin/bash

jar="elements-tools-0.1.jar"
jarpath=$jar

if [ -f "target/$jar" ]
then
    jarpath="target/$jar"
fi

java -jar $jarpath $@
