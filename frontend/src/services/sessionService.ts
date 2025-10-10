import { User } from '../models'
import { api } from './api'

class SessionService {
    private sessionCheckInterval: number | null = null
    private readonly SESSION_CHECK_INTERVAL = 5 * 60 * 1000 // 5 minutes
    private currentUser: User | null = null

    isAuthenticated(): boolean {
        return this.hasValidSessionCookie()
    }

    private hasValidSessionCookie(): boolean {
        return document.cookie
            .split(';')
            .some((cookie) => cookie.trim().startsWith('JSESSIONID='))
    }

    async getCurrentUser(): Promise<User | null> {
        try {
            if (!this.currentUser) {
                this.currentUser = await api.getUser()
            }
            return this.currentUser
        } catch (error) {
            return null
        }
    }

    startSessionMonitoring(onSessionExpired: () => void): void {
        if (this.sessionCheckInterval) {
            clearInterval(this.sessionCheckInterval)
        }

        this.sessionCheckInterval = setInterval(async () => {
            const isSessionValid = await this.checkSessionWithServer()
            if (!isSessionValid) {
                this.stopSessionMonitoring()
                this.currentUser = null
                onSessionExpired()
                this.clearSession()
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
        } finally {
            this.clearSession()
        }
    }

    validateSession(): boolean {
        return this.isAuthenticated()
    }

    async checkSessionWithServer(): Promise<boolean> {
        try {
            await api.getUser()
            return true
        } catch (error) {
            this.currentUser = null
            return false
        }
    }
}

export const sessionService = new SessionService()
