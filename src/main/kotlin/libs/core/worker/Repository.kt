package libs.core.worker

import libs.common.ViewCallbacks
import libs.common.module.WorkerModulePack
import libs.core.worker.utils.*

class Repository(
    val settings: WorkerSettings, modulePacks: List<WorkerModulePack>, val viewCallbacks: ViewCallbacks
) {

    var isRunning: Boolean = false

    val logger: Logger = Logger(settings)
    val lock: Lock = Lock(logger)
    val parser = JsonParser()
    val modules = ModulesLoader().loadModules(modulePacks, this)
    val socket = Socket(this)
    val recruiters: MutableMap<String, Recruiter> = mutableMapOf()

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