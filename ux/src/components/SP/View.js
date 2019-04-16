import { VisMgr } from './VisMgr.js'

export function View(opts) {
    console.log("Init View");
    this.VisMgrs = [];
}

View.method("addManager",function(vm) {
    console.log("addManager")
    this.VisMgrs.push(vm);
});

View.method("createManager",function(opts) {
    var vm = new VisMgr(opts);
    this.VisMgrs.push(vm);
});


View.method("getTreeData",function() {
    var data = {
        name: 'Components',
        id: -1,
        toggled: true,
        children: []
    }
    this.VisMgrs.map(function(item){
        data.children.push(
           { "name":item.name,"id":item.id }
        );
    })
    console.log(JSON.stringify(data));
    return data;
});

View.method("getManager",function(id) {
    var sel = null;
    // TODO: Fix break when item is found, would improve performance
    this.VisMgrs.forEach(e => {
        console.log("s" + e.id + ":" + id + "e");
        if ( e.id === id){
            console.log("returning: " + e.name);
            sel=e;
        }
    });
    console.log(sel);
    return sel;
});