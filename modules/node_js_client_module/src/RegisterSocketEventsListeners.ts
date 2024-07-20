import {Repository} from "./domain/Repository";
import {NewRecruiterMsgEvent} from "./events/recruiters_managing_events/NewRecruiterMsgEvent";
import {NewJobMsgEvent} from "./events/peer_msg_events/NewJobMsgEvent";
import {NewTaskMsgEvent} from "./events/peer_msg_events/NewTaskMsgEvent";
import {StopJobMsgEvent} from "./events/peer_msg_events/StopJobMsgEvent";
import {NEW_JOB, NEW_TASK, STOP_JOB} from "./domain/PeerMsgTypes";
import {NEW_RECRUITER, REMOVE_RECRUITER} from "./domain/RecruitersManagingEvents";
import {INIT_CHANNEL, MODULE_CODE} from "./domain/Utils";
import {RemoveRecruiterMsgEvent} from "./events/recruiters_managing_events/RemoveRecruiterEvent";

const SOCKET_EVENT_CONNECT: string = "connect";
const SOCKET_EVENT_DISCONNECT: string = "disconnect";
const SOCKET_EVENT_CONNECT_ERROR: string = "connect_error";

export function registerSocketEventsListeners(repository: Repository): void {
    repository.socket.on(SOCKET_EVENT_CONNECT, () => {
        console.log("Connected to core");
        repository.emitMsg(INIT_CHANNEL, MODULE_CODE);
    });

    repository.socket.on(SOCKET_EVENT_DISCONNECT, () => {
        console.log("Disconnected from core, shutting down");
        process.exit();
    });

    repository.socket.on(SOCKET_EVENT_CONNECT_ERROR, (err) => {
        console.log(`Error while connecting to Core : ${err.message}\n\n Shutting down`);
        process.exit();
    });

    repository.socket.on(NEW_RECRUITER, (data: any, callback) => {
        callback(true)
        repository.events.push(new NewRecruiterMsgEvent(repository, data));
    });

    repository.socket.on(REMOVE_RECRUITER, (data: any, callback) => {
        repository.events.push(new RemoveRecruiterMsgEvent(repository, data));
    });

    repository.socket.on(NEW_JOB, (data: any, callback) => {
        callback(true)
        repository.events.push(new NewJobMsgEvent(repository, data));
    });

    repository.socket.on(NEW_TASK, (data: any, callback) => {
        callback(true)
        repository.events.push(new NewTaskMsgEvent(repository, data));
    });

    repository.socket.on(STOP_JOB, (data: any, callback) => {
        callback(true)
        repository.events.push(new StopJobMsgEvent(repository, data));
    });

}
