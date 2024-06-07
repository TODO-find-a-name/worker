package module

import messages.PeerMsg

interface WorkerModuleBuilder {

    fun onCriticalError(callback: (recruiterId: String) -> Any)
    fun sendPeerMsg(callback: (recruiterId: String, msg: PeerMsg) -> Any)
    fun build(): WorkerModule

}