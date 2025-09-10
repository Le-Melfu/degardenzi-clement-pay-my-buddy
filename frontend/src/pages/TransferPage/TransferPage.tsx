import React, { useState, useEffect } from 'react'
import Snackbar from '../../components/atoms/Snackbar'
import NavBar from '../../components/molecules/NavBar'
import UserBalance from '../../components/organisms/UserBalance'
import TransferForm from '../../components/organisms/TransferForm'
import TransactionHistory from '../../components/organisms/TransactionHistory'
import { useSession } from '../../hooks/useSession'
import { api } from '../../services/api'
import { User, Transaction } from '../../models'
import './TransferPage.scss'
import CircularProgressIndicator from '../../components/atoms/CircularProgressIndicator'

const TransferPage: React.FC = () => {
    const { user } = useSession()
    const [connections, setConnections] = useState<User[]>([])
    const [selectedConnection, setSelectedConnection] = useState<User | null>(
        null
    )
    const [description, setDescription] = useState('')
    const [amount, setAmount] = useState(0) // Toujours en centimes
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

    const loadConnections = async () => {
        try {
            const connectionsData = await api.getConnections()
            setConnections(connectionsData || [])
        } catch (error) {
            console.log(
                '[DebugClem] - Erreur lors du chargement des connexions:',
                error
            )
        }
    }

    const loadTransactions = async () => {
        try {
            setTransactionLoading(true)
            const transactionsData = await api.getTransactions()
            setTransactions(transactionsData || [])
        } catch (error) {
            console.log(
                '[DebugClem] - Erreur lors du chargement des transactions:',
                error
            )
        } finally {
            setTransactionLoading(false)
        }
    }

    const handleTransfer = async () => {
        if (!selectedConnection || !amount) {
            showSnackbar('Veuillez remplir tous les champs', false)
            return
        }

        try {
            setTransactionProcessing(true)
            await api
                .createTransaction({
                    receiverId: selectedConnection.id,
                    description: description,
                    amountInCents: amount,
                })
                .then((result) => {
                    if (result) {
                        showSnackbar('Transfert effectué avec succès', true)
                        setDescription('')
                        setAmount(0)
                        setSelectedConnection(null)
                        loadTransactions()
                        loadBalance()
                    } else {
                        showSnackbar('Erreur lors du transfert', false)
                    }
                })
        } catch (error) {
            showSnackbar(error.message, false)
        } finally {
            setTransactionProcessing(false)
        }
    }

    return (
        <div className="transfer-page">
            <NavBar activePage="transfer" />
            <UserBalance balance={balance} onBalanceUpdate={loadBalance} />

            <div className="transfer-content">
                <TransferForm
                    connections={connections}
                    selectedConnection={selectedConnection}
                    onConnectionChange={setSelectedConnection}
                    description={description}
                    onDescriptionChange={setDescription}
                    amount={amount}
                    onAmountChange={setAmount}
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
