<script setup lang="ts">
import {authApi} from "/@/api/modules/auth";

const handleDemo = async () => {
  await authApi.demo()
}
</script>

<template>
  <n-result
      status="info"
      title="Dashboard"
      description="在这个年代，信息就是金钱，金钱就是信息。"
  >
    <template #footer>
      <n-button @click="handleDemo()">Not yet</n-button>
    </template>
  </n-result>
</template>

<style scoped>

</style>