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

function startContainer(addr, organization, isLocalhost){
    return new Promise((resolve, reject) => {
        exec(buildCliCommand(getStartContainerScriptPath(), [addr, organization, isLocalhost]), (error) => {
            if (error) {
                reject(error.message)
            } else {
                resolve()
            }
        });
    });
}

function stopContainer(){
    return new Promise((resolve, reject) => {
        exec(buildCliCommand(getStopContainerScriptPath()), (error) => {
            if (error) {
                reject(error.message)
            } else {
                resolve()
            }
        });
    });
}

module.exports = {startContainer, stopContainer}