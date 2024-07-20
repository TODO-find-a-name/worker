import {Event} from "./Event";
import {Repository} from "../domain/Repository";

export abstract class RecruitersManagingEvent implements Event {

    protected readonly repository: Repository;
    protected readonly recruiterId: string;

    constructor(repository: Repository, msg: any) {
        this.repository = repository;
        this.recruiterId = msg;
    }

    abstract handle(): Promise<void>;

}