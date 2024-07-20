export class MapReduceChunk {

    readonly id: number;
    readonly data: Array<any>;

    constructor(id: number, data: Array<any>) {
        this.id = id;
        this.data = data;
    }

    static fromString(str: string): MapReduceChunk {
        const parsed = JSON.parse(str);

        const id: any = parsed.id;
        if(id === null || id === undefined || isNaN(id)) {
            throw new Error("MapReduceChunk: id must be a number");
        }

        const data: any = parsed.data;
        if(data === null || data === undefined || !Array.isArray(data)) {
            throw new Error("MapReduceChunk: data must be an array");
        }

        return new MapReduceChunk(id, data);
    }
}