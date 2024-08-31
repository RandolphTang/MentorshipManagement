import React, {useEffect, useRef, useState} from 'react';
import {MentorshipProfileService} from "../service/MentorshipProfileService";
import '../css/IncomingSession.css';
import {UserProfileService} from "../service/UserProfileService";

function IncomingSessions() {
    const[UserInfo, setUserInfo] = useState('');
    const [IncomingEvents, setIncomingEvents] = useState([]);
    const scrollContainerRef = useRef(null);

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                setUserInfo(await UserProfileService.getUserInfo(userId));
                setIncomingEvents(await MentorshipProfileService.getIncomingEvent(userId));
            } catch (err) {
                console.error('Error fetching incoming events:', err);
            }
        };

        fetchUserInfo();
    }, []);

    const scroll = (direction) => {
        const container = scrollContainerRef.current;
        if (container) {
            const scrollAmount = direction === 'left' ? -300 : 300;
            container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
        }
    };

    if (!IncomingEvents) {
        return <div>Loading...</div>;
    }

    return (
        <div className="incoming-sessions">
            <h2 className="incoming-sessions__title">Upcoming Sessions</h2>
            <div className="incoming-sessions__carousel">
                <div className="incoming-sessions__events-container" ref={scrollContainerRef}>
                    {IncomingEvents.length > 0 ? (
                        <div className="incoming-sessions__events-container" ref={scrollContainerRef}>
                            {IncomingEvents.map((event) => (
                                <div key={event.id} className="incoming-sessions__event-block">
                                    <h3 className="incoming-sessions__event-title">{event.title}</h3>
                                    <p className="incoming-sessions__event-detail"><strong>Start:</strong> {new Date(event.startTime).toLocaleString()}</p>
                                    <p className="incoming-sessions__event-detail"><strong>End:</strong> {new Date(event.endTime).toLocaleString()}</p>
                                    <p className="incoming-sessions__event-detail"><strong>Location:</strong> {event.location}</p>
                                    <p className="incoming-sessions__event-detail"><strong>Mentee:</strong> {event.mentees[0].username}</p>
                                    <p className="incoming-sessions__event-detail"><strong>Mentor:</strong> {event.mentor.username}</p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="incoming-sessions__no-events">No incoming meetings</div>
                    )}
                </div>
                <button className="incoming-sessions__scroll-btn incoming-sessions__scroll-btn--left" onClick={() => scroll('left')}>&lt;</button>
                <button className="incoming-sessions__scroll-btn incoming-sessions__scroll-btn--right" onClick={() => scroll('right')}>&gt;</button>
            </div>
        </div>
    );
}

export default IncomingSessions;