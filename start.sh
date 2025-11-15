#!/bin/bash

echo "--- Iniciando unimusic-server ---"
JAR_ONE=$(find unimusic-server/target -maxdepth 1 -name "*.jar" ! -name "*.jar.original")

if [ -z "$JAR_ONE" ]; then
    exit 1
fi

java -jar $JAR_ONE > unimusic-server.log 2>&1 &
PID_ONE=$!
echo "$PID_ONE" > unimusic-server.pid
echo "$JAR_ONE PID: $PID_ONE"


echo ""
echo "--- Iniciando playlist-server ---"
JAR_TWO=$(find playlist-server/target -maxdepth 1 -name "*.jar" ! -name "*.jar.original")

if [ -z "$JAR_TWO" ]; then
    kill $PID_ONE
    rm unimusic-server.pid
    exit 1
fi

java -jar $JAR_TWO > playlist-server.log 2>&1 &
PID_TWO=$!
echo "$PID_TWO" > playlist-server.pid
echo "$JAR_TWO with PID: $PID_TWO"

echo ""
echo "-------------------------------------"
echo "  ✅ Microservices inicializados"
echo "-------------------------------------"