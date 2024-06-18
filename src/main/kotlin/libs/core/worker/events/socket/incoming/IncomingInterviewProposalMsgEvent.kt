package libs.core.worker.events.socket.incoming

import libs.core.worker.recruiter.Recruiter
import libs.core.worker.socket.messages.InterviewProposalMsg
import libs.core.worker.SharedRepository
import libs.core.worker.events.Event
import libs.core.worker.socket.messages.InterviewProposalMsgChecked
import libs.core.worker.socket.messages.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

class IncomingInterviewProposalMsgEvent(
    repository: SharedRepository, private val payload:String
) : Event(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, InterviewProposalMsg::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(checkedMsg: InterviewProposalMsgChecked) {
        val recruiterId = checkedMsg.from
        if(repository.recruiters.contains(recruiterId)){
            repository.logger.errorSocket(
                SocketMsgType.INTERVIEW_PROPOSAL_NAME, "Already working with Recruiter, discarding msg"
            )
        } else {
            repository.logger.logSocketIncoming(
                LoggerLvl.MID, SocketMsgType.INTERVIEW_PROPOSAL_NAME, recruiterId, "Creating new Recruiter"
            )
            val recruiter = Recruiter(recruiterId, repository)
            repository.recruiters[recruiterId] = recruiter
            recruiter.setRemoteDescription(checkedMsg.sessionDescription)
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.INTERVIEW_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

}