import type {InjectionKey} from "vue";

export const useInjection = <T>(key: InjectionKey<T> | string) => {
    const context = inject(key, null)
    if (!context) {
        throw new Error('useInjection must be used within Injection provider')
    }
    return context
}