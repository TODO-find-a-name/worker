package libs.core.worker.events.recruiter.state

import dev.onvoid.webrtc.RTCDataChannel
import libs.core.worker.Recruiter
import libs.core.worker.Repository
import libs.core.worker.events.RecruiterEvent
import libs.core.worker.gui.RecruiterConnectedGuiMsg
import libs.core.worker.utils.LoggerLvl
import java.util.*

class OnDataChannelOpenEvent(
    repository: Repository, recruiterId: String, private val channel: RTCDataChannel, private val timer: Timer
) : RecruiterEvent(OnDataChannelOpenEvent::class.simpleName.toString(), repository, recruiterId) {

    override fun handleImpl(recruiter: Recruiter) {
        timer.cancel()
        recruiter.isConnected = true
        recruiter.dataChannel = channel
        if(recruiter.module.addRecruiter(recruiterId)){
            repository.logger.log(LoggerLvl.LOW, "Recruiter $recruiterId connected")
            repository.guiSocket.send(RecruiterConnectedGuiMsg(recruiterId), repository.parser)
        } else {
            repository.removeRecruiter(recruiter, "Tried to add Recruiter multiple times to module " + recruiter.module.id())
        }
    }

}