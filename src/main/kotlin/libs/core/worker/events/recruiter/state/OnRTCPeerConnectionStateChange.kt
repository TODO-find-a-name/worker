package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCPeerConnectionState
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent

class OnRTCPeerConnectionStateChange(
    repository: Repository, private val recruiterId: String, private val state: RTCPeerConnectionState
) : Event(repository) {

    override fun handleImpl() {
        if(state == RTCPeerConnectionState.DISCONNECTED || state == RTCPeerConnectionState.FAILED){
            RemoveRecruiterEvent(repository, recruiterId).handle()
        }
    }

}