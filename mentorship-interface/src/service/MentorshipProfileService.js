import API from "../utils/api";

export const MentorshipProfileService = {
    getUserInfo: async (userId) => {
        try {
            const response = await API.get(`/mentorship/user/info/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching user info:', error);
            throw error;
        }
    },

    getUserProfilePic: async (userId) => {
        try {
            const response = await API.get(`/profiles/pic/${userId}`, {
                responseType: 'blob',
                headers: {
                    'Cache-Control': 'no-cache',
                    'Pragma': 'no-cache',
                    'Expires': '0',
                }
            });
            if (response.status === 404) {
                return null;
            }
            return response.data;
        } catch (error) {
            console.log('Error fetching user profile pic:', error);
        }
    },

    userSelectRole: async (role) => {
        try {
            const response = await API.post('/mentorship/user/select-role',
                { userRole: role }
            );

        } catch (error) {
            console.log('Error fetching user profile pic:', error);
        }
    },

    getIncomingEvent: async (userId) => {
        try {
            const response = await API.get(`/mentorship/calendar/incoming/user/${userId}`);
            console.log(response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching user incoming event:', error);
            throw error;
        }
    },

    getMentorshipsByMentor: async (userId) => {
        try {
            const response = await API.get(`/mentorship/relationships/mentor/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching mentorships for mentor:', error);
            throw error;
        }
    },

    getMentorshipByMentee: async (userId) => {
        try {
            const response = await API.get(`/mentorship/relationships/mentee/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching mentorship for mentee:', error);
            throw error;
        }
    },

    getRequestsForMentee: async (menteeId) => {
        try {
            const response = await API.get(`/mentorship/requests/mentee/${menteeId}`);
            return response.data.filter(request => request.status !== 'ACCEPTED');
        } catch (error) {
            console.error('Error fetching requests for mentee:', error);
            throw error;
        }
    },

    getRequestsForMentor: async (mentorId) => {
        try {
            const response = await API.get(`/mentorship/requests/mentor/${mentorId}`);
            return response.data.filter(request => request.status !== 'ACCEPTED');
        } catch (error) {
            console.error('Error fetching requests for mentor:', error);
            throw error;
        }
    },

    createRequest: async (menteeId, mentorId, message, senderId) => {
        console.log('Request params:', { menteeId, mentorId, message });
        try {
            const response = await API.post('/mentorship/requests', {
                menteeId: Number(menteeId),
                mentorId: Number(mentorId),
                message: message,
                senderId: senderId
            });
            return response.data;
        } catch (error) {
            if (error.response) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    },

    searchMatchingUsers: async (term) => {
        try {
            const response = await API.get('/mentorship/user/info/search', {
                params: { term }
            });
            return response.data;
        } catch (error) {
            console.error('Error creating request:', error);
            throw error;
        }
    },

    getRequestHistoryForMentor: async (mentorId) => {
        try {
            const response = await API.get(`/mentorship/requests/mentor/${mentorId}`);
            console.log(response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching request history:', error);
            throw error;
        }
    },

    getRequestHistoryForMentee: async (menteeId) => {
        try {
            const response = await API.get(`/mentorship/requests/mentee/${menteeId}`);
            console.log(response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching request history:', error);
            throw error;
        }
    },

    acceptRequest: async (requestId) => {
        try {
            const response = await API.post(`/mentorship/requests/${requestId}/accept`);
            return response.data;
        } catch (error) {
            console.error('Error accepting request:', error);
            throw error;
        }
    },

    declineRequest: async (requestId) => {
        try {
            const response = await API.post(`/mentorship/requests/${requestId}/decline`);
            return response.data;
        } catch (error) {
            console.error('Error declining request:', error);
            throw error;
        }
    },

    createSessionRequest: async (sessionDetails) => {
        try {
            const response = await API.post(`/mentorship/calendar/create/events`, {
                mentorId: sessionDetails.mentorId,
                menteeIds: sessionDetails.menteeIds,
                startTime: sessionDetails.startTime,
                endTime: sessionDetails.endTime,
                status: sessionDetails.status,
                title: sessionDetails.title,
                location: sessionDetails.location,
                description: sessionDetails.description
            });
            return response.data;
        } catch (error) {
            console.error('Error declining request:', error);
            throw error;
        }
    },

    acceptSessionRequest: async (requestId) => {
        try {
            const response = await API.post(`/mentorship/calendar/${requestId}/accept`);
            return response.data;
        } catch (error) {
            console.error('Error accepting request:', error);
            throw error;
        }
    },

    declineSessionRequest: async (requestId, userId) => {
        try {
            const response = await API.post(`/mentorship/calendar/${requestId}/${userId}/decline`);
            return response.data;
        } catch (error) {
            console.error('Error declining request:', error);
            throw error;
        }
    },

    getSessionsDateBasedForUser: async (userId, start, end) => {
        try {
            const startStr = start.toISOString().split('.')[0] + "Z";
            const endStr = end.toISOString().split('.')[0] + "Z";
            const response = await API.get(`/mentorship/calendar/incoming/dateBased/user/${userId}/${startStr}/${endStr}`);
            return response.data;
        } catch (error) {
            console.error('Error details:', error.response ? error.response.data : error.message);
            throw error;
        }
    }
};
