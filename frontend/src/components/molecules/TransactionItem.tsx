import React from 'react'
import { Transaction, User } from '../../models'
import './TransactionItem.scss'

interface TransactionItemProps {
    transaction: Transaction
    currentUser: User | null
}

const TransactionItem: React.FC<TransactionItemProps> = ({
    transaction,
    currentUser,
}) => {
    const isOutgoing = currentUser && transaction.sender.id === currentUser.id

    const formatAmount = (amountInCents: number) => {
        const amount = (amountInCents / 100).toFixed(2)
        const symbol = isOutgoing ? '-' : '+'
        return `${symbol}${amount}€`
    }

    const getConnectionName = (transaction: Transaction) => {
        // Déterminer si l'utilisateur actuel est l'expéditeur ou le destinataire
        if (currentUser && transaction.sender.id === currentUser.id) {
            return transaction.receiver.username
        } else {
            return transaction.sender.username
        }
    }

    return (
        <div className="table-row">
            <div className="table-cell">{getConnectionName(transaction)}</div>
            <div className="table-cell">{transaction.description}</div>
            <div
                className={`table-cell amount-cell ${
                    transaction.status === 'SUCCESS'
                        ? isOutgoing
                            ? 'outgoing'
                            : 'incoming'
                        : 'failed'
                }`}
            >
                {formatAmount(transaction.amountInCents)}
            </div>
        </div>
    )
}

export default TransactionItem
