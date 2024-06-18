package libs.core.worker

import libs.core.worker.recruiter.Recruiter
import libs.core.worker.socket.Socket
import libs.core.ViewCallbacks
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.SendPeerMsgToRecruiterEvent
import libs.core.worker.utils.JsonParser
import libs.core.worker.utils.Lock
import libs.core.worker.utils.Logger
import libs.core.worker.utils.WorkerSettings
import libs.common.module.WorkerModule
import libs.common.module.WorkerModulePack

class SharedRepository(
    val settings: WorkerSettings, modulePacks: List<WorkerModulePack>, val viewCallbacks: ViewCallbacks
) {

    var isRunning: Boolean = false

    val lock: Lock = Lock()
    val modules = createModules(modulePacks)
    val logger: Logger = Logger(settings)
    val socket = Socket(this)
    val parser = JsonParser()
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

    private fun createModules(modulePacks: List<WorkerModulePack>): Map<String, WorkerModule> {
        return modulePacks.associateBy(
            {pack -> pack.id()},
            {pack -> pack.builder()
                .sendPeerMsg{ recruiterId, msg ->
                    SendPeerMsgToRecruiterEvent(this, recruiterId, msg).handle()
                }
                .onCriticalError{ recruiterId ->
                    RemoveRecruiterEvent(this, recruiterId).handle()
                }
                .build()
            }
        )
    }

    fun removeRecruiter(id: String) {
        recruiters.remove(id)?.let {
            it.disconnect()
            it.timeoutTimer.cancel()
            it.pendingMessages.forEach{ pair -> pair.value.cancelTimeout() }
            it.pendingMessages.clear()
        }
    }

}