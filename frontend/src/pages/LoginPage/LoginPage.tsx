import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Form from '../../components/molecules/Form'
import InputField from '../../components/atoms/InputField'
import { api } from '../../services/api'
import { sessionService } from '../../services/sessionService'
import { useSessionContext } from '../../contexts/SessionContext'
import { User } from '../../models'
import './LoginPage.scss'
import Snackbar from '../../components/atoms/Snackbar'

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [passwordError, setPasswordError] = useState<string | undefined>()
    const [error, setError] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const navigate = useNavigate()
    const { updateUser } = useSessionContext()
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
        setError('')
        setIsLoading(true)

        if (password.length < 6) {
            showSnackbar(
                'Le mot de passe doit contenir au moins 6 caractères',
                false
            )
            setIsLoading(false)
            return
        }

        try {
            const user: User = await api.login({
                email,
                password,
            })
            sessionService.saveUser(user)
            updateUser(user)
            showSnackbar('Connexion réussie', true)
            navigate('/profile')
        } catch (error) {
            showSnackbar('Connexion échouée', false)
            setError('Email ou mot de passe incorrect')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="login-page page-enter">
            <Form
                title="Pay My Buddy"
                submitButtonText={'Se connecter'}
                onSubmit={handleSubmit}
                isLoading={isLoading}
                className="login-form"
            >
                <InputField
                    label="Mail"
                    type="email"
                    placeholder="Votre adresse email"
                    value={email}
                    onChange={setEmail}
                    required
                />
                <InputField
                    label="Mot de passe"
                    type="password"
                    placeholder="Votre mot de passe"
                    value={password}
                    onChange={handlePasswordChange}
                    required
                />
            </Form>
            <Snackbar
                message={snackbar.message}
                success={snackbar.success}
                isVisible={snackbar.isVisible}
                onClose={hideSnackbar}
            />
        </div>
    )
}

export default LoginPage
