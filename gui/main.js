const { app, BrowserWindow, Tray, Menu, dialog, ipcMain } = require('electron');
const path = require('path');
const { stopContainer, startContainer } = require('./container');
const { isDevelopment } = require("./utils");

let tray = null;
let mainWindow = null;

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 600,
        height: 300,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        },
        resizable: false
    });
    Menu.setApplicationMenu(null);

    mainWindow.loadURL(getUrl());

    mainWindow.on('closed', function () {
        mainWindow = null;
    });

    ipcMain.on('Start', async (event, data) => {
        startContainer(data[1], data[0], data[2]).then(
            () => {
                dialog.showMessageBox({type: 'info', message: `Container avviato con successo!`});
            },
            (err) => {
                dialog.showErrorBox('Errore', `Errore avvio container: ${err}`);
            }
        )
    });

}

function getUrl(){
    if(isDevelopment()){
        return 'http://localhost:9000';
    } else {
        // TODO this works on linux, check windows/macOs
        return `file://${path.join(__dirname, '../index.html')}`;
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
    stopContainer();
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('activate', () => {
    if (mainWindow === null) {
        createWindow();
    }
});
