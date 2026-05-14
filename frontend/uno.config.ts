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
        presetWind4(),
        presetAttributify(),
        presetIcons({
            collections:{
                async ms() {
                    const icons = await import('@iconify-json/material-symbols/icons.json')
                    return icons.default as any
                }
            }
        }),
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
    ],
})