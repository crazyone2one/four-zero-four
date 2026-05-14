import {acceptHMRUpdate, defineStore, storeToRefs} from "pinia";
import {useStorage} from "@vueuse/core";
import type {IUserInfo} from "/@/typings/user.ts";
import {pinia} from "/@/stores";
import router from "/@/router";
import {clearToken} from "/@/utils/storage.ts";

const userInfo: IUserInfo = {
    avatar: '',
    id: 1,
    name: 'Lithe User',
    role: 'user',
    token: null,
}
export const useUserStore = defineStore("userStore", () => {
    const user = useStorage<IUserInfo>("user", userInfo);
    const cleanup = (redirectPath?: string) => {
        router.replace({
            name: 'signIn',
            ...(redirectPath ? {query: {r: redirectPath}} : {}),
        })
        clearToken()
    }
    return {user, cleanup}
})

export function toRefsUserStore() {
    return {
        ...storeToRefs(useUserStore(pinia)),
    }
}

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}