package module

import messages.PeerMsg

interface WorkerModule {

    fun incomingPeerMsg(recruiterId: String, msg: PeerMsg)
    fun removeRecruiter(recruiterId: String)

}