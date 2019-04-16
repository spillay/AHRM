import React from 'react';
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
    Grid,
    Row,
    Clearfix,
    MenuItem,
    DropdownButton,
    ListGroup, ListGroupItem,
    Modal
} from 'react-bootstrap';
import { DataHelper } from 'DataManager'
import { DataGridView }  from '../DataGridView'

const columns =
    [
        {
            name: "Word",
            key: "word",
            editable: true
        },
        {
            name: "Synonym",
            key: "synonym",
            editable: true
        }
    ];
const data = [
    {word:"beautiful",synonym:"attractive"},
    {word:"happy",synonym:"joy"},
    {word:"hardworking",synonym:"diligent"},
    {word:"kind",synonym:"thoughtfull"},
    {word:"mean",synonym:"difficult"},
    {word:"fair",synonym:"just"}
];

export default class DictCtl extends React.Component {
    constructor(props) {
        super(props);
        console.log("DictCtl in constructor")

        this.state = {
            dh: new DataHelper()
        };
    }
    render() {
        return (<div>
           <DataGridView columns={columns} data={data}/>
        </div>);
    }
}
/*

*/