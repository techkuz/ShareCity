import React, { Component } from 'react'
import './AppFooter.css';
import { withRouter } from "react-router-dom";

import { Layout } from 'antd';
const Footer = Layout.Footer;

class AppFooter extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Footer className="app-footer">
                <div className="container">
                    <p className="copyright">
                        2018 made with love from Innopolis
                    </p>
                </div>
            </Footer>
        );
    }
}

export default withRouter(AppFooter);
