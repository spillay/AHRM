package com.dsleng.nlp

//import breeze.linalg.
import breeze.linalg.{DenseMatrix,DenseVector}
import breeze.numerics._
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.Vector
import play.api.libs.json._
import scala.math._


case class Points(data:  IndexedSeq[Double])
case class Info(states: IndexedSeq[String],pts: IndexedSeq[Points]){
  def getArray(): List[List[Double]]={
    pts.map(i=>{
      (i.data.map(p=>p)).toList
    }).toList
  }
}
object Points {
  implicit val pointsWrites = new Writes[Points] {
    def writes(a: Points) = Json.toJson(a.data)
  }
  implicit val pointsReads = new Reads[Points] {
      def reads(json: JsValue): JsResult[Points] = {
          //println("json",json)
          json match {
            case JsArray(seq) =>
              //println(seq.map(s=>s))
              val ab = seq.map(s=>s.as[Double]).toIndexedSeq
              JsSuccess(Points(ab))
              //JsSuccess(Points(ArrayBuffer(0,1,0)))
            case _ => JsError("Must be an array")
          }
         //JsSuccess(Points(ArrayBuffer(0,1,0)))
      }
   }
}
object Info {
    implicit val infoWrites = new Writes[Info] {
      def writes(a: Info) = Json.obj(
        "states" -> Json.toJson(a.states),
        "points" ->  Json.toJson(a.pts)
        ) 
    }
    implicit val infoReads = new Reads[Info] {
      def reads(json: JsValue): JsResult[Info] = {
          //println("json",json)
          var pts: IndexedSeq[Points] = IndexedSeq()
          var states: IndexedSeq[String] = IndexedSeq()
          val jstates = json.\("states").as[JsArray]
          val jpoints =  json.\("points").as[JsArray]
          
          // println(jstates)
          // println(jpoints)
          jpoints match {
            case JsArray(seq) =>
              pts = seq.map(s=>s.as[Points]).toIndexedSeq
            case _ => JsError("Must be an array of points")
          }
          jstates match {
            case JsArray(seq) =>
              states = seq.map(s=>s.as[String]).toIndexedSeq
            case _ => JsError("Must be an array of states")
          }
          if (states.length > 0 && pts.length > 0){
            JsSuccess(Info(states,pts))
          } else {
            JsError("Must be an array")
          }
         //JsSuccess(Points(ArrayBuffer(0,1,0)))
      }
   }
}

