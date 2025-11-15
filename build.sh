#!/bin/bash

set -e

echo "--- Building unimusic-server FatJar---"
cd unimusic-server
mvn install -DskipTests
cd ..

echo ""
echo "--- Building playlist-server FatJar---"
cd playlist-server
mvn install -DskipTests
cd ..

echo ""
echo "-------------------------------------"
echo " Microservices criados com sucesso! -> Fat Jars criados"
echo "-------------------------------------"