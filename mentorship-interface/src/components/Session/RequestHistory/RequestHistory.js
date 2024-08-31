import React, { useState, useEffect } from 'react';
import { MentorshipProfileService } from '../../../service/MentorshipProfileService';
import '../../../css/RequestHistory.css';
import {createRequestDto} from "../../../utils/dto";

function RequestHistory() {
    const [requests, setRequests] = useState([]);
    const [userInfo, setUserInfo] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [filter, setFilter] = useState('ALL');
    const [sortOrder, setSortOrder] = useState('DESC');

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const userInfoResponse  = await MentorshipProfileService.getUserInfo(userId);
                setUserInfo(userInfoResponse);
            } catch (err) {
                console.error('Error fetching user info:', err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserInfo();
    }, []);

    useEffect(() => {
        if (userInfo) {
            fetchRequestHistory();
        }
    }, [userInfo, filter, sortOrder]);

    const fetchRequestHistory = async () => {
        const userId = localStorage.getItem('userId');
        try {
            let relationshipRequests, sessionRequests;
            if(userInfo.role === 'MENTOR'){
                relationshipRequests = await MentorshipProfileService.getRequestHistoryForMentor(Number(userId));
            } else if (userInfo.role === 'MENTEE') {
                relationshipRequests = await MentorshipProfileService.getRequestHistoryForMentee(Number(userId));
            }

            sessionRequests = await MentorshipProfileService.getIncomingEvent(Number(userId));
            console.log(sessionRequests);

            let allRequests = [
                ...relationshipRequests.map(req => createRequestDto(req, 'RELATIONSHIP')),
                ...sessionRequests.map(req => createRequestDto(req, 'SESSION'))
            ];

            if (filter !== 'ALL') {
                allRequests = allRequests.filter(request => request.status === filter);
            }

            allRequests.sort((a, b) => {
                return sortOrder === 'ASC'
                    ? new Date(a.date) - new Date(b.date)
                    : new Date(b.date) - new Date(a.date);
            });
            console.log(allRequests);

            setRequests(allRequests);
        } catch (error) {
            console.error('Error fetching request history:', error);
        }
    };
    const handleAccept = async (request) => {
        console.log(request);
        try {
            if (request.type === 'SESSION') {
                console.log(request);
                await MentorshipProfileService.acceptSessionRequest(Number(request.id));
            } else {
                await MentorshipProfileService.acceptRequest(Number(request.id));
            }
            fetchRequestHistory();
        } catch (error) {
            console.error('Error accepting request:', error);
        }
    };

    const handleDelete = async (request) => {
        const userId = localStorage.getItem('userId');
        try {
            if (request.type === 'SESSION') {
                await MentorshipProfileService.declineSessionRequest(Number(request.id), Number(userId));
            } else {
                await MentorshipProfileService.declineRequest(Number(request.id));
            }
            fetchRequestHistory();
        } catch (error) {
            console.error('Error declining request:', error);
        }
    };

    if (isLoading) {
        return <div>Loading user information...</div>;
    }

    if (!userInfo) {
        return <div>Failed to load user information. Please try again.</div>;
    }

    return (
        <div className="request-history">
            <h2 className="request-history__title">Request History</h2>
            <div className="request-history__filters">
                <select
                    className="request-history__filter-select"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                >
                    <option value="ALL">All Requests</option>
                    <option value="PENDING">Pending</option>
                    <option value="ACCEPTED">Accepted</option>
                    <option value="DECLINED">Declined</option>
                </select>
                <select
                    className="request-history__sort-select"
                    value={sortOrder}
                    onChange={(e) => setSortOrder(e.target.value)}
                >
                    <option value="DESC">Newest First</option>
                    <option value="ASC">Oldest First</option>
                </select>
            </div>
            <div className="request-history__list">
                {requests.map((request, index) => (
                    <div key={request.id} className={`request-history__item request-history__item--${request.status.toLowerCase()}`}>
                        <div className="request-history__item-content">
                            <p className="request-history__type"><strong>Type:</strong> {request.type}</p>
                            <p className="request-history__from"><strong>From:</strong> {
                                request.type === 'SESSION'
                                    ? request.mentor.username
                                    : (request.senderId === userInfo.id ? "You" :
                                        (request.senderId === request.mentee.id ? request.mentee.username : request.mentor.username))
                            }</p>
                            <p className="request-history__to"><strong>To:</strong> {
                                request.type === 'SESSION'
                                    ? "You"
                                    : (request.senderId === userInfo.id
                                        ? (request.senderId === request.mentee.id ? request.mentor.username : request.mentee.username)
                                        : "You")
                            }</p>
                            <p className="request-history__status"><strong>Status:</strong> {request.status}</p>
                            <p className="request-history__date"><strong>Requested Date:</strong> {new Date(request.date).toLocaleString()}</p>
                            {request.type === 'SESSION' && (
                                <>
                                    <p className="request-history__title"><strong>Title:</strong> {request.title}</p>
                                    <p className="request-history__start-time"><strong>Start Time:</strong> {new Date(request.startTime).toLocaleString()}</p>
                                    <p className="request-history__end-time"><strong>End Time:</strong> {new Date(request.endTime).toLocaleString()}</p>
                                    <p className="request-history__location"><strong>Location:</strong> {request.location}</p>
                                    <p className="request-history__description"><strong>Description:</strong> {request.description}</p>
                                </>
                            )}
                            {request.type === 'RELATIONSHIP' && (
                                <p className="request-history__message"><strong>Message:</strong> {request.message}</p>
                            )}
                        </div>
                        {((request.type === 'RELATIONSHIP' && request.status === 'PENDING' && userInfo.id !== request.senderId) ||
                            (request.type === 'SESSION' && request.status === 'PENDING' && request.mentor.id !== userInfo.id)) && (
                            <div className="request-history__actions">
                                <button className="request-history__accept-btn" onClick={() => handleAccept(request)}>Accept</button>
                                <button className="request-history__decline-btn" onClick={() => handleDelete(request)}>Decline</button>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default RequestHistory;