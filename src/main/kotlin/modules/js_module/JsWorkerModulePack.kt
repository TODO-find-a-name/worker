package modules.js_module

import libs.common.module.WorkerModuleBuilder
import libs.common.module.WorkerModulePack

class JsWorkerModulePack : WorkerModulePack {

    override fun id(): String {
        return "JS"
    }

    override fun builder(): WorkerModuleBuilder {
        return JsWorkerModuleBuilder(id())
    }

}