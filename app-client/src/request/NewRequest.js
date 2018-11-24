import React, {Component} from 'react';
import {createPoll} from '../util/APIUtils';
import {MAX_CHOICES, POLL_QUESTION_MAX_LENGTH, POLL_CHOICE_MAX_LENGTH} from '../constants';
import './NewRequest.css';
import {Form, Input, Button, Icon, Select, Col, notification} from 'antd';

const Option = Select.Option;
const FormItem = Form.Item;
const {TextArea} = Input

class NewRequest extends Component {
    constructor(props) {
        super(props);
        this.state = {
            question: {
                text: ''
            },
            choices: [],
            pollLength: {
                days: 1,
                hours: 0
            }
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleQuestionChange = this.handleQuestionChange.bind(this);
        this.handleChoiceChange = this.handleChoiceChange.bind(this);
        this.handlePollDaysChange = this.handlePollDaysChange.bind(this);
        this.handlePollHoursChange = this.handlePollHoursChange.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
        this.handleChangeFields = this.handleChangeFields.bind(this);
    }

    handleChangeFields(value) {
        console.log(value);
    }

    handleSubmit(event) {
        event.preventDefault();
        const pollData = {
            question: this.state.question.text,
            choices: this.state.choices.map(choice => {
                return {text: choice.text}
            }),
            pollLength: this.state.pollLength
        };

        createPoll(pollData)
            .then(response => {
                this.props.history.push("/");
            }).catch(error => {
            if (error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create poll.');
            } else {
                notification.error({
                    message: 'Polling App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    validateQuestion = (questionText) => {
        if (questionText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter your message!'
            }
        } else if (questionText.length > POLL_QUESTION_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Question is too long (Maximum ${POLL_QUESTION_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleQuestionChange(event) {
        const value = event.target.value;
        this.setState({
            question: {
                text: value,
                ...this.validateQuestion(value)
            }
        });
    }

    validateChoice = (choiceText) => {
        if (choiceText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter a choice!'
            }
        } else if (choiceText.length > POLL_CHOICE_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Choice is too long (Maximum ${POLL_CHOICE_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleChoiceChange(event, index) {
        const choices = this.state.choices.slice();
        const value = event.target.value;

        choices[index] = {
            text: value,
            ...this.validateChoice(value)
        }

        this.setState({
            choices: choices
        });
    }


    handlePollDaysChange(value) {
        const pollLength = Object.assign(this.state.pollLength, {days: value});
        this.setState({
            pollLength: pollLength
        });
    }

    handlePollHoursChange(value) {
        const pollLength = Object.assign(this.state.pollLength, {hours: value});
        this.setState({
            pollLength: pollLength
        });
    }

    isFormInvalid() {
        if (this.state.question.validateStatus !== 'success') {
            return true;
        }

        for (let i = 0; i < this.state.choices.length; i++) {
            const choice = this.state.choices[i];
            if (choice.validateStatus !== 'success') {
                return true;
            }
        }
    }

    render() {
        return (
            <div className="new-poll-container">
                <h1 className="page-title">Create Data Request</h1>
                <div className="new-poll-content">
                    <Form onSubmit={this.handleSubmit} className="create-poll-form">
                        <FormItem validateStatus={this.state.question.validateStatus}
                                  help={this.state.question.errorMsg} className="poll-form-row">
                        <TextArea
                            placeholder="Enter your message"
                            style={{fontSize: '16px'}}
                            autosize={{minRows: 3, maxRows: 6}}
                            name="question"
                            value={this.state.question.text}
                            onChange={this.handleQuestionChange}/>
                        </FormItem>
                        <FieldsChoice handleChangeFields={this.handleChangeFields}/>
                        <FormItem className="poll-form-row">
                            <Col xs={24} sm={4}>
                                Poll length:
                            </Col>
                            <Col xs={24} sm={20}>
                                <span style={{marginRight: '18px'}}>
                                    <Select
                                        name="days"
                                        defaultValue="1"
                                        onChange={this.handlePollDaysChange}
                                        value={this.state.pollLength.days}
                                        style={{width: 60}}>
                                        {
                                            Array.from(Array(8).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Days
                                </span>
                                <span>
                                    <Select
                                        name="hours"
                                        defaultValue="0"
                                        onChange={this.handlePollHoursChange}
                                        value={this.state.pollLength.hours}
                                        style={{width: 60}}>
                                        {
                                            Array.from(Array(24).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Hours
                                </span>
                            </Col>
                        </FormItem>
                        <FormItem className="poll-form-row">
                            <Button type="primary"
                                    htmlType="submit"
                                    size="large"
                                    disabled={this.isFormInvalid()}
                                    className="create-poll-form-button">Create Request</Button>
                        </FormItem>
                    </Form>
                </div>
            </div>
        );
    }
}

class FieldsChoice extends React.Component {

    constructor(props) {
        super(props);
        this.children = [];
        for (let i = 0; i < 5; i++) {
            this.children.push(<Option key={i}>{i}</Option>);
        }
    }

    render() {
        return <Select
            className="poll-form-row"
            mode="multiple"
            style={{width: '100%'}}
            placeholder="Please select a field"
            onChange={this.props.handleChangeFields}
        >
            {this.children}
        </Select>
    }
}

export default NewRequest;