import './App.scss'
import HomePage from './pages/HomePage/HomePage'
import LoginPage from './pages/LoginPage/LoginPage'
import ProfilePage from './pages/ProfilePage/ProfilePage'
import AddRelationPage from './pages/AddRelationPage/AddRelationPage'
import TransferPage from './pages/TransferPage/TransferPage'
import CreateAccountPage from './pages/CreateAccountPage/CreateAccountPage'
import ProtectedRoute from './components/ProtectedRoute'
import SessionNotification from './components/SessionNotification'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import ErrorPage from './pages/ErrorPage/ErrorPage'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <BrowserRouter>
            <SessionNotification />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<CreateAccountPage />} />
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
                <Route
                    path="/transfer"
                    element={
                        <ProtectedRoute>
                            <TransferPage />
                        </ProtectedRoute>
                    }
                />
                <Route path="*" element={<ErrorPage />} />
            </Routes>
        </BrowserRouter>
    </StrictMode>
)
