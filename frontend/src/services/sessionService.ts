import { User } from '../models'
import { api } from './api'
import { localStorageService } from './localStorageService'

class SessionService {
    private sessionCheckInterval: number | null = null
    private readonly SESSION_CHECK_INTERVAL = 5 * 60 * 1000 // 5 minutes
    private readonly SESSION_WARNING_TIME = 10 * 60 * 1000 // 10 minutes avant expiration

    // Vérifier si l'utilisateur est connecté
    isAuthenticated(): boolean {
        const user = localStorageService.getUser()
        if (!user) {
            return false
        }

        // Vérifier si la session est encore valide
        return localStorageService.isSessionValid(30) // 30 minutes
    }

    // Obtenir l'utilisateur actuel
    getCurrentUser(): User | null {
        const user = localStorageService.getUser()
        if (!user) {
            return null
        }

        // Vérifier si la session est encore valide
        if (!localStorageService.isSessionValid(30)) {
            this.clearSession()
            return null
        }

        return user
    }

    // Démarrer la surveillance de session
    startSessionMonitoring(
        onSessionExpired: () => void,
        onSessionWarning?: () => void
    ): void {
        if (this.sessionCheckInterval) {
            clearInterval(this.sessionCheckInterval)
        }

        this.sessionCheckInterval = setInterval(() => {
            const isAuth = this.isAuthenticated()
            if (!isAuth) {
                this.stopSessionMonitoring()
                onSessionExpired()
            }
        }, this.SESSION_CHECK_INTERVAL)
    }

    // Arrêter la surveillance de session
    stopSessionMonitoring(): void {
        if (this.sessionCheckInterval) {
            clearInterval(this.sessionCheckInterval)
            this.sessionCheckInterval = null
        }
    }

    // Sauvegarder l'utilisateur après connexion
    saveUser(user: User): void {
        localStorageService.saveUser(user)
    }

    // Nettoyer la session locale
    clearSession(): void {
        localStorageService.clearUser()
        this.stopSessionMonitoring()
    }

    // Déconnexion propre
    async logout(): Promise<void> {
        try {
            await api.logout()
        } catch (error) {
            console.error('Erreur lors de la déconnexion:', error)
        } finally {
            this.clearSession()
        }
    }

    // Vérifier la validité de la session avant une action importante
    validateSession(): boolean {
        return this.isAuthenticated()
    }
}

export const sessionService = new SessionService()
