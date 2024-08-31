import React, {useEffect, useState} from 'react';
import StatBlock from '../../../utils/StatBlock';
import { FaCalendar, FaUserClock, FaUsers } from 'react-icons/fa';
import {MentorshipProfileService} from "../../../service/MentorshipProfileService";
import '../../../css/StatusBlock.css'
function StatusBlock({ userInfo }) {
    const [incomingEvents, setIncomingEvents] = useState([]);
    const [requests, setRequests] = useState([]);
    const [relationships, setRelationships] = useState([]);

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                setIncomingEvents(await MentorshipProfileService.getIncomingEvent(userId));

                if (userInfo.role === 'MENTOR') {
                    const mentorships = await MentorshipProfileService.getMentorshipsByMentor(userId);
                    const activeRelationships = mentorships.filter(mentorship => mentorship.status === 'ACTIVE')
                    setRelationships(activeRelationships);

                    const mentorRequests = await MentorshipProfileService.getRequestsForMentor(userId);
                    const pendingRequests = mentorRequests.filter(request => request.requestStatus === 'PENDING')
                    setRequests(pendingRequests);
                } else if (userInfo.role === 'MENTEE') {
                    const mentorship = await MentorshipProfileService.getMentorshipByMentee(userId);
                    setRelationships(mentorship && mentorship.status === 'ACTIVE' ? [mentorship] : []);

                    const menteeRequests = await MentorshipProfileService.getRequestsForMentee(userId);
                    const pendingRequests = menteeRequests.filter(request => request.requestStatus === 'PENDING')
                    setRequests(pendingRequests);
                }
            } catch (err) {
                console.error('Error fetching user info:', err);
            }
        };

        fetchUserInfo();
    }, []);

    return (
        <div className="sb-status-block">
            <StatBlock
                icon={<FaCalendar />}
                value={incomingEvents.length}
                label="Incoming Events"
            />
            <StatBlock
                icon={<FaUserClock />}
                value={requests.length}
                label="Unaccepted Requests"
            />
            <StatBlock
                icon={<FaUsers />}
                value={relationships.length}
                label={userInfo?.role === 'MENTOR' ? 'Active Mentees' : 'Active Mentors'}
            />
        </div>
    );
}

export default StatusBlock;