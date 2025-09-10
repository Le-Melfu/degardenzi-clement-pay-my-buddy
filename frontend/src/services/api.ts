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
        credentials: 'include', // Inclure les cookies de session
    }

    try {
        const response = await fetch(url, config)

        if (!response.ok) {
            // Gestion spécifique des erreurs d'authentification
            if (response.status === 401) {
                // Session expirée ou non authentifié
                console.warn('Session expirée ou non authentifié')
                throw new Error('SESSION_EXPIRED')
            }
            throw new Error(`Erreur ${response.status}: ${response.statusText}`)
        }

        // Vérifier si la réponse a du contenu avant de parser le JSON
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
