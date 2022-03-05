#!/bin/sh

./wait && java -jar /usr/app/app.jar -d $datasources -i $datastoreHost -p $datastorePort -f $file -q $pid