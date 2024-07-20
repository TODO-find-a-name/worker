import {Recruiter} from "../../domain/Recruiter";
import {RecruitersManagingEvent} from "../RecruitersManagingEvent";

export class NewRecruiterMsgEvent extends RecruitersManagingEvent {

    async handle(): Promise<void> {
        // No need to send an error to the core if the job to created already exists
        if(!this.repository.recruiters.has(this.recruiterId)){
            this.repository.recruiters.set(this.recruiterId, new Recruiter(this.recruiterId));
        }
    }

}