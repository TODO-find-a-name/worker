package org.example

import com.todo.todo.ViewCallbacks
import com.todo.todo.worker.Worker
import com.todo.todo.worker.utils.LoggerLvl
import com.todo.todo.worker.utils.WorkerSettings
import js_module.JsWorkerModulePack

fun main() {
    Worker(
        WorkerSettings(
            "http://localhost:8080",
            "fatate",
            loggingLvl = LoggerLvl.LOW
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