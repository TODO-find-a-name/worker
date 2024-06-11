package com.todo.todo.worker.utils

import messages.PeerMsg
import messages.PeerMsgPartChecked
import java.time.LocalDateTime

class Logger(settings: WorkerSettings) {

    private val settingsLvl: Int = settings.loggingLvl.ordinal

    fun logRegular(lvl: LoggerLvl, log: String){
        if (lvl.ordinal <= settingsLvl) {
            println(timestamp() + "\n" + log + "\n")
        }
    }

    fun logSocketIncoming(lvl: LoggerLvl, msgType: String, from: String, log: String){
        if (lvl.ordinal <= settingsLvl) {
            println(
                timestamp() +
                        brackets("Incoming socket msg $msgType from $from") +
                        "\n" + log + "\n"
            )
        }
    }

    fun logSocketOutgoing(lvl: LoggerLvl, msgType: String, to: String, log: String){
        if (lvl.ordinal <= settingsLvl) {
            println(
                timestamp() +
                        brackets("Outgoing socket msg $msgType to $to") +
                        "\n" + log + "\n"
            )
        }
    }

    fun logSocketOutgoingAck(lvl: LoggerLvl, msgType: String, to: String, ack: Boolean, log: String = ""){
        if (lvl.ordinal <= settingsLvl) {
            println(
                timestamp() +
                        brackets("Outgoing socket msg $msgType to $to") +
                        brackets("Ack: $ack") +
                        (if(log == "") "\n" else "\n" + log + "\n")
            )
        }
    }

    fun logP2PIncomingPart(lvl: LoggerLvl, p2PeerMsgPartChecked: PeerMsgPartChecked, from: String){
        if (lvl.ordinal <= settingsLvl) {
            println(
                timestamp() +
                        brackets("Incoming p2p msg ${p2PeerMsgPartChecked.msgType} from $from") +
                        brackets("Id: " + p2PeerMsgPartChecked.msgId) +
                        brackets(p2PeerMsgPartChecked.part.toString() + "/" + p2PeerMsgPartChecked.total) +
                        "\n"
            )
        }
    }

    fun logP2PIncomingComplete(lvl: LoggerLvl, p2PeerMsgPartChecked: PeerMsg, from: String){
        if (lvl.ordinal <= settingsLvl) {
            println(
                timestamp() +
                        brackets("Incoming p2p msg ${p2PeerMsgPartChecked.msgType} from $from") +
                        brackets("Id: " + p2PeerMsgPartChecked.msgId) +
                        brackets("Module: ${p2PeerMsgPartChecked.module}") +
                        "\n"
            )
        }
    }

    private fun timestamp(): String {
        return brackets(LocalDateTime.now().toString())
    }

    private fun brackets(content: String): String{
        return "[$content]"
    }

    fun error(log: String){
        println("[" + LocalDateTime.now().toString() + "]\n" + log + "\n") // TODO
    }

    fun errorSocket(msgType: String, log: String, from: String = ""){
        println(
            timestamp() +
                    brackets("socket msg: $msgType") +
                    (if(from == "") "" else brackets("from: $from")) + "\n" +
                    log + "\n"
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