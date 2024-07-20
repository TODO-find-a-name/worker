import {Job} from "./Job";

export class Recruiter {

    readonly recruiterId: string;
    readonly jobs: Map<string, Job> = new Map();

    constructor(recruiterId: string) {
        this.recruiterId = recruiterId;
    }

}