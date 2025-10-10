import React, { useEffect, useState } from 'react'
import './Snackbar.scss'

interface SnackbarProps {
    message: string
    success: boolean
    isVisible: boolean
    onClose: () => void
    duration?: number
}

const Snackbar: React.FC<SnackbarProps> = ({
    message,
    success,
    isVisible,
    onClose,
    duration = 3000,
}) => {
    const [isClosing, setIsClosing] = useState(false)

    useEffect(() => {
        if (isVisible) {
            setIsClosing(false)
            const timer = setTimeout(() => {
                setIsClosing(true)
                // Délai pour l'animation de fade out
                setTimeout(() => {
                    onClose()
                }, 300)
            }, duration)

            return () => clearTimeout(timer)
        }
    }, [isVisible, duration, onClose])

    const handleClose = () => {
        setIsClosing(true)
        setTimeout(() => {
            onClose()
        }, 300)
    }

    if (!isVisible) return null

    return (
        <div
            className={`snackbar ${success ? 'success' : 'error'} ${
                isVisible ? 'show' : ''
            } ${isClosing ? 'closing' : ''}`}
        >
            <div className="snackbar-content">
                <span className="snackbar-message">{message}</span>
                <button className="snackbar-close" onClick={handleClose}>
                    ×
                </button>
            </div>
        </div>
    )
}

export default Snackbar
