package module_packs

import libs.common.module.WorkerModulePack
import libs.core.worker.Repository

class NodeJsWorkerModulePack : WorkerModulePack {

    override fun startModule(repository: Repository) {
        ProcessBuilder(listOf(repository.settings.nodeModuleStartupScriptPath)).start()
    }

    override fun id(): String {
        return "NODE_JS"
    }

}