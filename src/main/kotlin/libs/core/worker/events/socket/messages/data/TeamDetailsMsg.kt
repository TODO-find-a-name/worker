package libs.core.worker.events.socket.messages.data

import com.google.gson.annotations.SerializedName
import dev.onvoid.webrtc.RTCIceCandidate
import libs.core.worker.Repository
import libs.core.worker.events.socket.messages.data.abstractions.DirectMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.events.socket.messages.data.adapters.IceCandidateAdapter
import java.util.*

data class TeamDetailsMsg(
    val sessionToken: String,
    val from: String,
    val to: String,
    val candidate: RTCIceCandidate
)

class TeamDetailsMsgParsable: DirectMsg() {
    @SerializedName("candidate") var candidate: IceCandidateAdapter? = null

    fun toChecked(): Optional<TeamDetailsMsg>{
        if(candidate == null || sessionToken == null || from == null || to == null){
            return Optional.empty()
        }
        val concreteCandidate = candidate!!.toConcrete()
        if(concreteCandidate.isEmpty){
            return Optional.empty()
        }
        return Optional.of(TeamDetailsMsg(sessionToken!!, from!!, to!!, concreteCandidate.get()))
    }

    companion object {
        fun send(
            repository: Repository,
            sessionToken: String,
            to: String,
            candidate: IceCandidateAdapter,
            onAck: (ack: Boolean) -> Any
        ){
            val msg = TeamDetailsMsgParsable()
            msg.sessionToken = sessionToken
            msg.to = to
            msg.from = repository.socket.id()
            msg.candidate = candidate
            repository.socket.sendMsg(SocketMsgType.TEAM_DETAILS, repository.parser.toJson(msg), onAck)
        }
    }
}
