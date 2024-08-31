import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { MentorshipProfileService } from '../service/MentorshipProfileService';
import { FaChalkboardTeacher, FaUserGraduate } from 'react-icons/fa';
import '../css/RoleSelection.css';

function RoleSelection() {
    const navigate = useNavigate();
    const location = useLocation();

    const handleRoleSelection = async (selectedRole) => {
        try {
            const params = new URLSearchParams(location.search);
            let userId = params.get('userId');
            if (userId) {
                localStorage.setItem('userId', userId);
            }
            await MentorshipProfileService.userSelectRole(selectedRole);
            navigate('/mentorship');
        } catch (error) {
            console.error('Role selection failed:', error);
        }
    };

    return (
        <div className="role-selection-container">
            <div className="role-selection-box">
                <h2>Choose Initial Role</h2>
                <div className="role-options">
                    <div className="role-option" onClick={() => handleRoleSelection('MENTOR')}>
                        <FaChalkboardTeacher className="role-icon"/>
                        <h3>Mentor</h3>
                    </div>
                    <div className="role-option" onClick={() => handleRoleSelection('MENTEE')}>
                        <FaUserGraduate className="role-icon"/>
                        <h3>Mentee</h3>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default RoleSelection;