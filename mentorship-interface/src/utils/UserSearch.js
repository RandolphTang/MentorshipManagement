import React, { useState } from 'react';
import { MentorshipProfileService } from '../service/MentorshipProfileService';
import { CiCircleMore } from "react-icons/ci";
import '../css/UserSearch.css';

function UserSearchComponent({isOpen, onClose, userInfo}) {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [expandedUser, setExpandedUser] = useState(null);
    const [requestMessage, setRequestMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleSearch = async () => {
        try {
            const results = await MentorshipProfileService.searchMatchingUsers(searchTerm);
            setSearchResults(results);
        } catch (error) {
            console.error('Error searching users:', error);
        }
    };

    const handleRequestClick = (userId) => {
        setExpandedUser(expandedUser === userId ? null : userId);
        setRequestMessage('');
    };

    const handleSendRequest = async (recipientId) => {
        const currentId = localStorage.getItem("userId");
        try {
            if(userInfo.role === 'MENTEE'){
                await MentorshipProfileService.createRequest(Number(currentId), recipientId, requestMessage, Number(currentId));
            } else {
                await MentorshipProfileService.createRequest(recipientId, Number(currentId), requestMessage, Number(currentId));
            }
            setExpandedUser(null);
            setRequestMessage('');
        } catch (error) {
            setErrorMessage(error.message || "An error occurred while sending the request.");
        }
    };

    if (!isOpen) return null;

    return (
        <div className="us-modal-overlay">
            <div className="us-modal-content">
                <h2 className="us-search-title">Search Users</h2>
                <div className="us-search-container">
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder="Search users..."
                        className="us-search-input"
                    />
                    <button onClick={handleSearch} className="us-search-button">Search</button>
                </div>
                <div className="us-search-results">
                    {searchResults.map(user => (
                        <div key={user.id} className="us-user-result">
                            <div className="us-user-basicInfo-content">
                                <div className="us-user-info">
                                    <h3 className="us-user-name">{user.username}</h3>
                                    <p className="us-user-email">{user.email}</p>
                                </div>
                                <button onClick={() => handleRequestClick(user.id)} className="us-request-button">
                                    <CiCircleMore/>
                                </button>
                            </div>
                            {expandedUser === user.id && (
                                <div className="us-request-form">
                                <textarea
                                    value={requestMessage}
                                    onChange={(e) => setRequestMessage(e.target.value)}
                                    placeholder="Enter your request message..."
                                    className="us-request-message"
                                />
                                    {errorMessage && <div className="us-error-message">{errorMessage}</div>}
                                    <button onClick={() => handleSendRequest(user.id)} className="us-send-request-button">
                                        Send Request
                                    </button>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
                <button onClick={onClose} className="us-close-button">Close</button>
            </div>
        </div>
    );
}

export default UserSearchComponent;