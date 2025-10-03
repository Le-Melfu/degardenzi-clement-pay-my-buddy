import React from 'react'
import './HomePage.scss'
import MainButton from '../../components/atoms/MainButton'
import { useNavigate } from 'react-router-dom'

const HomePage: React.FC = () => {
    const navigate = useNavigate()
    return (
        <div className="home-page page-slide">
            <h1>Bienvenue sur Pay My Buddy</h1>
            <p>Gérez vos transactions facilement et en toute sécurité</p>
            <div className="home-page-buttons">
                <MainButton onClick={() => navigate('/login')}>
                    Se connecter
                </MainButton>
                <MainButton onClick={() => navigate('/register')}>
                    Créer un compte
                </MainButton>
            </div>
        </div>
    )
}

export default HomePage
