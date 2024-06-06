package libs.common

import java.util.concurrent.CompletableFuture

interface ViewCallbacks {

    fun onBrokerConnectionEstablished(): CompletableFuture<Unit>
    fun onBrokerConnectionError(): CompletableFuture<Unit>
    fun onBrokerDisconnection(): CompletableFuture<Unit>
    fun onRecruiterConnected(id: String): CompletableFuture<Unit>
    fun onRecruiterDisconnected(id: String): CompletableFuture<Unit>
    fun onJobStarted(recruiterId: String, jobId: String): CompletableFuture<Unit>
    fun onJobEnded(recruiterId: String, jobId: String): CompletableFuture<Unit>

}