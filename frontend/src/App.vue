<script setup lang="ts">
import {layoutInjectionKey, mediaQueryInjectionKey} from "/@/injection";
import {breakpointsTailwind, useBreakpoints} from "@vueuse/core";
import type {LayoutSlideDirection} from "/@/injection/types.ts";
import AppView from "/@/components/AppView.vue";

const breakpoints = useBreakpoints(breakpointsTailwind)

const shouldRefreshRoute = ref(false)
const layoutSlideDirection = ref<LayoutSlideDirection>(null)
const isSidebarColResizing = ref(false)
const setLayoutSlideDirection = (direction: LayoutSlideDirection) => {
  layoutSlideDirection.value = direction === layoutSlideDirection.value ? null : direction
}
provide(mediaQueryInjectionKey, {
  isMaxSm: breakpoints.smaller('sm'),
  isMaxMd: breakpoints.smaller('md'),
  isMaxLg: breakpoints.smaller('lg'),
  isMaxXl: breakpoints.smaller('xl'),
  isMax2Xl: breakpoints.smaller('2xl'),
})

provide(layoutInjectionKey, {
  shouldRefreshRoute,
  layoutSlideDirection,
  setLayoutSlideDirection,
  isSidebarColResizing,
  mobileLeftAsideWidth: ref(0),
  mobileRightAsideWidth: ref(0),
})
</script>

<template>
  <n-config-provider>
    <n-global-style/>
    <n-el>
      <n-modal-provider>
        <n-notification-provider placement="bottom-right">
          <n-message-provider>
            <n-dialog-provider>
              <app-view/>
            </n-dialog-provider>
          </n-message-provider>
        </n-notification-provider>
      </n-modal-provider>
    </n-el>
  </n-config-provider>
</template>
