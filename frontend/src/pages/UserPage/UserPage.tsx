import React from 'react'
import { User } from '../../models'
import { useLocation } from 'react-router-dom'

const UserPage: React.FC = () => {
    const { user } = useLocation().state as { user: User }
    return (
        <div className="user-page">
            <h1>Mon Profil</h1>
            {user ? (
                <div className="user-info">
                    <p>Email: {user.email}</p>
                    <p>Nom d'utilisateur: {user.username}</p>
                </div>
            ) : (
                <p>Veuillez vous connecter pour voir vos informations.</p>
            )}
        </div>
    )
}

export default UserPage
