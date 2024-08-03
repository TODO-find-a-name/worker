#!/bin/bash

CONTAINER_NAME="worker"
IMAGE_NAME="alessandrotalmi/worker:latest"

install_brew() {
  if ! command -v brew &> /dev/null; then
      echo "Homebrew non è installato. Installazione in corso..."
      yes '' | /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
      eval "$(/opt/homebrew/bin/brew shellenv)"
  fi
}

install_podman() {
    if ! command -v podman &> /dev/null; then
        echo "Podman non è installato. Installazione in corso..."
        brew install podman
    else
        echo "Podman è già installato."
    fi
}

start_podman_machine() {
    echo "Verifica dello stato della macchina virtuale Podman..."
    if podman machine list | grep -q "Running"; then
        echo "La macchina virtuale Podman è già in esecuzione."
    else
        echo "Inizializzazione della macchina virtuale Podman..."
        podman machine init
        echo "Avvio della macchina virtuale Podman..."
        podman machine start
    fi
}


update_image() {
    echo "Verifica aggiornamenti per l'immagine $IMAGE_NAME..."

    # Pull the latest image
    podman pull $IMAGE_NAME

    # Get the ID of the latest image
    remote_image_id=$(podman inspect --format '{{.Id}}' $IMAGE_NAME)

    # Check if the image is already pulled locally
    local_image_id=$(podman images --noheading --format '{{.Id}}' $IMAGE_NAME)

    if [ "$remote_image_id" != "$local_image_id" ]; then
        echo "È disponibile una nuova versione dell'immagine. Scaricamento in corso..."
        podman pull $IMAGE_NAME
    else
        echo "L'immagine locale è già aggiornata."
    fi
}

run_container() {
    echo "Eseguendo il container..."
    if podman ps -a --format '{{.Names}}' | grep -q $CONTAINER_NAME; then
        echo "Un container con il nome $CONTAINER_NAME esiste già. Rimozione in corso..."
        podman rm -f $CONTAINER_NAME
    fi
    podman run --rm --name $CONTAINER_NAME $IMAGE_NAME
}

install_brew
install_podman
start_podman_machine
update_image
run_container