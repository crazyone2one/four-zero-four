<script setup lang="ts">
import {type DataTableColumns, NButton, NFlex, NSwitch} from "naive-ui";
import type {AdminList, ICreateOrUpdateSystemProjectParams, IProjectPageItem} from "/@/typings/project.ts";
import {useI18n} from "/@/composables/useI18n";
import {usePagination, useRequest} from "alova/client";
import {projectApi} from "/@/api/modules/project-api.ts";
import TableMoreAction from "/@/components/table/TableMoreAction.vue";
import UserAdmin from "/@/components/UserAdmin.vue";
import AddProjectModal from "/@/views/setting/project/components/AddProjectModal.vue";
import ShowOrEdit from "/@/components/ShowOrEdit.vue";
import UserDrawer from "/@/views/setting/project/components/UserDrawer.vue";

const {t} = useI18n()
const keyword = ref('');
const checkedRowKeys = ref<string[]>([])
const currentUserDrawer = reactive({
  visible: false,
  projectId: '',
  currentName: '',
});
const columns: DataTableColumns<IProjectPageItem> = [
  {type: 'selection', fixed: 'left', options: ['all', 'none']},
  {title: t('system.organization.NUM'), key: 'num', ellipsis: {tooltip: true}},
  {
    title: t('system.organization.name'), key: 'name', ellipsis: {tooltip: true},
    render(row) {
      return h(ShowOrEdit, {value: row.name, onUpdate: (v) => handleNameChange(v, row)})
    }
  },
  {
    title: t('system.organization.member'), key: 'memberCount',
    render(row) {
      return h('span', {class:'cursor-pointer',onClick: () => showUserDrawer(row)}, {default: () => row.memberCount})
    }
  },
  {
    title: t('system.organization.status'), key: 'enable',
    render(row) {
      return h(NSwitch, {
        size: 'small',
        value: row.enable,
        onUpdateValue: (v) => handleEnableChange(v, row)
      })
    }
  },

  {title: t('common.desc'), key: 'description', ellipsis: {tooltip: true}},
  {
    title: t('system.organization.creator'), key: 'createUser', width: 200,
    render(row) {
      return h(UserAdmin, {isAdmin: row.projectCreateUserIsAdmin, name: row.createUser})
    }
  },
  {title: t('system.organization.createTime'), key: 'createTime', width: 200},
  {
    title: t('system.organization.operation'), key: 'operation', fixed: 'right', width: 150,
    render(row) {
      if (!row.enable) {
        return h(NButton, {text: true, type: 'error'}, {default: () => t('common.delete')});
      } else {
        return h(NFlex, {}, {
          default: () => [
            h(NButton, {text: true, onClick: () => handleUpdateProject(row)}, {default: () => t('common.edit')}),
            h(NButton, {text: true}, {default: () => t('system.organization.addMember')}),
            h(TableMoreAction, {list: tableActions, onSelect: (key) => handleMoreAction(key, row)}),
          ]
        })
      }
    }
  },
]
const showUserDrawer = (record: IProjectPageItem) => {
  console.log(record)
  currentUserDrawer.visible = true;
  currentUserDrawer.projectId = record.id;
  currentUserDrawer.currentName = record.name;
}
const {send: modifyName} = useRequest(p => projectApi.modifyProjectName(p), {immediate: false})
const handleNameChange = (v: string, record: IProjectPageItem) => {
  const {id, organizationId} = record
  modifyName({id, name: v, organizationId}).then(() => {
    window.$message.success(t('common.updateSuccess'))
    fetchData()
  })
};
const handleEnableChange = (isEnable: boolean, record: IProjectPageItem) => {
  const title = isEnable ? t('system.project.enableTitle') : t('system.project.endTitle');
  const content = isEnable ? t('system.project.enableContent') : t('system.project.endContent');
  const okText = isEnable ? t('common.confirmStart') : t('common.confirmClose');
  window.$dialog.warning({
    title, content,
    positiveText: okText,
    negativeText: t('common.cancel'),
    maskClosable: false,
    onPositiveClick: () => {
      projectApi.enableOrDisableProject(record.id, isEnable).then(() => {
        window.$message.success(t('common.success'));
        fetchData();
      })
    }
  })
};
const tableActions = [{label: t('system.user.delete'), key: 'delete'}]
const handleMoreAction = (v: string, record: IProjectPageItem) => {
  console.log(v, record)
}
const {data, loading, page, pageSize, total, send: fetchData} = usePagination((page, pageSize) => {
  return projectApi.fetchProjectPage({page, pageSize, keyword: keyword.value})
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
const currentUpdateProject = ref<ICreateOrUpdateSystemProjectParams>();
const addProjectVisible = ref(false);
const handleUpdateProject = (record?: IProjectPageItem) => {
  if (record) {
    const {id, name, description, enable, adminList, organizationId, num} =
        record;
    currentUpdateProject.value = {
      id,
      name, num,
      description,
      enable,
      userIds: adminList.map((item: AdminList) => item.id),
      organizationId
    };
  } else {
    currentUpdateProject.value
  }
  addProjectVisible.value = true;
}
const handleAddProjectModalCancel = (shouldSearch: boolean) => {
  if (shouldSearch) {
    fetchData();
  }
  addProjectVisible.value = false;
}
const handleUserDrawerCancel = () => {
  currentUserDrawer.visible = false;
  currentUserDrawer.projectId = '';
  currentUserDrawer.currentName = '';
}
const rowClassName = (record: IProjectPageItem) => {
  return record.id === currentUserDrawer.projectId ? 'selected-row-class' : '';
}
onBeforeMount(() => {
  fetchData()
})
</script>

<template>
  <n-card segmented embedded :bordered="false">
    <template #header>
      <n-flex justify="space-between">
        <div>
          <n-button class="mr-3" @click="handleUpdateProject()">{{ $t('system.organization.createProject') }}</n-button>
        </div>
        <div>
          <n-input v-model:value="keyword"
                   :placeholder="$t('system.organization.searchIndexPlaceholder')"
                   class="w-[260px]"/>
        </div>
      </n-flex>
    </template>
    <n-data-table :columns="columns" :data="data"
                  :loading="loading"
                  v-model:checked-row-keys="checkedRowKeys"
                  :row-key="(row) => row.rowKey || row.id"
                  :row-class-name="rowClassName"
    />
    <template #footer>
      <table-action-bar :select-row-count="checkedRowKeys.length" v-model:page="page" v-model:page-size="pageSize"
                        :total="total" @clear="checkedRowKeys=[]"/>
    </template>
  </n-card>
  <add-project-modal v-model:show-modal="addProjectVisible"
                     :current-project="currentUpdateProject"
                     @cancel="handleAddProjectModalCancel"/>
  <user-drawer v-model:active="currentUserDrawer.visible"
               :project-id="currentUserDrawer.projectId"
               :current-name="currentUserDrawer.currentName"
               @cancel="handleUserDrawerCancel"/>
</template>

<style scoped>
:deep(.selected-row-class td) {
  background: #c7bbf3 !important;
}
</style>