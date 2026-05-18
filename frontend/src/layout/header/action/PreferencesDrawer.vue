<script setup lang="ts">
import {toRefsPreferencesStore} from "/@/stores";
import packageJson from '/@/../package.json'

const showPreferencesDrawer = ref(false)
const {themeColor} = toRefsPreferencesStore()
</script>

<template>
  <div>
    <button-animation :title="$t('settings.title')" @click="showPreferencesDrawer = true">
      <span class="i-mage:filter"/>
    </button-animation>
    <button-animation-provider>
      <n-notification-provider>
        <n-drawer v-model:show="showPreferencesDrawer" :auto-focus="false" :width="320"
                  :theme-overrides="{
            footerPadding: '14px 16px',
          }"
                  :style="{
            '--primary-color': themeColor,
          }">
          <n-drawer-content :native-scrollbar="false" :header-style="{height: '64px'}">
            <template #header>
              <div class="flex items-center gap-x-1">
                <span>{{ $t('settings.title') }}</span>
                <button-animation animation="rotate">
                  <span class="i-mage:refresh"/>
                </button-animation>
              </div>
            </template>
            <template #footer>
              <div class="flex w-full items-center justify-between">
                <div class="flex items-center gap-x-1">
                  <span class="size-5 i-mage:crown-a"/>
                  <span class="leading-4">{{ $t('settings.help.version') }}</span>
                </div>
                <span class="leading-4">{{ packageJson.version }}</span>
              </div>
            </template>
          </n-drawer-content>
        </n-drawer>
      </n-notification-provider>
    </button-animation-provider>
  </div>
</template>

<style scoped>

</style>