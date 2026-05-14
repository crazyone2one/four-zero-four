export interface IUserInfo {
    avatar?: string
    id: number
    name: string
    role: 'admin' | 'user' | 'guest'
    token: string | null
}