import React, {useEffect, useState} from 'react';
import {MentorshipProfileService} from "../../../service/MentorshipProfileService";
import '../../../css/SessionRequestBlock.css'

function SessionRequestBlock() {
    const userId = localStorage.getItem('userId');
    const [userInfo, setUserInfo] = useState(null);
    const [mentees, setMentees] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [eventDetails, setEventDetails] = useState({
        mentorId: userId,
        menteeIds: [],
        title: '',
        description: '',
        startTime: '',
        endTime: '',
        location: '',
        status: 'PENDING'
    });

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const userInfoResponse = await MentorshipProfileService.getUserInfo(userId);
                const mentorships = await MentorshipProfileService.getMentorshipsByMentor(userId);

                const menteeList = mentorships.map(mentorship => ({
                    id: mentorship.mentee.id,
                    username: mentorship.mentee.username
                }))

                setMentees(menteeList)
                setUserInfo(userInfoResponse);
            } catch (err) {
                console.error('Error fetching user info:', err);
            } finally {
                setIsLoading(false);
            }
        };
        fetchUserInfo();
    }, [userId]);

    useEffect(() => {
        console.log("eventDetails updated:", eventDetails);
    }, [eventDetails]);

    if (isLoading) {
        return <div>Loading user information...</div>;
    }

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setEventDetails(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await MentorshipProfileService.createSessionRequest(eventDetails);
            console.log('Session request created:', response);
        } catch (error) {
            console.error('Error creating session request:', error);
        }
    };

    const handleMenteeSelection = (e) => {
        const selectedId = e.target.value;

        setEventDetails(prev => {
            const updatedMenteeIds = prev.menteeIds.includes(Number(selectedId))
                ? prev.menteeIds.filter(id => Number(id) !== Number(selectedId))
                : [...prev.menteeIds, Number(selectedId)];
            return { ...prev, menteeIds: updatedMenteeIds };
        });
    };

    console.log("Rendering select. Current menteeIds:", eventDetails.menteeIds);

    return (
        <div className="srb-container">
            <form onSubmit={handleSubmit} className="srb-form">
                <div className="srb-column">
                    <div className="srb-form-group">
                        <label htmlFor="menteeSelect" className="srb-label">Mentees</label>
                        <select
                            multiple
                            name="menteeIds"
                            value={eventDetails.menteeIds}
                            onChange={handleMenteeSelection}
                            className="srb-select-mentees"
                            required
                        >
                            {mentees.map(mentee => (
                                <option key={mentee.id} value={mentee.id}>
                                    {mentee.username}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="srb-form-group">
                        <input
                            type="text"
                            name="title"
                            value={eventDetails.title}
                            onChange={handleInputChange}
                            placeholder="Session Title"
                            className="srb-input"
                            required
                        />
                        <textarea
                            name="location"
                            value={eventDetails.location}
                            onChange={handleInputChange}
                            placeholder="Location"
                            className="srb-input"
                        ></textarea>
                    </div>
                </div>
                <div className="srb-column">
                    <div className="srb-form-group">
                        <label className="srb-label">Start</label>
                        <input
                            type="datetime-local"
                            name="startTime"
                            value={eventDetails.startTime}
                            onChange={handleInputChange}
                            placeholder="Start Time"
                            className="srb-input srb-datetime"
                            required
                        />
                    </div>
                    <div className="srb-form-group">
                        <label className="srb-label">End</label>
                        <input
                            type="datetime-local"
                            name="endTime"
                            value={eventDetails.endTime}
                            onChange={handleInputChange}
                            placeholder="End Time"
                            className="srb-input srb-datetime"
                            required
                        />
                    </div>
                </div>
                <div className="srb-column">
                    <div className="srb-form-group">
                        <textarea
                            name="description"
                            value={eventDetails.description}
                            onChange={handleInputChange}
                            placeholder="Description"
                            className="srb-textarea"
                        ></textarea>
                    </div>
                    <button type="submit" className="srb-submit-btn">Create Session</button>
                </div>
            </form>
        </div>
    );
}

export default SessionRequestBlock;