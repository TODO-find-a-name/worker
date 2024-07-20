export interface MapReduceFunctions<X, Y>{

    mapFunction: (x: X) => Y;
    reduceFunction: (y1: Y, y2: Y) => Y;

}