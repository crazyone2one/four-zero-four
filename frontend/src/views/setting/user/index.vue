<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import EditUserModal from "/@/views/setting/user/components/EditUserModal.vue";
import {usePagination, useRequest} from "alova/client";
import {userApi} from "/@/api/modules/user-api.ts";
import type {ISystemRole, IUserState} from "/@/typings/user.ts";
import {type DataTableColumns, NButton, NFlex, NSelect, NSwitch, NTag} from "naive-ui";
import type {ITableQueryParams} from "/@/typings/common";
import TableActionBar from "/@/components/table/TableActionBar.vue";
import type {IBatchActionQueryParams} from "/@/components/table/types.ts";
import TableMoreAction from "/@/components/table/TableMoreAction.vue";

type UserModalMode = 'create' | 'edit';
const {t} = useI18n()
const visible = ref(false);
const keyword = ref('');
const userFormMode = ref<UserModalMode>('create');
const editUserModalRef = ref<InstanceType<typeof EditUserModal>>();
const checkedRowKeys = ref<string[]>([])
const userGroupOptions = ref<ISystemRole[]>([]);
const init = async () => {
  userGroupOptions.value = await userApi.getSystemRoles();
}
const showUserModal = (mode: UserModalMode) => {
  visible.value = true;
  userFormMode.value = mode;
}
const handleUserGroupChange = (v: boolean, record: IUserState) => {
  if (!v) {
    record.selectUserGroupVisible = true
    const params = {
      id: record.id,
      name: record.name,
      email: record.email,
      phone: record.phone,
      userRoleIdList: record.userRoleList?.map((e) => e.id),
    };
    console.log(params)
    window.$message.success(t('system.user.updateUserSuccess'));
    record.selectUserGroupVisible = false;
  }
}
const tableActions = [
  {
    label: t('system.user.resetPassword'),
    key: 'resetPassword'
  }, {
    type: 'divider',
    key: 'd1'
  },
  {
    label: t('system.user.delete'),
    key: 'delete'
  }
]
const tableBatchActions = {
  baseAction: [],
  moreAction: [{
    label: t('system.user.resetPassword'),
    key: 'resetPassword'
  },
    {
      label: t('system.user.disable'),
      key: 'disabled'
    },
    {
      label: t('system.user.enable'),
      key: 'enable'
    },
    {
      type: 'divider',
      key: 'd1'
    },
    {
      label: t('system.user.delete'),
      key: 'delete'
    }]
}
const columns: DataTableColumns<IUserState> = [
  {
    type: 'selection', fixed: 'left', options: ['all', 'none']
  },
  {title: t('system.user.tableColumnName'), key: 'name', ellipsis: {tooltip: true}},
  {title: t('system.user.tableColumnEmail'), key: 'email', ellipsis: {tooltip: true}},
  {title: t('system.user.tableColumnPhone'), key: 'phone', width: 140},
  {
    title: t('system.user.tableColumnUserGroup'), key: 'userRoleList', width: 300,
    render(row) {
      if (!row.selectUserGroupVisible) {
        return h(NFlex, {
          onClick: () => {
            row.selectUserGroupVisible = true
          }
        }, {
          default: () => row.userRoleList?.map(item =>
              h(NTag, {size: 'small', type: 'info'}, {default: () => item.name}))
        });
      } else {
        return h(NSelect, {
          placeholder: "t('system.user.createUserUserGroupPlaceholder')",
          value: row.userRoleList?.map(e => e.id), multiple: true, size: 'small',
          options: userGroupOptions.value, labelField: 'name', valueField: 'id',
          onUpdateShow: (v) => handleUserGroupChange(v, row)
        })
      }
    }
  },
  {
    title: t('system.user.tableColumnStatus'), key: 'enable',
    render(row) {
      return h(NSwitch, {size: 'small', value: row.enable, onUpdateValue: (v) => handleEnableChange(v, row)})
    }
  },
  {
    title: t('system.user.tableColumnActions'), key: 'operation', fixed: 'right', width: 120,
    render(row) {
      if (!row.enable) {
        return h(NButton, {text: true, onClick: () => deleteUser(row)}, {default: () => t('system.user.delete')});
      } else {
        return h(NFlex, {}, {
          default: () => [
            h(NButton, {text: true}, {default: () => t('system.user.editUser')}),
            h(TableMoreAction, {list: tableActions, onSelect: (key) => handleSelect(key, row)}),
          ]
        })
      }
    }
  },
]
const {send: fetchData, data, total, page, pageSize, loading} = usePagination((page, pageSize) => {
  const param: ITableQueryParams = {
    page, pageSize,
    keyword: keyword.value,
  }
  return userApi.getUserPage(param)
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
const resetPassword = (record?: IUserState, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = t('system.user.resetPswTip', {name: record?.name});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchResetPswTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value;
  }
  let content = t('system.user.resetPswContent');
  if (record && record.name === 'admin') {
    content = t('system.user.resetAdminPswContent');
  }
  window.$dialog.warning({
    title, content,
    positiveText: t('system.user.resetPswConfirm'),
    negativeText: t('system.user.resetPswCancel'),
    maskClosable: false,
    onPositiveClick: () => {
      const pv = {
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      }
      console.log(pv)
      window.$message.info("Not Yet!")
    }
  })
}
const {send: fetchUserStatus} = useRequest(p => userApi.toggleUserStatus(p), {immediate: false})
const disabledUser = (record?: IUserState, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = t('system.user.disableUserTip', {name: record?.name});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchDisableUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value;
  }
  let content = t('system.user.disableUserContent');
  window.$dialog.warning({
    title, content,
    positiveText: t('system.user.disableUserConfirm'),
    negativeText: t('system.user.disableUserCancel'),
    maskClosable: false,
    onPositiveClick: () => {
      fetchUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: false
      }).then(() => {
        window.$message.success(t('system.user.disableUserSuccess'))
        fetchData()
      })
    }
  })
}
const enableUser = (record?: IUserState, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = t('system.user.enableUserTip', {name: record?.name});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchEnableUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value;
  }
  let content = t('system.user.enableUserContent');
  window.$dialog.warning({
    title, content,
    positiveText: t('system.user.enableUserConfirm'),
    negativeText: t('system.user.enableUserCancel'),
    maskClosable: false,
    onPositiveClick: () => {
      fetchUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: true
      }).then(() => {
        window.$message.success(t('system.user.enableUserSuccess'))
        fetchData()
      })
    }
  })
}
const {send: fetchDeleteUserInfo} = useRequest(p => userApi.deleteUserInfo(p), {immediate: false})
const deleteUser = (record?: IUserState, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = t('system.user.deleteUserTip', {name: record?.name});
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = t('system.user.batchDeleteUserTip', {count: params?.currentSelectCount || checkedRowKeys.value.length});
    selectIds = checkedRowKeys.value;
  }
  let content = t('system.user.deleteUserContent');
  window.$dialog.error({
    title, content,
    positiveText: t('system.user.deleteUserConfirm'),
    negativeText: t('system.user.deleteUserCancel'),
    maskClosable: false,
    onPositiveClick: () => {
      fetchDeleteUserInfo({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      }).then(() => {
        window.$message.success(t('system.user.deleteUserSuccess'));
        fetchData()
      })
    }
  })
}
const handleSelect = (key: string, record: IUserState) => {
  switch (key) {
    case 'resetPassword':
      resetPassword(record);
      break;
    case 'delete':
      deleteUser(record);
      break;
    default:
      break;
  }
}
const handleTableBatch = (key: string) => {
  const params: IBatchActionQueryParams = {
    selectAll: false,
    selectedIds: checkedRowKeys.value,
    currentSelectCount: checkedRowKeys.value.length
  };
  switch (key) {
    case 'resetPassword':
      resetPassword(undefined, true, params);
      break;
    case 'disabled':
      disabledUser(undefined, true, params);
      break;
    case 'enable':
      enableUser(undefined, true, params);
      break;
    case 'delete':
      deleteUser(undefined, true, params);
      break;
    default:
      break;
  }
}
const handleEnableChange = (v: boolean, record: IUserState) => {
  if (v) {
    enableUser(record);
  } else {
    disabledUser(record);
  }
}
onBeforeMount(async () => {
  await fetchData()
  await init()
})
</script>

<template>
  <n-card segmented embedded :bordered="false">
    <template #header>
      <n-flex justify="space-between">
        <div>
          <n-button class="mr-3" @click="showUserModal('create')">{{ $t('system.user.createUser') }}</n-button>
          <n-button class="mr-3"> {{ $t('system.user.importUser') }}</n-button>
        </div>
        <div>
          <n-input v-model:value="keyword" :placeholder="t('system.user.searchUser')"
                   class="w-[260px]"/>
        </div>
      </n-flex>
    </template>
    <n-data-table :columns="columns" :data="data"
                  :loading="loading"
                  v-model:checked-row-keys="checkedRowKeys"
                  :row-key="(row) => row.rowKey || row.id"/>
    <template #footer>
      <table-action-bar :select-row-count="checkedRowKeys.length" :action-config="tableBatchActions"
                        v-model:page="page" v-model:page-size="pageSize" :total="total"
                        @batch-action="handleTableBatch"/>
    </template>
  </n-card>
  <edit-user-modal ref="editUserModalRef" v-model:show-modal="visible"
                   :user-form-mode="userFormMode"
                   :user-group-options="userGroupOptions"
                   @load="fetchData"/>
</template>

<style scoped>

</style>