import {createApp} from 'vue'
// import './style.css'
import App from './App.vue'

import 'vfonts/Lato.css' // 通用字体
import 'vfonts/FiraCode.css' // 等宽字体
import 'virtual:uno.css'
import {pinia} from "/@/stores";
import router from './router'

const setupApp = async () => {
    const app = createApp(App)

    app.use(pinia)
    app.use(router)
    app.mount('#app')
}
setupApp().then(() => {
})
