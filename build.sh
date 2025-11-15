#!/bin/bash

set -e

echo "--- Criando unimusic-server FatJar---"
cd unimusic-server
mvn install -DskipTests
cd ..

echo ""
echo "--- Criando playlist-server FatJar---"
cd playlist-server
mvn install -DskipTests
cd ..

echo ""
echo "-------------------------------------"
echo " Microservices Fat Jars criados!"
echo "-------------------------------------"