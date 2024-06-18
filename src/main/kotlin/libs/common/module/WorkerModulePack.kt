package libs.common.module

interface WorkerModulePack {

    fun id(): String
    fun builder(): WorkerModuleBuilder

}