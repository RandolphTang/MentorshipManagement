import React, { useEffect, useState } from 'react';
import '../../../css/MentorMenteeView.css'
import { MentorshipProfileService } from "../../../service/MentorshipProfileService";
import StatusBlock from './StatusBlock';
import MentorMenteeBlock from "./MentorMenteeBlock";

const MentorMenteeView = () => {
    const [userInfo, setUserInfo] = useState(null);

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const userInfoResponse = await MentorshipProfileService.getUserInfo(userId);
                setUserInfo(userInfoResponse);
            } catch (err) {
                console.error('Error fetching user info:', err);
            }
        };
        fetchUserInfo();
    }, []);

    if (!userInfo) {
        return <div className="mmv-loading">Loading user information...</div>;
    }

    return (
        <div className="mmv-container">
            <div className="mmv-header">
                <h2 className="mmv-welcome">Welcome, {userInfo.role}</h2>
            </div>
            <div className="mmv-content">
                <StatusBlock userInfo={userInfo} />
                <MentorMenteeBlock userInfo={userInfo} />
            </div>
        </div>
    );
}

export default MentorMenteeView;