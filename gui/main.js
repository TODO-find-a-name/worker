const { app, BrowserWindow, Tray, Menu, dialog } = require('electron');
const path = require('path');
const { exec } = require('child_process');

let tray = null;
let mainWindow = null;

const IMAGE_NAME = "docker.io/alessandrotalmi/worker:latest";
const CONTAINER_NAME = "worker";

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        }
    });

    mainWindow.loadURL(getUrl());

    mainWindow.on('closed', function () {
        mainWindow = null;
    });

    // Esegui il comando per aggiornare i pacchetti
    console.log("pull")
    exec('podman pull ' + IMAGE_NAME, (error, stdout, stderr) => {
        if (error) {
            console.log(error.message)
            dialog.showErrorBox('Errore', `Errore download immagine: ${error.message}`);
            return;
        }

        console.log(`Output download immagine:\n${stdout}`);

        exec('podman run --rm --name ' + CONTAINER_NAME + " " + IMAGE_NAME, (error, stdout, stderr) => {
            if (error) {
                dialog.showErrorBox('Errore', `Errore avvio container: ${error.message}`);
                return;
            }

            dialog.showMessageBox({
                type: 'info',
                message: 'Container avviato con successo!\n${stdout}',
            });
        });
    });
}

function getUrl(){
    if(process.env.NODE_ENV === 'development'){
        return 'http://localhost:9000';
    } else {
        // TODO this works on macos, check windows/linux
        //https://www.electron.build/configuration/contents.html#extrafiles
        return `file://${path.join(__dirname, '../../dist/index.html')}`;
    }
}

function createTray() {
    tray = new Tray(path.join(__dirname, 'icon.png'));
    const contextMenu = Menu.buildFromTemplate([
        {
            label: 'Mostra',
            click: () => {
                if (mainWindow) {
                    mainWindow.show();
                } else {
                    createWindow();
                }
            }
        },
        {
            label: 'Esci',
            click: () => {
                app.quit();
            }
        }
    ]);

    tray.setContextMenu(contextMenu);
    tray.setToolTip('Applicazione di esempio');
}

app.on('ready', () => {
    createWindow();
    createTray();
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('activate', () => {
    if (mainWindow === null) {
        createWindow();
    }
});
