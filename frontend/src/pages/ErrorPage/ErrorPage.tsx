import React from 'react'
import { useNavigate } from 'react-router-dom'
import './ErrorPage.scss'

const ErrorPage: React.FC = () => {
    const navigate = useNavigate()

    const handleGoHome = () => {
        navigate('/')
    }

    return (
        <div className="error-page">
            <div className="error-content">
                <div className="error-number">404</div>
                <h1 className="error-title">Page non trouvée</h1>
                <p className="not-found-message">
                    Désolé, la page que vous recherchez n'existe pas ou a été
                    déplacée.
                </p>
                <button className="home-button" onClick={handleGoHome}>
                    Retour à l'accueil
                </button>
            </div>
        </div>
    )
}

export default ErrorPage
