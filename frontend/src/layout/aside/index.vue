<script setup lang="ts">

import SidebarMenu from "/@/layout/aside/SidebarMenu.vue";
import SidebarUserPanel from "/@/layout/aside/SidebarUserPanel.vue";
import {DEFAULT_PREFERENCES_OPTIONS, toRefsPreferencesStore} from "/@/stores";
import {useInjection} from "/@/composables/useInjection.ts";
import {layoutInjectionKey} from "/@/injection";
import {useDraggable} from "@vueuse/core";

const {preferences, sidebarMenu} = toRefsPreferencesStore()
const {isSidebarColResizing} = useInjection(layoutInjectionKey)
const sidebarLineRef = useTemplateRef<HTMLDivElement>('sidebarLine')

const {x: sidebarLineX} = useDraggable(sidebarLineRef, {})
const {
  minWidth: defaultMinWidth,
  width: defaultWidth,
  maxWidth: defaultMaxWidth,
} = DEFAULT_PREFERENCES_OPTIONS.sidebarMenu
const menuCollapseWidth = computed(() => {
  const {minWidth, width, collapsed} = sidebarMenu.value
  const mergedMinWidth = minWidth || defaultMinWidth
  const mergedWidth = width || defaultWidth
  return collapsed ? mergedMinWidth : mergedWidth
})
const handleCollapseClick = () => {
  preferences.value.sidebarMenu.collapsed = !sidebarMenu.value.collapsed
}
const onSidelineMouseDown = () => {
  isSidebarColResizing.value = true
  document.documentElement.style.cssText = 'user-select: none; cursor: col-resize;'
  document.addEventListener(
      'mouseup',
      () => {
        isSidebarColResizing.value = false
        document.documentElement.style.cssText = ''
      },
      {
        once: true,
      },
  )
}
watch(sidebarLineX, (newSidebarLineX) => {
  const {minWidth, maxWidth} = sidebarMenu.value
  const mergedMinWidth = minWidth || defaultMinWidth
  const mergedMaxWidth = maxWidth || defaultMaxWidth

  if (newSidebarLineX <= mergedMinWidth) {
    preferences.value.sidebarMenu.width = mergedMinWidth
    preferences.value.sidebarMenu.collapsed = true
  } else if (newSidebarLineX >= mergedMaxWidth) {
    preferences.value.sidebarMenu.width = mergedMaxWidth
    preferences.value.sidebarMenu.collapsed = false
  } else {
    preferences.value.sidebarMenu.width = newSidebarLineX
    preferences.value.sidebarMenu.collapsed = false
  }
})
watch(
    () => sidebarMenu.value.width,
    (newWidth) => {
      preferences.value.sidebarMenu.collapsed = newWidth <= sidebarMenu.value.minWidth;
    },
)
</script>

<template>
  <div class="flex h-full">
    <aside class="flex h-full flex-col justify-between gap-y-4 bg-naive-card pb-4"
           :class="{
        'transition-[background-color,width]': !isSidebarColResizing,
      }"
           :style="{
        width: `${menuCollapseWidth}px`,
      }">
      <sidebar-menu/>
      <sidebar-user-panel/>
    </aside>
    <div class="relative flex h-full justify-center">
      <div
          ref="sidebarLine"
          class="absolute left-0 z-10 h-full w-1 cursor-col-resize border-l border-naive-border transition-[background-color,border-color] hover:bg-primary/25"
          @mousedown="onSidelineMouseDown"
      />
      <div
          class="absolute top-1/2 right-0 z-50 grid size-6 translate-x-1/2 -translate-y-1/2 cursor-pointer place-items-center rounded-full border border-naive-border bg-white transition-[background-color,border-color] hover:bg-neutral-50 dark:bg-neutral-750 dark:hover:bg-neutral-700"
          @click="handleCollapseClick"
      >
        <span
            class="size-4.5 transition-[color,rotate] i-material-symbols:chevron-left dark:text-neutral-400"
            :class="{
            'rotate-180': sidebarMenu.collapsed,
          }"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>