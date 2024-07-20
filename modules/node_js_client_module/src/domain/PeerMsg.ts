import {CRITICAL_ERROR} from "./PeerMsgTypes";

export class PeerMessage {

    readonly msgId: number;
    readonly msgType: string;
    readonly jobId: string;
    readonly jobType: string;
    readonly recruiterId: string;
    readonly payload: string;

    constructor(
        msgId: number,
        msgType: string,
        jobId: string,
        jobType: string,
        recruiterId: string,
        payload: string
    ) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.jobId = jobId;
        this.jobType = jobType;
        this.recruiterId = recruiterId;
        this.payload = payload;
    }

    toString(): string {
        return JSON.stringify(this);
    }

    createReply(msgType: string, payload: string = ""): PeerMessage {
        return new PeerMessage(
            this.msgId,
            msgType,
            this.jobId,
            this.jobType,
            this.recruiterId,
            payload
        );
    }

    createCriticalErrorReply(msg: string): PeerMessage {
        return this.createReply(CRITICAL_ERROR, msg);
    }

    static fromString(str: string): PeerMessage {
        const parsed = JSON.parse(str);
        return new PeerMessage(
            parsed.msgId,
            parsed.msgType,
            parsed.jobId,
            parsed.jobType,
            parsed.recruiterId,
            parsed.payload
        );
    }

}