package libs.core.worker

import libs.common.ViewCallbacks
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.SendPeerMsgToRecruiterEvent
import libs.core.worker.utils.JsonParser
import libs.core.worker.utils.Lock
import libs.core.worker.utils.Logger
import libs.core.worker.utils.WorkerSettings
import libs.common.module.WorkerModule
import libs.common.module.WorkerModulePack

class Repository(
    val settings: WorkerSettings, modulePacks: List<WorkerModulePack>, val viewCallbacks: ViewCallbacks
) {

    var isRunning: Boolean = false

    val logger: Logger = Logger(settings)
    val lock: Lock = Lock(logger)
    val modules = createModules(modulePacks)
    val socket = Socket(this)
    val parser = JsonParser()
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

    private fun createModules(modulePacks: List<WorkerModulePack>): Map<String, WorkerModule> {
        return modulePacks.associateBy(
            {pack -> pack.id()},
            {pack -> pack.builder()
                .sendPeerMsg{ recruiterId, msg ->
                    SendPeerMsgToRecruiterEvent(this, recruiterId, pack.id(), msg).handle()
                }
                .onCriticalError{ recruiterId ->
                    RemoveRecruiterEvent(
                        this,
                        recruiterId,
                        "Module " + pack.id() + " had a critical error with a Recruiter",
                    ).handle()
                }
                .viewCallbacks(viewCallbacks)
                .build()
            }
        )
    }

    fun removeRecruiter(id: String, log: String) {
        logger.error("Removing Recruiter $id: $log")
        recruiters.remove(id)?.disconnect()
        viewCallbacks.onRecruiterDisconnected(id)
    }

    fun removeRecruiter(recruiter: Recruiter, log: String){
        val id = recruiter.recruiterId
        logger.error("Removing Recruiter ${id}: $log")
        recruiters.remove(recruiter.recruiterId)
        recruiter.disconnect()
        viewCallbacks.onRecruiterDisconnected(id)
    }

}