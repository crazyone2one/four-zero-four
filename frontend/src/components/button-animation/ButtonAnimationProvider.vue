<script setup lang="ts">
import {buttonAnimationInjectionKey} from './injection'
import type {ButtonAnimationProps} from './types.ts'

defineProps<ButtonAnimationProps>()
defineOptions({
  inheritAttrs: false,
})
provide(buttonAnimationInjectionKey, useAttrs() as ButtonAnimationProps)
</script>

<template>
  <slot/>
</template>

<style scoped>

</style>