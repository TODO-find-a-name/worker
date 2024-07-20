export class NewMapReduceJobMsgPayload {

    readonly source: string;
    readonly className: string;

    constructor(source: string, className: string) {
        this.source = source;
        this.className = className;
    }

    static fromString(str: string): NewMapReduceJobMsgPayload {
        const parsed = JSON.parse(str);
        return new NewMapReduceJobMsgPayload(
            this.extractNonEmptyString(parsed, "source"),
            this.extractNonEmptyString(parsed, "className")
        );
    }

    static extractNonEmptyString(parsed: any, name: string): string {
        let res: any = parsed[name];
        if(res !== null && res !== undefined && typeof res === "string") {
            res = res.trim();
            if(res.length > 0){
                return res;
            }
        }
        throw new Error("NewMapReduceJobMsgPayload: " + name + " must be a non-empty string");
    }
}
