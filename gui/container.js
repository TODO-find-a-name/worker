const { dialog } = require('electron');
const path = require('path');
const { exec } = require('child_process');

const { getScriptPath, buildCliCommand } = require("./utils");

function getStartContainerScriptPath(){
    return getScriptPath(
        "./scripts/start-container-linux.sh",
        `${path.join(__dirname, '../scripts/start-container-linux.sh')}`
    )
}

function getStopContainerScriptPath(){
    return getScriptPath(
        "./scripts/stop-container-linux.sh",
        `${path.join(__dirname, '../scripts/stop-container-linux.sh')}`
    )
}

function startContainer(addr, organization){    
    exec(buildCliCommand(getStartContainerScriptPath(), [addr, organization]), (error) => {
        if (error) {
            dialog.showErrorBox('Errore', `Errore avvio container: ${error.message}`);
        } else {
            dialog.showMessageBox({
                type: 'info',
                message: `Container avviato con successo!`,
            });
        }
    });
}

function stopContainer(){
    exec(buildCliCommand(getStopContainerScriptPath()));
}

module.exports = {startContainer, stopContainer}