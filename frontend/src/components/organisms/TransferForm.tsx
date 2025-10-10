import React, { useState } from 'react'
import MainButton from '../atoms/MainButton'
import InputField from '../atoms/InputField'
import { User } from '../../models'
import './TransferForm.scss'

interface TransferFormProps {
    connections: User[]
    onTransfer: (data: {
        connection: User
        description: string
        amount: number
    }) => void
    isLoading: boolean
}

const TransferForm: React.FC<TransferFormProps> = ({
    connections,
    onTransfer,
    isLoading,
}) => {
    const [selectedConnection, setSelectedConnection] = useState<User | null>(
        null
    )
    const [description, setDescription] = useState('')
    const [amountInputValue, setAmountInputValue] = useState('')

    const amountToText = (amountInCents: number) => {
        return amountInCents > 0 ? (amountInCents / 100).toFixed(2) : ''
    }

    const handleAmountChange = (value: string) => {
        setAmountInputValue(value)
    }

    const getAmountInCents = () => {
        const numericValue = parseFloat(amountInputValue)
        return !isNaN(numericValue) ? Math.round(numericValue * 100) : 0
    }

    const handleTransfer = () => {
        if (!selectedConnection) return

        const amount = getAmountInCents()
        if (amount <= 0) return

        onTransfer({
            connection: selectedConnection,
            description,
            amount,
        })

        // Reset form
        setSelectedConnection(null)
        setDescription('')
        setAmountInputValue('')
    }

    return (
        <div className="transfer-form">
            <div className="form-row">
                <div className="form-field">
                    <label aria-hidden="true" className="hidden">
                        Sélectionner une relation
                    </label>
                    <select
                        value={selectedConnection?.id || ''}
                        onChange={(e) => {
                            const connId = parseInt(e.target.value)
                            const conn = connections.find(
                                (c) => c.id === connId
                            )
                            setSelectedConnection(conn || null)
                        }}
                        className="connection-select"
                    >
                        <option value="">Choisir une relation</option>
                        {connections.map((conn) => (
                            <option key={conn.id} value={conn.id}>
                                {conn.username}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-field">
                    <InputField
                        label="Description"
                        ariaHidden={true}
                        placeholder="Description du transfert"
                        maxLength={255}
                        value={description}
                        onChange={setDescription}
                    />
                </div>

                <div className="form-field amount-field">
                    <InputField
                        label="Montant"
                        ariaHidden={true}
                        type="number"
                        placeholder="0.00"
                        value={amountInputValue}
                        onChange={handleAmountChange}
                    />
                </div>

                <div className="form-field">
                    <MainButton
                        variant="primary"
                        onClick={handleTransfer}
                        disabled={
                            !selectedConnection ||
                            !getAmountInCents() ||
                            isLoading
                        }
                        isLoading={isLoading}
                    >
                        Payer
                    </MainButton>
                </div>
            </div>
        </div>
    )
}

export default TransferForm
