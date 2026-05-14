<script setup lang="ts">
import Logo from './logo/index.vue'
import Action from './action/index.vue';
import {toRefsPreferencesStore} from "/@/stores";
const { navigationMode } = toRefsPreferencesStore()
</script>

<template>
  <header class="flex bg-naive-card transition-[background-color]">
    <logo/>
    <div
        class="flex flex-1 items-center border-l px-4 py-3.5 transition-[border-color]"
        :class="navigationMode === 'sidebar' ? 'border-naive-border' : 'border-transparent'"
    >
      <!--      <Navigation />-->
      <Action class="gap-x-3 pl-4"/>
    </div>
  </header>
</template>

<style scoped>

</style>