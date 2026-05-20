<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import EditUserModal from "/@/views/setting/user/components/EditUserModal.vue";
import {usePagination} from "alova/client";
import {userApi} from "/@/api/modules/user-api.ts";
import type {IUserState} from "/@/typings/user.ts";

type UserModalMode = 'create' | 'edit';
const {t} = useI18n()
const visible = ref(false);
const keyword = ref('');
const userFormMode = ref<UserModalMode>('create');
const editUserModalRef = ref<InstanceType<typeof EditUserModal>>();
const showUserModal = (mode: UserModalMode) => {
  visible.value = true;
  userFormMode.value = mode;
}
const columns: DataTableColumns<IUserState> = [
  {
    type: 'selection', fixed: 'left', options: ['all', 'none']
  },
  {title: '用户名', key: 'name', ellipsis: {tooltip: true}},
  {title: '邮箱', key: 'email', ellipsis: {tooltip: true}},
  {title: '手机', key: 'phone'},
]
const {send: fetchData, data} = usePagination((page, pageSize) => {
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

onBeforeMount(async () => {
  fetchData()
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
          <n-input :placeholder="t('system.user.searchUser')"
                   class="w-[260px]"/>
        </div>
      </n-flex>
    </template>
    <n-data-table :columns="columns" :data="data" :row-key="(row: IUserState) => row.id"/>
    <template #footer>
      固定在底部的操作区
    </template>
  </n-card>
  <edit-user-modal ref="editUserModalRef" v-model:show-modal="visible" :user-form-mode="userFormMode"
                   @load="fetchData"/>
</template>

<style scoped>

</style>