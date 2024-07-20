package libs.common.module

import libs.core.worker.Repository

interface WorkerModulePack {

    fun startModule(repository: Repository): Unit

    fun id(): String

    fun builder(): WorkerModuleBuilder {
        return WorkerModuleBuilder(id())
    }

}