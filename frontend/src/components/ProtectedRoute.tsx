import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSession } from '../hooks/useSession'

interface ProtectedRouteProps {
    children: React.ReactNode
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const { isAuthenticated, isLoading } = useSession()
    const navigate = useNavigate()

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            navigate('/')
        }
    }, [isAuthenticated, isLoading, navigate])

    if (isLoading) {
        return <div>Chargement...</div>
    }

    if (!isAuthenticated) {
        return null
    }

    return <>{children}</>
}

export default ProtectedRoute
