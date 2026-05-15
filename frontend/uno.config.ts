import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetWind4,
    transformerDirectives,
    transformerVariantGroup
} from 'unocss'

export default defineConfig({
    shortcuts: {
        'wh-full': 'w-full h-full',
    },
    presets: [
        presetWind4({
            preflights: {
                reset: true
            }
        }),
        presetAttributify(),
        presetIcons({
            collections: {
                mage: () => import('@iconify-json/mage/icons.json').then(i => i.default)
            }
        }),
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
    ],
    safelist: ['mage:dashboard-chart', 'mage:settings']
})