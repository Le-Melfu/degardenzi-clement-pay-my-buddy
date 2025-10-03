import React from 'react'
import { User } from '../../models'
import './UserDisplay.scss'

interface UserDisplayProps {
    user: User
    onEdit: () => void
}

const UserDisplay: React.FC<UserDisplayProps> = ({ user, onEdit }) => {
    return (
        <div className="user-display">
            <div className="profile-fields">
                <div className="profile-field" onClick={onEdit}>
                    <span className="field-label">Username</span>
                    <div className="field-value">
                        <span>{user.username}</span>
                        <div className="arrow"></div>
                    </div>
                </div>
                <div className="profile-field" onClick={onEdit}>
                    <span className="field-label">Mail</span>
                    <div className="field-value">
                        <span>{user.email}</span>
                        <div className="arrow"></div>
                    </div>
                </div>
                <div className="profile-field" onClick={onEdit}>
                    <span className="field-label">Mot de passe</span>
                    <div className="field-value">
                        <span>••••••••</span>
                        <div className="arrow"></div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default UserDisplay
