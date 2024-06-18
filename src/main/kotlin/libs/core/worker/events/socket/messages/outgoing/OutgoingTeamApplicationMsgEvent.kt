package libs.core.worker.events.socket.messages.outgoing

import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.RemoveRecruiterEvent
import libs.core.worker.events.socket.messages.data.TeamApplicationMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

// it isn't a RecruiterEvent because the Recruiter isn't created yet
class OutgoingTeamApplicationMsgEvent(repository: Repository, private val recruiterId: String) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logSocketOutgoing(
            LoggerLvl.HIGH, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, "Applying for team"
        )
        TeamApplicationMsg.send(repository, recruiterId){
            repository.logger.logSocketOutgoingAck(
                LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
            )
            if(!it){
                RemoveRecruiterEvent(repository, recruiterId).handle()
            }
        }
    }

}