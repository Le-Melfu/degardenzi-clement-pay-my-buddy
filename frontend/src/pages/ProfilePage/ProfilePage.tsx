import React, { useEffect, useState } from 'react'
import MainButton from '../../components/atoms/MainButton'
import NavBar from '../../components/molecules/NavBar'
import UserDisplay from '../../components/molecules/UserDisplay'
import UserForm from '../../components/molecules/UserForm'
import { useSession } from '../../hooks/useSession'
import { User, UpdateUserRequest } from '../../models'
import { api } from '../../services/api'
import './ProfilePage.scss'
import CircularProgressIndicator from '../../components/atoms/CircularProgressIndicator'
import Snackbar from '../../components/atoms/Snackbar'

const ProfilePage: React.FC = () => {
    const { user, isLoading, forceRefreshUser, updateUser } = useSession()
    const [isEditing, setIsEditing] = useState(false)
    const [isSaving, setIsSaving] = useState(false)
    const [snackbar, setSnackbar] = useState<{
        isVisible: boolean
        message: string
        success: boolean
    }>({
        isVisible: false,
        message: '',
        success: false,
    })

    const showSnackbar = (message: string, success: boolean) => {
        setSnackbar({
            isVisible: true,
            message,
            success,
        })
    }

    const hideSnackbar = () => {
        setSnackbar((prev) => ({ ...prev, isVisible: false }))
    }

    useEffect(() => {
        forceRefreshUser()
    }, [forceRefreshUser])

    const handleEdit = () => {
        setIsEditing(true)
    }

    const handleCancel = () => {
        setIsEditing(false)
    }

    const handleSave = async (updateRequest: UpdateUserRequest) => {
        setIsSaving(true)
        try {
            const emailChanged = updateRequest.email !== undefined
            const updatedUser = await api.updateUser(updateRequest)

            // If email was changed, the session is invalidated and we need to re-login
            if (emailChanged) {
                showSnackbar(
                    'Email modifié avec succès. Veuillez vous reconnecter avec votre nouvel email.',
                    true
                )
                // Wait for the snackbar to be visible, then logout
                setTimeout(async () => {
                    await window.location.assign('/login')
                }, 2000)
            } else {
                updateUser(updatedUser)
                showSnackbar('Profil mis à jour avec succès', true)
                setIsEditing(false)
            }
        } catch (error) {
            const err = error as Error
            if (err.message === 'SESSION_EXPIRED') {
                showSnackbar(
                    'Session expirée. Veuillez vous reconnecter.',
                    false
                )
                setTimeout(() => {
                    window.location.assign('/login')
                }, 2000)
            } else {
                showSnackbar(
                    "Erreur lors de la mise à jour de l'utilisateur",
                    false
                )
            }
        } finally {
            setIsSaving(false)
        }
    }

    return (
        <div className="profile-page page-scale">
            <div className="profile-container">
                <div className="profile-content">
                    {isLoading ? (
                        <CircularProgressIndicator />
                    ) : user ? (
                        <>
                            {isEditing ? (
                                <UserForm
                                    user={user}
                                    onSave={handleSave}
                                    onCancel={handleCancel}
                                    isLoading={isSaving}
                                />
                            ) : (
                                <>
                                    <UserDisplay
                                        user={user}
                                        onEdit={handleEdit}
                                    />
                                    <MainButton
                                        variant="primary"
                                        onClick={handleEdit}
                                        className="modify-button"
                                    >
                                        Modifier
                                    </MainButton>
                                </>
                            )}
                        </>
                    ) : (
                        <div>Erreur de chargement</div>
                    )}
                </div>
            </div>
            <Snackbar
                message={snackbar.message}
                success={snackbar.success}
                isVisible={snackbar.isVisible}
                onClose={hideSnackbar}
            />
        </div>
    )
}

export default ProfilePage
