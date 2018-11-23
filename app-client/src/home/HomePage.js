import React, { Component } from 'react'
import './HomePage.css';
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

class HomePage extends Component {

    constructor(props) {
        super(props);
    }

    getText() {
        if (this.props.current === "Personal") {
            return (
                <div className="home-page-wrapper page1">
                    <h2>Extend your business opportunities. Join the digital nation</h2>
                    <p>Extend your business opportunities. Join the digital nation</p>
                </div>
            );
        } else if (this.props.current === "Business") {
            return (
                <div className="home-page-wrapper page1">
                    <h2>Find new startups that fit you best. Safe and sound.</h2>
                    <p>Find new startups that fit you best. Safe and sound.</p>
                </div>
            );
        }
    }

    render() {
        return (
            <div className="main-wrapper">
                {this.getText()}
            </div>
        );
    }
}

const mapStateToProps = (state) => ({
    current: state.current
});

export default connect(
    mapStateToProps
)(withRouter(HomePage))