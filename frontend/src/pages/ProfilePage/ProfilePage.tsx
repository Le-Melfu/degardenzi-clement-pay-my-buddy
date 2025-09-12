import React from 'react'
import MainButton from '../../components/atoms/MainButton'
import NavBar from '../../components/molecules/NavBar'
import { useSession } from '../../hooks/useSession'
import './ProfilePage.scss'
import CircularProgressIndicator from '../../components/atoms/CircularProgressIndicator'

const ProfilePage: React.FC = () => {
    const { user, isLoading } = useSession()

    const handleModify = () => {
        // TODO: Implémenter la modification du profil
        console.log('Modification du profil')
    }

    return (
        <div className="profile-page">
            <NavBar activePage="profile" />

            <div className="profile-container">
                <div className="profile-content">
                    {user ? (
                        <>
                            <div className="profile-fields">
                                <div className="profile-field">
                                    <span className="field-label">
                                        Username
                                    </span>
                                    <div className="field-value">
                                        <span>{user.username}</span>
                                        <div className="arrow"></div>
                                    </div>
                                </div>
                                <div className="profile-field">
                                    <span className="field-label">Mail</span>
                                    <div className="field-value">
                                        <span>{user.email}</span>
                                        <div className="arrow"></div>
                                    </div>
                                </div>
                                <div className="profile-field">
                                    <span className="field-label">
                                        Mot de passe
                                    </span>
                                    <div className="field-value">
                                        <span>••••••••</span>
                                        <div className="arrow"></div>
                                    </div>
                                </div>
                            </div>
                            <MainButton
                                variant="primary"
                                onClick={handleModify}
                                className="modify-button"
                            >
                                Modifier
                            </MainButton>
                        </>
                    ) : (
                        <div>
                            {isLoading
                                ? 'Chargement des informations...'
                                : 'Erreur de chargement'}
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default ProfilePage
