package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import libs.core.worker.events.socket.messages.data.abstractions.DirectMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.IceCandidateAdapter
import libs.core.worker.Repository
import dev.onvoid.webrtc.RTCIceCandidate
import java.util.Optional

class TeamDetailsMsg: DirectMsg() {
    @SerializedName("candidate") var candidate: IceCandidateAdapter? = null

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
            repository: Repository,
            to: String,
            candidate: IceCandidateAdapter,
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