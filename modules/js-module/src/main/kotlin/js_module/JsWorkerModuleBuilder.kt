package js_module

import messages.PeerMsg
import module.WorkerModule
import module.WorkerModuleBuilder

class JsWorkerModuleBuilder : WorkerModuleBuilder {
    override fun onCriticalError(callback: (recruiterId: String) -> Any) {
        TODO("Not yet implemented")
    }

    override fun sendPeerMsg(callback: (recruiterId: String, msg: PeerMsg) -> Any) {
        TODO("Not yet implemented")
    }

    override fun build(): WorkerModule {
        // TODO
        return JsWorkerModule()
    }
}