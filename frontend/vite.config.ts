import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import {NaiveUiResolver} from 'unplugin-vue-components/resolvers'
import Components from 'unplugin-vue-components/vite'
import UnoCSS from 'unocss/vite'
import path from 'path'
// https://vite.dev/config/
export default defineConfig(({mode}) => {
    const isProduction = mode === 'production'
    return {
        plugins: [vue(), UnoCSS(),
            AutoImport({
                imports: ['vue', {
                    'naive-ui': [
                        'useDialog',
                        'useMessage',
                        'useNotification',
                        'useLoadingBar'
                    ]
                }],
                dts: 'src/typings/auto-imports.d.ts',
            }),
            Components({
                resolvers: [NaiveUiResolver()],
                dts: 'src/typings/components.d.ts',
            })
        ],
        server: !isProduction ? {
            proxy: {
                '/front': {
                    target: 'http://127.0.0.1:8080/',
                    changeOrigin: true,
                    rewrite: (path: string) => path.replace(new RegExp('^/front'), ''),
                },
            }
        } : {},
        resolve: {
            alias: [
                {
                    find: /\/@\//,
                    replacement: path.resolve(__dirname, '.', 'src') + '/',
                }
            ]
        }
    }
})
