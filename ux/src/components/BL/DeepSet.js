export default class DeepSet  {
    constructor(arr){
        this.cdata = arr
        this.data = []
        for(var i = 0; i< arr.length;i++){
            if (this.compare(arr[i])){
                this.data.push(arr[i])
            }
        }
        console.log("data len ",this.data.length," cdata len ",this.cdata.length)
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
    getCount(from,to){
        var cnt = 0
        for(var i =0; i< this.cdata.length;i++){
            //console.log("from ",from," to ",to)
            //console.log("from cdata ",this.cdata[i][0]," to cdata ",this.cdata[i][1],i)
            if (this.cdata[i][0]===from && this.cdata[i][1]===to){
                cnt = cnt + 1
                console.log("found",cnt)
            }
        }
        return cnt
    }
    getMatrix(){
        console.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        var states = this.getStates()
        var data = {}
        data["states"] = JSON.parse(JSON.stringify(Array.from(states)));
        var matrix = []
        states.forEach(i=>{
            console.log("ith value ",i)
            var row = []
            states.forEach(j=>{
                console.log("jth value ",j)
                row.push(this.getCount(i,j))
            })
            matrix.push(row)
            console.log("mat",matrix)
        })
        //data["matrix"] = JSON.stringify(matrix)
        var stocmatrix = []
        matrix.forEach(r=>{
            var nr = []
            var sum = r.reduce((partial_sum, a) => partial_sum + a);
            r.forEach(e=>{
                nr.push(e/sum)
            })
            stocmatrix.push(nr)
        })
        data["points"] = JSON.parse(JSON.stringify(stocmatrix))
        return data
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
