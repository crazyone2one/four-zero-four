<script setup lang="ts">
import {usePagination} from "alova/client";
import type {ITableQueryParams} from "/@/typings/common";
import {systemTaskApi} from "/@/api/modules/task/system.ts";

const keyword = ref('');
const {send: fetchData, data, total, page, pageSize, loading} = usePagination((page, pageSize) => {
  const param: ITableQueryParams = {
    page, pageSize,
    keyword: keyword.value,
  }
  return systemTaskApi.getTaskPage(param)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
onMounted(() => {
  fetchData()
})
</script>

<template>
  task center
</template>

<style scoped>

</style>