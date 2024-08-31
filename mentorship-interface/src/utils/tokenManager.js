import API from "./api";

export const isAuthenticated = () => {
    return document.cookie.includes('jwt=');
};

export const logout = async () => {
    try {
        await API.post('/auth/logout');
        window.location.href = '/login';
    } catch (error) {
        console.error('Logout failed:', error);
    }
};