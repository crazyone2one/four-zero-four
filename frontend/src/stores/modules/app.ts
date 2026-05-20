import {acceptHMRUpdate, defineStore, storeToRefs} from "pinia";
import {useStorage} from "@vueuse/core";
import {pinia} from "/@/stores";
import type {IProjectOption} from "/@/typings/project.ts";
import {projectApi} from "/@/api/modules/project-api.ts";
import {useI18n} from "/@/composables/useI18n.ts";

export interface IAppState {
    loading: boolean;
    loadingTip: string;
    loginLoading: boolean;
    currentOrgId: string;
    currentProjectId: string;
    projectList: IProjectOption[];
}

export const useAppStore = defineStore('app', () => {
    const appStore = useStorage<IAppState>('app', {
        loading: false,
        loadingTip: '',
        loginLoading: false,
        currentOrgId: '',
        currentProjectId: '',
        projectList: [],
    })
    const initProjectList = async () => {
        try {
            if (appStore.value.currentOrgId) {
                projectApi.getProjectList(appStore.value.currentOrgId).then(res => {
                    appStore.value.projectList = res
                })
            } else {
                appStore.value.projectList = []
            }
        } catch (e) {
            console.log(e)
        }
    }
    const setCurrentOrgId = (id: string) => {
        appStore.value.currentOrgId = id
    }
    const setCurrentProjectId = (id: string) => {
        appStore.value.currentProjectId = id
    }
    const showLoading = (tip = '你不知道你有多幸运。。。') => {
        const {t} = useI18n();
        appStore.value.loading = true
        appStore.value.loadingTip = tip || t('message.loadingDefaultTip')
    }
    const hideLoading = () => {
        const {t} = useI18n();
        appStore.value.loading = false
        appStore.value.loadingTip = t('message.loadingDefaultTip')
    }
    return {appStore, initProjectList, setCurrentOrgId, setCurrentProjectId, showLoading, hideLoading}
})

export function toRefsAppStore() {
    return {
        ...storeToRefs(useAppStore(pinia)),
    }
}

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAppStore, import.meta.hot))
}