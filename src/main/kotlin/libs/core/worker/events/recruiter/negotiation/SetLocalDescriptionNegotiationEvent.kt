package libs.core.worker.events.recruiter.negotiation

import dev.onvoid.webrtc.RTCSessionDescription
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.Recruiter
import libs.core.worker.utils.LoggerLvl

class SetLocalDescriptionNegotiationEvent(
    repository: Repository, recruiterId: String, private val description: RTCSessionDescription
) : RecruiterEvent(SetLocalDescriptionNegotiationEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        repository.logger.log(LoggerLvl.COMPLETE, "Setting local description", "Recruiter $recruiterId")
        recruiter.setLocalDescription(description)
    }

}