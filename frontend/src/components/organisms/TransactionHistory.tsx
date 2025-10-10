import React from 'react'
import { Transaction, User } from '../../models'
import TransactionItem from '../molecules/TransactionItem'
import './TransactionHistory.scss'

interface TransactionHistoryProps {
    transactions: Transaction[]
    currentUser: User | null
}

const TransactionHistory: React.FC<TransactionHistoryProps> = ({
    transactions,
    currentUser,
}) => {
    return (
        <div className="transactions-section">
            <h2>Mes Transactions</h2>
            <div className="transactions-table">
                <div className="table-header">
                    <h3 className="header-cell">Relations</h3>
                    <h3 className="header-cell">Description</h3>
                    <h3 className="header-cell">Montant</h3>
                </div>
                <div className="table-body">
                    {transactions.length > 0 ? (
                        transactions.map((transaction) => (
                            <TransactionItem
                                key={transaction.id}
                                transaction={transaction}
                                currentUser={currentUser}
                            />
                        ))
                    ) : (
                        <div className="no-transactions">
                            Aucune transaction pour le moment
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default TransactionHistory
