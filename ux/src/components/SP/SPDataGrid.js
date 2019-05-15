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
  Glyphicon,
  Modal
} from 'react-bootstrap';

import { elasticsearch } from './spconfig';
import EmoService from '../security/EmoService';
import ESService from '../security/ESService';

import ReactTable from 'react-table';
import treeTableHOC from 'react-table/lib/hoc/treeTable';
import selectTableHOC from 'react-table/lib/hoc/selectTable';


import SPPieChart from './SPPieChart';
import Donut from '../Views/ui/Donut';

import EmailAnalysisView from '../Views/EmailAnalysisView';
import EmployeeView from '../Views/EmployeeView';

import DataHelper from '../SP/data/DataHelper';
import ModalView from '../Views/ModalView';

const TreeTable = treeTableHOC(ReactTable);
const SelectTreeTable = selectTableHOC(treeTableHOC(ReactTable));




/*
const b2columns = [{
  Header: '',
  id: 'click-me-button',
  Cell: ({ row }) => (<Button onClick={this.handleButtonClick(row)}>Click Me</Button>)
}];
*/
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
  Header: () => <span><i className='fa-tasks' />Analyzis</span>,
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
      <Button>Analyze Employee</Button>
    </div>
  )
}]
function getData() {
  var data = "";
  var essvr = new ESService();
  essvr.getData(JSON.parse(elasticsearch.testDataGrid)).then(function (resp) {
    data = resp.hits.hits;
  });
  return data;
}

const ignoreColumns = ['phone1', 'phone2', 'web', 'email', '_id'];

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


export default class SPDataGrid extends React.Component {
  constructor(props) {
    super(props);
    console.log("apdatagrid const################################");
    this.state = {
      data: [],
      query: this.props.query,
      pages: null,
      pSize: this.props.pageSize,
      loading: true,
      columns: this.getColumns(),
      selection: [],
      selectAll: false,
      selectType: 'checkbox',
      pivotBy: ['state', 'post'],
      expanded: {},
      employeeTLShow: false,
      employeeLCShow: false,
      contentShow: false,
      selectedRow: null,
      dh: new DataHelper(),
      modalData: ""
    };

  }

  // Modal Stuff
  handleEmployeeTLClose = () => {
    this.setState({ employeeTLShow: false });
  }

  handleEmployeeTLShow = (row) => {
    var it = this.state.dh.transform(row._original);
    this.setState({ employeeTLShow: true,modalData: it });
  }

  handleEmployeeLCClose = () => {
    this.setState({ employeeLCShow: false });
  }

  handleEmployeeLCShow = (row) => {
    var it = this.state.dh.transform(row._original);
    this.setState({ employeeLCShow: true,modalData: it });
  }
  handleContentClose = () => {
    console.log("content close");
    this.setState({ contentShow: false });
  }

  handleContentShow = (row) => {
    console.log(row._original);
    var it = this.state.dh.transform(row._original);
    console.log(it);
    this.setState({ contentShow: true, modalData: it });
  }
  // End Modal Stuff

  getColumns = () => {
    const columns = [];
    const structure = { "_source.model.from": "From", "_source.model.to": "To", "_source.model.date": "Date" };
    for (let key in structure) {
      if (ignoreColumns.includes(key)) continue;
      columns.push({
        accessor: key,
        Header: structure[key],
        style: { whiteSpace: 'normal' },
        maxWidth: 200
      })

    }
    const b2columns = [{
      Header: '',
      id: 'click-content',
      Cell: ({ row }) => (<Button className="btn btn-primary" id="content" onClick={(e) => this.handleButtonClick(e, row)}>Analyse Content</Button>)
    },
    {
      Header: '',
      id: 'click-employee-timeline',
      Cell: ({ row }) => (<Button className="btn btn-primary" id="employee-tl" onClick={(e) => this.handleButtonClick(e, row)}>Analyse Time Line</Button>)
    },
    {
      Header: '',
      id: 'click-employee-linechart',
      Cell: ({ row }) => (<Button className="btn btn-primary" id="employee-lc" onClick={(e) => this.handleButtonClick(e, row)}>Analyse Line Chart</Button>)
    }
    ];
    b2columns.forEach(b => {
      columns.push(b);
    })
    return columns;
  }


