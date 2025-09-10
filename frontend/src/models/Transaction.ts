import { User } from './User'

export interface Transaction {
    id: number
    amountInCents: number
    description: string
    sender: User
    receiver: User
    createdAt: string
    status: string
}

export interface CreateTransactionRequest {
    receiverId: number
    amountInCents: number
    description: string
}

export function fromJson(json: any): Transaction {
    return {
        id: json.id,
        amountInCents: json.amountInCents,
        description: json.description ?? '',
        sender: json.sender,
        receiver: json.receiver,
        createdAt: json.createdAt,
        status: json.status,
    }
}
