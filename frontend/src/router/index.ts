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
        children: [
            {path: '/dashboard', name: 'Dashboard', component: () => import('/@/views/dashboard/index.vue')},
            {
                path: '/setting', name: 'Setting', children: [
                    {path: 'project', name: 'Project', component: () => import('/@/views/setting/project/index.vue')},
                    {
                        path: 'user',
                        name: 'settingSystemUser',
                        component: () => import('/@/views/setting/user/index.vue')
                    },
                    {
                        path: 'user-group',
                        name: 'settingSystemUserGroup',
                        component: () => import('/@/views/setting/user-group/index.vue')
                    },
                    {
                        path: 'task-center',
                        name: 'settingSystemTaskCenter',
                        component: () => import('/@/views/setting/task-center/index.vue')
                    },
                ]
            },
        ],
    }
]

export const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
    strict: true,
})

export default router