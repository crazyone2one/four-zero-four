import FZFR from "/@/api";
import type {IAuthenticationResponse, ILogin} from "/@/typings/common";
import type {IUserState} from "/@/typings/user.ts";

export const authApi = {
    login: (data: ILogin) => {
        const method = FZFR.Post<IAuthenticationResponse>('/login', data)
        method.meta = {authRole: null,};
        return method;
    },
    refreshToken: (data: { refreshToken: string }) => {
        const method = FZFR.Post<IAuthenticationResponse>('/refresh-token', data,);
        method.meta = {authRole: 'refreshToken'};
        return method
    },
    isLogin: () => FZFR.Get<IUserState>('/is-login'),
    demo: () => FZFR.Get('/demo'),
    logout: () => FZFR.Post('/logout'),
}