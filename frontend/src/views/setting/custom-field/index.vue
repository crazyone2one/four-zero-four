<script setup lang="ts">

import {type DataTableColumns, NButton, NFlex} from "naive-ui";
import TableActionBar from "/@/components/table/TableActionBar.vue";
import {useI18n} from "/@/composables/useI18n.ts";
import {useAppStore} from "/@/stores";
import type {ICustomField} from "/@/typings/custom-field.ts";
import {usePagination} from "alova/client";
import {customFieldApi} from "/@/api/modules/custom-field.ts";
import EditFieldModal from "/@/views/setting/custom-field/components/EditFieldModal.vue";
import {getIconType} from "/@/views/setting/custom-field/field-setting.ts";

const {t} = useI18n()
const keyword = ref('');
const showAddFieldModal = ref(false);
const checkedRowKeys = ref<string[]>([])
const appStore = useAppStore();
const currentProjectId = computed(() => appStore.appStore.currentProjectId);
const columns: DataTableColumns<ICustomField> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {
    title: t('system.orgTemplate.name'), key: 'name', width: 300,
    render(row) {
      return h(NFlex, {}, {
        default: () => [
          h('span', {class: getIconType(row.type)?.iconName || ''}, {}),
          h('span', {}, {default: () => row.name}),
        ]
      })
    }
  },
  {
    title: t('system.orgTemplate.columnFieldType'), key: 'type', render(row) {
      return h('span', {}, {default: () => getIconType(row.type)?.label})
    }
  },
  {title: t('system.orgTemplate.columnFieldDescription'), key: 'remark', width: 300, ellipsis: {tooltip: true}},
  {title: t('system.orgTemplate.columnFieldUpdatedTime'), key: 'updateTime'},
  {title: t('system.organization.operation'), key: 'operation', fixed: 'right', width: 200},
]
const {data, loading, page, pageSize, total, send: fetchData} = usePagination((page, pageSize) => {
  return customFieldApi.getFieldPage({
    page,
    pageSize,
    keyword: keyword.value,
    projectId: currentProjectId.value,
    scene: "COMMON"
  })
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
const handleAdd = () => {
  showAddFieldModal.value = true
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
          <n-button class="mr-3" @click="handleAdd()">{{ t('system.orgTemplate.addField') }}</n-button>
        </div>
        <div>
          <n-input v-model:value="keyword"
                   :placeholder="$t('system.orgTemplate.searchTip')"
                   class="w-[260px]"/>
        </div>
      </n-flex>
    </template>
    <n-data-table :columns="columns" :data="data" :loading="loading"
                  v-model:checked-row-keys="checkedRowKeys"
                  :row-key="(row) => row.rowKey || row.id"/>
    <template #footer>
      <table-action-bar :select-row-count="checkedRowKeys.length" v-model:page="page" v-model:page-size="pageSize"
                        :total="total" @clear="checkedRowKeys=[]"/>
    </template>
  </n-card>
  <edit-field-modal v-model:show-modal="showAddFieldModal" :project-id="currentProjectId" @success="()=>fetchData()"/>
</template>

<style scoped>

</style>