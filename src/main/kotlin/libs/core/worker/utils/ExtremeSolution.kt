package libs.core.worker.utils

import kotlin.system.exitProcess

class ExtremeSolution {
    companion object {
        fun shutdown(logger: Logger, code: ShutdownCode, msg: String){
            logger.error(msg)
            exitProcess(code.ordinal)
        }
    }
}

enum class ShutdownCode {
    OK,
    WRONG_MODULE_LOADING_RESPONSE,
    MODULE_LOADING_TIMEOUT,
    MODULE_FORWARDING_TIMEOUT,
    MODULE_DISCONNECTED
}