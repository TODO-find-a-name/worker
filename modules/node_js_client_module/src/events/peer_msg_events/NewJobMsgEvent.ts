import {PeerMsgEvent} from "../PeerMsgEvent";
import {Job} from "../../domain/Job";
import {Recruiter} from "../../domain/Recruiter";
import {NEW_JOB_ACK} from "../../domain/PeerMsgTypes";
import {createJob} from "../../jobs/JobCreator";
import {PeerMessage} from "../../domain/PeerMsg";

export class NewJobMsgEvent extends PeerMsgEvent {

    async handleImpl(recruiter: Recruiter, peerMsg: PeerMessage) {
        if(recruiter.jobs.has(peerMsg.jobId)){
            this.repository.sendError(peerMsg.createCriticalErrorReply(
                "Tried to create a job with an id that already exists"
            ));
            return;
        }

        try {
            const job: Job | undefined = createJob(peerMsg);
            if(job === undefined){
                this.repository.sendError(peerMsg.createCriticalErrorReply(
                    "Tried to create a job with an unknown type"
                ));
            } else {
                recruiter.jobs.set(peerMsg.jobId, job);
                this.repository.sendPeerMsg(peerMsg.createReply(NEW_JOB_ACK));
            }
        } catch (e: any){
            this.repository.sendError(peerMsg.createCriticalErrorReply(
                "Exception while creating a job: " + e.message
            ));
        }
    }

}