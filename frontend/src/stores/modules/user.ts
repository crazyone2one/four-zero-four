import {acceptHMRUpdate, defineStore, storeToRefs} from "pinia";
import {useStorage} from "@vueuse/core";
import type {IUserState} from "/@/typings/user.ts";
import {pinia, useAppStore} from "/@/stores";
import router from "/@/router";
import {clearToken, setToken} from "/@/utils/storage.ts";
import {authApi} from "/@/api/modules/auth";
import type {ILogin} from "/@/typings/common";

const userInfo: IUserState = {
    avatar: '',
    id: "",
    name: '',
}
export const useUserStore = defineStore("userStore", () => {
    const user = useStorage<IUserState>("user", userInfo);
    const cleanup = (redirectPath?: string) => {
        router.replace({
            name: 'signIn',
            ...(redirectPath ? {query: {r: redirectPath}} : {}),
        })
        clearToken()
    }
    const isAdmin = () => {
        if (!user.value.userRolePermissions) {
            return false
        }
        return user.value.userRolePermissions.some(permission => permission.userRole.id === 'admin')
    }
    const login = async (data: ILogin) => {
        authApi.login(data).then(res => {
            const {accessToken, refreshToken} = res
            setToken(accessToken, refreshToken)
        })
    }
    const isLogin = async (forceSet = false) => {
        try {
            const appStore = useAppStore();
            const res = await authApi.isLogin();
            if (!res) {
                return false;
            }
            user.value = res
            if (forceSet) {
                appStore.setCurrentOrgId(res.lastOrganizationId || '');
                appStore.setCurrentProjectId(res.lastProjectId || '');
            }
            return true;
        } catch (e) {
            console.log(e)
            return false
        }
    };
    const checkIsLogin = async (forceSet = false) => {
        const isLoginFlag = await isLogin(forceSet);
        if (isLoginFlag) {
            await router.push({name: router.currentRoute.value.name});
        }
    }
    return {user, cleanup, isAdmin, login, isLogin, checkIsLogin}
})

export function toRefsUserStore() {
    return {
        ...storeToRefs(useUserStore(pinia)),
    }
}

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}