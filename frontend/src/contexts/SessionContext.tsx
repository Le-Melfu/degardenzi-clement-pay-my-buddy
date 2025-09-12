import React, { createContext, useContext, ReactNode } from 'react'
import { useSession } from '../hooks/useSession'
import { User } from '../models'

interface SessionContextType {
    user: User | null
    isLoading: boolean
    isAuthenticated: boolean
    logout: () => Promise<void>
    refreshUser: () => Promise<User | null>
    updateUser: (user: User) => void
    forceRefreshUser: () => Promise<User | null>
}

const SessionContext = createContext<SessionContextType | undefined>(undefined)

interface SessionProviderProps {
    children: ReactNode
}

export const SessionProvider: React.FC<SessionProviderProps> = ({
    children,
}) => {
    const session = useSession()

    return (
        <SessionContext.Provider value={session}>
            {children}
        </SessionContext.Provider>
    )
}

export const useSessionContext = (): SessionContextType => {
    const context = useContext(SessionContext)
    if (context === undefined) {
        throw new Error(
            'useSessionContext must be used within a SessionProvider'
        )
    }
    return context
}
