package module

import messages.PeerMsg

interface WorkerModuleBuilder {

    fun onCriticalError(callback: (recruiterId: String) -> Unit): WorkerModuleBuilder
    fun sendPeerMsg(callback: (recruiterId: String, msg: PeerMsg) -> Unit): WorkerModuleBuilder
    fun build(): WorkerModule

}