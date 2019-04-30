import DeepSet from './DeepSet';
var obj = require('../SPObject');
var d3 = require("d3");


export default function EntropyHandler(opts) {
    console.log("Init EntropyHandler----------------------------------------------->");
    console.log(opts)

}
EntropyHandler.method("processByDay", function (data) {
    console.log("processByDay",data)
    Array.prototype.sumEmotions = function (props) {
        //console.log(Array.isArray(props),typeof(props))
        var emo = {}
        for (var i = 0, ilen = props.length; i < ilen; i++) {
          var total = 0.0
          for ( var j = 0, jlen = this.length; j < jlen; j++ ) {
              total += parseFloat(this[j][props[i]])
          }
          emo[props[i]]=total
        }
        return emo
    }
    const groups = data.reduce((groups, emo) => {
        const date = emo.date;
        if (!groups[date]) {
          groups[date] = [];
        }
        groups[date].push(emo);
        return groups;
      }, {});
      var emotions = ["Agreeableness","Anger","Anxiety","Contentment","Disgust","Fear","Interest","Joy","Pride","Relief","Sadness","Shame"]
      const groupArrays = Object.keys(groups).map((date) => {
        return {
          date,
          emotions: groups[date].sumEmotions(emotions)
        };
      });
      
      console.log("groupArrays length: ",groupArrays,groupArrays.length);
      var daily = []
      for (var i = 0, ilen = groupArrays.length; i < ilen; i++) {
        var obj = groupArrays[i].emotions
        var date = groupArrays[i].date
        var max = Object.keys(obj).reduce((a, b) => obj[a] > obj[b] ? a : b);
        console.log(max)
        daily.push({date,max})
      }
      var sdaily = daily.sort((a,b)=>{
        return new Date(a.date) - new Date(b.date);
      })
      console.log(sdaily)
      var a = sdaily.slice(0,sdaily.length-1)
      var b = sdaily.slice(1,sdaily.length)
      var c = a.map(function(e, i) {
        return [e.max, b[i].max];
        //return [[e.date,e.max], [b[i].date,b[i].max]];
      });
      console.log(c)
      var s = new DeepSet(c)
   
      // console.log("states :> ",s.getStates())
      // console.log("matrix :>",s.getMatrix())
      return s.getMatrix()
    //   var newGroups = []
    //   groups.forEach(g => {
    //       console.log(g.sum("Shame"))
    //   });
     
});
