import React, { Component } from 'react';
import PollList from '../../poll/PollList';
import { getUserProfile } from '../../util/APIUtils';
import { Tabs } from 'antd';
import { getAvatarColor } from '../../util/Colors';
import { formatDate } from '../../util/Helpers';
import LoadingIndicator  from '../../common/LoadingIndicator';
import './Profile.css';
import NotFound from '../../common/NotFound';
import ServerError from '../../common/ServerError';
import { store }from '../../index';
import { st, co } from './startups';
import { Skeleton, Switch, Card, Icon} from 'antd';
import { Badge } from 'antd';
import { Drawer, List, Avatar, Divider, Col, Row } from 'antd';

const { Meta } = Card;
const TabPane = Tabs.TabPane;


const pStyle = {
    fontSize: 16,
    color: 'rgba(0,0,0,0.85)',
    lineHeight: '24px',
    display: 'block',
    marginBottom: 16,
};

const DescriptionItem = ({ title, content }) => (
    <div
        style={{
            fontSize: 14,
            lineHeight: '22px',
            marginBottom: 7,
            color: 'rgba(0,0,0,0.65)',
        }}
    >
        <p
            style={{
                marginRight: 8,
                display: 'inline-block',
                color: 'rgba(0,0,0,0.85)',
            }}
        >
            {title}:
        </p>
        {content}
    </div>
);


class Profile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      user: null,
      isLoading: false,
      info: null,
      visible: false,
    };
    this.loadUserProfile = this.loadUserProfile.bind(this);
  }


    showDrawer = () => {
        this.setState({
            visible: true}
        );
    };

    onClose = () => {
        this.setState({
            visible: false
        });
    };


  loadUserProfile(username) {
    this.setState({
      isLoading: true
    });

    getUserProfile(username)
        .then(response => {
          this.setState({
            user: response,
            isLoading: false,
            info: (response.roleName === 'ROLE_BUSINESS') ? co : st
          });
        }).catch(error => {
      if(error.status === 404) {
        this.setState({
          notFound: true,
          isLoading: false
        });
      } else {
        this.setState({
          serverError: true,
          isLoading: false
        });
      }
    });
  }

  componentDidMount() {
    const username = this.props.match.params.username;
    this.loadUserProfile(username);
  }

  componentDidUpdate(nextProps) {
    if(this.props.match.params.username !== nextProps.match.params.username) {
      this.loadUserProfile(nextProps.match.params.username);
    }
  }

  render() {
    if(this.state.isLoading) {
      return <LoadingIndicator />;
    }

    if(this.state.notFound) {
      return <NotFound />;
    }

    if(this.state.serverError) {
      return <ServerError />;
    }

    const tabBarStyle = {
      textAlign: 'center'
    };

    return (
        <div className="profile">
          {
            this.state.user ? (
                <div className="user-profile">
                  <div className="user-details">
                    {/*<div className="user-avatar">*/}
                      {/*<Avatar className="user-avatar-circle" style={{ backgroundColor: getAvatarColor(this.state.user.name)}}>*/}
                        {/*{this.state.user.name[0].toUpperCase()}*/}
                      {/*</Avatar>*/}
                    {/*</div>*/}
                    {/*<div className="user-summary">*/}
                      {/*<div className="full-name">{this.state.user.name}</div>*/}
                      {/*<div className="username">@{this.state.user.username}</div>*/}
                      {/*<div className="user-joined">*/}
                        {/*Joined {formatDate(this.state.user.joinedAt)}*/}
                      {/*</div>*/}
                    {/*</div>*/}
                      {this.state.user.roleName === 'ROLE_USER' ? (
                      <a href="/business/polls" style={{position: 'absolute', zIndex: 999}}>
                          <Badge count={99} overflowCount={99} showZero={false} >
                          </Badge>
                      </a>) : null}
                    <CardV info={this.state.info} showDrawer={this.showDrawer}/>
                  </div>
                  <div className="user-poll-details">
                    <Tabs defaultActiveKey="1"
                          animated={false}
                          tabBarStyle={tabBarStyle}
                          size="large"
                          className="profile-tabs">
                        {console.log(this.state.user.roleName)}
                        {this.state.user.roleName === 'ROLE_USER' ? (
                            <TabPane tab={`${this.state.user.voteCount} Shares`}  key="1">
                                <PollList username={this.props.match.params.username} type="USER_VOTED_POLLS" />
                            </TabPane>) : null}

                        {this.state.user.roleName === 'ROLE_BUSINESS' ? (
                            <TabPane tab={`Requests`} key="1">
                            <PollList isAuthenticated={this.state.isAuthenticated}
                            currentUser={this.state.user}/>
                            </TabPane>) : null}
                    </Tabs>
                  </div>
                    <Drawer
                        width={640}
                        placement="right"
                        closable={false}
                        onClose={this.onClose}
                        visible={this.state.visible}
                    >
                        <p style={{ ...pStyle, marginBottom: 24 }}>User Profile</p>
                        <p style={pStyle}>Personal</p>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="Full Name" content="Lily" />{' '}
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Account" content="AntDesign@example.com" />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="City" content="HangZhou" />
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Country" content="China🇨🇳" />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="Birthday" content="February 2,1900" />
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Website" content="-" />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <DescriptionItem
                                    title="Message"
                                    content="Make things as simple as possible but no simpler."
                                />
                            </Col>
                        </Row>
                        <Divider />
                        <p style={pStyle}>Company</p>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="Position" content="Programmer" />
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Responsibilities" content="Coding" />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="Department" content="AFX" />
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Supervisor" content={<a>Lin</a>} />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <DescriptionItem
                                    title="Skills"
                                    content="C / C + +, data structures, software engineering, operating systems, computer networks, databases, compiler theory, computer architecture, Microcomputer Principle and Interface Technology, Computer English, Java, ASP, etc."
                                />
                            </Col>
                        </Row>
                        <Divider />
                        <p style={pStyle}>Contacts</p>
                        <Row>
                            <Col span={12}>
                                <DescriptionItem title="Email" content="AntDesign@example.com" />
                            </Col>
                            <Col span={12}>
                                <DescriptionItem title="Phone Number" content="+86 181 0000 0000" />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                <DescriptionItem
                                    title="Github"
                                    content={(
                                        <a href="http://github.com/ant-design/ant-design/">
                                            github.com/ant-design/ant-design/
                                        </a>
                                    )}
                                />
                            </Col>
                        </Row>
                    </Drawer>
                </div>
            ): null
          }
        </div>
    );
  }
}

function CardV(props) {
    return (
        <Card style={{ width: 300, marginTop: 16 }}
              actions={[            <div style={{  marginTop: 5 }}>{props.info.bytom_id}</div>, <a onClick={props.showDrawer}>View Profile</a>]}>
            <Skeleton loading={false} avatar active>
            <Meta
                avatar={<Avatar src={props.info.logo.imageServiceUrl} />}
                title={props.info.name}
                description={props.info.description}
            />
            </Skeleton>
        </Card>

    );
}

export default Profile;