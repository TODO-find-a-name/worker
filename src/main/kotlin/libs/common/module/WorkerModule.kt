package libs.common.module

import libs.common.messages.PeerMsg

interface WorkerModule {

    fun addRecruiter(recruiterId: String)
    fun incomingPeerMsg(recruiterId: String, msg: PeerMsg)
    fun removeRecruiter(recruiterId: String)

}