import type {RouteRecordRaw} from 'vue-router'
import {createRouter, createWebHistory} from 'vue-router'

const routes: RouteRecordRaw[] = [
    {path: '/sign-in', name: 'signIn', component: () => import('/@/views/login/index.vue')},
    {
        path: '/:pathMatch(.*)*',
        name: 'errorPage',
        component: () => import('/@/views/error/index.vue'),
    },
    {
        path: '/',
        name: 'layout',
        component: () => import('/@/layout/index.vue'),
        // if you need to have a redirect when accessing / routing
        redirect: '/dashboard',
        children: [{path: '/dashboard', name: 'dashboard', component: () => import('/@/views/dashboard/index.vue')}],
    }
]

export const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
    strict: true,
})

export default router