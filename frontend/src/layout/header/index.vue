<script setup lang="ts">
import Logo from './logo/index.vue'
import Action from './action/index.vue';
</script>

<template>
  <div class="flex items-center wh-full">
    <logo/>
    <div class=" flex flex-1 items-center  px-4 py-3.5 transition-[border-color]">
      <div class="flex h-9 flex-1 items-center">
        <n-button>n1</n-button>
        <n-button>n2</n-button>
      </div>
      <Action class="gap-x-3 pl-4"/>
    </div>
  </div>
</template>

<style scoped>

</style>