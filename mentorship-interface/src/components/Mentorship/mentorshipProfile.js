import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { MentorshipProfileService } from '../../service/MentorshipProfileService';
import '../../css/Mentorship.css'
import {FaUser} from "react-icons/fa";

function MentorshipProfile() {
    const [userInfo, setUserInfo] = useState(null);
    const [profilePic, setProfilePic] = useState(null);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const params = new URLSearchParams(location.search);
                let userId = params.get('userId');

                if (userId) {
                    localStorage.setItem('userId', userId);
                    navigate(location.pathname, { replace: true });
                } else {
                    userId = localStorage.getItem('userId');
                }

                const userInfoResponse  = await MentorshipProfileService.getUserInfo(userId);
                setUserInfo(userInfoResponse);

                try {
                    const picBlob = await MentorshipProfileService.getUserProfilePic(userId);
                    const picUrl = picBlob ? URL.createObjectURL(picBlob) : null;
                    setProfilePic(picUrl);
                } catch (picError) {
                }
            } catch (err) {
                console.error('Error fetching user info:', err);
            }
        };

        fetchUserInfo();

        return () => {
            if (profilePic) {
                URL.revokeObjectURL(profilePic);
            }
        };
    }, [location, navigate]);


    if (!userInfo) {
        return <div>Loading user information...</div>;
    }

    return (
        <div className="mp-profile-basic-info">
            <div className="mp-profile-picture-container">
                {profilePic ? (
                    <img
                        src={profilePic}
                        alt="User profile"
                        className="mp-profile-picture"
                    />
                ) : (
                    <div className="mp-profile-picture-placeholder">
                        <FaUser className="mp-profile-icon"/>
                    </div>
                )}
            </div>
            <div className="mp-profile-info">
                <h1 className="mp-profile-welcome">Welcome back!</h1>
                <p className="mp-profile-name">{userInfo.username}</p>
                <p className="mp-profile-email">{userInfo.email}</p>
            </div>
        </div>
    );
}

export default MentorshipProfile;