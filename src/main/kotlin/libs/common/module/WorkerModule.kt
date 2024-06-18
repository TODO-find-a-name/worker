package libs.common.module

import libs.common.messages.PeerMsg

interface WorkerModule {

    fun incomingPeerMsg(recruiterId: String, msg: PeerMsg)
    fun removeRecruiter(recruiterId: String)

}