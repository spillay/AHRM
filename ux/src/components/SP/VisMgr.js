import { select } from "d3";
import {Vis} from './Vis.js';
//import {Filter} from 'DataManager';
import Filter from './data/query/Filter';
import { settings } from './spconfig'

export function VisMgr(opts) {
    console.log("Init VisMgr");
    console.log(opts);
    this.name = opts.name;
    console.log(opts.name);
    console.log(opts.id);
    this.id = opts.id;
    this.root = null;
    this.current = null;
    if (typeof opts.child != "undefined"){
        if (opts.child != null){
            console.log("VisMgr: adding child");
            console.log(opts.child);
            var filters = []
            console.log("VisMgr: adding child");
            if (this.checkField(opts.child.dataModel.Time)){
                // Add the filter for time
                console.log("VisMgr: adding child before f1");
                var f1 = new  Filter({
                    filterType: "must",
                    filterSub: "Time",
                    Time: opts.child.dataModel.Time,
                    filterTimeTemplate: settings.TimeFilter
                });
                console.log("VisMgr: adding child");
                filters.push(f1);
            }
            console.log("VisMgr: adding child");
            var v = new Vis(opts.child,filters,opts.server);
            this.root = v;
            this.current = v;
        }
    }
}

VisMgr.method("reset",function() {
    this.current = this.root;
});

VisMgr.method("setContainer",function(container) {
    this.container = container;
    if (this.root !== null){
        this.root.setContainer(container);
    }
});
VisMgr.method("setVis",function(vm) {
    console.log("setting Vis");
    this.root = vm;
    this.current = vm;
});

VisMgr.method("createVis",function(opts) {
    console.log("setting Vis");
    var vm = new Vis(opts);
    this.root = vm;
    this.current = vm;
});
VisMgr.method("drawNext",function(d3Container,reactContainer,info) {
    console.log("drawNext " + info);
    if ( this.current.getChild() !== null){
        this.clean(d3Container);
        var ninfo = this.current.getDrillFilter(info);
        this.current = this.current.getChild();
        if (this.current.type == "Bar" || this.current.type == "Pie"){
            this.current.draw(d3Container,ninfo);
        }
        if (this.current.type == "DataGrid"){
            this.current.draw(reactContainer,ninfo);
        }
    }
});

VisMgr.method("draw",function(d3Container,reactContainer) {
    console.log("in draw ");
    console.log(this);
    this.clean(d3Container);
    if (this.current.type == "Bar" || this.current.type == "Pie"){
        this.current.draw(d3Container);
    }
    if (this.current.type == "DataGrid"){
        this.current.draw(reactContainer);
    }
    
});

VisMgr.method('clean',function(element){
    select(element).selectAll("*").remove();
});


VisMgr.method('checkField', function (obj) {
    if (typeof obj != "undefined"){
        if (obj != null){
            return true;
        }
    }
    return false;
});

