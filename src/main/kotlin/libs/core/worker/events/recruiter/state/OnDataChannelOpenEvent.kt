package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCDataChannel
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.recruiter.Recruiter
import libs.core.worker.utils.LoggerLvl
import java.util.*

class OnDataChannelOpenEvent(
    repository: Repository, recruiterId: String, private val channel: RTCDataChannel, private val timer: Timer
) : RecruiterEvent(OnDataChannelOpenEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        timer.cancel()
        recruiter.isConnected = true
        repository.logger.log(LoggerLvl.LOW, "Recruiter $recruiterId connected")
        recruiter.module.addRecruiter(recruiterId)
        recruiter.dataChannel = channel
    }

}