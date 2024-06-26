package libs.core.worker.utils

import java.time.LocalDateTime

class Logger(settings: WorkerSettings) {

    private val settingsLvl: Int = settings.loggingLvl.ordinal

    private fun brackets(content: String): String{
        return "[$content]"
    }

    private fun buildLog(info: List<String> , log: String): String {
        var res = if(info.isEmpty()) {
            brackets(LocalDateTime.now().toString())
        } else {
            brackets(LocalDateTime.now().toString()) + info.map { i -> brackets(i) }.reduce{ acc, i -> acc + i }
        }
        if(log.isNotBlank()){
            res += "\n" + log
        }
        return res + "\n"
    }

    fun log(lvl: LoggerLvl, log: String, vararg info: String){
        if (lvl.ordinal <= settingsLvl) {
            println(buildLog(info.toList(), log))
        }
    }

    fun logSocketIncoming(lvl: LoggerLvl, msgType: String, from: String, log: String){
        log(lvl, log, "Incoming SOCKET msg $msgType from $from")
    }

    fun logSocketOutgoing(lvl: LoggerLvl, msgType: String, to: String, log: String){
        log(lvl, log, "Outgoing SOCKET msg $msgType to $to")
    }

    fun logSocketOutgoingAck(lvl: LoggerLvl, msgType: String, to: String, ack: Boolean){
        log(lvl, "", "Outgoing SOCKET msg $msgType to $to", "Ack: $ack")
    }

    fun error(log: String, vararg info: String){
        System.err.println(buildLog(info.toList(), log))
    }

    fun errorSocketMsg(msgType: String, log: String, from: String = ""){
        if(from == ""){
            error(log, "Incoming socket msg $msgType")
        } else {
            error(log, "Incoming socket msg $msgType from: $from")
        }
    }

}

enum class LoggerLvl {
    DISABLED,
    LOW,
    MID,
    HIGH,
    COMPLETE
}