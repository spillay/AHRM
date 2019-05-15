import DataHelper from './DataHelper';


export default class GraphHelper {
    constructor() {
       
    }
    Exists(nodes,n){
        var fd = false;
        nodes.forEach(o=>{
            if (n.id == o.id){
                fd = true
            }
        })
        return fd;
    }
    getGraphNodes(nodeList){
        var nodes = [];
        nodeList.forEach(n => {
           if (!this.Exists(nodes,n)){
                var len = nodes.push(n);
           }
        });
        return nodes;
    }
    getGraphLinks(root){
        return root.getLinks();
    }
    getGraphData(root){
        var nodes = this.getGraphNodes(root.getNodeData());
        var links = this.getGraphLinks(root);
        var data = {};
        data["nodes"] = nodes;
        data["links"] = links;
        //console.log("nodes ",nodes);
        //console.log("links ",links);
        return data;
    }

}
