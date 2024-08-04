#!/bin/bash

command_exists() {
    command -v "$1" &> /dev/null
}

if ! command_exists podman; then
    echo "Installing podman..."

    sudo apt-get update
    sudo apt-get install -y podman
fi

IMAGE_NAME="docker.io/alessandrotalmi/worker:latest"
CONTAINER_NAME="worker"

echo "Retrieving latest docker image..."
podman pull $IMAGE_NAME

if podman ps -a --format '{{.Names}}' | grep -Eq "^${CONTAINER_NAME}$"; then
    echo "Removing existing container named ${CONTAINER_NAME}..."
    podman rm -f $CONTAINER_NAME
fi

echo "Executing the container..."
podman run --rm --name $CONTAINER_NAME $IMAGE_NAME

