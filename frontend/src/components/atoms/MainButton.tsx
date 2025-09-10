import React from 'react'
import './MainButton.scss'
import CircularProgressIndicator from './CircularProgressIndicator'

interface MainButtonProps {
    children: React.ReactNode
    type?: 'button' | 'submit' | 'reset'
    variant?: 'primary' | 'secondary'
    size?: 'small' | 'medium' | 'large'
    disabled?: boolean
    onClick?: () => void
    className?: string
    isLoading?: boolean
}

const MainButton: React.FC<MainButtonProps> = ({
    children,
    type = 'button',
    variant = 'primary',
    size = 'medium',
    disabled = false,
    onClick,
    className = '',
    isLoading = false,
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
            {isLoading ? <CircularProgressIndicator color="white" /> : children}
        </button>
    )
}

export default MainButton
