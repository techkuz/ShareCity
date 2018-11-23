import { CHANGED_TYPE } from '../actions'


export default function(current = 'Personal', action) {
    switch (action.type) {
        case CHANGED_TYPE : return action.current;
        default : return current;
    }
}