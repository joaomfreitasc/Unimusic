#!/bin/bash

export AWS_ACCESS_KEY_ID="AWS_ACCESS_KEY_ID"
export AWS_SECRET_ACCESS_KEY="AWS_SECRET_ACCESS_KEY"
export DB_URL="DB_URL"
export DB_USERNAME="DB_USERNAME"
export DB_PASSWORD="DB_PASSWORD"
export S3_BUCKET_NAME="S3_BUCKET_NAME"

docker compose build
docker compose up -d
