package libs.core.worker.socket.messages

import com.google.gson.annotations.SerializedName
import libs.core.worker.socket.messages.abstractions.DirectMsg
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.socket.messages.data.AgnosticIceCandidate
import libs.core.worker.SharedRepository
import dev.onvoid.webrtc.RTCIceCandidate
import java.util.Optional

class TeamDetailsMsg: DirectMsg() {
    @SerializedName("candidate") var candidate: AgnosticIceCandidate? = null

    fun toChecked(): Optional<TeamDetailsMsgChecked>{
        if(candidate == null || from == null || to == null){
            return Optional.empty()
        }
        val concreteCandidate = candidate!!.toConcrete()
        if(concreteCandidate.isEmpty){
            return Optional.empty()
        }
        return Optional.of(TeamDetailsMsgChecked(from!!, to!!, concreteCandidate.get()))
    }

    companion object {
        fun send(
            repository: SharedRepository,
            to: String,
            candidate: AgnosticIceCandidate,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = TeamDetailsMsg()
            msg.to = to
            msg.from = repository.socket.id()
            msg.candidate = candidate
            repository.socket.sendMsg(SocketMsgType.TEAM_DETAILS, repository.parser.toJson(msg), onAck)
        }
    }
}

data class TeamDetailsMsgChecked(
    val from: String,
    val to: String,
    val candidate: RTCIceCandidate
)