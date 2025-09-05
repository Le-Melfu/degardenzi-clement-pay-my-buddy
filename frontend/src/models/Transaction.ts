import { User } from './User'

export interface Transaction {
    id: number
    amount: number
    description: string
    sender: User
    receiver: User
    createdAt: string
}

export interface CreateTransactionRequest {
    receiverId: number
    amount: number
    description: string
}

export function fromJson(json: any): Transaction {
    return {
        id: json.id,
        amount: json.amount,
        description: json.description,
        sender: json.sender,
        receiver: json.receiver,
        createdAt: json.createdAt,
    }
}
