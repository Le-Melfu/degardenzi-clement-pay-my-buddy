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

    return (
        <div className="navbar">
            <div className="navbar-logo">Pay My Buddy</div>
            <nav className="navbar-links">
                <a
                    href="/transfer"
                    className={`navbar-link ${
                        activePage === 'transfer' ? 'active' : ''
                    }`}
                >
                    Transférer
                </a>
                <a
                    href="/profile"
                    className={`navbar-link ${
                        activePage === 'profile' ? 'active' : ''
                    }`}
                >
                    Profil
                </a>
                <a
                    href="/add-connection"
                    className={`navbar-link ${
                        activePage === 'add-connection' ? 'active' : ''
                    }`}
                >
                    Ajouter relation
                </a>
                <a href="#" className="navbar-link" onClick={handleLogout}>
                    Se déconnecter
                </a>
            </nav>
        </div>
    )
}

export default NavBar
