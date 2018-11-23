import { FETCH_BUSINESS } from '../actions'

let businessSt = {
    businessEmail: 'business@example.com',
    businessId: 1,
    companyName: 'Company',
    myCategories: ['voting'],
    categories: ['voting'],
    currentCategory: 'voting'
};

export default function(businessState = businessSt, action) {
    switch (action.type) {
        case FETCH_BUSINESS : return {...businessState, businessState: action.payload};
        default : return businessState;
    }
}