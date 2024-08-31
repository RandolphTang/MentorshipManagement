import React from 'react';
import { Link } from 'react-router-dom';
import { FaUserFriends, FaCalendarAlt, FaHistory } from 'react-icons/fa';
import '../css/SideBar.css'

const SideBar = ({ activeItem, userRole }) => {
    const menuItems = [
        {id: 'mentorship-relationship', label: 'Relationship', icon: <FaUserFriends />},
        {id: 'mentorship-sessions', label: 'Sessions', icon: <FaCalendarAlt />, roleRequired: 'MENTOR'},
        {id: 'mentorship-requests-history', label: 'History', icon: <FaHistory />}
    ];

    return (
        <nav className="al-sidebar">
            <ul className="al-sidebar-list">
                {menuItems.map((item) => (
                    (!item.roleRequired || item.roleRequired === userRole) && (
                        <li key={item.id} className="al-sidebar-item">
                            <Link
                                to={item.id}
                                className={`al-sidebar-link ${activeItem === item.id ? 'active' : ''}`}
                            >
                                <span className="al-sidebar-icon">{item.icon}</span>
                                {item.label}
                            </Link>
                        </li>
                    )
                ))}
            </ul>
        </nav>
    );
};

export default SideBar;