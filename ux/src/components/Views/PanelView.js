import React, { Component } from 'react';
import {withWindowDimensionsDOM} from './withWindowDimensions';

class PanelView extends React.Component {
    /*
    Props available: 
    - panelCustomClass 
    - header and headerCustomClass
    - body and bodyCustomClass
    - footer and footerCustomClass
    
    You can add PropType.isRequired as you wish.
    */
    render() {
        console.log("PanelView width",this.props.windowWidth)
        console.log("PanelView height",this.props.windowHeight)
        return (
            <div className={["panel", this.props.panelCustomClass].join(' ') || "panel panel-default"} onClick={this.props.action}>
                {this.props.header &&
                    <div className={this.props.headerCustomClass || "panel-heading"} width={this.props.windowWidth}>{this.props.header}</div>
                }
                {this.props.body &&
                    <div className={this.props.bodyCustomClass || "panel-body"}>{this.props.body}</div>
                }
                {this.props.children}
                {this.props.footer &&
                    <div className={this.props.footerCustomClass || "panel-footer"}>{this.props.footer}</div>
                }
            </div>
        );
    }
}
export const DPanelView = withWindowDimensionsDOM(PanelView);

