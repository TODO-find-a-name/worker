package app

import libs.core.ViewCallbacks
import libs.core.worker.Worker
import libs.core.worker.utils.LoggerLvl
import libs.core.worker.utils.WorkerSettings
import modules.js_module.JsWorkerModulePack

fun main() {
    Worker(
        WorkerSettings(
            "http://localhost:8080",
            "fatate",
            loggingLvl = LoggerLvl.COMPLETE
        ),
        listOf(JsWorkerModulePack()),
        TerminalViewCallbacks()
    ).connect()
}

private class TerminalViewCallbacks : ViewCallbacks {

    override fun onBrokerConnectionEstablished() {
        // println("Connection with Broker established")
    }

    override fun onBrokerConnectionError() {
        // println("Error while trying to connect with Broker")
    }

    override fun onBrokerDisconnection() {
        // println("Broker disconnected")
    }

    override fun onRecruiterConnected(id: String) {
        // println("Recruiter $id connected")
    }

    override fun onRecruiterDisconnected(id: String) {
        // println("Recruiter $id disconnected")
    }

    override fun onJobStarted(recruiterId: String, jobId: String) {
        // println("Recruiter $recruiterId started a new job $jobId")
    }

    override fun onJobEnded(recruiterId: String, jobId: String) {
        // println("Recruiter $recruiterId concluded a new job $jobId")
    }

}