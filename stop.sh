#!/bin/bash

echo "--- Parando services ---"

if [ -f "unimusic-server.pid" ]; then
    PID_ONE=$(cat unimusic-server.pid)
    echo "Parando unimusic-server (PID: $PID_ONE)..."
    kill $PID_ONE
    rm unimusic-server.pid
    rm unimusic-server.log
else
    echo "unimusic-server PID nao encontrado"
fi

echo ""

if [ -f "playlist-server.pid" ]; then
    PID_TWO=$(cat playlist-server.pid)
    echo "Parando playlist-server (PID: $PID_TWO)..."
    kill $PID_TWO
    rm playlist-server.pid
    rm playlist-server.log
else
    echo "playlist-server PID nao encontrado."
fi
