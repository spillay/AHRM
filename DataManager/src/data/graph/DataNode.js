var obj = require('../../SPObject');
import DataHelper from '../../DataHelper';


function sanitizeJSON(unsanitized) {
    return unsanitized.replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t").replace(/\f/g, "\\f").replace(/"/g, "\\\"").replace(/'/g, "\\\'").replace(/\&/g, "\\&");
}
function spsanitizeJSON(unsanitized) {
    return unsanitized.replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t").replace(/\f/g, "\\f").replace(/"/g, "\\\"").replace(/\&/g, "\\&");
}


export default class DataNode {
    constructor(id, name, group,props={cnt:0}) {
        this.nodes = [];
        this.id = spsanitizeJSON(id);
        this.name = name;
        this.group = group;
        this.dh = new DataHelper();
        this.query = this.dh.getModel().getQuery("To", "Terms");
        this.complete = false;
        this.props = props;
    }

    setProps(props){
        this.props = props;
    }
    getLinks(){
        var links = [];
        this.nodes.forEach(c=>{
            var link = {};
            link["source"] = this.id;
            link["target"] = c.id;
            link["cnt"] = c.props.cnt;
            links.push(link);
        })
        this.nodes.forEach(c=>{
            c.getLinks().forEach(l=>{
                links.push(l);
            })
        });
        return links;
    }
    getLevel(){
        if (this.complete && this.nodes.length > 0){
            this.nodes.forEach(c=>{
                if (!c.complete){
                    return this.group;
                }
            })
            var currLevel = this.group;
            this.nodes.forEach(c=>{
                var l = c.getLevel();
                if (l > currLevel){
                    currLevel = l;
                }
            });
            return currLevel;
        } else {
            return this.group;
        }
    }
    getLeafNodes(){
        var l = this.getLevel();
        var ns = this.getIncompleteNodes();
        var leaf = [];
        ns.forEach(n=>{
            if (n.group==l){
                leaf.push(n);
            }
        })
        return leaf;
    }
    getIncompleteNodes(){
        var inodes = [];
        //console.log("node: " + this.id + " complete " + this.complete);
        if (!this.complete){
            inodes.push(this);
        }
        this.nodes.forEach(n=>{
            n.getIncompleteNodes().forEach(cn=>{
                if (!cn.complete){
                    inodes.push(cn);
                }
            })
        });
        return inodes;
    }
    getLevelParent(){
        this.nodes.forEach(n=>{
            if (!n.complete){
                return n.getLevelParent();
            }
        });
        return this;
    }
    addChild(id, name,props) {
        var c = new DataNode(id, name, this.group + 1,props);
        this.nodes.push(c);
        return c;
    }
    addChildNodes(resp){
        var data = this.dh.getData(resp,this.query.dataposition);
        data.forEach(c=>{
            var props = {};
            props["cnt"] = c.doc_count;
            this.addChild(c.key,c.key,props);
        });
        this.addedAllChildren();
    }
    addedAllChildren(){
        this.complete = true;
    }
    getChildNodes(){ return this.nodes; }
    getChildrenQuery(range) {
        var timeFilter = this.dh.generateTimeFilter(range, "filter");
        this.query.addFilterObj(timeFilter);
        var matchFilter = this.dh.generateMatchFilter("query", "must", "model.from.keyword", this.id);
        this.query.addFilterObj(matchFilter);
        var res = this.query.getFullQuery();
        return res;
    }
    getNodeData() {
        var alldata = [];
        var ndata = {};
        ndata["id"] = this.id;
        ndata["name"] = this.name;
        ndata["group"] = this.group;
        alldata.push(ndata);
        this.nodes.forEach(n => {
            n.getNodeData().forEach(c => {
                alldata.push(c);
            })
        })
        return alldata;
    }
}