package js_module

import module.WorkerModuleBuilder
import module.WorkerModulePack

class JsWorkerModulePack : WorkerModulePack {
    override fun id(): String {
        return "JS"
    }

    override fun builder(): WorkerModuleBuilder {
        return JsWorkerModuleBuilder()
    }
}