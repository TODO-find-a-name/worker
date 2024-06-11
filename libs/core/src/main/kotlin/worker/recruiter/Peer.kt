package com.todo.todo.worker.recruiter

import com.todo.todo.worker.events.general.RemoveRecruiterEvent
import com.todo.todo.worker.socket.messages.InterviewAcceptanceMsg
import com.todo.todo.worker.socket.messages.TeamDetailsMsg
import com.todo.todo.worker.socket.messages.data.AgnosticIceCandidate
import com.todo.todo.worker.socket.messages.data.AgnosticRTCSessionDescription
import com.todo.todo.worker.SharedRepository
import com.todo.todo.worker.events.recruiter.IncomingRecruiterMsgPartEvent
import com.todo.todo.worker.socket.messages.abstractions.SocketMsgType
import com.todo.todo.worker.utils.LoggerLvl
import dev.onvoid.webrtc.*
import java.nio.charset.StandardCharsets

class Peer(private val recruiterId: String, private val repository: SharedRepository) {

    private val peer: RTCPeerConnection = createPeer()

    fun setRemoteDescription(sessionDescription: RTCSessionDescription){
        peer.setRemoteDescription(
            sessionDescription,
            object : OnDescriptionSet(recruiterId, repository){
                override fun onSuccess() {
                    repository.recruiters[recruiterId]?.peer?.createAnswer()
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
                    AgnosticRTCSessionDescription.adaptConcrete(sessionDescription).ifPresent{
                        repository.logger.logSocketOutgoing(
                            LoggerLvl.MID,
                            SocketMsgType.INTERVIEW_ACCEPTANCE_NAME,
                            recruiterId,
                            "Sending session description"
                        )
                        InterviewAcceptanceMsg.send(repository, recruiterId, it){ ack ->
                            repository.logger.logSocketOutgoingAck(
                                LoggerLvl.COMPLETE, SocketMsgType.INTERVIEW_ACCEPTANCE_NAME, recruiterId, ack
                            )
                            if(!ack){
                                repository.eventQueues.general.add(RemoveRecruiterEvent(repository, recruiterId))
                            }
                        }
                    }
                }
            }
        )
    }

    fun isConnected(): Boolean {
        return peer.connectionState == RTCPeerConnectionState.CONNECTED
    }

    fun addIceCandidate(candidate: RTCIceCandidate){
        peer.addIceCandidate(candidate)
    }

    fun disconnect(){
        println("disconnecting recruiter $recruiterId")
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
            rtcConfiguration, RecruiterPeerConnectionObserver(recruiterId, repository, this)
        )
    }

}

private class RecruiterPeerConnectionObserver(
    private val recruiterId: String, private val repository: SharedRepository, private val peer: Peer
): PeerConnectionObserver {

    override fun onIceCandidate(candidate: RTCIceCandidate?) {
        if(peer.isConnected()){
            candidate?.let {
                repository.logger.logSocketOutgoing(LoggerLvl.HIGH, SocketMsgType.TEAM_DETAILS_NAME, recruiterId, "Sending ice candidate")
                TeamDetailsMsg.send(
                    repository, recruiterId, AgnosticIceCandidate.adaptConcrete(candidate)
                ){
                    repository.logger.logSocketOutgoingAck(
                        LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
                    )
                    // TODO remove Recruiter if ack false
                }
            }
        }
    }

    override fun onConnectionChange(state: RTCPeerConnectionState?) {
        state?.let {
            // TODO CONNECTING, CONNECTED, DISCONNECTED, FAILED, maybe more
        }
    }

    override fun onDataChannel(dataChannel: RTCDataChannel?) {
        dataChannel?.registerObserver(DataChannelObserver(dataChannel, repository, recruiterId))
    }

}

private class DataChannelObserver(val dataChannel: RTCDataChannel, val repository: SharedRepository, private val recruiterId: String) : RTCDataChannelObserver {
    override fun onBufferedAmountChange(p0: Long) {
        //TODO("Not yet implemented")
        println("DATA CHANNEL OBSERVER ON BUFFERED AMOUNT CHANGE")
    }

    override fun onStateChange() {
        if(dataChannel.state.equals(RTCDataChannelState.OPEN)){
            repository.logger.logRegular(LoggerLvl.LOW, "Recruiter $recruiterId connected")
        }
    }

    override fun onMessage(buffer: RTCDataChannelBuffer?) {
        buffer?.data?.let {
            repository.logger.logRegular(LoggerLvl.COMPLETE, "Incoming p2p msg part from $recruiterId, enqueueing its event")
            repository.eventQueues.recruiter.add(IncomingRecruiterMsgPartEvent(repository, recruiterId, it))
        }
    }

}

private abstract class OnDescriptionSet(
    private val id: String, private val repository: SharedRepository
): SetSessionDescriptionObserver {

    override fun onFailure(p0: String?) {
        println("on description set failure")
        repository.eventQueues.general.add(RemoveRecruiterEvent(repository, id))
    }

}

private class OnAnswerCreated(
    private val id: String, private val repository: SharedRepository
): CreateSessionDescriptionObserver{

    override fun onSuccess(description: RTCSessionDescription?) {
        description?.let {
            repository.recruiters[id]?.peer?.setLocalDescription(description)
        }
    }

    override fun onFailure(p0: String?) {
        repository.eventQueues.general.add(RemoveRecruiterEvent(repository, id))
    }

}