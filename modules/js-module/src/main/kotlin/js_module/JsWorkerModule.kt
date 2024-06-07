package js_module

import messages.PeerMsg
import module.WorkerModule

class JsWorkerModule : WorkerModule {
    override fun incomingPeerMsg(recruiterId: String, msg: PeerMsg) {
        TODO("Not yet implemented")
    }

    override fun removeRecruiter(recruiterId: String) {
        TODO("Not yet implemented")
    }

}