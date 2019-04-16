import React from 'react';
import {PropTypes as types} from 'prop-types';
//import Button from 'react-bootstrap';
//import ButtonGroup from 'react-bootstrap';
import Button from 'react-bootstrap/lib/Button'
import ButtonGroup from 'react-bootstrap/lib/ButtonGroup'

class RadioGroup extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    let {disabled, name, onChange, options, value, ...props} = this.props
    return <ButtonGroup {...props}>
      {options.map(option =>
        <Button
          key={option[0]}
          bsStyle={option[0] === value ? 'primary' : 'default'}
          children={option[1]}
          disabled={disabled}
          name={name}
          onClick={onChange}
          value={option[0]}
        />
      )}
    </ButtonGroup>
  }
}

RadioGroup.propTypes = {
  name: types.string.isRequired,
    onChange: types.func.isRequired,
    options: types.arrayOf(types.arrayOf(types.string)),
    value: types.string.isRequired,
}

export default RadioGroup;
/*
 <RadioGroup
                  name="test"
                  onChange={this.handleChange}
                  options={[
                    ['dairy', 'Cheese'],
                    ['fruit', 'Apple'],
                    ['meat', 'Ham'],
                  ]}
                  value={this.state.test}
                />
                <ToggleButtonGroup
                  type="radio"
                  name="appointmentMade"
                  onChange={this.isAppMade}>
                  <ToggleButton value={1}>Yes</ToggleButton>
                  <ToggleButton value={2}>No</ToggleButton>
                </ToggleButtonGroup>
                <ButtonToolbar>
                  <ToggleButtonGroup type="checkbox" defaultValue={[1, 3]}>
                    <ToggleButton value={1}>Checkbox 1 (pre-checked)</ToggleButton>
                    <ToggleButton value={2}>Checkbox 2</ToggleButton>
                    <ToggleButton value={3}>Checkbox 3 (pre-checked)</ToggleButton>
                  </ToggleButtonGroup>
                </ButtonToolbar>
                */