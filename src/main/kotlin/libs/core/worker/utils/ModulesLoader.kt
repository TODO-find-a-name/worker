package libs.core.worker.utils

import libs.common.module.WorkerModule
import libs.common.module.WorkerModulePack
import libs.core.worker.Repository
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.SendPeerMsgToRecruiterEvent

class ModulesLoader {
    companion object {
        fun loadModules(modulePacks: List<WorkerModulePack>, repository: Repository): Map<String, WorkerModule> {
            return modulePacks.associateBy({ it.id() }, { loadModule(it, repository) })
        }

        private fun loadModule(pack: WorkerModulePack, repository: Repository): WorkerModule {
            val module = pack.builder()
                .sendPeerMsg{ recruiterId, msg ->
                    SendPeerMsgToRecruiterEvent(repository, recruiterId, pack.id(), msg).handle()
                }
                .onCriticalError{ recruiterId ->
                    RemoveRecruiterEvent(
                        repository,
                        recruiterId,
                        "Module " + pack.id() + " had a critical error with Recruiter " + recruiterId,
                    ).handle()
                }
                .viewCallbacks(repository.viewCallbacks)
                .build()
            repository.logger.log(LoggerLvl.LOW, "Module ${pack.id()} loaded")
            return module
        }
    }
}