#!/bin/bash

export AWS_ACCESS_KEY_ID="AWS_ACCESS_KEY_ID"
export AWS_SECRET_ACCESS_KEY="AWS_SECRET_ACCESS_KEY"
export DB_USERNAME="unimusic"
export DB_PASSWORD="Nsg2*J3O5CfU"

cd unimusic-server
mvn clean install -DskipTests

cd ..
docker compose build
docker compose up -d
