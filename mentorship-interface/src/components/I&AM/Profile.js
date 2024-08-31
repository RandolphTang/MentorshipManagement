import React, {useEffect, useState} from 'react';
import {UserProfileService} from "../../service/UserProfileService";
import {MentorshipProfileService} from "../../service/MentorshipProfileService";
import { useNavigate } from "react-router-dom";
import { FaUser, FaEdit, FaSave, FaCalendar} from 'react-icons/fa';
import '../../css/UserProfile.css';

function UserProfile() {
    const [userInfo, setUserInfo] = useState(null);
    const [profilePic, setProfilePic] = useState(null);
    const [error, setError] = useState(null);
    const [editingField, setEditingField] = useState(null);
    const [accountAge, setAccountAge] = useState(null);
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [newEmail, setNewEmail] = useState('');
    const [modifyType, setModifyType] = useState('');
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await UserProfileService.logUserOut(navigate);
        } catch (error) {
            console.error('Logout failed', error);
        }
    };

    const handleSecurityModify = (type) => {
        setModifyType(type);
        setShowModal(true);
    };

    const handleDeleteAccount = async () => {
        if (window.confirm("Are you sure you want to delete your account? This action cannot be undone.")) {
            try {
                await UserProfileService.deleteUser(navigate);
            } catch (error) {
                console.error('Error deleting account:', error);
                setError('Failed to delete account. Please try again.');
            }
        }
    };

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const userInfoResponse  = await UserProfileService.getUserInfo(userId);
                const userInfoRoleResponse  = await MentorshipProfileService.getUserInfo(userId);
                const userAccountAge  = await UserProfileService.getUserAccountAge(userId);
                setUserInfo({...userInfoResponse, role: userInfoRoleResponse.role});
                setAccountAge(userAccountAge);
                try {
                    const picBlob = await MentorshipProfileService.getUserProfilePic(userId);
                    const picUrl = picBlob ? URL.createObjectURL(picBlob) : null;
                    setProfilePic(picUrl);
                } catch (picError) {
                }
            } catch (err) {
                console.error('Error fetching user info:', err);
                setError(err.message || 'Failed to load user information');
            }
        };

        fetchUserInfo();

        return () => {
            if (profilePic) {
                URL.revokeObjectURL(profilePic);
            }
        };
    }, []);

    const handleProfilePicChange = async (event) => {
        const file = event.target.files[0];
        if (file) {
            try {
                const userId = localStorage.getItem('userId');
                const response = await UserProfileService.setUserProfilePic(file, userId);
                setProfilePic(response.data);
            } catch (error) {
                console.error('Error uploading profile picture:', error);
            }
        }
    };

    const handleRoleChange = async (event) => {
        const newRole = event.target.value;
        const userId = localStorage.getItem('userId');
        try {
            await UserProfileService.toggleUserRole(userId);
            setUserInfo(prevInfo => ({
                ...prevInfo,
                role: newRole
            }));
        } catch (error) {
            console.error('Error changing role:', error);
        }
    };

    const handleSave = async (field, value) => {
        try {
            await UserProfileService.updateUserInfo(userInfo.userId, userInfo);
            setUserInfo(prevInfo => ({
                ...prevInfo,
                [field]: value
            }));
            setEditingField(null);
        } catch (error) {
            console.error('Error updating user info:', error);
        }
    };

    const handleChangePassword = async () => {
        try {
            const userId = localStorage.getItem('userId');
            await UserProfileService.changePassword(userId, {
                currentPassword: currentPassword,
                newPassword: newPassword
            });
            setCurrentPassword('');
            setNewPassword('');
        } catch (error) {
            console.error('Error change user password:', error);
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        setError('Current password is incorrect');
                        break;
                    case 409:
                        setError('password already in use');
                        break;
                    default:
                        setError('An error occurred while changing password');
                }
            } else {
                setError('Network error. Please try again.');
            }
        }
    };

    const handleChangeEmail = async () => {
        try {
            const userId = localStorage.getItem('userId');
            await UserProfileService.changeEmail(userId, {
                password: currentPassword,
                newEmail: newEmail
            });
            setCurrentPassword('');
            setNewEmail('');
        } catch (error) {
            console.error('Error change user email:', error);
            if (error.response) {
                switch (error.response.status) {
                    case 401:
                        setError('Current password is incorrect');
                        break;
                    case 409:
                        setError('Email already in use');
                        break;
                    default:
                        setError('An error occurred while changing email');
                }
            } else {
                setError('Network error. Please try again.');
            }
        }
    };

    const handleSecuritySubmit = async (e) => {
        e.preventDefault();
        try {
            if (modifyType === 'email') {
                await handleChangeEmail();
            } else if (modifyType === 'password') {
                await handleChangePassword();
            }
            setShowModal(false);
            setCurrentPassword('');
            setNewEmail('');
            setNewPassword('');
        } catch (error) {
            console.error(`Error changing ${modifyType}:`, error);
        }
    };

    const renderField = (field, label) => {
        return (
            <div className="up-profile-field">
                <div className="up-field-header">
                    <h3>{label}</h3>
                    {editingField !== field && (
                        <button onClick={() => handleEdit(field)} className="up-edit-button">
                            <FaEdit />
                        </button>
                    )}
                </div>
                {editingField === field ? (
                    <div className="up-field-edit">
                        <input
                            type="text"
                            value={userInfo[field] || ''}
                            onChange={(e) => setUserInfo({...userInfo, [field]: e.target.value})}
                            autoFocus
                            className="up-field-input"
                        />
                        <button
                            onClick={() => {
                                handleSave(field, userInfo[field]);
                                setEditingField(null);
                            }}
                            className="up-save-button"
                        >
                            <FaSave />
                        </button>
                    </div>
                ) : (
                    <p className="up-field-content">{userInfo[field] || `this human is lazy and won't write anything down`}</p>
                )}
            </div>
        );
    };

    const handleEdit = (field) => {
        setEditingField(field);
    };

    if (!userInfo) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div style={{ color: 'red' }}>{error}</div>;
    }


    return (
        <div className="up-profile-container">
            <div className="up-profile-content">
                <div className="up-profile-basic-info">
                    <div className="up-profile-picture-container">
                        {profilePic ? (
                            <img
                                src={profilePic}
                                alt="User profile"
                                className="up-profile-picture"
                            />
                        ) : (
                            <div className="up-profile-picture-placeholder">
                                <FaUser className="up-profile-icon"/>
                            </div>
                        )}
                        <div className="up-profile-picture-overlay"
                             onClick={() => document.getElementById('profilePicUpload').click()}>
                            <span>Change Profile</span>
                        </div>
                        <input
                            type="file"
                            id="profilePicUpload"
                            hidden
                            accept="image/*"
                            onChange={handleProfilePicChange}
                        />
                    </div>
                    <h2 className="up-profile-name">{userInfo.username}</h2>
                    <p className="up-profile-email">{userInfo.email}</p>
                    <div className="up-profile-role">
                        <select
                            value={userInfo.role}
                            onChange={handleRoleChange}
                            className="up-role-dropdown"
                        >
                            <option value="MENTEE">Mentee</option>
                            <option value="MENTOR">Mentor</option>
                        </select>
                    </div>
                </div>

                <div className="up-profile-stat">
                    <div className="up-profile-stat-age">
                        <FaCalendar/>
                        <h2>Active as a {userInfo.role.toLowerCase()} for {accountAge + 1} days</h2>
                    </div>
                    <div className="up-profile-stat-security">
                        <div className="up-stat-item">
                            <span>Email: {userInfo.email}</span>
                            <button onClick={() => handleSecurityModify('email')} className="up-save-button">
                                <FaEdit/>
                            </button>
                        </div>
                        <div className="up-stat-item">
                            <span>Password: ********</span>
                            <button onClick={() => handleSecurityModify('password')} className="up-save-button">
                                <FaEdit/>
                            </button>
                        </div>
                    </div>
                </div>

                {showModal && (
                    <div className="up-modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="up-modal-content" onClick={(e) => e.stopPropagation()}>
                            {error && <p className="up-error-message">{error}</p>}
                            <h2>Change {modifyType === 'email' ? 'Email' : 'Password'}</h2>
                            <form onSubmit={handleSecuritySubmit}>
                                <input
                                    type="password"
                                    placeholder="Current Password (leave it blank if no password)"
                                    value={currentPassword}
                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                />
                                {modifyType === 'email' ? (
                                    <input
                                        type="email"
                                        placeholder="New Email"
                                        value={newEmail}
                                        onChange={(e) => setNewEmail(e.target.value)}
                                        required
                                    />
                                ) : (
                                    <input
                                        type="password"
                                        placeholder="New Password"
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                        required
                                    />
                                )}
                                <div className="up-modal-buttons">
                                    <button type="submit">Confirm</button>
                                    <button type="button" onClick={() => setShowModal(false)}>Cancel</button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

                <div className="up-profile-details">
                    <div className="up-profile-widget">
                        {renderField('bio', 'Bio')}
                    </div>
                    <div className="up-profile-widget">
                        {renderField('skills', 'Skills')}
                    </div>
                    <div className="up-profile-widget">
                        {renderField('interests', 'Interests')}
                    </div>
                    <div className="up-action-buttons">
                        <button onClick={handleLogout} className="up-logout-button">Logout</button>
                        <button onClick={handleDeleteAccount} className="up-delete-account-button">Delete Account
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default UserProfile;