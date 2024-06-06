package app

import kotlinx.coroutines.*
import libs.common.ViewCallbacks
import libs.core.worker.Worker
import modules.js_module.JsWorkerModulePack
import java.util.concurrent.CompletableFuture

fun main() {
    Worker(
        WorkerSettingsByEnvVariables.create(),
        listOf(JsWorkerModulePack()),
        TerminalViewCallbacks(false)
    ).connect()
}

private class TerminalViewCallbacks(private val print: Boolean) : ViewCallbacks {

    @OptIn(DelicateCoroutinesApi::class)
    private fun future(s: String): CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()
        GlobalScope.launch {
            if(print){
                println("\nView: $s\n")
            }
            future.complete(Unit)
        }
        return future
    }

    override fun onBrokerConnectionEstablished(): CompletableFuture<Unit> {
        return future("Connection with Broker established")
    }

    override fun onBrokerConnectionError(): CompletableFuture<Unit> {
        return future("Broker connection error")
    }

    override fun onBrokerDisconnection(): CompletableFuture<Unit> {
        return future("Broker disconnection")
    }

    override fun onRecruiterConnected(id: String): CompletableFuture<Unit> {
        return future("Recruiter $id connected")
    }

    override fun onRecruiterDisconnected(id: String): CompletableFuture<Unit> {
        return future("Recruiter $id disconnected")
    }

    override fun onJobStarted(recruiterId: String, jobId: String): CompletableFuture<Unit> {
        return future("Recruiter $recruiterId started job $jobId")
    }

    override fun onJobEnded(recruiterId: String, jobId: String): CompletableFuture<Unit> {
        return future("Recruiter $recruiterId ended job $jobId")
    }

}