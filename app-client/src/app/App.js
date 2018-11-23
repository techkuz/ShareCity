import React, { Component } from 'react';
import './App.css';
import {
    Route,
    withRouter,
    Switch
} from 'react-router-dom';

import { store } from '../index';

import { getCurrentUser} from '../util/APIUtils';
import { ACCESS_TOKEN } from '../constants';

import PollList from '../poll/PollList';
import NewPoll from '../poll/NewPoll';
import NewRequest from '../request/NewRequest';
import Login from '../user/login/Login';
import Signup from '../user/signup/Signup';
import LoginBusiness from '../business/login/LoginBusiness';
import SignupBusiness from '../business/signup/SignupBusiness';
import Profile from '../user/profile/Profile';
import AppHeader from '../common/AppHeader';
import AppFooter from '../common/AppFooter';
import NotFound from '../common/NotFound';
import HomePage from "../home/HomePage";
import LoadingIndicator from '../common/LoadingIndicator';
import PrivateRoute from '../common/PrivateRoute';

import { Layout, notification } from 'antd';

const { Content } = Layout;

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: false
        };
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);

        notification.config({
            placement: 'topRight',
            top: 70,
            duration: 3,
        });
    }

    async loadCurrentUser() {
        this.setState({
            isLoading: true
        });
        await getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response,
                    isAuthenticated: true,
                    isLoading: false
                });
            }).catch(error => {
                this.setState({
                    isLoading: false
                });
            });
    }

    componentDidMount() {
        this.loadCurrentUser();
    }

    // Handle Logout, Set currentUser and isAuthenticated state which will be passed to other components
    handleLogout(redirectTo="/", notificationType="success", description="You're successfully logged out.") {
        localStorage.removeItem(ACCESS_TOKEN);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });

        this.props.history.push(redirectTo);

        notification[notificationType]({
            message: 'Polling App',
            description: description,
        });
    }

    /*
     This method is called by the Login component after successful login
     so that we can load the logged-in user details and set the currentUser &
     isAuthenticated state, which other components will use to render their JSX
    */
    async handleLogin() {
        notification.success({
            message: 'Polling App',
            description: "You're successfully logged in.",
        });
        await this.loadCurrentUser().then(() => {
            console.log(this.state.currentUser);
            if (this.state.currentUser && this.state.currentUser.roleName === 'ROLE_BUSINESS') {
                this.props.history.push("/business/polls");
            } else if (this.state.currentUser && this.state.currentUser.roleName === 'ROLE_USER') {
                this.props.history.push(`/users/${this.state.currentUser.username}`);
            }
        });
    }

    render() {
        if(this.state.isLoading) {
            return <LoadingIndicator />
        }
        return (
            <Layout className="app-container">
                <AppHeader isAuthenticated={this.state.isAuthenticated}
                           currentUser={this.state.currentUser}
                           onLogout={this.handleLogout} />

                <Content className="app-content">
                    <div className="container">
                        <Switch>
                            <Route exact path="/" component={HomePage}>
                            </Route>
                            <Route path="/business/polls"
                                   render={(props) => <PollList isAuthenticated={this.state.isAuthenticated}
                                                                currentUser={this.state.currentUser} handleLogout={this.handleLogout} {...props} />}>
                            </Route>
                            <Route path="/login"
                                   render={(props) => <Login onLogin={this.handleLogin} {...props} />}>
                            </Route>
                            <Route path="/signup" component={Signup}>
                            </Route>
                            <Route path="/test" component={NewRequest}>
                            </Route>
                            <Route path="/business/login"
                                   render={(props) => <LoginBusiness onLogin={this.handleLogin} {...props} />}>
                            </Route>
                            <Route path="/business/signup" component={SignupBusiness}>
                            </Route>
                            <Route path="/users/:username"
                                   render={(props) => <Profile isAuthenticated={this.state.isAuthenticated} currentUser={this.state.currentUser} {...props}  />}>
                            </Route>
                            <PrivateRoute authenticated={this.state.isAuthenticated} path="/poll/new" component={NewPoll} handleLogout={this.handleLogout}>
                            </PrivateRoute>
                            <PrivateRoute authenticated={this.state.isAuthenticated} path="/request/new" component={NewRequest} handleLogout={this.handleLogout}>
                            </PrivateRoute>
                            <Route component={NotFound}>
                            </Route>
                        </Switch>
                    </div>
                </Content>
                <AppFooter />
            </Layout>
        );
    }
}

export default withRouter(App);
