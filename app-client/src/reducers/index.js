import { combineReducers } from 'redux';
import ReducerPage from './reducer-page';
import ReducerCorporation from './reducer-business';

const rootReducer = combineReducers({
    current: ReducerPage,
    businessState: ReducerCorporation
});

export default rootReducer;