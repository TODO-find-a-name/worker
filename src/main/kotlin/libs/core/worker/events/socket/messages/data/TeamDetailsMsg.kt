package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import libs.core.worker.events.socket.messages.data.abstractions.DirectMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.IceCandidateAdapter
import libs.core.worker.Repository
import dev.onvoid.webrtc.RTCIceCandidate
import java.util.Optional

data class TeamDetailsMsg(
    val from: String,
    val to: String,
    val candidate: RTCIceCandidate
)

class TeamDetailsMsgParsable: DirectMsg() {
    @SerializedName("candidate") var candidate: IceCandidateAdapter? = null

    fun toChecked(): Optional<TeamDetailsMsg>{
        if(candidate == null || from == null || to == null){
            return Optional.empty()
        }
        val concreteCandidate = candidate!!.toConcrete()
        if(concreteCandidate.isEmpty){
            return Optional.empty()
        }
        return Optional.of(TeamDetailsMsg(from!!, to!!, concreteCandidate.get()))
    }

    companion object {
        fun send(
            repository: Repository,
            to: String,
            candidate: IceCandidateAdapter,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = TeamDetailsMsgParsable()
            msg.to = to
            msg.from = repository.socket.id()
            msg.candidate = candidate
            repository.socket.sendMsg(SocketMsgType.TEAM_DETAILS, repository.parser.toJson(msg), onAck)
        }
    }
}
