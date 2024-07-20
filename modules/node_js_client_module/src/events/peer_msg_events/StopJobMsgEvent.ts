import {PeerMsgEvent} from "../PeerMsgEvent";
import {Recruiter} from "../../domain/Recruiter";
import {STOP_JOB_ACK} from "../../domain/PeerMsgTypes";
import {PeerMessage} from "../../domain/PeerMsg";

export class StopJobMsgEvent extends PeerMsgEvent {

    async handleImpl(recruiter: Recruiter, peerMsg: PeerMessage): Promise<void> {
        // No need to send an error to the core if the job to delete was already deleted
        recruiter.jobs.delete(peerMsg.jobId);
        this.repository.sendPeerMsg(peerMsg.createReply(STOP_JOB_ACK));
    }

}