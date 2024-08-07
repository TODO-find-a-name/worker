const { app, BrowserWindow, Tray, Menu } = require('electron');
const path = require('path');

let tray = null;
let mainWindow = null;

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
    exec('sudo apt update', (error, stdout, stderr) => {
        if (error) {
            console.error(`Errore nell'aggiornamento dei pacchetti: ${error.message}`);
            dialog.showErrorBox('Errore', `Errore nell'aggiornamento dei pacchetti: ${error.message}`);
            return;
        }

        console.log(`Output aggiornamento pacchetti:\n${stdout}`);

        // Esegui il comando per installare Podman
        exec('sudo apt install podman -y', (error, stdout, stderr) => {
            if (error) {
                console.error(`Errore nell'installazione di Podman: ${error.message}`);
                dialog.showErrorBox('Errore', `Errore nell'installazione di Podman: ${error.message}`);
                return;
            }

            console.log(`Output installazione Podman:\n${stdout}`);
            dialog.showMessageBox({
                type: 'info',
                message: 'Podman è stato installato con successo!',
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
