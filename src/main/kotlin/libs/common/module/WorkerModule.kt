package libs.common.module

import libs.common.messages.PeerMsg
import java.util.concurrent.CompletableFuture

interface WorkerModule {

    fun id(): String
    fun addRecruiter(recruiterId: String): Boolean
    fun incomingPeerMsg(recruiterId: String, msg: PeerMsg): CompletableFuture<Unit>
    fun removeRecruiter(recruiterId: String)

}