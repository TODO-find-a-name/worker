import {io, Socket} from 'socket.io-client';
import {Recruiter} from "./Recruiter";
import {PeerMessage} from "./PeerMsg";
import {CORE_ADDRESS, ERROR_CHANNEL, MODULE_CODE} from "./Utils";
import {Event} from "../events/Event";

export class Repository {

    readonly socket: Socket;
    readonly recruiters: Map<string, Recruiter> = new Map();
    readonly events: Array<Event> = []

    constructor() {
        this.socket = io(CORE_ADDRESS, {
            transports: ["websocket"], autoConnect: false
        });
    }

    emitMsg(channel: string, msg: string): void {
        this.socket.emit(channel, msg);
    }

    sendPeerMsg(peerMsg: PeerMessage): void {
        this.socket.emit(MODULE_CODE, peerMsg.toString());
    }

    sendError(peerMsg: PeerMessage): void {
        this.socket.emit(ERROR_CHANNEL, peerMsg.toString());
    }

}