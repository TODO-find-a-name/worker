import {PeerMsgEvent} from "../PeerMsgEvent";
import {Job} from "../../domain/Job";
import {Recruiter} from "../../domain/Recruiter";
import {TASK_RESULT} from "../../domain/PeerMsgTypes";
import {PeerMessage} from "../../domain/PeerMsg";
export class NewTaskMsgEvent extends PeerMsgEvent {

    async handleImpl(recruiter: Recruiter, peerMsg: PeerMessage): Promise<void> {
        const job: Job | undefined = recruiter.jobs.get(peerMsg.jobId);
        if(job === undefined){
            this.repository.sendError(peerMsg.createCriticalErrorReply(
                "The job requested for a new task was not found"
            ))
            return;
        }
        try {
            this.repository.sendPeerMsg(peerMsg.createReply(
                TASK_RESULT,
                job.handleNewTaskMessage(peerMsg)
            ));
        } catch (e: any){
            // TODO maybe it should exist something like a "USER_ERROR", generated by faulty code submitted by the user
            // so that the error can be propagated to the recruiter, making the user aware of it
            this.repository.sendError(peerMsg.createCriticalErrorReply(
                "Exception while performing the task: " + e.message
            ))
        }
    }

}