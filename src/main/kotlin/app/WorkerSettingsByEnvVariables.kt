package app

import app.StringEnvVariableReader.Companion.readOptionalString
import libs.core.worker.utils.DEFAULT_IS_GUI_ENABLED
import libs.core.worker.utils.DEFAULT_LOAD_MODULES_MANUALLY
import libs.core.worker.utils.DEFAULT_LOGGING_LEVEL
import libs.core.worker.utils.DEFAULT_MODULE_FORWARDING_TIMEOUT_SECONDS
import libs.core.worker.utils.DEFAULT_MODULE_LOADING_TIMEOUT_MS
import libs.core.worker.utils.DEFAULT_NODE_MODULE_STARTUP_SCRIPT_PATH
import libs.core.worker.utils.DEFAULT_P2P_MSG_TIMEOUT_MS
import libs.core.worker.utils.DEFAULT_P2P_PAYLOAD_SIZE_BYTES
import libs.core.worker.utils.DEFAULT_RECRUITMENT_TIMEOUT_MS
import libs.core.worker.utils.LoggerLvl
import libs.core.worker.utils.WorkerSettings

const val ENV_BROKER_ADDR = "BROKER_ADDR"
const val ENV_WORKER_ID = "WORKER_ID"
const val ENV_KEY = "KEY"
const val ENV_LOGGING_LVL = "LOGGING_LVL"
const val ENV_IS_GUI_ENABLED = "IS_GUI_ENABLED"
const val ENV_MODULE_LOADING_TIMEOUT_MS = "MODULE_LOADING_TIMEOUT_MS"
const val ENV_P2P_PAYLOAD_SIZE_BYTES = "P2P_PAYLOAD_SIZE_BYTES"
const val ENV_RECRUITMENT_TIMEOUT_MS = "RECRUITMENT_TIMEOUT_MS"
const val ENV_LOAD_MODULES_MANUALLY = "LOAD_MODULES_MANUALLY"
const val ENV_P2P_MSG_TIMEOUT_MS = "P2P_MSG_TIMEOUT_MS"
const val ENV_MODULE_FORWARDING_TIMEOUT_SECONDS = "MODULE_FORWARDING_TIMEOUT_SECONDS"

const val NODE_MODULE_SCRIPT_PATH = "NODE_MODULE_SCRIPT_PATH"

class WorkerSettingsByEnvVariables {
    companion object {
        fun create(): WorkerSettings {
            return WorkerSettings(
                readMandatoryString(ENV_BROKER_ADDR),
                readMandatoryString(ENV_WORKER_ID),
                readMandatoryString(ENV_KEY),
                getLoggingLvl(),
                readBooleanOrElse(ENV_IS_GUI_ENABLED, DEFAULT_IS_GUI_ENABLED),
                readGreaterThanZeroOrElse(ENV_MODULE_LOADING_TIMEOUT_MS, DEFAULT_MODULE_LOADING_TIMEOUT_MS),
                readGreaterThanZeroOrElse(ENV_P2P_PAYLOAD_SIZE_BYTES, DEFAULT_P2P_PAYLOAD_SIZE_BYTES),
                readGreaterThanZeroOrElse(ENV_RECRUITMENT_TIMEOUT_MS, DEFAULT_RECRUITMENT_TIMEOUT_MS),
                readGreaterThanZeroOrElse(ENV_P2P_MSG_TIMEOUT_MS, DEFAULT_P2P_MSG_TIMEOUT_MS),
                readIntOrElse(ENV_MODULE_FORWARDING_TIMEOUT_SECONDS, DEFAULT_MODULE_FORWARDING_TIMEOUT_SECONDS),
                readBooleanOrElse(ENV_LOAD_MODULES_MANUALLY, DEFAULT_LOAD_MODULES_MANUALLY),
                readStringOrElse(NODE_MODULE_SCRIPT_PATH, DEFAULT_NODE_MODULE_STARTUP_SCRIPT_PATH)
            )
        }

        private fun getLoggingLvl(): LoggerLvl {
            val env = readOptionalString(ENV_LOGGING_LVL)
            if(env.isEmpty){
                return DEFAULT_LOGGING_LEVEL
            }
            return when(env.get()){
                LoggerLvl.DISABLED.toString() -> LoggerLvl.DISABLED
                LoggerLvl.LOW.toString() -> LoggerLvl.LOW
                LoggerLvl.MID.toString() -> LoggerLvl.MID
                LoggerLvl.HIGH.toString() -> LoggerLvl.HIGH
                LoggerLvl.COMPLETE.toString() -> LoggerLvl.COMPLETE
                else -> throw IllegalStateException("Env variable $ENV_LOGGING_LVL is not valid")
            }
        }

        private fun readGreaterThanZeroOrElse(varName: String, default: Long): Long {
            return readGreaterThanZeroOrElse(varName, default.toInt()).toLong()
        }

        private fun readGreaterThanZeroOrElse(varName: String, default: Int): Int{
            val env = readOptionalString(varName)
            if(env.isEmpty){
                return default
            }
            try {
                val casted = env.get().toInt()
                if(casted <= 0){
                    throw IllegalStateException("Env variable $varName must be a greater than zero integer")
                }
                return casted
            } catch (e: NumberFormatException) {
                throw IllegalStateException("Env variable $varName must be a greater than zero integer")
            }
        }

        private fun readBooleanOrElse(varName: String, default: Boolean): Boolean{
            val env = readOptionalString(varName)
            if(env.isEmpty){
                return default
            }
            try {
                return env.get().toBoolean()
            } catch (e: Exception) {
                throw IllegalStateException("Env variable $varName must be a boolean")
            }
        }

        private fun readIntOrElse(varName: String, default: Int): Int{
            val env = readOptionalString(varName)
            if(env.isEmpty){
                return default
            }
            try {
                return env.get().toInt()
            } catch (e: Exception) {
                throw IllegalStateException("Env variable $varName must be an integer")
            }
        }

        private fun readMandatoryString(varName: String): String{
            val tmp = readOptionalString(varName)
            if(tmp.isPresent){
                return tmp.get()
            }
            throw IllegalStateException("Env variable $varName not found")
        }

        private fun readStringOrElse(varName: String, default: String): String{
            return readOptionalString(varName).orElse(default)
        }

    }

}