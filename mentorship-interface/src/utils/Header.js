import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import '../css/Header.css';

const Header = () => {
    const location = useLocation();
    const showHeader = ['/profile', '/mentorship', '/session'].some(path =>
        location.pathname.startsWith(path)
    );

    if (!showHeader) return null;

    return (
        <header className="ap-header">
            <div className="ap-header-content">
                <nav className="ap-header-nav">
                    <Link to="/mentorship" className={`ap-nav-link ${location.pathname.startsWith('/mentorship') ? 'active' : ''}`}>Mentorship</Link>
                    <Link to="/profile" className={`ap-nav-link ${location.pathname.startsWith('/profile') ? 'active' : ''}`}>I&AM</Link>
                    <Link to="/session" className={`ap-nav-link ${location.pathname.startsWith('/session') ? 'active' : ''}`}>Session</Link>
                </nav>
            </div>
        </header>
    );
};

export default Header;