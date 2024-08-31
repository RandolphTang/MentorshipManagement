import React, {useEffect, useState} from 'react';
import {Navigate} from 'react-router-dom';
import API from "./api";

const ProtectedRoute = ({ children, allowedRoles = [] }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                await API.get('/auth/status');
                setIsAuthenticated(true);
            } catch (error) {
                console.log(error);
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuthStatus();
        const intervalId = setInterval(checkAuthStatus, 5 * 60 * 1000);
        return () => clearInterval(intervalId);
    }, []);

    if (isLoading) {
        return <div>Loading...</div>;
    }

    return isAuthenticated ? children : <Navigate to="/login" />;
};

export default ProtectedRoute;