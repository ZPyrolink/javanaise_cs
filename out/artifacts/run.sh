#!/bin/bash

java -jar Coord.jar &

sleep 1
echo Server started at pid: $!

for _ in {1..100} ; do
  java -jar IRC.jar &
  sleep 0.1
done