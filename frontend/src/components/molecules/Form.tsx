import React from 'react'
import MainButton from '../atoms/MainButton'
import './Form.scss'

interface FormProps {
    title: string
    children: React.ReactNode
    submitButtonText: string
    onSubmit: (e: React.FormEvent) => void
    className?: string
    isLoading?: boolean
}

const Form: React.FC<FormProps> = ({
    title,
    children,
    submitButtonText,
    onSubmit,
    className = '',
    isLoading = false,
}) => {
    const formClasses = ['form', className].filter(Boolean).join(' ')

    return (
        <div className={formClasses}>
            <div className="form__header">
                <h1 className="form__title">{title}</h1>
            </div>

            <form className="form__content" onSubmit={onSubmit}>
                <div className="form__fields">{children}</div>

                <div className="form__actions">
                    <MainButton
                        type="submit"
                        size="large"
                        isLoading={isLoading}
                    >
                        {submitButtonText}
                    </MainButton>
                </div>
            </form>
        </div>
    )
}

export default Form
