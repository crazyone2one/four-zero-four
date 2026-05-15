<script setup lang="ts">
import type {MenuInst, MenuOption} from "naive-ui";
import {toRefsPreferencesStore} from "/@/stores";
import {RouterLink} from "vue-router";
import router from '/@/router'

const menuRef = useTemplateRef<MenuInst>('menuRef');
const menuActiveKey = ref('')
const {sidebarMenu} = toRefsPreferencesStore()
const renderLink = (key: string) => {
  return () => h(RouterLink, {to: {name: key}}, {default: () => key})
}
const menuOptions: MenuOption[] = [
  {
    label: renderLink('Dashboard'),
    key: 'Dashboard',
    icon: () => h('div', {class: 'i-mage:dashboard-chart'})
  },
  {
    label: "Setting",
    key: 'Setting',
    icon: () => h('div', {class: 'i-mage:settings'}),
    children: [
      {
        label: renderLink('Project'),
        key: 'Project',
        icon: () => h('div', {class: 'i-mage:dashboard-chart'})
      },
      {
        label: () => h(RouterLink, {to: {name: 'User'}}, {default: () => 'User'}),
        key: 'User',
        icon: () => h('div', {class: 'i-mage:users'})
      }
    ]
  }
]
watch(
    () => router.currentRoute.value,
    (newRoute) => {
      menuActiveKey.value = newRoute.name as string
      menuRef.value?.showOption(menuActiveKey.value)
    },
    {
      immediate: true,
    },
)
</script>

<template>
  <div class="mt-5">
    <n-menu ref="menuRef"
            v-model:value="menuActiveKey"
            :options="menuOptions"
            :collapsed="sidebarMenu.collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="22"
    />
  </div>

</template>

<style scoped>

</style>