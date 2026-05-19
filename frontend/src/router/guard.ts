import type {LocationQueryRaw, Router} from "vue-router";
import {hasToken} from "/@/utils/storage.ts";
import {setRouteEmitter} from "/@/utils/route-listener.ts";

export function setupRouterGuard(router: Router) {

    router.beforeEach(async (to) => {
        setRouteEmitter(to);
        const isLoginPage = to.name === 'signIn';
        const isLoggedIn = hasToken();
        if (!isLoginPage && !isLoggedIn) {
            return {
                name: 'signIn',
                query: {
                    redirect: to.name,
                    ...to.query,
                } as LocationQueryRaw,
            }
        }

    });
}