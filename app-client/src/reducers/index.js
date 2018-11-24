import { combineReducers } from 'redux';
import ReducerPage from './reducer-page';
import ReducerBusiness from './reducer-business';

const rootReducer = combineReducers({
    current: ReducerPage,
    businessState: ReducerBusiness
});

export default rootReducer;