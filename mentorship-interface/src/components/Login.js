import React, { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import { FaGithub, FaGoogle } from 'react-icons/fa';
import API from '../utils/api';
import '../css/Login.css';

function Login() {
    const [isLogin, setIsLogin] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [username, setUsername] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        localStorage.removeItem('token');
    }, []);

    const handleGoogleLogin = () => {
        window.location.href = 'http://localhost:8084/oauth2/authorization/google';
    };

    const handleGithubLogin = () => {
        window.location.href = 'http://localhost:8084/oauth2/authorization/github';
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        try {
            if (isLogin) {
                const response = await API.post('/auth/Login', { email, password });
                localStorage.setItem('userId', response.data.user.id);
                setMessage('Login successful!');
                setTimeout(() => navigate('/mentorship'), 1500);
            } else {
                const response = await API.post('/auth/register', { username, email, password });
                localStorage.setItem('userId', response.data.userId);
                setMessage('Registration successful! Redirecting to role selection...');
                setTimeout(() => navigate('/select-role'), 1500);
            }
        } catch (error) {
            console.error('Error:', error.response?.data || error.message);
            setMessage(error.response?.data?.message || 'An error occurred. Please try again.');
        }
    };

    return (
        <div className="al-auth-container">
            <div className="al-auth-box">
                <h2 className="al-auth-title">{isLogin ? 'Sign In' : 'Create Account'}</h2>
                <form onSubmit={handleSubmit}>
                    {!isLogin && (
                        <div className="al-form-group">
                            <input
                                type="text"
                                id="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="Username"
                                required
                                className="al-input-field"
                            />
                        </div>
                    )}
                    <div className="al-form-group">
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Email"
                            required
                            className="al-input-field"
                        />
                    </div>
                    <div className="al-form-group">
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                            required
                            className="al-input-field"
                        />
                    </div>
                    <button type="submit" className="al-submit-button">
                        {isLogin ? 'Sign In' : 'Create Account'}
                    </button>
                </form>
                <p className="al-toggle-form-link" onClick={() => setIsLogin(!isLogin)}>
                    {isLogin ? 'Need an account? Create one' : 'Already have an account? Sign In'}
                </p>
                {message && <p className="al-message-display">{message}</p>}
                <div className="al-oauth-options-container">
                    <button className="al-oauth-button al-oauth-button-github" onClick={handleGithubLogin}
                            aria-label="Sign in with GitHub">
                        <FaGithub/>
                    </button>
                    <button className="al-oauth-button al-oauth-button-google" onClick={handleGoogleLogin}
                            aria-label="Sign in with Google">
                        <FaGoogle/>
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Login;