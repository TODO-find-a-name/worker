package app

import app.StringEnvVariableReader.Companion.readOptionalString
import libs.common.module.WorkerModulePack
import module_packs.NodeJsWorkerModulePack

const val MODULES_ENV_VAR_NAME: String = "MODULES"

class LoadModulesByEnvVariables {
    companion object {
        fun load (): List<WorkerModulePack> {
            val allModules = getAllModules()

            val variable = readOptionalString(MODULES_ENV_VAR_NAME)
            if(variable.isEmpty) {
                return allModules
            }

            val inputModules = variable.get().split(",").map { it.trim() }
            if(inputModules.isEmpty()){
                return allModules
            }

            val mappedModules = allModules.associateBy { it.id() }

            return inputModules.map {
                mappedModules[it] ?: throw IllegalArgumentException("Input module '$it' does not exist")
            }
        }

        private fun getAllModules(): List<WorkerModulePack> {
            // Expand on new module creation
            return listOf(
                NodeJsWorkerModulePack()
            )
        }
    }
}