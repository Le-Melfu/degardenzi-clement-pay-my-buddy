import { User } from '../models'

const USER_STORAGE_KEY = 'paymybuddy_user'
const SESSION_TIMESTAMP_KEY = 'paymybuddy_session_timestamp'

class LocalStorageService {
    // Sauvegarder l'utilisateur en local
    saveUser(user: User): void {
        try {
            localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
            localStorage.setItem(SESSION_TIMESTAMP_KEY, Date.now().toString())
        } catch (error) {
            console.error(
                "Erreur lors de la sauvegarde de l'utilisateur:",
                error
            )
        }
    }

    // Récupérer l'utilisateur depuis le stockage local
    getUser(): User | null {
        try {
            const userData = localStorage.getItem(USER_STORAGE_KEY)
            if (!userData) {
                return null
            }
            return JSON.parse(userData) as User
        } catch (error) {
            console.error(
                "Erreur lors de la récupération de l'utilisateur:",
                error
            )
            return null
        }
    }

    // Vérifier si la session est encore valide (optionnel)
    isSessionValid(maxAgeMinutes: number = 30): boolean {
        try {
            const timestamp = localStorage.getItem(SESSION_TIMESTAMP_KEY)
            if (!timestamp) {
                return false
            }

            const sessionTime = parseInt(timestamp)
            const now = Date.now()
            const maxAge = maxAgeMinutes * 60 * 1000 // Convertir en millisecondes

            return now - sessionTime < maxAge
        } catch (error) {
            console.error('Erreur lors de la vérification de session:', error)
            return false
        }
    }

    // Supprimer l'utilisateur du stockage local
    clearUser(): void {
        try {
            localStorage.removeItem(USER_STORAGE_KEY)
            localStorage.removeItem(SESSION_TIMESTAMP_KEY)
        } catch (error) {
            console.error(
                "Erreur lors de la suppression de l'utilisateur:",
                error
            )
        }
    }

    // Mettre à jour les informations de l'utilisateur
    updateUser(updatedUser: Partial<User>): void {
        try {
            const currentUser = this.getUser()
            if (currentUser) {
                const mergedUser = { ...currentUser, ...updatedUser }
                this.saveUser(mergedUser)
            }
        } catch (error) {
            console.error(
                "Erreur lors de la mise à jour de l'utilisateur:",
                error
            )
        }
    }
}

export const localStorageService = new LocalStorageService()
