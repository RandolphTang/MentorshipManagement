import axios from 'axios';
import {AuthService} from "../service/AuthService";

const API = axios.create({
    baseURL: 'http://localhost:8084',
    withCredentials: true
});

API.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response.status === 401 && !error.config._retry) {
            error.config._retry = true;
            try {
                await AuthService.refreshToken();
                return API(error.config);
            } catch (refreshError) {
                console.error('Token refresh failed:', refreshError);
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export default API;