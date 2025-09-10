import React, { useState, useEffect } from 'react'
import MainButton from '../../components/atoms/MainButton'
import InputField from '../../components/atoms/InputField'
import Snackbar from '../../components/atoms/Snackbar'
import NavBar from '../../components/molecules/NavBar'
import { useSession } from '../../hooks/useSession'
import { api } from '../../services/api'
import { User, Transaction } from '../../models'
import './TransferPage.scss'

const TransferPage: React.FC = () => {
    const { user } = useSession()
    const [connections, setConnections] = useState<User[]>([])
    const [selectedConnection, setSelectedConnection] = useState<User | null>(
        null
    )
    const [description, setDescription] = useState('')
    const [amount, setAmount] = useState(0) // Toujours en centimes
    const [transactions, setTransactions] = useState<Transaction[]>([])
    const [snackbar, setSnackbar] = useState<{
        isVisible: boolean
        message: string
        success: boolean
    }>({
        isVisible: false,
        message: '',
        success: false,
    })

    const [addFundsAmount, setAddFundsAmount] = useState(0)
    const [balance, setBalance] = useState(0) // Toujours en centimes

    const showSnackbar = (message: string, success: boolean) => {
        setSnackbar({
            isVisible: true,
            message,
            success,
        })
    }

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
            const transactionsData = await api.getTransactions()
            setTransactions(transactionsData || [])
        } catch (error) {
            console.log(
                '[DebugClem] - Erreur lors du chargement des transactions:',
                error
            )
        }
    }

    const handleTransfer = async () => {
        if (!selectedConnection || !amount) {
            showSnackbar('Veuillez remplir tous les champs', false)
            return
        }

        try {
            const result = await api.createTransaction({
                receiverId: selectedConnection.id,
                description: description,
                amountInCents: amount,
            })

            if (result) {
                showSnackbar('Transfert effectué avec succès', true)
                setDescription('')
                setAmount(0)
                setSelectedConnection(null)
                loadTransactions() // Recharger l'historique
            } else {
                showSnackbar('Erreur lors du transfert', false)
            }
        } catch (error) {
            console.log('[DebugClem] - Erreur lors du transfert:', error)
            showSnackbar('Erreur lors du transfert', false)
        }
    }

    const amountToText = (amountInCents: number) => {
        return (amountInCents / 100).toFixed(2)
    }

    const formatAmount = (amountInCents: number) => {
        return `${amountToText(amountInCents)}€`
    }

    const getConnectionName = (transaction: Transaction) => {
        // Déterminer si l'utilisateur actuel est l'expéditeur ou le destinataire
        if (user && transaction.sender.id === user.id) {
            return transaction.receiver.username
        } else {
            return transaction.sender.username
        }
    }

    return (
        <div className="transfer-page">
            <NavBar activePage="transfer" />
            <div className="transfer-page-user-content">
                <div className="transfer-page-user-content-header">
                    <h1>Mon profil</h1>
                    <p>Solde : {formatAmount(balance)}</p>
                    <div className="add-funds-section">
                        <InputField
                            label="Montant à ajouter"
                            type="number"
                            placeholder="0.00"
                            value={amountToText(addFundsAmount)}
                            onChange={(value) =>
                                setAddFundsAmount(
                                    Math.round(parseFloat(value) * 100)
                                )
                            }
                        />
                        <MainButton
                            variant="secondary"
                            onClick={() => {
                                api.addMoney(addFundsAmount).then(() => {
                                    loadBalance()
                                    setAddFundsAmount(0)
                                })
                            }}
                        >
                            Ajouter des fonds
                        </MainButton>
                    </div>
                </div>
            </div>

            <div className="transfer-content">
                {/* Formulaire de transfert */}
                <div className="transfer-form">
                    <div className="form-row">
                        <div className="form-field">
                            <label>Sélectionner une relation</label>
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
                                placeholder="Description du transfert"
                                value={description}
                                onChange={setDescription}
                            />
                        </div>

                        <div className="form-field amount-field">
                            <InputField
                                label="Montant"
                                type="number"
                                placeholder="0.00"
                                value={amountToText(amount)}
                                onChange={(value) =>
                                    setAmount(
                                        Math.round(parseFloat(value) * 100)
                                    )
                                }
                            />
                        </div>

                        <div className="form-field">
                            <MainButton
                                variant="primary"
                                onClick={handleTransfer}
                                disabled={!selectedConnection || !amount}
                            >
                                Payer
                            </MainButton>
                        </div>
                    </div>
                </div>

                {/* Historique des transactions */}
                <div className="transactions-section">
                    <h2>Mes Transactions</h2>
                    <div className="transactions-table">
                        <div className="table-header">
                            <div className="header-cell">Relations</div>
                            <div className="header-cell">Description</div>
                            <div className="header-cell">Montant</div>
                        </div>
                        <div className="table-body">
                            {transactions.length > 0 ? (
                                transactions.map((transaction) => (
                                    <div
                                        key={transaction.id}
                                        className="table-row"
                                    >
                                        <div className="table-cell">
                                            {getConnectionName(transaction)}
                                        </div>
                                        <div className="table-cell">
                                            {transaction.description}
                                        </div>
                                        <div className="table-cell">
                                            {formatAmount(
                                                transaction.amountInCents
                                            )}
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <div className="no-transactions">
                                    Aucune transaction pour le moment
                                </div>
                            )}
                        </div>
                    </div>
                </div>
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
