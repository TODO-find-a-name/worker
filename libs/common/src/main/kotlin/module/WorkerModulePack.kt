package module

interface WorkerModulePack {

    fun id(): String
    fun builder(): WorkerModuleBuilder

}