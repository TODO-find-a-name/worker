import {PeerMessage} from "./PeerMsg";

export abstract class Job {

    readonly jobId: string;

    protected constructor(jobId: string, _newJobMsg: PeerMessage) {
        this.jobId = jobId;
    }

    abstract handleNewTaskMessage(msg: PeerMessage): string;

}