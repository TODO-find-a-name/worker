import {RecruitersManagingEvent} from "../RecruitersManagingEvent";

export class RemoveRecruiterMsgEvent extends RecruitersManagingEvent {

    async handle() {
        // No need to send an error to the core if the recruiter to delete was already deleted
        this.repository.recruiters.delete(this.recruiterId);
    }
}