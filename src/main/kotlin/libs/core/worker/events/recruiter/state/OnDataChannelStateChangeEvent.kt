package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCDataChannel
import dev.onvoid.webrtc.RTCDataChannelState
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.utils.LoggerLvl
import java.util.*

class OnDataChannelStateChangeEvent(
    repository: Repository,
    private val recruiterId: String,
    private val channel: RTCDataChannel,
    private val timer: Timer
) : Event(repository) {

    override fun handleImpl() {
        if(channel.state.equals(RTCDataChannelState.OPEN)){
            timer.cancel()
            repository.logger.logRegular(LoggerLvl.LOW, "Recruiter $recruiterId connected")
            repository.recruiters[recruiterId]?.dataChannel = channel
        }
    }

}