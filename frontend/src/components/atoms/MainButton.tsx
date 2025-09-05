import React from 'react'
import './MainButton.scss'

interface MainButtonProps {
    children: React.ReactNode
    type?: 'button' | 'submit' | 'reset'
    variant?: 'primary' | 'secondary'
    size?: 'small' | 'medium' | 'large'
    disabled?: boolean
    onClick?: () => void
    className?: string
}

const MainButton: React.FC<MainButtonProps> = ({
    children,
    type = 'button',
    variant = 'primary',
    size = 'medium',
    disabled = false,
    onClick,
    className = '',
}) => {
    const buttonClasses = [
        'main-button',
        `main-button--${variant}`,
        `main-button--${size}`,
        className,
    ]
        .filter(Boolean)
        .join(' ')

    return (
        <button
            type={type}
            className={buttonClasses}
            disabled={disabled}
            onClick={onClick}
        >
            {children}
        </button>
    )
}

export default MainButton
