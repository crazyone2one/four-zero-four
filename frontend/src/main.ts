import {createApp} from 'vue'
// import './style.css'
import App from './App.vue'

import 'vfonts/Lato.css' // 通用字体
import 'vfonts/FiraCode.css' // 等宽字体
import 'virtual:uno.css'
import {pinia} from "/@/stores";
import router from './router'
import {checkVersion} from "/@/utils/check-version.ts";
import {setupI18n} from "/@/i18n";
import {setupRouterGuard} from "/@/router/guard.ts";

const setupApp = async () => {
    checkVersion()
    const app = createApp(App)
    await setupI18n(app)
    app.use(pinia)
    app.use(router)
    setupRouterGuard(router)
    app.mount('#app')
}
setupApp().then(() => {
})
