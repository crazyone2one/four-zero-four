<script setup lang="ts">

import SidebarMenu from "/@/layout/aside/SidebarMenu.vue";
import SidebarUserPanel from "/@/layout/aside/SidebarUserPanel.vue";
</script>

<template>
  <div class="aside-container">
    <sidebar-menu/>
    <sidebar-user-panel/>
  </div>
</template>

<style scoped>
.aside-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}
</style>