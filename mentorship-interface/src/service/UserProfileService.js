import API from "../utils/api";

export const UserProfileService = {

    getUserInfo: async (userId) => {
        try {
            const response = await API.get(`/profiles/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching user info:', error);
            throw error;
        }
    },

    logUserOut: async (navigate) => {
        try {
            await API.post(`/auth/logout`);

            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            sessionStorage.clear();

            if (navigate) {
                navigate('/login');
            }
        } catch (error) {
            console.error('Error log user out:', error);
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            sessionStorage.clear();

            if (navigate) {
                navigate('/login');
            }
            throw error;
        }
    },

    setUserProfilePic: async (file, userId) => {
        try {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', userId);
            const response = await API.post('/profiles/upload-pic', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            console.log(response.data);
            return response.data;
        } catch (error) {
            console.error('Error uploading profile picture:', error);
            throw error;
        }
    },

    toggleUserRole: async (userId) => {
        try {
            await API.post(`/mentorship/user/toggle-role/${userId}`);
        } catch (error) {
            console.error('Error toggling user role:', error);
            throw error;
        }
    },

    updateUserInfo: async (userId, userInfo) => {
        try{
            console.log(userInfo);
            await API.put(`/profiles/${userId}`, userInfo);
        }catch (error) {
            console.error('Error update user info:', error);
            throw error;
        }
    },

    deleteUser: async (navigate) => {
        try{
            await API.delete(`/auth/deleteUser`);
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            sessionStorage.clear();

            if (navigate) {
                navigate('/login');
            }
        }catch (error) {
            console.error('Error delete user info:', error);
            throw error;
        }
    },

    getUserAccountAge: async (userId) => {
        try{
            const response = await API.get(`/auth/${userId}/account-age`);
            return response.data
        }catch (error) {
            console.error('Error get user account age:', error);
            throw error;
        }
    },

    changePassword: async (userId, changePasswordDTO) => {
        try{
            const response = await API.put(`/auth/${userId}/change-password`, changePasswordDTO);
            return response.data
        }catch (error) {
            console.error('Error change User password:', error);
            throw error;
        }
    },

    changeEmail: async (userId, changeEmailDTO) => {
        try{
            const response = await API.put(`/auth/${userId}/change-email`, changeEmailDTO);
            return response.data
        }catch (error) {
            console.error('Error change User email:', error);
            throw error;
        }
    },
};