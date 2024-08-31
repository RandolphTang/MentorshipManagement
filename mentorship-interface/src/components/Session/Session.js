import React, {useEffect, useState} from 'react';
import SideBar from '../../utils/SideBar';
import {Navigate, Route, Routes, useLocation} from "react-router-dom";
import MentorMenteeView from "./MentorMenteeView/MentorMenteeView";
import RequestHistory from "./RequestHistory/RequestHistory";
import '../../css/DashBoard.css'
import SessionsView from "./Sessions/SessionsView";
import { MentorshipProfileService } from '../../service/MentorshipProfileService';

const Dashboard = () => {
    const [activeItem, setActiveItem] = useState('mentorship-relationship');
    const [userRole, setUserRole] = useState(null);

    const location = useLocation();

    useEffect(() => {
        const path = location.pathname.split('/').pop();
        setActiveItem(path);
    }, [location]);

    useEffect(() => {
        const fetchUserRole = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const response = await MentorshipProfileService.getUserInfo(userId);
                setUserRole(response.role);
            } catch (error) {
                console.error('Error fetching user role:', error);
            }
        };
        fetchUserRole();
    }, []);

    return (
        <div className="dashboard">
            <SideBar activeItem={activeItem} userRole={userRole} />
            <div className="dashboard-main">
                <Routes>
                    <Route index element={<Navigate to="mentorship-relationship" replace />} />
                    <Route path="mentorship-relationship" element={<MentorMenteeView />}/>
                    <Route path="mentorship-sessions" element={<SessionsView />}/>
                    <Route path="mentorship-requests-history" element={<RequestHistory />}/>
                </Routes>
            </div>
        </div>
    );
};

export default Dashboard;