  handleButtonClick = (e, row) => {
    Object.keys(row).map((key) => {
      console.log(" key " + key + " value " + row[key]);
    })
    console.log("--------------------next-------------------------");
    Object.keys(e).map((key) => {
      console.log(" key " + key + " value " + e[key]);
    })
    console.log("Button id " + e.target.id);
    switch (e.target.id) {
      case "content":
        console.log("content");
        this.setState({ contentShow: true, selectedRow: row });
        this.handleContentShow(row);
        break;

      case "employee-tl":
        console.log("employee-tl");
        this.handleEmployeeTLShow(row);
        break;
      case "employee-lc":
        console.log("employee-lc");
        this.handleEmployeeLCShow(row);
        break;

    }
  }

  fetchData = (state, instance) => {
    console.log(state);
    console.log(instance);

    // {"PAGEEND":4},{"PAGESTART":0}
    console.log("fetch ----------------------" + this.state.query);
    var query = this.state.query;
    this.setState({ loading: true });
    if (this.state.pages == null) {
      query = query.replace("##PAGESTART##", 0);
      query = query.replace("##PAGEEND##", state.pageSize);
    } else {
      query = query.replace("##PAGESTART##", state.page * state.pageSize);
      query = query.replace("##PAGEEND##", state.pageSize);
    }
    console.log("fetch ----------------------" + query);

    var essvr = new ESService();

    //var client = new Client(JSON.parse(elasticsearch.server));
    essvr.getDatabyHost(JSON.parse(query),this.props.server).then(res => {
      var resp = JSON.parse(res);
      var data = resp.hits.hits;
      console.log(data.toString());
      this.setState({
        data: data,
        pages: resp.hits.total,
        loading: false
      });
    });

  }
  toggleSelection = (key, shift, row) => {

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
        Header: 'Emotions',
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
      {
        Header: 'Value',
        accessor: 'value',
        Cell: (ci) => (
          <div width="100">
            <Donut data={JSON.parse(ci.value)} color="#8884d8" innerRadius="40%" outerRadius="60%" />
          </div>
        )
      },
    ]
    var rowData = []
    var eData = row.original["_source"]["emotions"];
    console.log("------------------------------------------=========",eData);
    var emotions = JSON.parse(eData);
    var dataset = [];
    if (emotions.norm !== ""){
      var norm = JSON.parse(emotions.norm);
      Object.keys(norm).map((key) => {
        console.log(key);
        console.log(norm[key]);
        dataset.push({ label: key, value: norm[key] });
      })
    } else {
      dataset.push({ label: "NA", value: 1 });
    }
    console.log("source data " + JSON.stringify(dataset) + JSON.stringify(emotions.norm));
    rowData.push({
      property: "Emotion Distribution",
      value: JSON.stringify(dataset)
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
  onSubComponent1 = (row) => {
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
      {
        Header: 'Emotions',
        accessor: 'emotion',
        width: 200,
        Cell: (ci) => { return `${ci.value}:` },
        style:
          {
            backgroundColor: '#DDD',
            textAlign: 'right',
            fontWeight: 'bold'
          }
      },
    ]
    const useColumns = ['subject', 'htmlContent', 'textContent'];
    var rData = row.original["_source"]["model"];
    var rowData = []
    Object.keys(rData).map((key) => {
      if (rData.hasOwnProperty(key)) {
        console.log(key);
        if (useColumns.includes(key)) {
          var val = ""
          if (rData[key] != null) {
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
    var eData = row.original["_source"]["emotions"];
    var emotions = JSON.parse(eData);
    console.log("source data " + JSON.stringify(eData) + emotions.norm);
    rowData.push({
      property: "emotion",
      value: emotions.norm
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
      onExpandedChange, toggleTree, handleButtonClick
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

      console.log("))))))))))))))))))))))))))))))))))))))))))))))))))))" + this.state.modalData.from)
      console.log(this.state.modalData.to)
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
          style={{
            height: "800px" // This will force the table body to overflow and scroll, since there is not enough room
          }}
        />
        <ModalView show={this.state.contentShow} closeaction={this.handleContentClose} title="Email Analysis">
          <EmailAnalysisView data={this.state.modalData} />
        </ModalView>
        <ModalView show={this.state.employeeTLShow} closeaction={this.handleEmployeeTLClose} title="Email Analysis">
          <EmployeeView emailData={this.state.modalData} timeline="true"/>
        </ModalView>
        <ModalView show={this.state.employeeLCShow} closeaction={this.handleEmployeeLCClose} title="Email Analysis">
          <EmployeeView emailData={this.state.modalData} linechart="true"/>
        </ModalView>

      </div>
    );
  }
}

/*
   
 */