<script setup lang="ts">
import type {ButtonAnimationProps} from "/@/components/button-animation/types.ts";
import {buttonAnimationInjectionKey} from "/@/components/button-animation/injection.ts";
import {mergeWith} from "lodash-es";

const {duration = 600, animation = 'beat'} = defineProps<ButtonAnimationProps>()
const buttonAnimationInjection = inject(buttonAnimationInjectionKey, null)
const isAnimating = ref(false)
const buttonAnimationProps = computed<ButtonAnimationProps>(() => {
  return mergeWith(
      mergeWith({}, buttonAnimationInjection ?? {}, (target: any, source: any) => source ?? target),
      useAttrs(),
      (target: any, source: any) => source ?? target,
  )
})
const handleButtonClick = () => {
  if (isAnimating.value) return
  isAnimating.value = true
  setTimeout(() => {
    isAnimating.value = false
  }, duration)
}
</script>

<template>
  <n-button quaternary
            circle
            v-bind="buttonAnimationProps"
            @click.stop="handleButtonClick">
    <template #icon>
      <div
          class="grid place-items-center"
          :class="{
          'animate-beat': isAnimating && animation === 'beat',
          'animate-rotate': isAnimating && animation === 'rotate',
          'animate-shake': isAnimating && animation === 'shake',
        }"
          :style="{
          '--duration': `${duration}ms`,
        }"
      >
        <slot/>
      </div>
    </template>
  </n-button>
</template>

<style scoped>

</style>