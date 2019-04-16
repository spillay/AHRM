import React from 'react';
import update from 'immutability-helper';
import ReactDataGrid from 'react-data-grid';
const { Editors, Formatters } = require('react-data-grid-addons');

export class DataGridView extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      rows: this.props.data
    };
  }

  handleGridRowsUpdated = ({ fromRow, toRow, updated }) => {
    let rows = this.state.rows.slice();

    for (let i = fromRow; i <= toRow; i++) {
      let rowToUpdate = rows[i];
      let updatedRow = update(rowToUpdate, { $merge: updated });
      rows[i] = updatedRow;
    }

    this.setState({ rows });
  };


  rowGetter = (i) => {
    return this.state.rows[i];
  };

  render() {
    return (
      <ReactDataGrid
        enableCellSelect={true}
        columns={this.props.columns}
        rowGetter={this.rowGetter}
        rowsCount={this.state.rows.length}
        onGridRowsUpdated={this.handleGridRowsUpdated}
        minHeight={200} />);
  }
}

/*
import React from 'react';
import update from 'immutability-helper';

if (typeof window === 'undefined') {
  module.exports = {}
} else {
  var ReactDataGrid = require('react-data-grid');
  //const {Toolbar, Data: {Selectors}} = require('react-data-grid-addons');
  const { Editors, Formatters } = require('react-data-grid-addons');

  class DataGridView extends React.Component {
    constructor(props, context) {
      super(props, context);
      this.state = {
        rows: this.props.data
      };
    }
  
    handleGridRowsUpdated = ({ fromRow, toRow, updated }) => {
      let rows = this.state.rows.slice();
  
      for (let i = fromRow; i <= toRow; i++) {
        let rowToUpdate = rows[i];
        let updatedRow = update(rowToUpdate, {$merge: updated});
        rows[i] = updatedRow;
      }
  
      this.setState({ rows });
    };
  
  
    rowGetter = (i) => {
      return this.state.rows[i];
    };
  
    render() {
      return  (
        <ReactDataGrid
          enableCellSelect={true}
          columns={this.props.columns}
          rowGetter={this.rowGetter}
          rowsCount={this.state.rows.length}
          onGridRowsUpdated={this.handleGridRowsUpdated}
          minHeight={200} />);
    }
  }
  

  module.exports = DataGridView
}
*/




