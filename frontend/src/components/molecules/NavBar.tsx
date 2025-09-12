import React from 'react'
import './NavBar.scss'
import { useSession } from '../../hooks/useSession'
import { useNavigate } from 'react-router-dom'

interface NavBarProps {
    activePage?: string
}

const NavBar: React.FC<NavBarProps> = ({ activePage = '' }) => {
    const { logout } = useSession()
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
            <div className="navbar-logo">Pay My Buddy</div>
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
        </div>
    )
}

export default NavBar
