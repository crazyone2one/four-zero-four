<script setup lang="ts">
import UserGroupLeft from "/@/views/setting/user-group/components/UserGroupLeft.vue";
import {AuthScopeEnum} from "/@/typings/common";
import {useRouter} from "vue-router";
import type {IBaseUserGroup} from "/@/typings/user-group.ts";
import AuthTable from "/@/views/setting/user-group/components/AuthTable.vue";

provide('systemType', AuthScopeEnum.SYSTEM);
const router = useRouter();
const ugLeftRef = ref<InstanceType<typeof UserGroupLeft>>();
const currentTable = ref('auth');
const currentUserGroupItem = ref<IBaseUserGroup>({
  id: '',
  name: '',
  type: AuthScopeEnum.SYSTEM,
  internal: true,
});
const handleSelect = (item: IBaseUserGroup) => {
  currentUserGroupItem.value = item;
};
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM);
watchEffect(() => {
  if (!couldShowUser.value) {
    currentTable.value = 'auth';
  } else {
    currentTable.value = 'auth';
  }
});
onMounted(() => {
  ugLeftRef.value?.initData(router.currentRoute.value.query.id as string, true);
});

</script>

<template>
  <n-card>
    <n-split size="300px" :max="0.5" min="300px" class="h-full">
      <template #1>
        <user-group-left ref="ugLeftRef" :is-global-disable="false" @handle-select="handleSelect"/>
      </template>
      <template #2>
        <div class="flex h-full flex-col overflow-hidden pt-[16px]">
          <div class="flex flex-row items-center justify-between px-[16px]">
            <n-radio-group v-model:value="currentTable" name="currentTable">
              <n-radio-button value="auth" class="show-type-icon p-[2px]">
                {{ $t('system.userGroup.auth') }}
              </n-radio-button>
              <n-radio-button value="user" class="show-type-icon p-[2px]">
                {{ $t('system.userGroup.user') }}
              </n-radio-button>
            </n-radio-group>
            <div class="flex items-center">
              <n-input v-if="currentTable === 'user'" :placeholder="$t('system.user.searchUser')"/>
            </div>
          </div>
          <div class="flex-1 overflow-hidden">
            <div v-if="currentTable === 'user'">
              user
            </div>
            <auth-table v-if="currentTable === 'auth'" :current="currentUserGroupItem"/>
          </div>
        </div>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>
:deep(.show-type-icon .n-radio__label) {
  @apply flex;

  padding: 4px;
  line-height: 20px;
}

</style>