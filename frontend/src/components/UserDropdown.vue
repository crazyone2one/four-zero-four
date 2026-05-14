<script setup lang="ts">
import {useUserStore} from "/@/stores";

const userDropdownOptions = [
  {
    icon: () => h('span', { class: 'iconify ph--user size-5' }),
    key: 'user',
    label: '个人中心',
  },
  {
    icon: () => h('span', { class: 'iconify ph--sign-out size-5' }),
    key: 'signOut',
    label: '退出登录',
  },
]
const { cleanup } = useUserStore()
const onUserDropdownSelected = (key: string) => {
  switch (key) {
    case 'user':
     window.$message.info('点击了个人中心')
      break
    case 'signOut':
      cleanup()
      break
    default:
      break
  }
}
</script>

<template>
<n-dropdown  v-bind="$attrs" trigger="click"
             :options="userDropdownOptions"
             show-arrow
             @select="onUserDropdownSelected">
  <slot/>
</n-dropdown>
</template>

<style scoped>

</style>