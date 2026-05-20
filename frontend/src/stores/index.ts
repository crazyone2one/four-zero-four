import {createPinia} from "pinia";

const pinia = createPinia()

export {pinia}
export * from './modules/user'
export * from './modules/preferences'
export * from './modules/app'