const os = require('os');

function isDevelopment(){
    return process.env.NODE_ENV === 'development';
}

function buildCliCommand(scriptPath, args = []){
    const platform = os.platform();
    if(platform === "linux"){
        var res = "sh " + scriptPath;
        args.forEach(a => res += " " + a);
        return  res
    } else {
        throw Error("Building a cli command for os " + platform + " is not implemented");
    }
}

function getScriptPath(
    linuxDev, linuxProd
){
    const platform = os.platform();
    if(isDevelopment()){
        if(platform === "linux"){
            return linuxDev;
        } else {
            throw Error("A script path is not defined for os " + platform + " in dev environment");
        }
    } else {
        if(platform === "linux"){
            return linuxProd
        } else {
            throw Error("A script path is not defined for os " + platform + " in production environment");
        }
    }
}

module.exports = {isDevelopment, buildCliCommand, getScriptPath}