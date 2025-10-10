import React, { useState } from 'react'
import MainButton from '../atoms/MainButton'
import InputField from '../atoms/InputField'
import { api } from '../../services/api'
import './UserBalance.scss'

interface UserBalanceProps {
    balance: number
    onBalanceUpdate: () => void
}

const UserBalance: React.FC<UserBalanceProps> = ({
    balance,
    onBalanceUpdate,
}) => {
    const [addFundsAmount, setAddFundsAmount] = useState(0)
    const [inputValue, setInputValue] = useState('')

    const amountToText = (amountInCents: number) => {
        return (amountInCents / 100).toFixed(2)
    }

    const formatAmount = (amountInCents: number) => {
        return `${amountToText(amountInCents)} €`
    }

    const handleAddFunds = () => {
        api.addMoney(addFundsAmount).then(() => {
            onBalanceUpdate()
            setAddFundsAmount(0)
            setInputValue('')
        })
    }

    const handleInputChange = (value: string) => {
        setInputValue(value)
        const numericValue = parseFloat(value)
        if (!isNaN(numericValue)) {
            setAddFundsAmount(Math.round(numericValue * 100))
        } else {
            setAddFundsAmount(0)
        }
    }

    return (
        <div className="user-balance">
            <div className="balance-header">
                <div className="balance-info">
                    <h1>Mes fonds</h1>
                    <p>Solde : {formatAmount(balance)}</p>
                </div>
                <div className="add-funds-section">
                    <InputField
                        label="Montant à ajouter"
                        type="number"
                        placeholder="0.00"
                        value={inputValue}
                        onChange={handleInputChange}
                    />
                    <MainButton
                        variant="secondary"
                        onClick={handleAddFunds}
                        disabled={!addFundsAmount}
                    >
                        Ajouter des fonds
                    </MainButton>
                </div>
            </div>
        </div>
    )
}

export default UserBalance
