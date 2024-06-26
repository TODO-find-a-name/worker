package libs.common.module

import libs.common.ViewCallbacks
import libs.common.messages.PeerMsg

interface WorkerModuleBuilder {

    fun onCriticalError(callback: (recruiterId: String) -> Unit): WorkerModuleBuilder
    fun sendPeerMsg(callback: (recruiterId: String, msg: PeerMsg) -> Unit): WorkerModuleBuilder
    fun viewCallbacks(viewCallbacks: ViewCallbacks): WorkerModuleBuilder
    fun build(): WorkerModule

}