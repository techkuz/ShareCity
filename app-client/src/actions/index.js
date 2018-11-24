import axios from 'axios';
import { API_BASE_URL } from '../constants';

export const CHANGED_TYPE = 'CHANGED_TYPE';

export function changeCurrent(current) {
    return {
        type: CHANGED_TYPE,
        current
    };
}

export const FETCH_BUSINESS = 'FETCH_BUSINESS';

export function fetchCorporation(props) {
    const request = axios.post(`${API_BASE_URL}/business/`, props);

    return {
        type: FETCH_BUSINESS,
        payload: request
    };
}