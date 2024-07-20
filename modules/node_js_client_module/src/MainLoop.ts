import {Repository} from "./domain/Repository";
import {Event} from "./events/Event";
import {MAIN_LOOP_TIMEOUT_MS, MAX_EVENTS_PER_LOOP_EXECUTION} from "./domain/Utils";

let mainLoop = async(mainLoopFunction: any, repository: Repository): Promise<void> => {
    const eventsToHandle: Array<Event> = repository.events.splice(0, MAX_EVENTS_PER_LOOP_EXECUTION);
    const length: number = eventsToHandle.length;
    for(let i: number = 0; i < length; i++) {
        await eventsToHandle[i].handle();
    }
    setTimeout(mainLoopFunction, MAIN_LOOP_TIMEOUT_MS, mainLoopFunction, repository);
}

export function startMainLoop(repository: Repository): void {
    setTimeout(mainLoop, MAIN_LOOP_TIMEOUT_MS, mainLoop, repository);
}