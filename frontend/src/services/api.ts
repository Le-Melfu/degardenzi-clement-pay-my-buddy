// Configuration de base
const API_BASE_URL = 'http://localhost:8080'

// Import des modèles
import {
    User,
    Transaction,
    CreateTransactionRequest,
    LoginRequest,
    AddConnectionRequest,
    RegisterRequest,
} from '../models'

// Interface pour les réponses d'erreur de l'API
interface ApiErrorResponse {
    timestamp: string
    status: number
    error: string
    message: string
}

// Fonction utilitaire pour les requêtes
async function apiRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T | null> {
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
        credentials: 'include',
    }

    try {
        const response = await fetch(url, config)

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('SESSION_EXPIRED')
            }

            const contentType = response.headers.get('content-type')
            if (contentType?.includes('application/json')) {
                const errorData: ApiErrorResponse = await response.json()
                throw new Error(errorData.message || response.statusText)
            }

            throw new Error(response.statusText)
        }

        const contentType = response.headers.get('content-type')
        if (contentType && contentType.includes('application/json')) {
            const text = await response.text()
            return text ? JSON.parse(text) : null
        }

        return null
    } catch (error) {
        console.error('Erreur API:', error)
        throw error
    }
}

// Méthodes API
export const api = {
    // Authentification
    async login(credentials: LoginRequest): Promise<User> {
        const result = await apiRequest<User>('/login', {
            method: 'POST',
            body: JSON.stringify(credentials),
        })
        if (!result) {
            throw new Error('Échec de la connexion')
        }
        return result
    },

    async register(credentials: RegisterRequest): Promise<User> {
        const result = await apiRequest<User>('/register', {
            method: 'POST',
            body: JSON.stringify(credentials),
        })
        if (!result) {
            throw new Error('Échec de la création de compte')
        }
        return result
    },

    async logout(): Promise<void> {
        await apiRequest('/log-out', {
            method: 'POST',
        })
    },

    async addMoney(amountInCents: number): Promise<void> {
        await apiRequest('/add-money', {
            method: 'POST',
            body: JSON.stringify(amountInCents),
        })
    },

    async getBalance(): Promise<number> {
        const result = await apiRequest<number>('/user/balance')
        return result || 0
    },

    // Transactions
    async getTransactions(): Promise<Transaction[]> {
        const result = await apiRequest<Transaction[]>('/transactions')
        return result || []
    },

    async getTransaction(transactionId: number): Promise<Transaction> {
        const result = await apiRequest<Transaction>(
            `/transactions/${transactionId}`
        )
        if (!result) {
            throw new Error('Transaction non trouvée')
        }
        return result
    },

    async createTransaction(
        transaction: CreateTransactionRequest
    ): Promise<Transaction> {
        const result = await apiRequest<Transaction>('/transaction', {
            method: 'POST',
            body: JSON.stringify(transaction),
        })
        if (!result) {
            throw new Error('Échec de la création de transaction')
        }
        return result
    },

    // Connexions
    async getConnections(): Promise<User[]> {
        const result = await apiRequest<User[]>('/connections')
        return result || []
    },

    async addConnection(request: AddConnectionRequest): Promise<User> {
        const result = await apiRequest<User>('/add-connection', {
            method: 'POST',
            body: JSON.stringify(request),
        })
        if (!result) {
            throw new Error("Échec de l'ajout de connexion")
        }
        return result
    },
}
