import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import MainButton from '../../components/atoms/MainButton'
import InputField from '../../components/atoms/InputField'
import Form from '../../components/molecules/Form'
import Snackbar from '../../components/atoms/Snackbar'
import { api } from '../../services/api'
import './CreateAccountPage.scss'

const CreateAccountPage: React.FC = () => {
    const navigate = useNavigate()
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [passwordError, setPasswordError] = useState<string | undefined>()
    const [isLoading, setIsLoading] = useState(false)
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

    const handlePasswordChange = (value: string) => {
        setPassword(value)

        if (value && value.length < 6) {
            setPasswordError(
                'Le mot de passe doit contenir au moins 6 caractères'
            )
        } else {
            setPasswordError(undefined)
        }
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!username || !email || !password) {
            showSnackbar('Veuillez remplir tous les champs', false)
            return
        }

        if (password.length < 6) {
            showSnackbar(
                'Le mot de passe doit contenir au moins 6 caractères',
                false
            )
            return
        }

        setIsLoading(true)

        try {
            await api.register({
                username,
                email,
                password,
            })

            showSnackbar('Compte créé avec succès', true)
            setTimeout(() => {
                navigate('/login')
                setIsLoading(false)
            }, 1500)
        } catch (error) {
            showSnackbar('Erreur lors de la création du compte', false)
        } finally {
        }
    }

    return (
        <div className="create-account-page page-enter">
            <div className="create-account-container">
                <Form
                    title="Pay My Buddy"
                    submitButtonText="S'inscrire"
                    onSubmit={handleSubmit}
                    isLoading={isLoading}
                >
                    <InputField
                        label="Username"
                        placeholder="Username"
                        value={username}
                        onChange={setUsername}
                        required
                    />

                    <InputField
                        label="Mail"
                        type="email"
                        placeholder="Mail"
                        value={email}
                        onChange={setEmail}
                        required
                    />

                    <InputField
                        label="Mot de passe"
                        type="password"
                        placeholder="Mot de passe"
                        value={password}
                        onChange={handlePasswordChange}
                        error={passwordError}
                        required
                    />
                </Form>
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

export default CreateAccountPage
