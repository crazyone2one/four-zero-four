import {acceptHMRUpdate, defineStore, storeToRefs} from "pinia";
import {useStorage} from "@vueuse/core";
import type {IUserState} from "/@/typings/user.ts";
import {pinia} from "/@/stores";
import router from "/@/router";
import {clearToken} from "/@/utils/storage.ts";

const userInfo: IUserState = {
    avatar: '',
    id: "",
    name: '',
    token: null,
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
    return {user, cleanup, isAdmin}
})

export function toRefsUserStore() {
    return {
        ...storeToRefs(useUserStore(pinia)),
    }
}

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}