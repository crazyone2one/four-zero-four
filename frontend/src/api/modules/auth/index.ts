import FZFR from "/@/api";
import type { IAuthenticationResponse, ILogin } from "/@/typings/common";
export const authApi = {
    login: (data: ILogin) => {
        const method = FZFR.Post<IAuthenticationResponse>('/login', data)
        method.meta = { authRole: null, };
        return method;
    },
    refreshToken: (data: { refreshToken: string }) => {
        const method = FZFR.Post<IAuthenticationResponse>('/refresh-token', data,);
        method.meta = { authRole: 'refreshToken' };
        return method
    },
}