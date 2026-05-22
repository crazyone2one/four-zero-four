<script setup lang="ts">
const {}=defineProps<{ name?: string, isAdmin?: boolean}>()
</script>

<template>
  <div class="flex w-full flex-row flex-nowrap">
    <n-tooltip>
      <template #trigger>
        <div class="one-line-text max-w-[calc(100%-60px)]">{{ name }}</div>
      </template>
      {{ name}}
    </n-tooltip>
    <div v-if="isAdmin" class="ml-1 flex flex-row flex-nowrap text-gray">{{
        `(${$t('common.admin')})`
      }}</div>
  </div>
</template>

<style scoped>

</style>