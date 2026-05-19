<script setup lang="ts">
import {useUserStore} from "/@/stores";
import {useRequest} from "alova/client";
import {authApi} from "/@/api/modules/auth";
import {useI18n} from "vue-i18n";

const {t} = useI18n();
const userDropdownOptions = [
  {
    icon: () => h('span', {class: 'i-mage:user size-5'}),
    key: 'user',
    label: '个人中心',
  },
  {
    icon: () => h('span', {class: 'i-mage:logout size-5'}),
    key: 'signOut',
    label: '退出登录',
  },
]
const {cleanup} = useUserStore()
const {send: fetchSignOut} = useRequest(() => authApi.logout(), {immediate: false})
const onUserDropdownSelected = (key: string) => {
  switch (key) {
    case 'user':
      window.$message.info('点击了个人中心')
      break
    case 'signOut':
      fetchSignOut().then(() => {
        window.$message.success(t('message.logoutSuccess'))
        cleanup(undefined)
      })
      break
    default:
      break
  }
}
</script>

<template>
  <n-dropdown v-bind="$attrs" trigger="click"
              :options="userDropdownOptions"
              show-arrow
              @select="onUserDropdownSelected">
    <slot/>
  </n-dropdown>
</template>

<style scoped>

</style>