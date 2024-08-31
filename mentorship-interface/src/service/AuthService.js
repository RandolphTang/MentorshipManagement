import API from "../utils/api";

export const AuthService = {

    refreshToken: async () => {
        try {
            const response = await API.post('/auth/refresh-token');
            return response.data;
        } catch (error) {
            throw error.response.data;
        }
    }
};
