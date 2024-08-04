#!/bin/bash

if ! command -v podman &> /dev/null
then
    echo "Podman non è installato. Installazione in corso..."

    sudo apt-get update
    sudo apt-get install -y podman
fi

podman run --rm alessandrotalmi/worker