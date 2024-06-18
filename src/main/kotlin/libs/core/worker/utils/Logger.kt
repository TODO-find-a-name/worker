package libs.core.worker.utils

import libs.common.messages.PeerMsg
import libs.common.messages.PeerMsgPartChecked
import java.time.LocalDateTime

class Logger(settings: WorkerSettings) {

    private val settingsLvl: Int = settings.loggingLvl.ordinal

    private fun brackets(content: String): String{
        return "[$content]"
    }

    private fun buildLog(info: List<String> , log: String): String {
        if(info.isEmpty()) {
            return brackets(LocalDateTime.now().toString()) + "\n" + log + "\n"
        }
        return brackets(LocalDateTime.now().toString()) + info.reduce{ acc, i -> acc + brackets(i) } + "\n" + log + "\n"
    }

    fun log(lvl: LoggerLvl, log: String, vararg info: String){
        if (lvl.ordinal <= settingsLvl) {
            println(buildLog(info.toList(), log))
        }
    }

    fun logRegular(lvl: LoggerLvl, log: String){
        log(lvl, log)
    }

    fun logSocketIncoming(lvl: LoggerLvl, msgType: String, from: String, log: String){
        log(lvl, log, "Incoming socket msg $msgType from $from")
    }

    fun logSocketOutgoing(lvl: LoggerLvl, msgType: String, to: String, log: String){
        log(lvl, log, "Outgoing socket msg $msgType to $to")
    }

    fun logSocketOutgoingAck(lvl: LoggerLvl, msgType: String, to: String, ack: Boolean, log: String = ""){
        if (lvl.ordinal <= settingsLvl) {
            println(
                brackets(LocalDateTime.now().toString()) +
                        brackets("Outgoing socket msg $msgType to $to") +
                        brackets("Ack: $ack") +
                        (if(log == "") "\n" else "\n" + log + "\n")
            )
        }
    }

    fun logP2PIncomingPart(lvl: LoggerLvl, p2PeerMsgPartChecked: PeerMsgPartChecked, from: String){
        log(
            lvl,
            "Received p2p msg part with index " + p2PeerMsgPartChecked.part.toString() + " (total: " + p2PeerMsgPartChecked.total + ")",
            "Incoming p2p msg ${p2PeerMsgPartChecked.msgType} from $from",
            "Id: " + p2PeerMsgPartChecked.msgId
        )
    }

    fun logP2PIncomingComplete(lvl: LoggerLvl, p2PeerMsgPartChecked: PeerMsg, from: String){
        log(
            lvl,
            "Reconstructed p2p msg",
            "Incoming p2p msg ${p2PeerMsgPartChecked.msgType} from $from",
            "Id: " + p2PeerMsgPartChecked.msgId
        )
    }

    private fun error(log: String, vararg info: String){
        System.err.println(buildLog(info.toList(), log))
    }

    fun errorRecruiter(recruiterId: String, log: String){
        error(log, "Incoming p2p msg from $recruiterId")
    }

    fun errorSocket(msgType: String, log: String, from: String = ""){
        if(from == ""){
            error(log, "socket msg: $msgType")
        } else {
            error(log, "socket msg: $msgType", "from: $from")
        }
    }

    fun errorIncomingP2PMsg(recruiterId: String, peerMsg: PeerMsg, log: String){
        error(
            log,
            "Incoming p2p ${peerMsg.msgType} msg from $recruiterId",
            "Id: ${peerMsg.msgId}",
            "Module: ${peerMsg.module}"
        )
    }
}

enum class LoggerLvl {
    DISABLED,
    LOW,
    MID,
    HIGH,
    COMPLETE
}