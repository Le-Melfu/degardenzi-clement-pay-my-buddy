import React from 'react'
import './InputField.scss'

interface InputFieldProps {
    label: string
    type?: 'text' | 'email' | 'password' | 'number' | 'date'
    placeholder?: string
    value?: string
    onChange?: (value: string) => void
    required?: boolean
    error?: string
}

const InputField: React.FC<InputFieldProps> = ({
    label,
    type = 'text',
    placeholder,
    value = '',
    onChange,
    required = false,
    error,
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange?.(e.target.value)
    }

    return (
        <div className="text-field">
            <label className="text-field__label">
                {label}
                {required && <span className="text-field__required">*</span>}
            </label>
            <input
                type={type}
                className={`text-field__input ${
                    error ? 'text-field__input--error' : ''
                }`}
                placeholder={placeholder}
                value={value}
                onChange={handleChange}
                required={required}
            />
            {error && <span className="text-field__error">{error}</span>}
        </div>
    )
}

export default InputField
