import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { User } from '../models'
import { sessionService } from '../services/sessionService'

interface UseSessionReturn {
    user: User | null
    isLoading: boolean
    isAuthenticated: boolean
    logout: () => Promise<void>
    refreshUser: () => Promise<User | null>
    updateUser: (user: User) => void
    forceRefreshUser: () => Promise<User | null>
}

export const useSession = (): UseSessionReturn => {
    const [user, setUser] = useState<User | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const navigate = useNavigate()

    const refreshUser = useCallback(async (): Promise<User | null> => {
        try {
            const currentUser = await sessionService.getCurrentUser()
            setUser(currentUser)
            return currentUser
        } catch (error) {
            setUser(null)
            return null
        }
    }, [])

    const updateUser = useCallback((user: User) => {
        setUser(user)
        sessionService.saveUser(user)
    }, [])

    const forceRefreshUser = useCallback(async () => {
        const currentUser = await sessionService.getCurrentUser()
        setUser(currentUser)
        return currentUser
    }, [])

    const logout = useCallback(async () => {
        setIsLoading(true)
        try {
            await sessionService.logout()
        } catch (error) {
            navigate('/login')
        } finally {
            sessionService.clearSession()
            setUser(null)
            setIsLoading(false)
            navigate('/login')
        }
    }, [navigate])

    const handleSessionExpired = useCallback(async () => {
        setUser(null)
        try {
            await sessionService.logout()
        } catch (error) {
        } finally {
            navigate('/login')
        }
    }, [navigate])

    useEffect(() => {
        const initializeSession = async () => {
            setIsLoading(true)

            const currentUser = await sessionService.getCurrentUser()
            setUser(currentUser)

            if (currentUser) {
                sessionService.startSessionMonitoring(handleSessionExpired)
            }

            setIsLoading(false)
        }

        if (window.location.pathname !== '/login') {
            initializeSession()
        } else {
            setIsLoading(false)
        }

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
