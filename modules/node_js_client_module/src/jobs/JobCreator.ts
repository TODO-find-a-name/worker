import {Job} from "../domain/Job";
import {MapReduceJob} from "./mapReduce/MapReduceJob";
import {PeerMessage} from "../domain/PeerMsg";
import {MAP_REDUCE_JOB} from "../domain/AvailableJobs";

export function createJob(peerMsg: PeerMessage): Job | undefined {
    // extend this when a new kind of job is created
    switch(peerMsg.jobType){
        case MAP_REDUCE_JOB: return new MapReduceJob(peerMsg.jobId, peerMsg);
    }
    return undefined;
}