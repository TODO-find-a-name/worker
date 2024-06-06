package libs.core.worker.utils

class Lock(private val logger: Logger) {

    @Synchronized
    fun execute(who: String, function: () -> Unit){
        logger.log(LoggerLvl.COMPLETE, "LOCK ACQUIRED", who)
        function()
        logger.log(LoggerLvl.COMPLETE, "LOCK RELEASED", who)
    }

}