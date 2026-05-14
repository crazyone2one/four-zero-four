<script setup lang="ts">
import type {MenuInst} from "naive-ui";
import {toRefsPreferencesStore} from "/@/stores";

const menuRef = useTemplateRef<MenuInst>('menuRef');
const menuActiveKey = ref('')
const { sidebarMenu } = toRefsPreferencesStore()
const menuOptions = [
  {
    label: 'Home',
    key: 'home',

  }
]
</script>

<template>
  <n-scrollbar>
    <n-menu ref="menuRef"
            :value="menuActiveKey"
            :options="menuOptions"
            :collapsed-width="sidebarMenu.minWidth"
            :collapsed="sidebarMenu.collapsed"
            :collapsed-icon-size="20"
            :dropdown-props="{
        size: 'medium',
        trigger: 'click',
      }"
    />
  </n-scrollbar>
</template>

<style scoped>

</style>