import Utils from './Utils';
var obj = require('../SPObject');


export default function TSNE(opts) {
    console.log("Init TSNE----------------------------------------------->");
    var opt = opt || {};
    this.perplexity = this.getopt(opt, "perplexity", 30); // effective number of nearest neighbors
    this.dim = this.getopt(opt, "dim", 2); // by default 2-D tSNE
    this.epsilon = this.getopt(opt, "epsilon", 10); // learning rate
    this.iter = 0;
    this.ddata = "";

}
TSNE.inherits(Utils)
TSNE.method("init", function (D) {
  this.display(D,"Original Arr");
    var N = D.length;
    this.assert(N > 0, " X is empty? You must have some data!");
    // convert D to a (fast) typed array version
    var dists = this.zeros(N * N); // allocate contiguous array
    this.display(dists,"Zero Arr");
    for(var i=0;i<N;i++) {
      for(var j=i+1;j<N;j++) {
          var d = D[i][j];
          dists[i*N+j] = d;
          dists[j*N+i] = d;
      }
    }
    this.display(dists,"Distance Arr");
    this.P = this.d2p(dists, this.perplexity, 1e-4);
    this.display(this.P,"Probability Arr");
    this.N = N;
    this.initSolution(); // refresh this
})
TSNE.method("initDataDist",function(D) {
  var N = D.length;
  this.assert(N > 0, " X is empty? You must have some data!");
  // convert D to a (fast) typed array version
  var dists = this.zeros(N * N); // allocate contiguous array
  for(var i=0;i<N;i++) {
    for(var j=i+1;j<N;j++) {
      var d = D[i][j];
      dists[i*N+j] = d;
      dists[j*N+i] = d;
    }
  }
  this.P = this.d2p(dists, this.perplexity, 1e-4);
  this.N = N;
  this.initSolution(); // refresh this
})

TSNE.method("initSolution", function () {
      this.Y = this.randn2d(this.N, this.dim); // the solution
      this.gains = this.randn2d(this.N, this.dim, 1.0); // step gains to accelerate progress in unchanging directions
      this.ystep = this.randn2d(this.N, this.dim, 0.0); // momentum accumulator
      this.iter = 0;

})
TSNE.method("getSolution", function() {
  return this.Y;
})
TSNE.method("step", function() {
  this.iter += 1;
  var N = this.N;

  var cg = this.costGrad(this.Y); // evaluate gradient
  var cost = cg.cost;
  var grad = cg.grad;

  // perform gradient step
  var ymean = this.zeros(this.dim);
  for(var i=0;i<N;i++) {
    for(var d=0;d<this.dim;d++) {
      var gid = grad[i][d];
      var sid = this.ystep[i][d];
      var gainid = this.gains[i][d];

      // compute gain update
      var newgain = this.sign(gid) === this.sign(sid) ? gainid * 0.8 : gainid + 0.2;
      if(newgain < 0.01) newgain = 0.01; // clamp
      this.gains[i][d] = newgain; // store for next turn

      // compute momentum step direction
      var momval = this.iter < 250 ? 0.5 : 0.8;
      var newsid = momval * sid - this.epsilon * newgain * grad[i][d];
      this.ystep[i][d] = newsid; // remember the step we took

      // step!
      this.Y[i][d] += newsid; 

      ymean[d] += this.Y[i][d]; // accumulate mean so that we can center later
    }
  }

  // reproject Y to be zero mean
  for(var i=0;i<N;i++) {
    for(var d=0;d<this.dim;d++) {
      this.Y[i][d] -= ymean[d]/N;
    }
  }

  //if(this.iter%100===0) console.log('iter ' + this.iter + ', cost: ' + cost);
  //console.log(cost,this.Y);
  //this.display(this.Y,"Result")
  return cost; // return current cost
})
TSNE.method("costGrad",function(Y) {
  var N = this.N;
  var dim = this.dim; // dim of output space
  var P = this.P;

  var pmul = this.iter < 100 ? 4 : 1; // trick that helps with local optima

  // compute current Q distribution, unnormalized first
  var Qu = this.zeros(N * N);
  var qsum = 0.0;
  for(var i=0;i<N;i++) {
    for(var j=i+1;j<N;j++) {
      var dsum = 0.0;
      for(var d=0;d<dim;d++) {
        var dhere = Y[i][d] - Y[j][d];
        dsum += dhere * dhere;
      }
      var qu = 1.0 / (1.0 + dsum); // Student t-distribution
      Qu[i*N+j] = qu;
      Qu[j*N+i] = qu;
      qsum += 2 * qu;
    }
  }
  // normalize Q distribution to sum to 1
  var NN = N*N;
  var Q = this.zeros(NN);
  for(var q=0;q<NN;q++) { Q[q] = Math.max(Qu[q] / qsum, 1e-100); }

  var cost = 0.0;
  var grad = [];
  for(var i=0;i<N;i++) {
    var gsum = new Array(dim); // init grad for point i
    for(var d=0;d<dim;d++) { gsum[d] = 0.0; }
    for(var j=0;j<N;j++) {
      cost += - P[i*N+j] * Math.log(Q[i*N+j]); // accumulate cost (the non-constant portion at least...)
      var premult = 4 * (pmul * P[i*N+j] - Q[i*N+j]) * Qu[i*N+j];
      for(var d=0;d<dim;d++) {
        gsum[d] += premult * (Y[i][d] - Y[j][d]);
      }
    }
    grad.push(gsum);
  }

  return {cost: cost, grad: grad};
})


