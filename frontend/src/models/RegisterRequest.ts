export interface RegisterRequest {
    username: string
    email: string
    password: string
}

export function fromJson(json: any): RegisterRequest {
    return {
        username: json.username,
        email: json.email,
        password: json.password,
    }
}
