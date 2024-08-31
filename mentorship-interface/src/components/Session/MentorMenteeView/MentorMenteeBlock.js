import React, {useEffect, useState} from 'react';
import UserSearchComponent from "../../../utils/UserSearch";
import '../../../css/Mentor-mentee-block.css';
import {FaSearch, FaUser} from "react-icons/fa";
import {MentorshipProfileService} from "../../../service/MentorshipProfileService";
import { IoMdArrowDropdown } from "react-icons/io";
import {UserProfileService} from "../../../service/UserProfileService";

function MentorMenteeBlock({ userInfo }) {
    const [relationships, setRelationships] = useState([]);
    const [isSearchOpen, setIsSearchOpen] = useState(false);
    const [expandedUser, setExpandedUser] = useState(false);
    const [expandedUserInfo, setExpandedUserInfo] = useState(null);

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                let activeRelationships = [];
                if (userInfo.role === 'MENTOR') {
                    activeRelationships = userInfo.menteeRelationships.filter(
                        relationship => relationship.status === 'ACTIVE'
                    );
                } else if (userInfo.role === 'MENTEE') {
                    activeRelationships = userInfo.mentorRelationship &&
                    userInfo.mentorRelationship.status === 'ACTIVE'
                        ? [userInfo.mentorRelationship]
                        : [];
                }

                const relationshipsWithPics = await Promise.all(activeRelationships.map(async (relationship) => {
                    const user = userInfo.role === 'MENTOR' ? relationship.mentee : relationship.mentor;
                    console.log(user);
                    try {
                        const picBlob = await MentorshipProfileService.getUserProfilePic(user.id);
                        const picUrl = picBlob ? URL.createObjectURL(picBlob) : null;
                        return { ...relationship, profilePicUrl: picUrl };
                    } catch (picError) {
                        console.error('Error fetching profile picture:', picError);
                        return { ...relationship, profilePicUrl: null };
                    }
                }));

                setRelationships(relationshipsWithPics);

            } catch (err) {
                console.error('Error fetching user info:', err);
            }
        };

        fetchUserInfo();

        return () => {
            relationships.forEach(relationship => {
                if (relationship.profilePicUrl) {
                    URL.revokeObjectURL(relationship.profilePicUrl);
                }
            });
        };

    }, [userInfo]);

    const handleExpand = async (userId) => {
        if(expandedUser === true){
            setExpandedUser(false);
            setExpandedUserInfo(null);
        } else {
            try {
                const userInfoResponse = await UserProfileService.getUserInfo(userId);
                setExpandedUser(true);
                setExpandedUserInfo(userInfoResponse);
            } catch (error) {
                console.error("Error fetching user info:", error);
                setExpandedUserInfo(null);
            }
        }
    };

    const relationshipTitle = userInfo.role === 'MENTOR' ? 'Current Mentees' : 'Current Mentors';

    return (
        <div className="mmb-container">
            <div className="mmb-header">
                <h3 className="mmb-title">{relationshipTitle}</h3>
                <button className="mmb-search-btn" onClick={() => setIsSearchOpen(true)}>
                    <FaSearch />
                    <span>Search</span>
                </button>
            </div>
            <UserSearchComponent
                isOpen={isSearchOpen}
                onClose={() => setIsSearchOpen(false)}
                userInfo={userInfo}
            />
            <div className="mmb-content">
                {relationships.length > 0 ? (
                    <div className="mmb-user-list">
                        {relationships.map((relationship, index) => {
                            const user = userInfo.role === 'MENTOR' ? relationship.mentee : relationship.mentor;
                            return (
                                <div key={index} className="mmb-user-item">
                                    <div className="mmb-user-item-preview-info">
                                        <div className="mmb-user-item-profile-pic">
                                            {relationship.profilePicUrl ? (
                                                <img
                                                    src={relationship.profilePicUrl}
                                                    alt="User profile"
                                                    className="mmb-profile-picture"
                                                />
                                            ) : (
                                                <div className="mmb-profile-picture-placeholder">
                                                    <FaUser className="mmb-profile-icon"/>
                                                </div>
                                            )}
                                        </div>
                                        <div className="mmb-user-item-basic-info">
                                            <p className="mmb-user-name">{user.username}</p>
                                            <p className="mmb-user-email">{user.email}</p>
                                        </div>
                                        <button
                                            className="mmb-expand-button"
                                            onClick={() => handleExpand(user.id)}
                                        >
                                            <IoMdArrowDropdown className={expandedUser ? 'rotated' : ''}/>
                                        </button>
                                    </div>
                                    <div className={`mmb-user-item-more-info ${expandedUser ? 'expanded' : ''}`}>
                                        {expandedUser && (
                                            <div className="mmb-user-item-expanded-info">
                                                {Object.keys(expandedUserInfo)
                                                    .filter(key => !['email', 'userId', 'role', 'username'].includes(key))
                                                    .map(key => (
                                                        <p key={key}
                                                           className={`mmb-user-item-info-${key.toLowerCase()}`}>
                                                            <strong>{key}:</strong> {expandedUserInfo[key] || `Nothing yet`}
                                                        </p>
                                                    ))}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ) : (
                    <p className="mmb-no-relationships">No
                        current {userInfo.role === 'MENTOR' ? 'mentees' : 'mentors'}</p>
                )}
            </div>
        </div>
    );
}

export default MentorMenteeBlock;