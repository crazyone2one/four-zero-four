import {createAlova} from "alova";
import {createServerTokenAuthentication} from "alova/client";
import adapterFetch from "alova/fetch";
import type {VueHookType} from "alova/vue";
import vueHook from "alova/vue";
import {getToken} from "/@/utils/storage";
import {handleRefreshToken} from "./helper";

const {onAuthRequired, onResponseRefreshToken} = createServerTokenAuthentication<VueHookType>({
    refreshTokenOnSuccess: {
        isExpired: async (response, method) => {
            const res = await response.clone().json()
            const isExpired = method.meta && method.meta.isExpired
            return (response.status === 401 || res.code === 100401) && !isExpired
        },
        handler: async (_, method) => {
            method.meta = method.meta || {};
            method.meta.isExpired = true;
            await handleRefreshToken()
        }
    },
    assignToken: (method) => {
        method.config.headers.Authorization = `Bearer ${getToken().accessToken}`
    }
})
const FZFR = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL as string}`,
    statesHook: vueHook,
    requestAdapter: adapterFetch(),
    cacheFor: null,
    timeout: 300 * 1000,
    beforeRequest: onAuthRequired((method) => {
        method.config.headers = {...method.config.headers,};
    }),
    responded: onResponseRefreshToken({
        onSuccess: async (response, method) => {
            const {status} = response
            if (status === 200) {
                if (method.meta?.isBlob) {
                    return response.blob()
                }
                // 解析 JSON 数据
                let data: any;
                try {
                    data = await response.json();
                } catch (parseError) {
                    throw new Error('服务器响应格式错误'); // JSON 解析失败处理
                }
                if (data.code === 100200) {
                    // 成功响应，返回数据部分
                    return data.data !== undefined ? data.data : data;
                }
            } else {
                const res = await response.clone().json()
                window.$message?.error(res.message)
                throw new Error(res.message);
            }
        },
        onError: (error, method) => {
            const tip = `[${method.type}] - [${method.url}] - ${error.message}`
            window.$message?.warning(tip)
        },
        onComplete: async (_method) => {
            // 处理请求完成逻辑
        },
    })
})

export default FZFR