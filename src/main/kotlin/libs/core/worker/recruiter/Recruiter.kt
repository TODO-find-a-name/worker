package libs.core.worker.recruiter

import libs.core.worker.Repository
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.IncomingRecruiterMsgPartEvent
import libs.core.worker.events.recruiter.negotiation.CreateAnswerNegotiationEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingInterviewAcceptanceMsgEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingTeamDetailsMsgEvent
import dev.onvoid.webrtc.*
import libs.common.module.WorkerModule
import libs.core.worker.events.recruiter.negotiation.AnswerCreatedNegotiationEvent
import libs.core.worker.events.recruiter.negotiation.RecruitmentTimeoutEvent
import libs.core.worker.events.recruiter.state.OnDataChannelOpenEvent
import libs.core.worker.utils.LoggerLvl
import libs.core.worker.utils.scheduleEvent
import java.util.*

class Recruiter(val recruiterId: String, val module: WorkerModule, private val repository: Repository) {

    private val timer = Timer()
    private val peer: RTCPeerConnection = createPeer()

    val pendingMessages: MutableMap<String, PendingMsg> = mutableMapOf()
    var dataChannel: RTCDataChannel? = null

    init {
        timer.scheduleEvent(RecruitmentTimeoutEvent(repository, recruiterId), repository.settings.recruitmentTimeoutMs)
    }

    fun isConnected(): Boolean {
        return peer.connectionState == RTCPeerConnectionState.CONNECTED
    }

    fun setRemoteDescription(description: RTCSessionDescription){
        peer.setRemoteDescription(
            description,
            object : SetSessionDescriptionObserver {
                override fun onSuccess() { CreateAnswerNegotiationEvent(repository, recruiterId).handle() }
                override fun onFailure(s: String?) { RemoveRecruiterEvent(repository, recruiterId).handle() }
            }
        )
    }

    fun setLocalDescription(description: RTCSessionDescription){
        peer.setLocalDescription(
            description,
            object : SetSessionDescriptionObserver {
                override fun onSuccess() {
                    OutgoingInterviewAcceptanceMsgEvent(repository, recruiterId, description).handle()
                }
                override fun onFailure(s: String?) { RemoveRecruiterEvent(repository, recruiterId).handle() }
            }
        )
    }

    fun createAnswer(){
        peer.createAnswer(
            RTCAnswerOptions(),  // TODO ???
            object : CreateSessionDescriptionObserver {
                override fun onSuccess(description: RTCSessionDescription?) {
                    description?.let { AnswerCreatedNegotiationEvent(repository, recruiterId, description).handle() }
                }
                override fun onFailure(p0: String?) {
                    RemoveRecruiterEvent(repository, recruiterId).handle()
                }
            }
        )
    }

    fun addIceCandidate(candidate: RTCIceCandidate){
        peer.addIceCandidate(candidate)
    }

    fun disconnect(){
        println("disconnecting recruiter $recruiterId")
        if(isConnected()){
            dataChannel?.close()
            peer.close()
        }
        module.removeRecruiter(recruiterId)
        dataChannel = null
        timer.cancel()
        pendingMessages.forEach{ pair -> pair.value.cancelTimeout() }
        pendingMessages.clear()
        // TODO implement
    }

    private fun createPeer(): RTCPeerConnection {
        return PeerConnectionFactory().createPeerConnection(
            repository.settings.getRTCConfiguration(),
            object : PeerConnectionObserver {
                override fun onIceCandidate(candidate: RTCIceCandidate?) {
                    candidate?.let { OutgoingTeamDetailsMsgEvent(repository, recruiterId, it).handle() }
                }

                override fun onConnectionChange(state: RTCPeerConnectionState?) {
                    state?.let {
                        if(it == RTCPeerConnectionState.DISCONNECTED || it == RTCPeerConnectionState.FAILED){
                            RemoveRecruiterEvent(repository, recruiterId).handle()
                        }
                    }
                }

                override fun onDataChannel(channel: RTCDataChannel?) {
                    channel?.registerObserver(object : RTCDataChannelObserver {
                        override fun onStateChange() {
                            if(channel.state.equals(RTCDataChannelState.OPEN)){
                                OnDataChannelOpenEvent(repository, recruiterId, channel, timer).handle()
                            }
                        }

                        override fun onMessage(buffer: RTCDataChannelBuffer?) {
                            buffer?.data?.let { IncomingRecruiterMsgPartEvent(repository, recruiterId, it).handle() }
                        }

                        override fun onBufferedAmountChange(p0: Long) {}
                    })
                }
            }
        )
    }

}
