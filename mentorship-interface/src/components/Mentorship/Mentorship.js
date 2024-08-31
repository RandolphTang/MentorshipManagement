import React from 'react';
import MentorshipProfile from './mentorshipProfile';
import IncomingSessions from "../../utils/IncomingSessions";
import '../../css/Mentorship.css'
import MentorshipMentorMenteeView from "./MentorshipMentorMenteeView";

function Mentorship() {
    return (
        <div className="mentorshipMainContainer">
            <MentorshipMentorMenteeView />
            <div className="mentorshipMainContainer-nonCalendar">
                <MentorshipProfile/>
                <IncomingSessions/>
            </div>
            {/*<Calendar isMainPage={true}/>*/}
        </div>
    );
}

export default Mentorship;