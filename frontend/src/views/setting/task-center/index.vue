<script setup lang="ts">
import {usePagination, useRequest} from "alova/client";
import type {ITableQueryParams} from "/@/typings/common";
import {systemTaskApi} from "/@/api/modules/task/system.ts";
import {type DataTableColumns, NButton, NFlex, NSwitch} from "naive-ui";
import type {ITaskDetailInfo} from "/@/typings/task.ts";
import {useI18n} from "/@/composables/useI18n.ts";
import CronSelect from '/@/components/cron-select/index.vue'
import {useAppStore} from "/@/stores";
import EditScheduleModal from "/@/views/setting/task-center/components/EditScheduleModal.vue";

const {t} = useI18n()
const keyword = ref('');
const showEditModal = ref(false);
const appStore = useAppStore()
const checkedRowKeys = ref<string[]>([])
const {send: fetchData, data, total, page, pageSize, loading} = usePagination((page, pageSize) => {
  const param: ITableQueryParams = {
    page, pageSize,
    keyword: keyword.value,
    projectId: appStore.appStore.currentProjectId
  }
  return systemTaskApi.getTaskPage(param)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
const columns: DataTableColumns<ITaskDetailInfo> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: t('ms.taskCenter.taskID'), key: 'num', width: 100},
  {title: t('ms.taskCenter.taskName'), key: 'name', ellipsis: {tooltip: true}, width: 200},
  {
    title: t('common.status'), key: 'enable', width: 50, render(row) {
      return h(NSwitch, {
        size: 'small',
        value: row.enable,
        disabled: true,
        onUpdateValue: (v) => handleEnableChange(v, row)
      })
    }
  },
  {
    title: t('ms.taskCenter.runRule'), key: 'value', width: 120, render(row) {
      return h(CronSelect, {
        modelValue: row.value,
        loading: row.runRuleLoading,
        onChangeCron: (v) => handleRunRuleChange(v, row)
      })
    }
  },
  {title: t('ms.taskCenter.lastFinishTime'), key: 'lastTime', width: 170},
  {title: t('ms.taskCenter.nextExecuteTime'), key: 'nextTime', width: 170},
  {
    title: t('common.operation'), key: 'operation', width: 170, fixed: 'right', render(row) {
      return h(NFlex, {}, {
        default: () => [
          h(NButton, {disabled: true,text: true}, {default: () => t('common.edit')}),
          h(NButton, {disabled: true,text: true}, {default: () => t('common.execute')}),
          h(NButton, {disabled: true,text: true}, {default: () => t('common.stop')}),
          h(NButton, {disabled: true,text: true}, {default: () => t('common.delete')}),
        ]
      })
    }
  }
]
const {send: fetchUpdateCron} = useRequest(param => systemTaskApi.editCron(param), {immediate: false})
const handleRunRuleChange = (v: string, record: ITaskDetailInfo) => {
  record.runRuleLoading = true
  fetchUpdateCron({id: record.id, cron: v}).then(() => {
    record.runRuleLoading = false
    window.$message?.success(t('common.updateSuccess'))
  })
}
const {send: fetchEnable} = useRequest(param => systemTaskApi.scheduleSwitch(param), {immediate: false})
const handleEnableChange = (_: boolean, record: ITaskDetailInfo) => {
  fetchEnable(record.id).then(() => {
    window.$message?.success(t(record.enable ? 'ms.taskCenter.closeTaskSuccess' : 'ms.taskCenter.openTaskSuccess'))
    fetchData()
  })
}
const handleUpdateTask = () => {
  showEditModal.value = true
}
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card segmented embedded :bordered="false">
    <template #header>
      <n-flex justify="space-between">
        <div>
          <n-button class="mr-3" @click="handleUpdateTask()">{{ $t('common.create') }}</n-button>
        </div>
        <div>
          <n-input v-model:value="keyword"
                   :placeholder="$t('common.searchByName')"
                   class="w-[260px]"/>
        </div>
      </n-flex>
    </template>
    <n-data-table :columns="columns" :data="data"
                  :loading="loading"
                  v-model:checked-row-keys="checkedRowKeys"
                  :row-key="(row) => row.rowKey || row.id"
    />
    <template #footer>
      <table-action-bar :select-row-count="checkedRowKeys.length" v-model:page="page" v-model:page-size="pageSize"
                        :total="total" @clear="checkedRowKeys=[]"/>
    </template>
  </n-card>
  <edit-schedule-modal v-model:show-modal="showEditModal"/>
</template>

<style scoped>

</style>