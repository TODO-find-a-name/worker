package libs.core.worker.events.socket.messages.incoming

import libs.core.worker.recruiter.Recruiter
import libs.core.worker.events.socket.messages.data.InterviewProposalMsgParsable
import libs.core.worker.Repository
import libs.core.worker.events.Event
import libs.core.worker.events.socket.messages.data.InterviewProposalMsg
import libs.core.worker.events.socket.messages.data.abstractions.SocketMsgType
import libs.core.worker.utils.LoggerLvl

class IncomingInterviewProposalMsgEvent(repository: Repository, private val payload:String) : Event(repository) {

    override fun handleImpl() {
        repository.parser.fromJson(payload, InterviewProposalMsgParsable::class.java).ifPresentOrElse(
            { parsedMsg -> parsedMsg.toChecked().ifPresentOrElse(
                {handleCheckedMsg(it)},
                {handleErrorOnMsgStructure("Incomplete")}
            )},
            {handleErrorOnMsgStructure("Unparsable")}
        )
    }

    private fun handleCheckedMsg(msg: InterviewProposalMsg) {
        val module = repository.modules[msg.module]
        if (module == null) {
            // TODO
            return;
        }
        val recruiterId = msg.from
        if(repository.recruiters.contains(recruiterId)){
            repository.logger.errorSocket(
                SocketMsgType.INTERVIEW_PROPOSAL_NAME, "Already working with Recruiter, discarding msg"
            )
        } else {
            repository.logger.logSocketIncoming(
                LoggerLvl.MID, SocketMsgType.INTERVIEW_PROPOSAL_NAME, recruiterId, "Creating new Recruiter"
            )
            val recruiter = Recruiter(recruiterId, module, repository)
            repository.recruiters[recruiterId] = recruiter
            recruiter.setRemoteDescription(msg.sessionDescription)
        }
    }

    private fun handleErrorOnMsgStructure(cause: String){
        repository.logger.errorSocket(SocketMsgType.INTERVIEW_PROPOSAL_NAME, "$cause msg, discarding it:\n$payload")
    }

}