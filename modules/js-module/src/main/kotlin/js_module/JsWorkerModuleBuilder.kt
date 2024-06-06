package js_module

import module.WorkerModule
import module.WorkerModuleBuilder

class JsWorkerModuleBuilder : WorkerModuleBuilder {

    override fun build(): WorkerModule {
        // TODO
        return JsWorkerModule()
    }
}