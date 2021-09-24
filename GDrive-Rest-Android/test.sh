#!/bin/sh
 
for i in `find -name "*.java"`
do
  sed -i '/^$/d' $i
done
