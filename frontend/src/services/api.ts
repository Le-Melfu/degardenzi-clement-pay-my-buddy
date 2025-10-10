// Configuration de base
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
const API_TIMEOUT_MILLISECONDS = 10000

// Import des modèles
import {
    User,
    Transaction,
    CreateTransactionRequest,
    LoginRequest,
    AddConnectionRequest,
    RegisterRequest,
    UpdateUserRequest,
} from '../models'

// Interface pour les réponses d'erreur de l'API
interface ApiErrorResponse {
    timestamp: string
    status: number
    error: string
    message: string
}

/**
 * Utility function to read a cookie by name
 * Used to extract the CSRF token sent by Spring Security
 */
function getCookie(name: string): string | undefined {
    return document.cookie
        .split('; ')
        .find((row) => row.startsWith(name + '='))
        ?.split('=')[1]
}

/**
 * Check if the HTTP method requires CSRF protection
 */
function requiresCsrfToken(method?: string): boolean {
    if (!method) return false
    const writeMethods = ['POST', 'PUT', 'PATCH', 'DELETE']
    return writeMethods.includes(method.toUpperCase())
}

// Fonction utilitaire pour les requêtes
async function apiRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T | null> {
    const url = `${API_BASE_URL}${endpoint}`

    // Build default headers
    const defaultHeaders: HeadersInit = {
        'Content-Type': 'application/json',
    }

    // Add CSRF token only for write operations (POST, PUT, PATCH, DELETE)
    // Spring Security validates this header against the XSRF-TOKEN cookie
    if (requiresCsrfToken(options.method)) {
        const csrfToken = getCookie('XSRF-TOKEN')
        if (csrfToken) {
            defaultHeaders['X-XSRF-TOKEN'] = csrfToken
        }
    }

    const config: RequestInit = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
        // Include credentials to send/receive cookies (JSESSIONID, XSRF-TOKEN)
        credentials: 'include',
    }

    try {
        // Gestion du timeout
        const controller = new AbortController()
        const timeoutId = setTimeout(
            () => controller.abort(),
            API_TIMEOUT_MILLISECONDS
        )

        const response = await fetch(url, {
            ...config,
            signal: controller.signal,
        })

        clearTimeout(timeoutId)

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
        if (error instanceof Error && error.name === 'AbortError') {
            throw new Error('La requête a expiré. Veuillez réessayer.')
        }
        throw error
    }
}

/**
 * Initialize CSRF token by making a request to the /csrf endpoint
 * This ensures the XSRF-TOKEN cookie is set before any write operation
 * Should be called when the application starts
 */
async function initializeCsrfToken(): Promise<void> {
    try {
        await fetch(`${API_BASE_URL}/csrf`, {
            method: 'GET',
            credentials: 'include',
        })
    } catch (error) {
        console.error('Failed to initialize CSRF token:', error)
    }
}

/**
 * Verify CSRF protection is working correctly
 * For development/testing purposes
 */
async function verifyCsrfProtection(): Promise<{
    cookiePresent: boolean
    cookieValue: string | undefined
}> {
    // First, ensure token is initialized
    await initializeCsrfToken()

    // Check if cookie is present
    const cookieValue = getCookie('XSRF-TOKEN')

    return {
        cookiePresent: !!cookieValue,
        cookieValue,
    }
}

// Méthodes API
export const api = {
    /**
     * Initialize CSRF token from backend
     * Call this when the app starts to ensure CSRF cookie is set
     */
    initializeCsrf: initializeCsrfToken,

    /**
     * Verify CSRF configuration (dev/test only)
     */
    verifyCsrf: verifyCsrfProtection,
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

    async getUser(): Promise<User> {
        const result = await apiRequest<User>('/user')
        if (!result) {
            throw new Error("Échec de la récupération de l'utilisateur")
        }
        return result
    },

    async updateUser(updateRequest: UpdateUserRequest): Promise<User> {
        const result = await apiRequest<User>('/user', {
            method: 'PUT',
            body: JSON.stringify(updateRequest),
        })
        if (!result) {
            throw new Error("Échec de la mise à jour de l'utilisateur")
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
