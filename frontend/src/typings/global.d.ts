
interface Window {
    $loadingBar: import('naive-ui').LoadingBarApi
    $dialog: import('naive-ui').DialogApi
    $message: import('naive-ui').MessageApi
    $notification: import('naive-ui').NotificationApi
    loaderElement?: HTMLElement | null
}
declare module '*.vue' {
    import type { DefineComponent } from 'vue'

    const component: DefineComponent
    export default component
}
declare namespace App {
    type lang = 'zhCN' | 'enUS'
}

interface ImportMetaEnv {
    readonly VITE_APP_TITLE: string
    // 更多环境变量...
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}