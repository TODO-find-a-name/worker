import {Repository} from "../domain/Repository";
import {PeerMessage} from "../domain/PeerMsg";
import {Event} from "./Event";
import {Recruiter} from "../domain/Recruiter";

export abstract class PeerMsgEvent implements Event {

    protected readonly repository: Repository;
    protected readonly msg: any;

    constructor(repository: Repository, msg: any) {
        this.repository = repository;
        this.msg = msg;
    }

    async handle(): Promise<void>{
        // I cannot parse the PeerMsg in the constructor, too much time for the interrupt
        const peerMsg: PeerMessage = PeerMessage.fromString(this.msg);
        /**
          I cannot search for the Recruiter in the constructor because it can happen that the core sends me a
          NewRecruiterMsg and then a PeerMsg BEFORE the NewRecruiterMsg's event is computed.
          In that case, the recruiter would not be found.

         Source: it happened
         */
        const recruiter: Recruiter | undefined = this.repository.recruiters.get(peerMsg.recruiterId);
        if(recruiter === undefined){
            this.repository.sendError(peerMsg.createCriticalErrorReply("Recruiter not found"));
        } else {
            await this.handleImpl(recruiter, peerMsg);
        }
    }

    abstract handleImpl(recruiter: Recruiter, peerMsg: PeerMessage): Promise<void>;

}