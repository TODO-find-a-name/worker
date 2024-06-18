package libs.core.worker.recruiter

import libs.core.worker.SharedRepository
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.recruiter.IncomingRecruiterMsgPartEvent
import libs.core.worker.events.recruiter.negotiation.CreateAnswerNegotiationEvent
import libs.core.worker.events.socket.outgoing.OutgoingInterviewAcceptanceMsgEvent
import libs.core.worker.events.socket.outgoing.OutgoingTeamDetailsMsgEvent
import libs.core.worker.utils.LoggerLvl
import dev.onvoid.webrtc.*
import libs.common.messages.PeerMsg
import java.nio.ByteBuffer
import java.util.*

class Recruiter(private val recruiterId: String, private val repository: SharedRepository) {

    val pendingMessages: MutableMap<String, PendingMsg> = mutableMapOf()

    val timeoutTimer = Timer()
    private val parser = repository.parser
    private val peer: RTCPeerConnection = createPeer()

    var dataChannel: RTCDataChannel? = null

    init {
        timeoutTimer.schedule(
            object : TimerTask() {
                override fun run() {
                    repository.lock.execute {
                        if(repository.isRunning){
                            if(!isConnected() && repository.recruiters.contains(recruiterId)){
                                RemoveRecruiterEvent(repository, recruiterId).handle()
                            }
                        }
                    }
                }
            },
            repository.settings.recruitmentTimeoutMs
        )
    }

    fun isConnected(): Boolean {
        return peer.connectionState == RTCPeerConnectionState.CONNECTED
    }

    fun sendMsg(msg: PeerMsg): Boolean {
        if(dataChannel == null){
            return false
        } else {
            try {
                msg.splitIntoParts(repository.settings.p2pPayloadSizeBytes).forEach {
                    dataChannel?.send(RTCDataChannelBuffer(ByteBuffer.wrap(parser.toJson(it).toByteArray()), false))
                }
            } catch(e: Exception){
                return false
            }
            return true
        }
    }

    fun setRemoteDescription(sessionDescription: RTCSessionDescription){
        peer.setRemoteDescription(
            sessionDescription,
            object : OnDescriptionSet(recruiterId, repository){
                override fun onSuccess() {
                    CreateAnswerNegotiationEvent(repository, recruiterId).handle()
                }
            }
        )
    }

    fun createAnswer(){
        peer.createAnswer(
            RTCAnswerOptions(),  // TODO ???
            OnAnswerCreated(recruiterId, repository)
        )
    }

    fun setLocalDescription(sessionDescription: RTCSessionDescription){
        peer.setLocalDescription(
            sessionDescription,
            object : OnDescriptionSet(recruiterId, repository){
                override fun onSuccess() {
                    OutgoingInterviewAcceptanceMsgEvent(repository, recruiterId, sessionDescription).handle()
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
        // TODO implement
    }

    private fun createRTCIceServer(urls: List<String>, username: String? = null, password: String? = null): RTCIceServer {
        val rtcIceServer = RTCIceServer()
        rtcIceServer.urls = urls
        username?.let { rtcIceServer.username = username }
        password?.let { rtcIceServer.password = password }
        return rtcIceServer
    }

    private fun createPeer(): RTCPeerConnection {
        val rtcConfiguration = RTCConfiguration()
        rtcConfiguration.iceServers = listOf(
            createRTCIceServer(listOf("stun:openrelay.metered.ca:80")),
            createRTCIceServer(listOf("turn:openrelay.metered.ca:80"), "openrelayproject", "openrelayproject"),
            createRTCIceServer(listOf("turn:openrelay.metered.ca:443"), "openrelayproject", "openrelayproject"),
            createRTCIceServer(
                listOf("turn:openrelay.metered.ca:443?transport=tcp"),
                "openrelayproject",
                "openrelayproject"
            )
        )
        return PeerConnectionFactory().createPeerConnection(
            rtcConfiguration, RecruiterPeerConnectionObserver(recruiterId, repository, this, timeoutTimer)
        )
    }

}

private class RecruiterPeerConnectionObserver(
    private val recruiterId: String,
    private val repository: SharedRepository,
    private val recruiter: Recruiter,
    private val timeoutTimer: Timer
): PeerConnectionObserver {

    override fun onIceCandidate(candidate: RTCIceCandidate?) {
        candidate?.let {
            OutgoingTeamDetailsMsgEvent(repository, recruiterId, it).handle()
        }
    }

    override fun onConnectionChange(state: RTCPeerConnectionState?) {
        state?.let {
            println(it.toString())
            if(it == RTCPeerConnectionState.DISCONNECTED || it == RTCPeerConnectionState.FAILED){
                RemoveRecruiterEvent(repository, recruiterId).handle()
            }
        }
    }

    override fun onDataChannel(dataChannel: RTCDataChannel?) {
        dataChannel?.registerObserver(DataChannelObserver(dataChannel, repository, recruiterId, timeoutTimer))
    }

}

private class DataChannelObserver(
    val dataChannel: RTCDataChannel,
    val repository: SharedRepository,
    private val recruiterId: String,
    private val timeoutTimer: Timer
) : RTCDataChannelObserver {
    override fun onBufferedAmountChange(p0: Long) {
        //TODO("Not yet implemented")
        println("DATA CHANNEL OBSERVER ON BUFFERED AMOUNT CHANGE")
    }

    override fun onStateChange() {
        if(dataChannel.state.equals(RTCDataChannelState.OPEN)){
            timeoutTimer.cancel()
            repository.logger.logRegular(LoggerLvl.LOW, "Recruiter $recruiterId connected")
            repository.recruiters[recruiterId]?.dataChannel = dataChannel
        }
    }

    override fun onMessage(buffer: RTCDataChannelBuffer?) {
        buffer?.data?.let {
            repository.logger.logRegular(LoggerLvl.COMPLETE, "Incoming p2p msg part from $recruiterId, enqueueing its event")
            IncomingRecruiterMsgPartEvent(repository, recruiterId, it).handle()
        }
    }

}

private abstract class OnDescriptionSet(
    private val id: String, private val repository: SharedRepository
): SetSessionDescriptionObserver {

    override fun onFailure(p0: String?) {
        println("on description set failure")
        RemoveRecruiterEvent(repository, id).handle()
    }

}

private class OnAnswerCreated(
    private val id: String, private val repository: SharedRepository
): CreateSessionDescriptionObserver{

    override fun onSuccess(description: RTCSessionDescription?) {
        description?.let {
            repository.recruiters[id]?.setLocalDescription(description)
        }
    }

    override fun onFailure(p0: String?) {
        println("OnAnswerCreated onFailure")
        RemoveRecruiterEvent(repository, id).handle()
    }

}
