#!/bin/bash
docker-compose down --volumes
docker system prune -a --volumes -f
docker volume prune -f
docker network prune -f