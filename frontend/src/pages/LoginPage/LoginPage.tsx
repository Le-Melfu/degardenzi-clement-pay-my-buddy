import React, { useState } from 'react'
import Form from '../components/molecules/Form'
import TextField from '../components/atoms/TextField'
import './LoginPage.scss'

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        console.log('Login attempt:', { email, password })
        // TODO: Implémenter la logique de connexion
    }

    return (
        <div className="login-page">
            <Form
                title="Pay My Buddy"
                submitButtonText="Se connecter"
                onSubmit={handleSubmit}
            >
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
