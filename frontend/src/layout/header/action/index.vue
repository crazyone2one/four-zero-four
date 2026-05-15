<script setup lang="ts">

import PreferencesDrawer from "/@/layout/header/action/PreferencesDrawer.vue";
</script>

<template>
  <div class="flex items-center">
    <button-animation :animation="false"
                      tag="a"
                      href="https://github.com/crazyone2one/four-zero-four"
                      target="_blank"
                      rel="noopener noreferrer"
    >
      <span class="i-mage:github"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:notification-bell-snooze"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:dashboard-bar-notification"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:gem-stone"/>
    </button-animation>
    <preferences-drawer/>
  </div>
</template>

<style scoped>

</style>