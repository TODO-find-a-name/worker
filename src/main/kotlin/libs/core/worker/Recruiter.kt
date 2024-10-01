package libs.core.worker

import dev.onvoid.webrtc.CreateSessionDescriptionObserver
import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.PeerConnectionObserver
import dev.onvoid.webrtc.RTCAnswerOptions
import dev.onvoid.webrtc.RTCDataChannel
import dev.onvoid.webrtc.RTCDataChannelBuffer
import dev.onvoid.webrtc.RTCDataChannelObserver
import dev.onvoid.webrtc.RTCDataChannelState
import dev.onvoid.webrtc.RTCIceCandidate
import dev.onvoid.webrtc.RTCPeerConnection
import dev.onvoid.webrtc.RTCPeerConnectionState
import dev.onvoid.webrtc.RTCSessionDescription
import dev.onvoid.webrtc.SetSessionDescriptionObserver
import libs.common.module.WorkerModule
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.IncomingRecruiterMsgPartEvent
import libs.core.worker.events.recruiter.negotiation.CreateAnswerNegotiationEvent
import libs.core.worker.events.recruiter.negotiation.RecruitmentTimeoutEvent
import libs.core.worker.events.recruiter.negotiation.SetLocalDescriptionNegotiationEvent
import libs.core.worker.events.recruiter.state.OnDataChannelOpenEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingInterviewAcceptanceMsgEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingTeamDetailsMsgEvent
import libs.core.worker.utils.PendingMsg
import libs.core.worker.utils.scheduleEvent
import java.util.*

class Recruiter(
    val recruiterId: String, val sessionToken: String, val module: WorkerModule, private val repository: Repository
) {

    private val timer = Timer()
    private val peer: RTCPeerConnection = createPeer()

    val pendingMessages: MutableMap<Int, PendingMsg> = mutableMapOf()
    var dataChannel: RTCDataChannel? = null
    var isConnected = false

    init {
        timer.scheduleEvent(RecruitmentTimeoutEvent(repository, recruiterId), repository.settings.recruitmentTimeoutMs)
    }

    fun setRemoteDescription(description: RTCSessionDescription){
        peer.setRemoteDescription(
            description,
            object : SetSessionDescriptionObserver {
                override fun onSuccess() { CreateAnswerNegotiationEvent(repository, recruiterId).handle() }
                override fun onFailure(s: String?) {
                    RemoveRecruiterEvent(repository, recruiterId, "Error while setting remote description").handle()
                }
            }
        )
    }

    fun setLocalDescription(description: RTCSessionDescription){
        peer.setLocalDescription(
            description,
            object : SetSessionDescriptionObserver {
                override fun onSuccess() {
                    OutgoingInterviewAcceptanceMsgEvent(repository, recruiterId, sessionToken, description).handle()
                }
                override fun onFailure(s: String?) {
                    RemoveRecruiterEvent(repository, recruiterId, "Error while setting local description").handle()
                }
            }
        )
    }

    fun createAnswer(){
        peer.createAnswer(
            RTCAnswerOptions(),  // TODO ???
            object : CreateSessionDescriptionObserver {
                override fun onSuccess(description: RTCSessionDescription?) {
                    description?.let {
                        SetLocalDescriptionNegotiationEvent(repository, recruiterId, description).handle()
                    }
                }
                override fun onFailure(p0: String?) {
                    RemoveRecruiterEvent(repository, recruiterId, "Error while creating answer").handle()
                }
            }
        )
    }

    fun addIceCandidate(candidate: RTCIceCandidate){
        peer.addIceCandidate(candidate)
    }

    fun disconnect(){
        isConnected = false
        dataChannel?.close()
        peer.close()
        module.removeRecruiter(recruiterId)
        dataChannel = null
        timer.cancel()
        pendingMessages.forEach{ pair -> pair.value.cancelTimeout() }
        pendingMessages.clear()
    }

    private fun createPeer(): RTCPeerConnection {
        return PeerConnectionFactory().createPeerConnection(
            repository.settings.getRTCConfiguration(),
            object : PeerConnectionObserver {
                override fun onIceCandidate(candidate: RTCIceCandidate?) {
                    candidate?.let { OutgoingTeamDetailsMsgEvent(repository, recruiterId, sessionToken, it).handle() }
                }

                override fun onConnectionChange(state: RTCPeerConnectionState?) {
                    state?.let {
                        if(it == RTCPeerConnectionState.DISCONNECTED || it == RTCPeerConnectionState.FAILED){
                            RemoveRecruiterEvent(repository, recruiterId, "Connection lost").handle()
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
