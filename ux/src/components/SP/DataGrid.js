import React from 'react';
import ReactDOM from 'react-dom';
import {
  Panel,
  Button,
  Col,
  PageHeader,
  ControlLabel,
  FormControl,
  HelpBlock,
  FormGroup,
  Checkbox,
  Form,
  Radio,
  InputGroup,
  Glyphicon } from 'react-bootstrap';

import { elasticsearch } from './spconfig';
import EmoService from '../security/EmoService';
import ESService from '../security/ESService';


import ReactTable from 'react-table';
import treeTableHOC from 'react-table/lib/hoc/treeTable';
import selectTableHOC from 'react-table/lib/hoc/selectTable'

const TreeTable = treeTableHOC(ReactTable);
const SelectTreeTable = selectTableHOC(treeTableHOC(ReactTable));

const b2columns = [{
  header: '',
  id: 'click-me-button',
  render: ({ row }) => (<button onClick={(e) => this.handleButtonClick(e, row)}>Click Me</button>)
}];
const btncolumns = [{
  Header: () => <span><i className='fa-tasks' /> Progress</span>,
  accessor: 'progress',
  Cell: row => (
    <div
      style={{
        width: '100%',
        height: '100%',
        backgroundColor: '#dadada',
        borderRadius: '2px'
      }}
    >
      <div
        style={{
          width: `${row.value}%`,
          height: '100%',
          backgroundColor: row.value > 66 ? '#85cc00'
            : row.value > 33 ? '#ffbf00'
            : '#ff2e00',
          borderRadius: '2px',
          transition: 'all .2s ease-out'
        }}
      />
    </div>
  )
}]
const btnAnalyze = [{
  Header: () => <span><i className='fa-tasks' /> Progress</span>,
  accessor: 'progress',
  Cell: row => (
    <div
      style={{
        width: '100%',
        height: '100%',
        backgroundColor: '#dadada',
        borderRadius: '2px'
      }}
    >
     <Button>Analyze</Button>
    </div>
  )
}]
function getData() {
  var data = "";
  var essvr = new ESService(JSON.parse(elasticsearch.server));
  essvr.getData(JSON.parse(elasticsearch.testDataGrid)).then(function (resp) {
    data = resp.hits.hits;
  });
  return data;
}

const ignoreColumns = ['phone1', 'phone2', 'web', 'email', '_id'];
function getColumns() {
  const columns = [];
  const structure = { "_source.model.from": "From", "_source.model.to": "To", "_source.model.date": "Date" };
  for (let key in structure) {
    if (ignoreColumns.includes(key)) continue;
    columns.push({
      accessor: key,
      Header: structure[key],
      style: { whiteSpace: 'normal' },
    })
   
  }
  btnAnalyze.forEach(b=>{
    columns.push(b);
  })
  return columns;
}
function getNodes(data, node = []) {
  data.forEach((item) => {
    if (item.hasOwnProperty('_subRows') && item._subRows) {
      node = getNodes(item._subRows, node);
    } else {
      node.push(item._original);
    }
  });
  return node;
}


export default class DataGrid extends React.Component {
  constructor(props) {
    super(props);
    console.log("datagrid const################################");
    this.state = {
      data: [],
      query: this.props.query,
      pages: null,
      pSize: this.props.pageSize,
      loading: true,
      columns: getColumns(),
      selection: [],
      selectAll: false,
      selectType: 'checkbox',
      pivotBy: ['state', 'post'],
      expanded: {}
    };
  }


