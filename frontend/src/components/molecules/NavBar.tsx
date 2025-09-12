import React, { useEffect } from 'react'
import './NavBar.scss'
import { useSessionContext } from '../../contexts/SessionContext'
import { useNavigate } from 'react-router-dom'

interface NavBarProps {
    activePage?: string
}

const NavBar: React.FC<NavBarProps> = ({ activePage = '' }) => {
    const { user, logout, isAuthenticated } = useSessionContext()
    const navigate = useNavigate()

    const handleLogout = async () => {
        await logout()
        navigate('/')
    }

    const handleNavigation = (path: string) => {
        navigate(path)
    }

    return (
        <div className="navbar">
            <button
                className="navbar-logo"
                onClick={() => {
                    if (isAuthenticated) {
                        handleNavigation('/profile')
                    } else {
                        handleNavigation('/')
                    }
                }}
            >
                Pay My Buddy
            </button>

            {user ? (
                <nav className="navbar-links">
                    <button
                        onClick={() => handleNavigation('/transfer')}
                        className={`navbar-link ${
                            activePage === 'transfer' ? 'active' : ''
                        }`}
                    >
                        Transférer
                    </button>
                    <button
                        onClick={() => handleNavigation('/profile')}
                        className={`navbar-link ${
                            activePage === 'profile' ? 'active' : ''
                        }`}
                    >
                        Profil
                    </button>
                    <button
                        onClick={() => handleNavigation('/add-connection')}
                        className={`navbar-link ${
                            activePage === 'add-connection' ? 'active' : ''
                        }`}
                    >
                        Ajouter relation
                    </button>
                    <button className="navbar-link" onClick={handleLogout}>
                        Se déconnecter
                    </button>
                </nav>
            ) : (
                <nav className="navbar-links">
                    <button
                        onClick={() => handleNavigation('/login')}
                        className="navbar-link"
                    >
                        Se connecter
                    </button>
                    <button
                        onClick={() => handleNavigation('/register')}
                        className="navbar-link"
                    >
                        Créer un compte
                    </button>
                </nav>
            )}
        </div>
    )
}

export default NavBar
