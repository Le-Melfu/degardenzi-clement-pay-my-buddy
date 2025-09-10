import React from 'react'
import MainButton from '../atoms/MainButton'
import InputField from '../atoms/InputField'
import { User } from '../../models'
import './TransferForm.scss'

interface TransferFormProps {
    connections: User[]
    selectedConnection: User | null
    onConnectionChange: (connection: User | null) => void
    description: string
    onDescriptionChange: (description: string) => void
    amount: number
    onAmountChange: (amount: number) => void
    onTransfer: () => void
    isLoading: boolean
}

const TransferForm: React.FC<TransferFormProps> = ({
    connections,
    selectedConnection,
    onConnectionChange,
    description,
    onDescriptionChange,
    amount,
    onAmountChange,
    onTransfer,
    isLoading,
}) => {
    const amountToText = (amountInCents: number) => {
        return amountInCents > 0 ? (amountInCents / 100).toFixed(2) : ''
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
                            onConnectionChange(conn || null)
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
                        onChange={onDescriptionChange}
                    />
                </div>

                <div className="form-field amount-field">
                    <InputField
                        label="Montant"
                        ariaHidden={true}
                        type="number"
                        placeholder="0.00"
                        value={amountToText(amount)}
                        onChange={(value) =>
                            onAmountChange(Math.round(parseFloat(value) * 100))
                        }
                    />
                </div>

                <div className="form-field">
                    <MainButton
                        variant="primary"
                        onClick={onTransfer}
                        disabled={!selectedConnection || !amount || isLoading}
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
