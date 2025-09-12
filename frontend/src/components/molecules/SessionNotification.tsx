import React, { useEffect, useState } from 'react'
import { useSession } from '../../hooks/useSession'
import './SessionNotification.scss'
import Snackbar from '../atoms/Snackbar'

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
        <Snackbar
            message="Votre session va bientôt expirer."
            success={false}
            isVisible={showWarning}
            onClose={() => setShowWarning(false)}
        />
    )
}

export default SessionNotification
