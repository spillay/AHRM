export default class DeepSet  {
    constructor(arr){
        this.data = []
        for(var i = 0; i< arr.length;i++){
            if (this.compare(arr[i])){
                this.data.push(arr[i])
            }
        }
    }
    compare(o) {
        for(var i =0; i< this.data.length;i++){
            console.log(this.data[i])
            if (this.data[i][0] == o[0] && this.data[i][1] == o[1]){
                return false
            }
        }
        return true
    }
    getStates(){
        var states = new Set([])
        for(var i =0; i< this.data.length;i++){
            if (!states.has(this.data[i][0])){ states.add(this.data[i][0])}
            if (!states.has(this.data[i][1])){ states.add(this.data[i][1])}
        }
        return states
    }
    getMatrix(){
        
    }
}

// export default function DeepSet(arr) {
//     console.log("Create DeepSet",arr)
//     super()
//     for(var i=0; i<arr.length;i++){
//         this.add2(arr[i])
//     }
// }
// DeepSet.prototype = Object.create(Set.prototype);
// DeepSet.prototype.constructor = DeepSet;
// DeepSet.prototype.deepCompare = function(a,b) {
//     console.log(a,b)
//     return true
// };
// DeepSet.prototype.add2 = function(o) {
//     console.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",this)
//     // for (let i of this)
//     //     if (this.deepCompare(o, i))
//     //         throw "Already existed";
//     Set.prototype.add.call(this, o);
// };
