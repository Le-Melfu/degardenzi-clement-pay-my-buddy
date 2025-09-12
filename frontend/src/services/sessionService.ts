import { User } from '../models'
import { api } from './api'

class SessionService {
    private sessionCheckInterval: number | null = null
    private readonly SESSION_CHECK_INTERVAL = 2 * 60 * 1000 // 2 minutes
    private currentUser: User | null = null

    isAuthenticated(): boolean {
        return this.hasValidSessionCookie()
    }

    private hasValidSessionCookie(): boolean {
        return document.cookie
            .split(';')
            .some((cookie) => cookie.trim().startsWith('JSESSIONID='))
    }

    getCurrentUser(): User | null {
        return this.currentUser
    }

    startSessionMonitoring(onSessionExpired: () => void): void {
        if (this.sessionCheckInterval) {
            clearInterval(this.sessionCheckInterval)
        }

        this.sessionCheckInterval = setInterval(() => {
            const isAuth = this.isAuthenticated()
            if (!isAuth) {
                this.stopSessionMonitoring()
                this.currentUser = null
                onSessionExpired()
            }
        }, this.SESSION_CHECK_INTERVAL)
    }

    stopSessionMonitoring(): void {
        if (this.sessionCheckInterval) {
            clearInterval(this.sessionCheckInterval)
            this.sessionCheckInterval = null
        }
    }

    saveUser(user: User): void {
        this.currentUser = user
    }

    clearSession(): void {
        this.currentUser = null
        this.stopSessionMonitoring()
    }

    async logout(): Promise<void> {
        try {
            await api.logout()
        } catch (error) {
            console.error('Erreur lors de la déconnexion:', error)
        } finally {
            this.clearSession()
        }
    }

    validateSession(): boolean {
        return this.isAuthenticated()
    }

    async checkSessionWithServer(): Promise<boolean> {
        try {
            await api.getBalance()
            return true
        } catch (error) {
            console.error('Erreur lors de la vérification de session:', error)
            this.currentUser = null
            return false
        }
    }
}

export const sessionService = new SessionService()
