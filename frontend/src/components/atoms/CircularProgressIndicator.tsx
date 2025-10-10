import React from 'react'
import './CircularProgressIndicator.scss'

interface CircularProgressIndicatorProps {
    size?: 'small' | 'medium' | 'large'
    color?: 'primary' | 'secondary' | 'white'
    className?: string
}

const CircularProgressIndicator: React.FC<CircularProgressIndicatorProps> = ({
    size = 'medium',
    color = 'primary',
    className = '',
}) => {
    const sizeClass = `circular-progress--${size}`
    const colorClass = `circular-progress--${color}`

    return (
        <div
            className={`circular-progress ${sizeClass} ${colorClass} ${className}`}
        >
            <div className="circular-progress__spinner"></div>
        </div>
    )
}

export default CircularProgressIndicator
