package app

import io.github.cdimascio.dotenv.dotenv
import libs.core.worker.utils.*
import java.util.Optional

const val ENV_BROKER_ADDR = "BROKER_ADDR"
const val ENV_ORGANIZATION = "ORGANIZATION"
const val ENV_LOGGING_LVL = "LOGGING_LVL"
const val ENV_P2P_PAYLOAD_SIZE_BYTES = "ENV_P2P_PAYLOAD_SIZE_BYTES"
const val ENV_RECRUITMENT_TIMEOUT_MS = "RECRUITMENT_TIMEOUT_MS"
const val ENV_P2P_MSG_TIMEOUT_MS = "P2P_MSG_TIMEOUT_MS"

class WorkerSettingsByEnvVariables {
    companion object {
        fun create(): WorkerSettings {
            return WorkerSettings(
                readMandatoryString(ENV_BROKER_ADDR),
                readMandatoryString(ENV_ORGANIZATION),
                getLoggingLvl(),
                readGreaterThanZeroOrElse(ENV_P2P_PAYLOAD_SIZE_BYTES, DEFAULT_P2P_PAYLOAD_SIZE_BYTES),
                readGreaterThanZeroOrElse(ENV_RECRUITMENT_TIMEOUT_MS, DEFAULT_RECRUITMENT_TIMEOUT_MS),
                readGreaterThanZeroOrElse(ENV_P2P_MSG_TIMEOUT_MS, DEFAULT_P2P_MSG_TIMEOUT_MS)
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

        private fun readOptionalString(varName: String): Optional<String>{
            var res = try {
                dotenv()[varName]
            } catch (e: Exception){
                try {
                    System.getenv(varName)
                } catch (e: Exception){
                    return Optional.empty()
                }
            }

            if(res == null){
                return Optional.empty()
            }

            res = res.trim()
            if(res.isEmpty()){
                return Optional.empty()
            }
            return Optional.of(res)
        }

        private fun readMandatoryString(varName: String): String{
            val tmp = readOptionalString(varName)
            if(tmp.isPresent){
                return tmp.get()
            }
            throw IllegalStateException("Env variable $varName not found")
        }

    }

}