// Distance to Probability
TSNE.method("d2p", function (D, perplexity, tol) {
  var Nf = Math.sqrt(D.length); // this better be an integer
  var N = Math.floor(Nf);
  this.assert(N === Nf, "D should have square number of elements.");
  var Htarget = Math.log(perplexity); // target entropy of distribution
  var P = this.zeros(N * N); // temporary probability matrix

  var prow = this.zeros(N); // a temporary storage compartment
  for(var i=0;i<N;i++) {
    var betamin = -Infinity;
    var betamax = Infinity;
    var beta = 1; // initial value of precision
    var done = false;
    var maxtries = 50;

    // perform binary search to find a suitable precision beta
    // so that the entropy of the distribution is appropriate
    var num = 0;
    while(!done) {
      //debugger;

      // compute entropy and kernel row with beta precision
      var psum = 0.0;
      for(var j=0;j<N;j++) {
        var pj = Math.exp(- D[i*N+j] * beta);
        if(i===j) { pj = 0; } // we dont care about diagonals
        prow[j] = pj;
        psum += pj;
      }
      // normalize p and compute entropy
      var Hhere = 0.0;
      for(var j=0;j<N;j++) {
        if(psum == 0) {
           var pj = 0;
        } else {
           var pj = prow[j] / psum;
        }
        prow[j] = pj;
        if(pj > 1e-7) Hhere -= pj * Math.log(pj);
      }

      // adjust beta based on result
      if(Hhere > Htarget) {
        // entropy was too high (distribution too diffuse)
        // so we need to increase the precision for more peaky distribution
        betamin = beta; // move up the bounds
        if(betamax === Infinity) { beta = beta * 2; }
        else { beta = (beta + betamax) / 2; }

      } else {
        // converse case. make distrubtion less peaky
        betamax = beta;
        if(betamin === -Infinity) { beta = beta / 2; }
        else { beta = (beta + betamin) / 2; }
      }

      // stopping conditions: too many tries or got a good precision
      num++;
      if(Math.abs(Hhere - Htarget) < tol) { done = true; }
      if(num >= maxtries) { done = true; }
    }

    // console.log('data point ' + i + ' gets precision ' + beta + ' after ' + num + ' binary search steps.');
    // copy over the final prow to P at row i
    for(var j=0;j<N;j++) { P[i*N+j] = prow[j]; }

  } // end loop over examples i

  // symmetrize P and normalize it to sum to 1 over all ij
  var Pout = this.zeros(N * N);
  var N2 = N*2;
  for(var i=0;i<N;i++) {
    for(var j=0;j<N;j++) {
      Pout[i*N+j] = Math.max((P[i*N+j] + P[j*N+i])/N2, 1e-100);
    }
  }

  return Pout;
})
TSNE.method("getDisplayData", function () {
  return this.ddata;
})
TSNE.method("display", function (arr,name) {
  this.ddata += this.displayArr(arr,name);
})
