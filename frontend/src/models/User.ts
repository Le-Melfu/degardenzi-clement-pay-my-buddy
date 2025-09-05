export interface User {
    id: number
    username: string
    email: string
}

export function fromJson(json: any): User {
    return {
        id: json.id,
        username: json.username,
        email: json.email,
    }
}
