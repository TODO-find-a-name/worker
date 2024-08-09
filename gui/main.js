const { app, BrowserWindow, Tray, Menu, dialog } = require('electron');
const path = require('path');
const { stopContainer, startContainer } = require('./container');
const { isDevelopment } = require("./utils");

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

    startContainer("http://localhost:8080", "fatate");
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
