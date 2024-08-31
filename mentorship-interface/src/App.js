import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import Login from './components/Login';
import Profile from './components/I&AM/Profile';
import Mentorship from './components/Mentorship/Mentorship'
import RoleSelection from "./components/RoleSelection";
import ProtectedRoute from "./utils/ProtectedRoute";
import Header from "./utils/Header";
import Dashboard from "./components/Session/Session";
import './App.css'


function App() {
  return (
      <Router>
          <div className="App">
              <Header/>
              <main className="main-content">
                  <Routes>
                      {/*<Route path="/" element={<Home />} />*/}
                      <Route path="/login" element={<Login/>}/>
                      <Route path="/select-role" element={
                          <ProtectedRoute>
                              <RoleSelection/>
                          </ProtectedRoute>
                      }/>
                      <Route path="/profile" element={
                          <ProtectedRoute>
                              <Profile/>
                          </ProtectedRoute>
                      }/>
                      <Route path="/mentorship" element={
                          <ProtectedRoute>
                              <Mentorship/>
                          </ProtectedRoute>
                      }/>

                      <Route path="/session/*" element={
                          <ProtectedRoute>
                              <Dashboard/>
                          </ProtectedRoute>
                      }/>

                      <Route path="*" element={<Navigate to="/login" replace/>}/>
                  </Routes>
                </main>
              </div>
      </Router>

);
}
export default App;
