import {Job} from "../../domain/Job";
import {PeerMessage} from "../../domain/PeerMsg";
import {NewMapReduceJobMsgPayload} from "./NewMapReduceJobMsgPayload";
import {MapReduceFunctions} from "./MapReduceFunctions";
import {MapReduceChunk} from "./MapReduceChunk";

export class MapReduceJob extends Job{

    private readonly mapFunction: (x: any) => any;
    private readonly reduceFunction: (y1: any, y2: any) => any;
    
    constructor(jobId: string, newJobMsg: PeerMessage){
        super(jobId, newJobMsg);
        const mapReduceFunctions: MapReduceFunctions<any, any> = this.createMapReduceFunctionsInstance(
            NewMapReduceJobMsgPayload.fromString(newJobMsg.payload)
        );
        this.mapFunction = mapReduceFunctions.mapFunction;
        this.reduceFunction = mapReduceFunctions.reduceFunction;
    }

    private createMapReduceFunctionsInstance(p: NewMapReduceJobMsgPayload): MapReduceFunctions<any, any> {
        /*
            Basically, "source" contains the MapReduceFunctions interface implementation recruiter-side, including all its dependencies.
            Knowing the actual name of the concrete class (p.className), I'm able to create a new instance of said class.
        */
        return eval(p.source + ' new ' + p.className + '()');
    }

    handleNewTaskMessage(msg: PeerMessage): string {
        const chunk: MapReduceChunk = MapReduceChunk.fromString(msg.payload);
        return JSON.stringify(new MapReduceChunk(
            chunk.id,
            chunk.data.map(this.mapFunction).reduce(this.reduceFunction)
        ));
    }

}