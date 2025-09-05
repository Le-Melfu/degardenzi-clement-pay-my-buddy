// Configuration de base
const API_BASE_URL = 'http://localhost:8080'

// Import des modèles
import {
    User,
    Transaction,
    CreateTransactionRequest,
    LoginRequest,
    AddConnectionRequest,
} from '../models'

// Fonction utilitaire pour les requêtes
async function apiRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`

    const defaultHeaders: HeadersInit = {
        'Content-Type': 'application/json',
    }

    const config: RequestInit = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
        credentials: 'include', // Inclure les cookies de session
    }

    try {
        const response = await fetch(url, config)

        if (!response.ok) {
            throw new Error(`Erreur ${response.status}: ${response.statusText}`)
        }

        return await response.json()
    } catch (error) {
        console.error('Erreur API:', error)
        throw error
    }
}

// Méthodes API
export const api = {
    // Authentification
    async login(credentials: LoginRequest): Promise<User> {
        return apiRequest<User>('/login', {
            method: 'POST',
            body: JSON.stringify(credentials),
        })
    },

    async logout(): Promise<void> {
        await apiRequest('/log-out', {
            method: 'POST',
        })
    },

    async addMoney(amountInCents: number): Promise<void> {
        return apiRequest('/add-money', {
            method: 'POST',
            body: JSON.stringify(amountInCents),
        })
    },

    // Transactions
    async getTransactions(): Promise<Transaction[]> {
        return apiRequest<Transaction[]>('/transactions')
    },

    async getTransaction(transactionId: number): Promise<Transaction> {
        return apiRequest<Transaction>(`/transactions/${transactionId}`)
    },

    async createTransaction(
        transaction: CreateTransactionRequest
    ): Promise<Transaction> {
        return apiRequest<Transaction>('/transaction', {
            method: 'POST',
            body: JSON.stringify(transaction),
        })
    },

    // Connexions
    async getConnections(): Promise<User[]> {
        return apiRequest<User[]>('/connections')
    },

    async addConnection(request: AddConnectionRequest): Promise<User> {
        return apiRequest<User>('/add-connection', {
            method: 'POST',
            body: JSON.stringify(request),
        })
    },
}
