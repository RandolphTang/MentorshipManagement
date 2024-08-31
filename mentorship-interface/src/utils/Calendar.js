import React, { useEffect, useState, useRef } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import { MentorshipProfileService } from '../service/MentorshipProfileService';
import '../css/Calendar.css';

const Calendar = ({ isMainPage }) => {
    const containerClassName = isMainPage
        ? 'calendar-container calendar-container-full-width'
        : 'calendar-container';
    const [events, setEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const popoverRef = useRef(null);

    useEffect(() => {
        fetchEvents();
    }, []);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (popoverRef.current && !popoverRef.current.contains(event.target)) {
                closePopover();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const fetchEvents = async () => {
        const userId = localStorage.getItem('userId');
        try {
            const start = new Date();
            start.setMonth(start.getMonth() - 1);
            const end = new Date();
            end.setMonth(end.getMonth() + 2);

            const fetchedEvents = await MentorshipProfileService.getSessionsDateBasedForUser(Number(userId), start, end);
            const formattedEvents = fetchedEvents.map(event => ({
                id: event.id,
                title: event.title,
                start: event.startTime,
                end: event.endTime,
                backgroundColor: getEventColor(event),
                borderColor: getEventColor(event),
                extendedProps: {
                    description: event.description,
                    location: event.location
                }
            }));
            setEvents(formattedEvents);
        } catch (error) {
            console.error('Error fetching events:', error);
        }
    };

    const getEventColor = (event) => {
        const colors = ['#4285F4', '#EA4335', '#FBBC05', '#34A853', '#46D1FD', '#E67C73'];
        return colors[event.id % colors.length];
    };

    const handleEventClick = (info) => {
        setSelectedEvent(info.event);
        const popover = popoverRef.current;

        if (popover && info.jsEvent) {
            const calendarElement = document.querySelector('.calendar');
            if (calendarElement) {
                const calendarRect = calendarElement.getBoundingClientRect();
                const clickX = info.jsEvent.clientX - calendarRect.left;
                const clickY = info.jsEvent.clientY - calendarRect.top;

                popover.style.display = 'block';
                popover.style.left = `${clickX + 10}px`;
                popover.style.top = `${clickY + 10}px`;

                // Ensure the popover doesn't go out of the calendar bounds
                const popoverRect = popover.getBoundingClientRect();
                if (popoverRect.right > calendarRect.right) {
                    popover.style.left = `${calendarRect.right - popoverRect.width - 10}px`;
                }
                if (popoverRect.bottom > calendarRect.bottom) {
                    popover.style.top = `${calendarRect.bottom - popoverRect.height - 10}px`;
                }
            } else {
                console.error('Calendar element not found');
            }
        }
    };

    const closePopover = () => {
        setSelectedEvent(null);
        popoverRef.current.style.display = 'none';
    };

    return (
        <div className={containerClassName}>
            <div className="calendar">
                <FullCalendar
                    height="100%"
                    aspectRatio={1.5}
                    plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                    initialView="dayGridMonth"
                    headerToolbar={{
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,timeGridWeek,timeGridDay'
                    }}
                    events={events}
                    // dateClick={handleDateClick}
                    eventClick={handleEventClick}
                    eventContent={(eventInfo) => (
                        <div className="fc-event-content">
                            <b>{eventInfo.timeText}</b>
                            <i>{eventInfo.event.title}</i>
                        </div>
                    )}
                />
                <div ref={popoverRef} className="event-popover">
                    {selectedEvent && (
                        <div className="event-details">
                            <h3>{selectedEvent.title}</h3>
                            <p><strong>Start:</strong> {selectedEvent.start.toLocaleString()}</p>
                            <p><strong>End:</strong> {selectedEvent.end.toLocaleString()}</p>
                            <p><strong>Description:</strong> {selectedEvent.extendedProps.description}</p>
                            <p><strong>Location:</strong> {selectedEvent.extendedProps.location}</p>
                            <button onClick={closePopover}>Close</button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Calendar;