  fetchData = (state, instance) => {
    console.log(state);
    console.log(instance);

    // {"PAGEEND":4},{"PAGESTART":0}
    console.log("fetch ----------------------" + this.state.query);
    var query = this.state.query;
    this.setState({ loading: true });
    if (this.state.pages == null){
      query = query.replace("##PAGESTART##", 0);
      query = query.replace("##PAGEEND##", state.pageSize);
    } else {
      query = query.replace("##PAGESTART##", state.page * state.pageSize);
      query = query.replace("##PAGEEND##", state.pageSize);
    }
    console.log("fetch ----------------------" + query);
    var essvr = new EmoService(JSON.parse(elasticsearch.server));
    essvr.getDatabyHost(JSON.parse(query),this.props.server).then(function (resp) {
    // var client = new es.Client(JSON.parse(elasticsearch.server));
    // client.search(JSON.parse(query)).then(resp => {
      var data = resp.hits.hits;
      console.log(data.toString());
      this.setState({
        data: data,
        pages: resp.hits.total,
        loading: false
      });
    });
    /*
    this.setState({ loading: true });
    var client = new es.Client(JSON.parse(elasticsearch.server));
    if (this.state.pages == null) {
      var query = elasticsearch.testDataGrid;
      query = query.replace("##PAGESTART##", 0);
      query = query.replace("##PAGESIZE##", 0);
      client.search(JSON.parse(query)).then(resp => {
        console.log(resp.hits.total);
        this.setState({ pages: resp.hits.total });
      });
    }

    var query = elasticsearch.testDataGrid;
    query = query.replace("##PAGESTART##", state.page * state.pageSize);
    query = query.replace("##PAGESIZE##", state.pageSize);
    console.log(query);



    client.search(JSON.parse(query)).then(resp => {
      var data = resp.hits.hits;
      this.setState({
        data: data,
        loading: false
      });
    });
    */
  }
  toggleSelection = (key, shift, row) => {
    /*
      Implementation of how to manage the selection state is up to the developer.
      This implementation uses an array stored in the component state.
      Other implementations could use object keys, a Javascript Set, or Redux... etc.
    */
    // start off with the existing state
    if (this.state.selectType === 'radio') {
      let selection = [];
      if (selection.indexOf(key) < 0) selection.push(key);
      this.setState({ selection });
    } else {
      let selection = [
        ...this.state.selection
      ];
      const keyIndex = selection.indexOf(key);
      // check to see if the key exists
      if (keyIndex >= 0) {
        // it does exist so we will remove it using destructing
        selection = [
          ...selection.slice(0, keyIndex),
          ...selection.slice(keyIndex + 1)
        ]
      } else {
        // it does not exist so add it
        selection.push(key);
      }
      // update the state
      this.setState({ selection });
    }
  }
  toggleAll = () => {
    const selectAll = this.state.selectAll ? false : true;
    const selection = [];
    if (selectAll) {
      // we need to get at the internals of ReactTable
      const wrappedInstance = this.selectTable.getWrappedInstance();
      // the 'sortedData' property contains the currently accessible records based on the filter and sort
      const currentRecords = wrappedInstance.getResolvedState().sortedData;
      // we need to get all the 'real' (original) records out to get at their IDs
      const nodes = getNodes(currentRecords);
      // we just push all the IDs onto the selection array
      nodes.forEach((item) => {
        selection.push(item._id);
      })
    }
    this.setState({ selectAll, selection })
  }
  isSelected = (key) => {
    console.log("isSelected");
    console.log(key);
    return this.state.selection.includes(key);
  }
  logSelection = () => {
    console.log('selection:', this.state.selection);
  }
  toggleType = () => {
    this.setState({ selectType: this.state.selectType === 'radio' ? 'checkbox' : 'radio', selection: [], selectAll: false, });
  }
  toggleTree = () => {
    if (this.state.pivotBy.length) {
      this.setState({ pivotBy: [], expanded: {} });
    } else {
      this.setState({ pivotBy: ['state', 'post'], expanded: {} });
    }
  }
  onExpandedChange = (expanded) => {
    this.setState({ expanded });
  }
  onSubComponent = (row) => {
    console.log("rowdata" + row.toString());
    const columns = [
      {
        Header: 'Property',
        accessor: 'property',
        width: 200,
        Cell: (ci) => { return `${ci.value}:` },
        style:
          {
            backgroundColor: '#DDD',
            textAlign: 'right',
            fontWeight: 'bold'
          }
      },
      { Header: 'Value', accessor: 'value' },
    ]
    const useColumns = ['subject', 'htmlContent', 'textContent'];
    var rData = row.original["_source"]["model"];
    var rowData = []
    Object.keys(rData).map((key) => {
      if (rData.hasOwnProperty(key)) {
        console.log(key);
        if (useColumns.includes(key)) {
          var val = ""
          if (rData[key] != null){
            val = rData[key].toString();
          }
          console.log(key + " : " + val);
          rowData.push({
            property: key,
            value: val
          });
        }
      }
    });
    console.log(rowData);
    return (
      <div style={{ padding: '10px' }}>
        <ReactTable
          data={rowData}
          columns={columns}
          pageSize={rowData.length}
          showPagination={false}
        />
      </div>
    );
  }

  render() {

    const {
      toggleSelection, toggleAll, isSelected,
      logSelection, toggleType,
      onExpandedChange, toggleTree,
    } = this;
    const { data, pages, loading, columns, selectAll, selectType, pivotBy, expanded } = this.state;
    const extraProps =
      {
        selectAll,
        isSelected,
        toggleAll,
        toggleSelection,
        selectType,
        //pivotBy,
        expanded,
        onExpandedChange,
      }
    return (
      <div>
        <SelectTreeTable
          columns={columns}
          manual // Forces table not to paginate or sort automatically, so we can handle it server-side
          data={data}
          pages={pages} // Display the total number of pages
          loading={loading} // Display the loading overlay when we need it
          onFetchData={this.fetchData} // Request new data when things change
          //filterable
          ref={(r) => this.selectTable = r}
          defaultPageSize={10}
          className="-striped -highlight"
          {...extraProps}
          SubComponent={this.onSubComponent}
        />
      </div>
    );
  }
}

/*
 */