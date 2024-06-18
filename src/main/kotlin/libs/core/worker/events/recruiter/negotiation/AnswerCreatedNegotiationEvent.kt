package libs.core.worker.events.recruiter.negotiation

import dev.onvoid.webrtc.RTCSessionDescription
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.recruiter.Recruiter

class AnswerCreatedNegotiationEvent(
    repository: Repository, recruiterId: String, private val description: RTCSessionDescription
) : RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        recruiter.setLocalDescription(description)
    }

}