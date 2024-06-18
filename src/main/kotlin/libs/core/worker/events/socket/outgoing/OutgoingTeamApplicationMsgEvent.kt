package libs.core.worker.events.socket.outgoing

import libs.core.worker.SharedRepository
import libs.core.worker.events.Event
import libs.core.worker.socket.messages.TeamApplicationMsg
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

class OutgoingTeamApplicationMsgEvent(
    repository: SharedRepository, private val recruiterId: String
) : Event(repository) {

    override fun handleImpl() {
        repository.logger.logSocketOutgoing(
            LoggerLvl.HIGH, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, "Applying for team"
        )
        TeamApplicationMsg.send(repository, recruiterId){
            repository.logger.logSocketOutgoingAck(
                LoggerLvl.COMPLETE, SocketMsgType.TEAM_APPLICATION_NAME, recruiterId, it
            )
        }
    }

}