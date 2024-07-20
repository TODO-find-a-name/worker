import {Repository} from "./domain/Repository";
import {registerSocketEventsListeners} from "./RegisterSocketEventsListeners";
import {startMainLoop} from "./MainLoop";

const repository: Repository = new Repository();
registerSocketEventsListeners(repository);
repository.socket.connect();
startMainLoop(repository);