class MCAnalysis {
  def process(input: String): String ={
    val info = Json.parse(input).as[Info]
    //println(info)
    val vec = getVector(info.getArray())
    val ss = getStateVector(info.states, vec)
    val nvec = getNormVector(vec)
    val nss = getStateVector(info.states, nvec)
    val entropy = getEntropy(info.states, info.getArray(), nss)
    val normentropy = entropy/getMaxEntropy()
    val jss = Json.toJson(ss)
    val jobj = Json.obj(
        "entropy"->entropy,
        "norm_entropy"->normentropy,
        "steady_states" -> Json.toJson(ss),
        "norm_steady_states" -> Json.toJson(nss)
    )
    Json.stringify(jobj)
  }
  def getVector(data: List[List[Double]]/*Requires Stochastic Matrix*/): DenseVector[Double] = {   
    val mat = DenseMatrix(((data).toArray):_*)
    //println(mat)
    //println(mat.rows)
    //println(mat.cols)
    
    val s=0.85
    val maxerr=0.000001
    val rows = mat.rows
    val cols = mat.cols
    
    // find Sink Values
    var sinkArray = new ArrayBuffer[Double]()
    for(i<-0 to cols-1){
      //val s = mat(*,::).reduce( _ + _ )
      val s = mat(i,::).t.reduce(_+_)
      if (s == 0){
        sinkArray += 1
      } else {
        sinkArray += 0
      }
    }
    val sink = DenseVector(sinkArray.toArray)
    //println(sink)
    
    
    var ro = DenseVector.zeros[Double](rows)
    var r = DenseVector.ones[Double](rows)
    val e = DenseVector.ones[Double](rows)
    
  
    
    while((breeze.numerics.abs(r-ro)).reduce(_+_) > maxerr){
      ro = r.copy
      for(i<-0 to rows-1){
        var Ai = mat(::,i)
        Ai = Ai.*:*(s)
        //println("Ai",Ai)
        var Di = sink  mapValues { _/rows }
        Di = Di.*:*(s)
        //println("Di",Di)
        var Ei = e mapValues { _/rows }
        Ei = Ei.*:*(1-s)
        //println("Ei",Ei)
        val sum = Ai+Di+Ei
        //println(ro)
        //println(sum.t)
        r(i) = sum.t * ro
      }
      //println(r)
    }
    //println("result",r)
    return r
  }
  def getNormVector(r: DenseVector[Double]): DenseVector[Double] = {
    val s = r.reduce(_+_)
    //println(s)
    val v = r./:/(s)
    //println(v)
    return v
  }
  def getStateVector(states:IndexedSeq[String],vec: DenseVector[Double]): IndexedSeq[(String,Double)] = {
    val c = states.zip(vec.toArray)
    //println(c)
    return c
  }
  def getMaxEntropy(): Double = {
    val slen = 14 //states.length
    val max_entropy = -math.pow(slen,2)*math.pow((1.0/slen),2)*math.log(1.0/slen)
    //println("max_entropy",max_entropy)
    return max_entropy
  }
  def getEntropy(states:IndexedSeq[String],sm: List[List[Double]],steady_states: IndexedSeq[(String,Double)]): Double ={
    //number_of_states = len(states)
    //max_entropy = -math.pow(number_of_states, 2) * math.pow((1.0/number_of_states), 2) * math.log(1.0/number_of_states)
    // 2.6390573296152584
   
    val mat = DenseMatrix(((sm).toArray):_*)
    //println(mat)
    
    var entropy = 0.0
    var ir,ic = 0
    //println("steady_states",steady_states)
    states.foreach(si=>{
      //println(si,ir)
      ic = 0
      states.foreach(sj=>{
        val p = mat(ir,ic)
        //println(ir,ic," : ",p)
        if (p > 0.0){
          val s1 = (steady_states.find(p=>(p._1==sj))).getOrElse(null)
          if (s1!=null){
            //println("s",s1._2,"p",p,"log",math.log(p),s1._2*p*math.log(p)) 
            entropy -= s1._2*p*math.log(p)
            //println("entropy: ",entropy)
          }
        }
        ic += 1
      })
      ir += 1
    })
    //println("entropy",entropy)
    return entropy
  }
}

object MCAnalysis extends App {
  println("Start MCAnalysis")
  //testJson()
  processStr()
  
  //process()
  
  def processStr(){
    val input = "{\"states\":[\"A\",\"B\",\"C\"],\"points\":[[0,1,0],[0.5,0.5,0],[1,0,0]]}"
    val o = new MCAnalysis()
    println(o.process(input))
  }
  def process(){
    var states = List("A","B","C").toIndexedSeq
    println(Json.toJson(states))
    // Stochastic Matrix
    var info = new Info(states,ArrayBuffer(
        new Points(ArrayBuffer(0,1,0)),
        new Points(ArrayBuffer(0.5,0.5,0)),
        new Points(ArrayBuffer(1,0,0))))
    
    println(Json.stringify(Json.toJson(info)))
    
    val o = new MCAnalysis()
    val vec = o.getVector(info.getArray())
    val ss = o.getStateVector(states, vec)
    println(o.getEntropy(states, info.getArray(), ss))
  }
  def testPoints(){
     //var pts = new Points(ArrayBuffer(new Point(0),new Point(1),new Point(0)))
    var pts = new Points(ArrayBuffer(0,1,1))
    val r = Json.toJson(pts)
    val s = Json.stringify(r)
    println(s)
    
    val ps = Json.fromJson[Points](Json.parse(s))
    println(ps)
    
    val points = Json.parse(s).as[Points]
  }
  def testJson(){ 
    // Following is how to process it
    var states = IndexedSeq("A","B","C")
    var i = new Info(states,ArrayBuffer(new Points(ArrayBuffer(0,1,0.5)),new Points(ArrayBuffer(0,1,0))))
    val inStr = Json.toJson(i)
    println(inStr)
    val pr = Json.parse(Json.stringify(inStr))
    println("result",pr)
    val in = pr.as[Info]
    println("info",in)
    System.exit(0)
    
  }
}