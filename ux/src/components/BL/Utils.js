var obj = require('../SPObject');


export default function Utils() {
    console.log("Init Utils----------------------------------------------->");
}
Utils.method("assert", function (condition, message) {
    if (!condition) { throw message || "Assertion failed"; }
})
Utils.method("getopt", function (opt,field,defaultval) {
    if(opt.hasOwnProperty(field)) {
      return opt[field];
    } else {
      return defaultval;
    }
})
Utils.method("zeros", function (n) {
    if(typeof(n)==='undefined' || isNaN(n)) { return []; }
    if(typeof ArrayBuffer === 'undefined') {
      // lacking browser support
      var arr = new Array(n);
      for(var i=0;i<n;i++) { arr[i]= 0; }
      return arr;
    } else {
      return new Float64Array(n); // typed arrays are faster
    }
})

Utils.method("sign",function(x) { return x > 0 ? 1 : x < 0 ? -1 : 0; })
Utils.method("randn2d",function(n,d,s) {
    var uses = typeof s !== 'undefined';
    var x = [];
    for(var i=0;i<n;i++) {
      var xhere = [];
      for(var j=0;j<d;j++) { 
        if(uses) {
          xhere.push(s); 
        } else {
          xhere.push(this.randn(0.0, 1e-4)); 
        }
      }
      x.push(xhere);
    }
    return x;
  })
  Utils.method("L2",function(x1, x2) {
    var D = x1.length;
    var d = 0;
    for(var i=0;i<D;i++) { 
      var x1i = x1[i];
      var x2i = x2[i];
      d += (x1i-x2i)*(x1i-x2i);
    }
    return d;
  })
  Utils.method("xtod",function(X) {
    var N = X.length;
    var dist = this.zeros(N * N); // allocate contiguous array
    for(var i=0;i<N;i++) {
      for(var j=i+1;j<N;j++) {
        var d = this.L2(X[i], X[j]);
        dist[i*N+j] = d;
        dist[j*N+i] = d;
      }
    }
    return dist;
  })
  Utils.method("randn",function(mu, std){ return mu+this.gaussRandom(false,0.0)*std; })

  //var return_v = false;
  //var v_val = 0.0;
  Utils.method("gaussRandom",function(return_v,v_val) {
    if(return_v) { 
      return_v = false;
      return v_val; 
    }
    var u = 2*Math.random()-1;
    var v = 2*Math.random()-1;
    var r = u*u + v*v;
    if(r == 0 || r > 1) return this.gaussRandom(return_v,v_val);
    var c = Math.sqrt(-2*Math.log(r)/r);
    v_val = v*c; // cache this for next function call for efficiency
    return_v = true;
    return u*c;
  })

  // Displaying Functions
Utils.method("displayArr", function (arr,name) {
    console.log(name)
    var v = "<div>"
    v += "<H1>" + name + "</H1>"
    arr.map((item, index) => (
        v +=  "<p>Index: " + index + " item " + item +  "</p>"
    ))
    v += "</div>";
    return v;
})