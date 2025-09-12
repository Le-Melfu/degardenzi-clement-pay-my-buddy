import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { User } from '../models'
import { sessionService } from '../services/sessionService'

interface UseSessionReturn {
    user: User | null
    isLoading: boolean
    isAuthenticated: boolean
    logout: () => Promise<void>
    refreshUser: () => User | null
    updateUser: (user: User) => void
    forceRefreshUser: () => User | null
}

export const useSession = (): UseSessionReturn => {
    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const navigate = useNavigate()

    const refreshUser = useCallback((): User | null => {
        try {
            const currentUser = sessionService.getCurrentUser()
            setUser(currentUser)
            return currentUser
        } catch (error) {
            console.error("Erreur lors du refresh de l'utilisateur:", error)
            setUser(null)
            return null
        }
    }, [])

    // Méthode pour mettre à jour l'utilisateur après login
    const updateUser = useCallback((user: User) => {
        setUser(user)
    }, [])

    // Méthode pour forcer le refresh de l'utilisateur depuis le sessionService
    const forceRefreshUser = useCallback(() => {
        const currentUser = sessionService.getCurrentUser()
        setUser(currentUser)
        return currentUser
    }, [])

    const logout = useCallback(async () => {
        setIsLoading(true)
        try {
            await sessionService.logout()
        } catch (error) {
            console.error('Erreur lors de la déconnexion:', error)
        } finally {
            // Nettoyer complètement la session
            sessionService.clearSession()
            setUser(null)
            setIsLoading(false)
            navigate('/login')
        }
    }, [navigate])

    const handleSessionExpired = useCallback(() => {
        setUser(null)
    }, [])

    useEffect(() => {
        const initializeSession = async () => {
            setIsLoading(true)

            // Récupérer l'utilisateur depuis le sessionService (même sans cookie)
            const currentUser = sessionService.getCurrentUser()
            setUser(currentUser)

            // Vérifier si l'utilisateur est authentifié via les cookies
            if (sessionService.isAuthenticated()) {
                // Démarrer la surveillance de session
                sessionService.startSessionMonitoring(handleSessionExpired)
            }

            setIsLoading(false)
        }

        // Ne pas initialiser la session sur la page de login
        if (window.location.pathname !== '/login') {
            initializeSession()
        } else {
            setIsLoading(false)
        }

        // Nettoyage à la destruction du composant
        return () => {
            sessionService.stopSessionMonitoring()
        }
    }, [handleSessionExpired])

    return {
        user,
        isLoading,
        isAuthenticated: user !== null,
        logout,
        refreshUser,
        updateUser,
        forceRefreshUser,
    }
}
