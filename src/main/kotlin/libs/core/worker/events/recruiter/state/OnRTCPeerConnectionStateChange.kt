package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCPeerConnectionState
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.recruiter.Recruiter

class OnRTCPeerConnectionStateChange(
    repository: Repository, recruiterId: String, private val state: RTCPeerConnectionState
) : RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        if(state == RTCPeerConnectionState.DISCONNECTED || state == RTCPeerConnectionState.FAILED){
            repository.removeRecruiter(recruiter)
        }
    }

}