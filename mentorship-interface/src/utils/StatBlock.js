import React from 'react';
import '../css/StatusBlock.css';

const StatBlock = ({ icon, value, label }) => {

    return (
        <div className="sb-incoming-meetings-block">
            <div className="sb-icon-container">
                {icon}
            </div>
            <div className="sb-count">{value}</div>
            <div className="sb-label">{label}</div>
        </div>
    );
};

export default StatBlock;