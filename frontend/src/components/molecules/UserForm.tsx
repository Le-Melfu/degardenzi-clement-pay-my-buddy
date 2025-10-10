import React, { useState, useEffect } from 'react'
import { User, UpdateUserRequest } from '../../models'
import InputField from '../atoms/InputField'
import MainButton from '../atoms/MainButton'
import './UserForm.scss'

interface UserFormProps {
    user: User
    onSave: (updateRequest: UpdateUserRequest) => Promise<void>
    onCancel: () => void
    isLoading?: boolean
}

interface FormData {
    username: string
    email: string
    password: string
}

const UserForm: React.FC<UserFormProps> = ({
    user,
    onSave,
    onCancel,
    isLoading = false,
}) => {
    const [formData, setFormData] = useState<FormData>({
        username: user.username || '',
        email: user.email || '',
        password: '',
    })

    const [errors, setErrors] = useState<Partial<FormData>>({})

    useEffect(() => {
        setFormData({
            username: user.username || '',
            email: user.email || '',
            password: '',
        })
    }, [user])

    const compileFormData = (): UpdateUserRequest => {
        const updateRequest: UpdateUserRequest = {}

        if (formData.username.trim() && formData.username !== user.username) {
            updateRequest.username = formData.username.trim()
        }

        if (formData.email.trim() && formData.email !== user.email) {
            updateRequest.email = formData.email.trim()
        }

        if (formData.password.trim()) {
            updateRequest.password = formData.password.trim()
        }

        return updateRequest
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        const updateRequest = compileFormData()

        try {
            await onSave(updateRequest)
        } catch (error) {
            console.error('Erreur lors de la sauvegarde:', error)
        }
    }

    const handleInputChange = (field: keyof FormData, value: string) => {
        setFormData((prev) => ({ ...prev, [field]: value }))

        // Validate password length
        if (field === 'password' && value && value.length < 6) {
            setErrors((prev) => ({
                ...prev,
                password: 'Le mot de passe doit contenir au moins 6 caractères',
            }))
        } else if (errors[field]) {
            setErrors((prev) => ({ ...prev, [field]: undefined }))
        }
    }

    return (
        <form className="user-form" onSubmit={handleSubmit}>
            <div className="form-fields">
                <InputField
                    label="Username"
                    type="text"
                    value={formData.username}
                    onChange={(value) => handleInputChange('username', value)}
                    error={errors.username}
                />

                <InputField
                    label="Email"
                    type="email"
                    value={formData.email}
                    onChange={(value) => handleInputChange('email', value)}
                    error={errors.email}
                />

                <InputField
                    label="Mot de passe"
                    type="password"
                    placeholder="Nouveau mot de passe"
                    value={formData.password}
                    onChange={(value) => handleInputChange('password', value)}
                    error={errors.password}
                />
            </div>

            <div className="form-actions">
                <MainButton
                    type="button"
                    variant="secondary"
                    onClick={onCancel}
                    disabled={isLoading}
                >
                    Annuler
                </MainButton>
                <MainButton
                    type="submit"
                    variant="primary"
                    disabled={isLoading}
                >
                    {isLoading ? 'Sauvegarde...' : 'Sauvegarder'}
                </MainButton>
            </div>
        </form>
    )
}

export default UserForm
