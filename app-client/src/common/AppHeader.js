import React, { Component } from 'react';
import {
  Link,
  withRouter
} from 'react-router-dom';
import './AppHeader.css';
import logo from './bytom.svg'

import { Layout, Menu, Dropdown, Icon } from 'antd';
import { connect } from "react-redux";
import { changeCurrent } from "../actions";


const Header = Layout.Header;


class AppHeader extends Component {
  constructor(props) {
    super(props);
    this.handleMenuClick = this.handleMenuClick.bind(this);
  }

  handleMenuClick({ key }) {
    if(key === "logout") {
      this.props.onLogout();
    }
  }

  render() {
    let menuItems;
    let accountItems;
    if(this.props.currentUser) {
        console.log(this.props.currentUser.roleName);
        if(this.props.currentUser.roleName === "ROLE_USER") {
            menuItems = [
                <Menu.Item selectable="false" className="profile-menu">
                    <span>{(this.props.currentUser.account) ? this.props.currentUser.account : '0.00'} BTM</span>
                </Menu.Item>,
                <Menu.Item key="/profile" className="profile-menu">
                    <ProfileDropdownMenu
                        currentUser={this.props.currentUser}
                        handleMenuClick={this.handleMenuClick}/>
                </Menu.Item>
            ];
        } else if (this.props.currentUser.roleName === "ROLE_BUSINESS") {
            menuItems = [
                <Menu.Item key="/poll/new">
                    <Link to="/poll/new">
                        <Icon type="plus-circle" className="nav-icon" />
                    </Link>
                </Menu.Item>,
                <Menu.Item key="/profile" className="profile-menu">
                    <ProfileDropdownMenu
                        currentUser={this.props.currentUser}
                        handleMenuClick={this.handleMenuClick}/>
                </Menu.Item>
            ];
        }
    } else {
      menuItems = [
        <Menu.Item key="/login">
            {(this.props.current === "Startup") ? (<Link to="/login">Login</Link>) : (<Link to="/business/login">Login</Link>)}
        </Menu.Item>,
        <Menu.Item key="/signup">
            {(this.props.current === "Startup") ? (<Link to="/signup">Signup</Link>) : (<Link to="/business/signup">Signup</Link>)}
        </Menu.Item>
      ];
      accountItems = [
          <Menu.Item key="Startup" onClick={() => this.props.makeCurrent("Startup")}>
              <Link to="/">Startup</Link>
          </Menu.Item>,
          <Menu.Item key="Corporation" onClick={() => this.props.makeCurrent("Corporation")}>
              <Link to="/">Corporation</Link>
          </Menu.Item>
      ];
    }

    return (
        <Header className="app-header">
          <div className="container">
            <div className="app-title">
              <Link to="/">SHARE<span id='span-logo'>city</span></Link>
              <a className="bytom" href="https://bytom.io/" target="_blank" >
                  <img src={logo} alt="Bytom" width="12" height="12"/>
              </a>
            </div>
            <Menu
                className="app-menu app-menu-left"
                mode="horizontal"
                selectedKeys={[this.props.current]}
                style={{ lineHeight: '64px' }} >
                {accountItems}
            </Menu>
            <Menu
                className="app-menu app-menu-right"
                mode="horizontal"
                selectedKeys={[this.props.current]}
                style={{ lineHeight: '64px' }} >
              {menuItems}
            </Menu>
          </div>
        </Header>
    );
  }
}

function ProfileDropdownMenu(props) {
  const dropdownMenu = (
      <Menu onClick={props.handleMenuClick} className="profile-dropdown-menu">
        <Menu.Item key="user-info" className="dropdown-item" disabled>
          <div className="user-full-name-info">
            {props.currentUser.name}
          </div>
          {/*<div className="username-info">*/}
            {/*@{props.currentUser.username}*/}
          {/*</div>*/}
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="profile" className="dropdown-item">
          <Link to={`/users/${props.currentUser.username}`}>Profile</Link>
        </Menu.Item>
        <Menu.Item key="logout" className="dropdown-item">
          Logout
        </Menu.Item>
      </Menu>
  );

  return (
      <Dropdown
          overlay={dropdownMenu}
          trigger={['click']}
          getPopupContainer = { () => document.getElementsByClassName('profile-menu')[0]}>
        <a className="ant-dropdown-link">
          <Icon type="user" className="nav-icon" style={{marginRight: 0}} /> <Icon type="down" />
        </a>
      </Dropdown>
  );
}

const mapStateToProps = (state) => ({
    current: state.current
});

const mapDispatchToProps = dispatch => ({
    makeCurrent: current => dispatch(changeCurrent(current))
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(withRouter(AppHeader))