import { authApi } from "../modules/auth"
import { getToken, setToken } from "/@/utils/storage"

export const handleRefreshToken = async () => {
    const { accessToken, refreshToken } = await authApi.refreshToken({ refreshToken: getToken().refreshToken })
    setToken(accessToken, refreshToken)
}