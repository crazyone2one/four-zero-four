<script setup lang="ts">
import {toRefsAppStore, toRefsPreferencesStore} from '../stores';
import MainLayout from './main/index.vue'
import HeaderLayout from './header/index.vue'
import SidebarMenu from "/@/layout/aside/index.vue";

const {sidebarMenu, preferences} = toRefsPreferencesStore()
const {appStore}=toRefsAppStore()
</script>

<template>
  <div>
    <n-layout position="absolute">
      <n-layout-header style="height: 64px; padding: 24px;" bordered
                       class="relative flex h-full flex-col">
        <header-layout/>
      </n-layout-header>
      <n-layout has-sider position="absolute" :style="{ top: '64px', bottom: preferences.showFooter ? '64px' : '1px' }">
        <n-layout-sider bordered collapse-mode="width"
                        :collapsed-width="64"
                        :width="240"
                        :collapsed="sidebarMenu.collapsed"
                        show-trigger
                        @collapse="sidebarMenu.collapsed=true"
                        @expand="sidebarMenu.collapsed=false">
          <sidebar-menu/>
        </n-layout-sider>
        <n-layout content-style="padding: 12px;" :native-scrollbar="false">
          <n-spin :show="appStore.loading" :size="50">
            <main-layout/>
            <template #description>
              {{ appStore.loadingTip}}
            </template>
          </n-spin>

        </n-layout>
      </n-layout>
      <n-layout-footer v-show="preferences.showFooter"
                       bordered
                       position="absolute"
                       style="height: 32px; padding: 24px"
      >
        城府路
      </n-layout-footer>
    </n-layout>
  </div>
</template>

<style scoped>

</style>