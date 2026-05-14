<script setup lang="ts">
import {useInjection} from '../composables/useInjection';
import {layoutInjectionKey, mediaQueryInjectionKey} from '/@/injection';
import {toRefsPreferencesStore} from '../stores';
import MainLayout from './main/index.vue'
import HeaderLayout from './header/index.vue'
import texturePng from '/@/assets/texture.png'
const {isMaxSm} = useInjection(mediaQueryInjectionKey)
const {
  layoutSlideDirection,
  setLayoutSlideDirection,
  mobileLeftAsideWidth,
  mobileRightAsideWidth,
} = useInjection(layoutInjectionKey)
const {
  preferences,
  sidebarMenu,
  navigationMode,
  showFooter,
  tabs: tabsOptions,
} = toRefsPreferencesStore()

const layoutTranslateOffset = computed(() => {
  return layoutSlideDirection.value === 'right'
      ? mobileLeftAsideWidth.value || 0
      : layoutSlideDirection.value === 'left'
          ? -(mobileRightAsideWidth.value || 0)
          : 0
})
const AsyncAsideLayout = defineAsyncComponent({
  loader: () => import('./aside/index.vue'),
  loadingComponent: () => {
    return h('div', {})
  },
  delay: 0,
})
watch(isMaxSm, (isMaxSm) => {
  if (isMaxSm) {
    preferences.value.sidebarMenu.collapsed = false
    setLayoutSlideDirection(null)
  }
})
</script>

<template>
  <div class="relative h-svh overflow-hidden" :style="{ backgroundImage: `url(${texturePng})` }">
    <div v-if="isMaxSm"/>
    <div class="relative flex h-full flex-col max-sm:bg-naive-card/50"
         :class="{
        'border-naive-border transition-[background-color,border-color,rounded,transform]': isMaxSm,
        'rounded-xl border pb-2': isMaxSm && layoutTranslateOffset,
      }"
         :style="
        isMaxSm &&
        layoutSlideDirection && {
          transform: `translate(${layoutTranslateOffset}px) scale(0.88)`,
        }
      ">
      <header-layout v-if="!isMaxSm"/>
      <div v-else>x</div>
      <div class="flex flex-1 overflow-hidden">
        <collapse-transition v-if="!isMaxSm"
                             :display="navigationMode === 'sidebar'"
                             content-class="min-h-0">
          <async-aside-layout/>
        </collapse-transition>
        <main class="relative flex-1 overflow-hidden">
          <main-layout />
        </main>
      </div>
    </div>

  </div>
</template>

<style scoped>

</style>