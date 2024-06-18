package libs.core.worker.recruiter

import libs.core.worker.Repository
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.messages.IncomingRecruiterMsgPartEvent
import libs.core.worker.events.recruiter.negotiation.CreateAnswerNegotiationEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingInterviewAcceptanceMsgEvent
import libs.core.worker.events.socket.messages.outgoing.OutgoingTeamDetailsMsgEvent
import dev.onvoid.webrtc.*
import libs.common.messages.PeerMsg
import libs.core.worker.events.Event
import libs.core.worker.events.recruiter.state.OnDataChannelStateChangeEvent
import libs.core.worker.events.recruiter.state.OnRTCPeerConnectionStateChange
import libs.core.worker.events.recruiter.negotiation.RecruitmentTimeoutEvent
import libs.core.worker.utils.scheduleEvent
import java.nio.ByteBuffer
import java.util.*

class Recruiter(private val recruiterId: String, private val repository: Repository) {

    private val timer = Timer()
    private val parser = repository.parser
    private val peer: RTCPeerConnection = createPeer()

    val pendingMessages: MutableMap<String, PendingMsg> = mutableMapOf()
    var dataChannel: RTCDataChannel? = null

    init {
        timer.scheduleEvent(RecruitmentTimeoutEvent(repository, recruiterId), repository.settings.recruitmentTimeoutMs)
    }

    fun isConnected(): Boolean {
        return peer.connectionState == RTCPeerConnectionState.CONNECTED
    }

    fun sendMsg(msg: PeerMsg): Boolean {
        if(dataChannel == null || !isConnected()){
            return false
        }
        try {
            msg.splitIntoParts(repository.settings.p2pPayloadSizeBytes).forEach {
                dataChannel?.send(RTCDataChannelBuffer(ByteBuffer.wrap(parser.toJson(it).toByteArray()), false))
            }
        } catch(e: Exception){
            return false
        }
        return true
    }

    fun setRemoteDescription(description: RTCSessionDescription){
        peer.setRemoteDescription(
            description,
            OnDescriptionSet(recruiterId, repository, CreateAnswerNegotiationEvent(repository, recruiterId))
        )
    }

    fun setLocalDescription(description: RTCSessionDescription){
        peer.setLocalDescription(
            description,
            OnDescriptionSet(
                recruiterId, repository, OutgoingInterviewAcceptanceMsgEvent(repository, recruiterId, description)
            )
        )
    }

    fun createAnswer(){
        peer.createAnswer(
            RTCAnswerOptions(),  // TODO ???
            object : CreateSessionDescriptionObserver {
                override fun onSuccess(description: RTCSessionDescription?) {
                    description?.let { repository.recruiters[recruiterId]?.setLocalDescription(description) }
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
                    state?.let { OnRTCPeerConnectionStateChange(repository, recruiterId, it).handle() }
                }

                override fun onDataChannel(dataChannel: RTCDataChannel?) {
                    dataChannel?.registerObserver(object : RTCDataChannelObserver {
                        override fun onStateChange() {
                            OnDataChannelStateChangeEvent(repository, recruiterId, dataChannel, timer).handle()
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

private class OnDescriptionSet(
    private val id: String, private val repository: Repository, private val onSuccessEvent: Event
): SetSessionDescriptionObserver {

    override fun onSuccess() {
        onSuccessEvent.handle()
    }

    override fun onFailure(p0: String?) {
        println("on description set failure")
        RemoveRecruiterEvent(repository, id).handle()
    }

}
