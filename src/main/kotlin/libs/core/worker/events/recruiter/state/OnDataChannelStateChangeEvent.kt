package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCDataChannel
import dev.onvoid.webrtc.RTCDataChannelState
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.recruiter.Recruiter
import libs.core.worker.utils.LoggerLvl
import java.util.*

class OnDataChannelStateChangeEvent(
    repository: Repository, recruiterId: String, private val channel: RTCDataChannel, private val timer: Timer
) : RecruiterEvent(repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        if(channel.state.equals(RTCDataChannelState.OPEN)){
            timer.cancel()
            repository.logger.logRegular(LoggerLvl.LOW, "Recruiter $recruiterId connected")
            recruiter.dataChannel = channel
        }
    }

}