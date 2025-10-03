import React, { useState, useEffect } from 'react'
import Snackbar from '../../components/atoms/Snackbar'
import NavBar from '../../components/molecules/NavBar'
import UserBalance from '../../components/organisms/UserBalance'
import TransferForm from '../../components/organisms/TransferForm'
import TransactionHistory from '../../components/organisms/TransactionHistory'
import { useSessionContext } from '../../contexts/SessionContext'
import { api } from '../../services/api'
import { User, Transaction } from '../../models'
import './TransferPage.scss'
import CircularProgressIndicator from '../../components/atoms/CircularProgressIndicator'

const TransferPage: React.FC = () => {
    const { user } = useSessionContext()
    const [connections, setConnections] = useState<User[]>([])
    const [transactions, setTransactions] = useState<Transaction[]>([])
    const [transactionLoading, setTransactionLoading] = useState(false)
    const [snackbar, setSnackbar] = useState<{
        isVisible: boolean
        message: string
        success: boolean
    }>({
        isVisible: false,
        message: '',
        success: false,
    })

    const [balance, setBalance] = useState(0) // Toujours en centimes

    const showSnackbar = (message: string, success: boolean) => {
        setSnackbar({
            isVisible: true,
            message,
            success,
        })
    }

    const [transactionProcessing, setTransactionProcessing] = useState(false)

    const hideSnackbar = () => {
        setSnackbar((prev) => ({ ...prev, isVisible: false }))
    }

    const loadBalance = async () => {
        const balanceData = await api.getBalance()
        setBalance(balanceData || 0)
    }

    // Charger les connexions et transactions
    useEffect(() => {
        loadConnections()
        loadTransactions()
        loadBalance()
    }, [])

    const loadConnections = async (tries: number = 0) => {
        try {
            const connectionsData = await api.getConnections()
            setConnections(connectionsData || [])
        } catch (error) {
            if (tries < 3) {
                loadConnections(tries + 1)
            } else {
                return
            }
        }
    }

    const loadTransactions = async (tries: number = 0) => {
        try {
            setTransactionLoading(true)
            const transactionsData = await api.getTransactions()
            setTransactions(transactionsData || [])
        } catch (error) {
            if (tries < 3) {
                loadTransactions(tries + 1)
            } else {
                return
            }
        } finally {
            setTransactionLoading(false)
        }
    }

    const handleTransfer = async (data: {
        connection: User
        description: string
        amount: number
    }) => {
        try {
            setTransactionProcessing(true)
            await api
                .createTransaction({
                    receiverId: data.connection.id,
                    description: data.description,
                    amountInCents: data.amount,
                })
                .then((result) => {
                    if (result) {
                        showSnackbar('Transfert effectué avec succès', true)
                    } else {
                        showSnackbar('Erreur lors du transfert', false)
                    }
                })
        } catch (error) {
            showSnackbar(error.message, false)
        } finally {
            loadTransactions()
            loadBalance()
            setTransactionProcessing(false)
        }
    }

    return (
        <div className="transfer-page page-slide">
            <UserBalance balance={balance} onBalanceUpdate={loadBalance} />

            <div className="transfer-content">
                <TransferForm
                    connections={connections}
                    onTransfer={handleTransfer}
                    isLoading={transactionProcessing}
                />
                {transactionLoading ? (
                    <CircularProgressIndicator size="large" />
                ) : (
                    <TransactionHistory
                        transactions={transactions}
                        currentUser={user}
                    />
                )}
            </div>

            <Snackbar
                message={snackbar.message}
                success={snackbar.success}
                isVisible={snackbar.isVisible}
                onClose={hideSnackbar}
            />
        </div>
    )
}

export default TransferPage
