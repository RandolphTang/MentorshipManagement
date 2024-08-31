import React from 'react';
import SessionRequestBlock from './SessionRequestBlock';
import GoogleCalendar from "../../../utils/Calendar";
import '../../../css/SessionsView.css';
import IncomingSessions from "../../../utils/IncomingSessions";

function SessionView() {
    return (
        <div className="SessionView">
            {/*<IncomingSessions />*/}
            <SessionRequestBlock />
            <GoogleCalendar isMainPage={false}/>
        </div>
    );
}

export default SessionView;