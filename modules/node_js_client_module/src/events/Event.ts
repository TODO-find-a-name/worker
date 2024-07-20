export interface Event {

    handle(): Promise<void>;

}