import './App.scss'
import HomePage from './pages/HomePage/HomePage'
import LoginPage from './pages/LoginPage/LoginPage'
import ProfilePage from './pages/UserPage/ProfilePage'
import AddRelationPage from './pages/AddRelationPage/AddRelationPage'
import ProtectedRoute from './components/ProtectedRoute'
import SessionNotification from './components/SessionNotification'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <SessionNotification />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <ProfilePage />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/add-connection"
                    element={
                        <ProtectedRoute>
                            <AddRelationPage />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<HomePage />} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)
