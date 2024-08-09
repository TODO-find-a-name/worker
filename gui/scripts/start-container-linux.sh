#!/bin/bash

CONTAINER_NAME="worker"
IMAGE_NAME="docker.io/alessandrotalmi/worker:latest"

if [ $# -ne 3 ]; then
    echo "Something is wrong with parameters: $0 <BROKER_ADDR> <ORGANIZATION> <IS_LOCAL_HOST>"
    exit 1
fi

BROKER_ADDR=$1
ORGANIZATION=$2
IS_LOCAL_HOST=$3

if podman ps | grep -q $CONTAINER_NAME; then
    echo "Container '$CONTAINER_NAME' is already being executed, stopping it..."
    podman stop $CONTAINER_NAME
    echo "Container '$CONTAINER_NAME' stopped."
fi

echo "Starting container '$CONTAINER_NAME'..."
if $IS_LOCAL_HOST; then
    podman run -d --rm --name $CONTAINER_NAME --network host \
        -e BROKER_ADDR=$BROKER_ADDR\
        -e ORGANIZATION=$ORGANIZATION \
        $IMAGE_NAME
else
    podman run -d --rm --name $CONTAINER_NAME \
        -e BROKER_ADDR=$BROKER_ADDR\
        -e ORGANIZATION=$ORGANIZATION \
        $IMAGE_NAME
fi

if podman ps | grep -q $CONTAINER_NAME; then
    echo "Container '$CONTAINER_NAME' started correctly."
    exit 0
else
    echo "Errore: Container '$CONTAINER_NAME' did not start correctly."
    exit 1
fi
