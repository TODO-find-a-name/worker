package modules.js_module

import libs.common.ViewCallbacks
import libs.common.messages.PeerMsg
import libs.common.module.WorkerModule
import libs.common.module.WorkerModuleBuilder

class JsWorkerModuleBuilder(private val id: String) : WorkerModuleBuilder {

    private var onCriticalErrorCallback: ((recruiterId: String) -> Unit)? = null
    private var sendPeerMsgCallback: ((recruiterId: String, msg: PeerMsg) -> Unit)? = null
    private var viewCallbacks: ViewCallbacks? = null

    override fun onCriticalError(callback: (recruiterId: String) -> Unit): JsWorkerModuleBuilder {
        this.onCriticalErrorCallback = callback
        return this
    }

    override fun sendPeerMsg(callback: (recruiterId: String, msg: PeerMsg) -> Unit): JsWorkerModuleBuilder {
        this.sendPeerMsgCallback = callback
        return this
    }

    override fun viewCallbacks(viewCallbacks: ViewCallbacks): WorkerModuleBuilder {
        this.viewCallbacks = viewCallbacks
        return this
    }

    override fun build(): WorkerModule {
        return JsWorkerModule(
            id, this.onCriticalErrorCallback!!, this.sendPeerMsgCallback!!, this.viewCallbacks!!
        )
    }
}