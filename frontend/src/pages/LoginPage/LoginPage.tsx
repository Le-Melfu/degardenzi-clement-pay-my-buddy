import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Form from '../../components/molecules/Form'
import TextField from '../../components/atoms/TextField'
import { api } from '../../services/api'
import { User } from '../../models'
import './LoginPage.scss'

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const navigate = useNavigate()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError('')
        setIsLoading(true)

        try {
            const user: User = await api.login({
                email,
                password,
            })
            console.log('Login successful:', user)

            // Rediriger vers la page user après connexion
            navigate('/profile', { state: { user } })
        } catch (error) {
            console.error('Login failed:', error)
            setError('Email ou mot de passe incorrect')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="login-page">
            <Form
                title="Pay My Buddy"
                submitButtonText={isLoading ? 'Connexion...' : 'Se connecter'}
                onSubmit={handleSubmit}
            >
                {error && <div className="error-message">{error}</div>}
                <TextField
                    label="Mail"
                    type="email"
                    placeholder="Votre adresse email"
                    value={email}
                    onChange={setEmail}
                    required
                />
                <TextField
                    label="Mot de passe"
                    type="password"
                    placeholder="Votre mot de passe"
                    value={password}
                    onChange={setPassword}
                    required
                />
            </Form>
        </div>
    )
}

export default LoginPage
