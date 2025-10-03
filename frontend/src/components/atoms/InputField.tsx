import React from 'react'
import './InputField.scss'

interface InputFieldProps {
    label: string
    ariaHidden?: boolean
    type?: 'text' | 'email' | 'password' | 'number' | 'date'
    placeholder?: string
    value?: string
    maxLength?: number
    onChange?: (value: string) => void
    required?: boolean
    error?: string
}

const InputField: React.FC<InputFieldProps> = ({
    label,
    type = 'text',
    ariaHidden = false,
    placeholder,
    value = '',
    maxLength = 255,
    onChange,
    required = false,
    error,
}) => {
    const inputId = `input-${label.toLowerCase().replace(/\s+/g, '-')}`

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange?.(e.target.value)
    }

    return (
        <div className="input-field">
            <label
                className={`input-field__label ${ariaHidden ? 'hidden' : ''}`}
                htmlFor={inputId}
                aria-hidden={ariaHidden}
            >
                {label}
            </label>
            <input
                id={inputId}
                type={type}
                className={`input-field__input ${
                    error ? 'input-field__input--error' : ''
                }`}
                placeholder={placeholder}
                value={value}
                maxLength={type === 'text' ? maxLength : undefined}
                onChange={handleChange}
                required={required}
                aria-describedby={error ? `${inputId}-error` : undefined}
                aria-invalid={error ? 'true' : 'false'}
            />
            {error && (
                <span
                    id={`${inputId}-error`}
                    className="input-field__error"
                    role="alert"
                    aria-live="polite"
                >
                    {error}
                </span>
            )}
        </div>
    )
}

export default InputField
