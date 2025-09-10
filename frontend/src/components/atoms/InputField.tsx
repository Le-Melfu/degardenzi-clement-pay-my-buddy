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
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange?.(e.target.value)
    }

    return (
        <div className="input-field">
            <label
                className={`input-field__label ${ariaHidden ? 'hidden' : ''}`}
                aria-hidden={ariaHidden}
            >
                {label}
                {required && <span className="input-field__required">*</span>}
            </label>
            <input
                type={type}
                className={`input-field__input ${
                    error ? 'input-field__input--error' : ''
                }`}
                placeholder={placeholder}
                value={value}
                maxLength={type === 'text' ? maxLength : undefined}
                onChange={handleChange}
                required={required}
            />
            {error && <span className="input-field__error">{error}</span>}
        </div>
    )
}

export default InputField
