<script setup lang="ts">
import type {MenuInst, MenuOption} from "naive-ui";
import {toRefsPreferencesStore} from "/@/stores";
import {RouterLink} from "vue-router";
import router from '/@/router'
import {useI18n} from "/@/composables/useI18n.ts";

const menuRef = useTemplateRef<MenuInst>('menuRef');
const menuActiveKey = ref('')
const {sidebarMenu} = toRefsPreferencesStore()
const {t} = useI18n()

const menuOptions: MenuOption[] = [
  {
    label: () => h(RouterLink, {to: {name: 'Dashboard'}}, {default: () => t('menu.workbench')}),
    key: 'Dashboard',
    icon: () => h('div', {class: 'i-mage:dashboard-chart'})
  },
  {
    label: t('menu.settings'),
    key: 'Setting',
    icon: () => h('div', {class: 'i-mage:settings'}),
    children: [
      {
        label: () => h(RouterLink, {to: {name: 'Project'}}, {default: () => t('menu.projectManagement')}),
        key: 'Project',
        icon: () => h('div', {class: 'i-mage:dashboard-chart'})
      },
      {
        label: () => h(RouterLink, {to: {name: 'settingSystemUser'}}, {default: () => t('menu.settings.system.user')}),
        key: 'settingSystemUser',
        icon: () => h('div', {class: 'i-mage:users'})
      },
      {
        label: () => h(RouterLink, {to: {name: 'settingSystemUserGroup'}}, {default: () => t('menu.settings.system.usergroup')}),
        key: 'settingSystemUserGroup',
        icon: () => h('div', {class: 'i-mage:user-check'})
      }
      ,
      {
        label: () => h(RouterLink, {to: {name: 'settingSystemTaskCenter'}}, {default: () => t('menu.projectManagement.taskCenter')}),
        key: 'settingSystemTaskCenter',
        icon: () => h('div', {class: 'i-mage:checklist-note'})
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