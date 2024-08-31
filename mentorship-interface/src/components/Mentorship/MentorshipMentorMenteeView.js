import React, {useEffect, useRef, useState} from "react";
import {MentorshipProfileService} from "../../service/MentorshipProfileService";
import {FaChevronDown, FaChevronUp, FaSearch, FaUser} from "react-icons/fa";
import '../../css/Mentorship.css'

function MentorshipMentorMenteeView() {
    const [userInfo, setUserInfo] = useState(null);
    const [relationships, setRelationships] = useState([]);
    const scrollContainerRef = useRef(null);

    useEffect(() => {
        const fetchUserInfo = async () => {
            const userId = localStorage.getItem('userId');
            try {
                const userInfoResponse = await MentorshipProfileService.getUserInfo(userId);
                setUserInfo(userInfoResponse);

                let activeRelationships = userInfoResponse.role === 'MENTOR'
                    ? userInfoResponse.menteeRelationships.filter(r => r.status === 'ACTIVE')
                    : userInfoResponse.mentorRelationship && userInfoResponse.mentorRelationship.status === 'ACTIVE'
                        ? [userInfoResponse.mentorRelationship]
                        : [];

                const relationshipsWithPics = await Promise.all(activeRelationships.map(async (relationship) => {
                    const user = userInfoResponse.role === 'MENTOR' ? relationship.mentee : relationship.mentor;
                    try {
                        const picBlob = await MentorshipProfileService.getUserProfilePic(user.id);
                        const picUrl = picBlob ? URL.createObjectURL(picBlob) : null;
                        return { ...relationship, profilePicUrl: picUrl, user };
                    } catch (picError) {
                        return { ...relationship, profilePicUrl: null, user };
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
    }, []);


    const scroll = (direction) => {
        const container = scrollContainerRef.current;
        if (container) {
            const scrollAmount = direction === 'up' ? -80 : 80;
            container.scrollBy({ top: scrollAmount, behavior: 'smooth' });
        }
    };

    const relationshipTitle = userInfo?.role === 'MENTOR' ? 'Current Mentees' : 'Current Mentors';

    return (
        <div className="mmmv-container">
            <h3 className="mmmv-title">{relationshipTitle}</h3>
            <div className="mmmv-carousel">
                <button className="mmmv-scroll-btn mmmv-scroll-btn--up" onClick={() => scroll('up')}>
                    <FaChevronUp />
                </button>
                <div className="mmmv-profile-list" ref={scrollContainerRef}>
                    {relationships.map((relationship, index) => (
                        <div key={index} className="mmmv-profile-item">
                            {relationship.profilePicUrl ? (
                                <img
                                    src={relationship.profilePicUrl}
                                    alt={`${relationship.user.username}'s profile`}
                                    className="mmmv-profile-pic"
                                />
                            ) : (
                                <div className="mmmv-profile-pic-placeholder">
                                    <FaUser className="mmmv-profile-icon"/>
                                </div>
                            )}
                            <div className="mmmv-profile-info">
                                <p className="mmmv-profile-name">{relationship.user.username}</p>
                                <p className="mmmv-profile-email">{relationship.user.email}</p>
                            </div>
                        </div>
                    ))}
                </div>
                {relationships.length === 0 && (
                    <p className="mmmv-no-relationships">
                        No current {userInfo?.role === 'MENTOR' ? 'mentees' : 'mentors'}
                    </p>
                )}
                <button className="mmmv-scroll-btn mmmv-scroll-btn--down" onClick={() => scroll('down')}>
                    <FaChevronDown />
                </button>
            </div>
        </div>
    );
}

export default MentorshipMentorMenteeView;