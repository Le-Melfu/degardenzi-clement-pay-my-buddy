import React, { useEffect, useState } from 'react'
import { useSession } from '../hooks/useSession'

const SessionNotification: React.FC = () => {
    const { isAuthenticated } = useSession()
    const [showWarning, setShowWarning] = useState(false)

    useEffect(() => {
        if (isAuthenticated) {
            // Afficher un avertissement 5 minutes avant l'expiration
            const warningTimer = setTimeout(() => {
                setShowWarning(true)
            }, 25 * 60 * 1000) // 25 minutes (si session expire à 30 min)

            return () => clearTimeout(warningTimer)
        } else {
            setShowWarning(false)
        }
    }, [isAuthenticated])

    if (!showWarning || !isAuthenticated) {
        return null
    }

    return (
        <div
            style={{
                position: 'fixed',
                top: '20px',
                right: '20px',
                backgroundColor: '#ff9800',
                color: 'white',
                padding: '15px',
                borderRadius: '5px',
                zIndex: 1000,
                boxShadow: '0 2px 10px rgba(0,0,0,0.2)',
            }}
        >
            <strong>Attention :</strong> Votre session va bientôt expirer.
            <br />
            <small>Veuillez sauvegarder votre travail.</small>
        </div>
    )
}

export default SessionNotification
