import React, { useState } from 'react'
import MainButton from '../../components/atoms/MainButton'
import InputField from '../../components/atoms/InputField'
import Snackbar from '../../components/atoms/Snackbar'
import NavBar from '../../components/molecules/NavBar'
import './AddRelationPage.scss'
import { api } from '../../services/api'

const AddRelationPage: React.FC = () => {
    const [email, setEmail] = useState('')
    const [snackbar, setSnackbar] = useState<{
        isVisible: boolean
        message: string
        success: boolean
    }>({
        isVisible: false,
        message: '',
        success: false,
    })

    const showSnackbar = (message: string, success: boolean) => {
        setSnackbar({
            isVisible: true,
            message,
            success,
        })
    }

    const hideSnackbar = () => {
        setSnackbar((prev) => ({ ...prev, isVisible: false }))
    }

    const handleAddRelation = async () => {
        try {
            const res = await api.addConnection({ connectionEmail: email })
            if (res) {
                showSnackbar('Relation ajoutée avec succès', true)
            } else {
                showSnackbar("Erreur lors de l'ajout de relation", false)
            }
            setEmail('')
        } catch (error) {
            console.log(
                "[DebugClem] - Erreur lors de l'ajout de relation:",
                error
            )
            showSnackbar("Erreur lors de l'ajout de relation", false)
            setEmail('')
        }
    }

    const handleEmailChange = (value: string) => {
        setEmail(value)
    }

    return (
        <div className="add-relation-page">
            <NavBar activePage="add-connection" />

            <div className="add-relation-content">
                <h1 className="page-title">Chercher une relation</h1>

                <div className="search-section">
                    <InputField
                        label="Adresse mail"
                        type="email"
                        placeholder="Saisir une adresse mail"
                        value={email}
                        onChange={handleEmailChange}
                    />
                    <MainButton
                        variant="primary"
                        onClick={handleAddRelation}
                        disabled={!email.trim()}
                    >
                        Ajouter
                    </MainButton>
                </div>
            </div>

            <Snackbar
                message={snackbar.message}
                success={snackbar.success}
                isVisible={snackbar.isVisible}
                onClose={hideSnackbar}
            />
        </div>
    )
}

export default AddRelationPage
