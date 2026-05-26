<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import CreateOrUpdateUserGroup from "/@/views/setting/user-group/components/CreateOrUpdateUserGroup.vue";
import type {IBaseUserGroup, IUserGroupItem, PopVisible} from "/@/typings/user-group.ts";
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/typings/common";
import {useRequest} from "alova/client";
import {userGroupApi} from "/@/api/modules/user-group.ts";

const {isGlobalDisable = false} = defineProps<{
  addPermission?: string[];
  updatePermission?: string[];
  isGlobalDisable: boolean;
}>();
const emit = defineEmits<{
  (e: 'handleSelect', element: IUserGroupItem): void;
  (e: 'addUserSuccess', id: string): void;
}>();
const systemType = inject<AuthScopeEnumType>('systemType');
const {t} = useI18n()
const systemToggle = ref(true);
const systemUserGroupVisible = ref(false);
const popVisible = ref<PopVisible>({});
const currentId = ref('');
const userGroupList = ref<IUserGroupItem[]>([]);
const systemUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM);
});
const systemMoreAction = [
  {
    label: t('system.userGroup.rename'),
    key: 'rename'
  }, {
    type: 'divider',
    key: 'd1'
  }, {
    label: t('system.userGroup.delete'),
    key: 'delete'
  }]
const currentItem = ref<IBaseUserGroup>({id: '', name: '', internal: false, type: AuthScopeEnum.SYSTEM});
const handleListItemClick = (element: IUserGroupItem) => {
  const {id, name, type, internal} = element;
  currentItem.value = {id, name, type, internal};
  currentId.value = id;
  emit('handleSelect', element);
}
const {send: fetchUserGroupList} = useRequest(() => userGroupApi.getUserGroupList(), {immediate: false});
const initData = (id?: string, isSelect = true) => {
  fetchUserGroupList().then(res => {
    if (res.length > 0) {
      userGroupList.value = res;
      if (isSelect) {
        if (id) {
          const item = res.find((i) => i.id === id);
          if (item) {
            handleListItemClick(item);
          } else {
            window.$message.warning(t('common.resourceDeleted'));
            handleListItemClick(res[0]);
          }
        } else {
          handleListItemClick(res[0]);
        }
      }
      const tmpObj: PopVisible = {};
      res.forEach((element) => {
        tmpObj[element.id] = {visible: false, authScope: element.type, defaultName: '', id: element.id};
      });
      popVisible.value = tmpObj;
    }
  })
}
const handleCreateUG = (scoped: AuthScopeEnumType) => {
  if (scoped === AuthScopeEnum.SYSTEM) {
    systemUserGroupVisible.value = true;
  }
}
const handleRenameCancel = (element: IUserGroupItem, id?: string) => {
  if (id) {
    initData(id, true);
  }
  popVisible.value[element.id].visible = false;
};
defineExpose({
  initData,
});
</script>

<template>
  <div class="flex flex-col px-[16px] pb-[16px]">
    <div class="sticky top-0 z-[999] pb-[8px] pt-[16px]">
      <n-input :placeholder="t('system.userGroup.searchHolder')"/>
    </div>
    <div class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-[var(--color-text-4)]">
          <span v-if="systemToggle" class="i-mage:double-arrow-down cursor-pointer" @click="systemToggle=false"/>
          <span v-else class="i-mage:double-arrow-right cursor-pointer" @click="systemToggle=true"/>
          <div class="text-[14px]">
            {{ t('system.userGroup.systemUserGroup') }}
          </div>
        </div>
        <create-or-update-user-group
            :visible="systemUserGroupVisible"
            :list="systemUserGroupList"
            :auth-scope="AuthScopeEnum.SYSTEM"
            @cancel="systemUserGroupVisible=false">
          <n-tooltip trigger="hover">
            <template #trigger>
              <span class="i-mage:plus-circle cursor-pointer text-purple hover:text-purple-500"
                    @click="handleCreateUG(AuthScopeEnum.SYSTEM)"/>
            </template>
            {{ `创建${t('system.userGroup.systemUserGroup')}` }}
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="systemToggle">
          <div v-for="element in systemUserGroupList" :key="element.id"
               class="list-item"
               :class="{ '!bg-green': element.id === currentId }"
               @click="handleListItemClick(element)">
            <create-or-update-user-group
                :visible="popVisible[element.id].visible"
                :list="systemUserGroupList"
                :auth-scope="element.type"
                @cancel="handleRenameCancel(element)"
                @submit="handleRenameCancel(element, element.id)">
              <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                <div
                    class="list-item-name one-line-text text-[var(--color-text-1)]"
                    :class="{ '!text-[rgb(var(--primary-5))]': element.id === currentId }"
                >
                  {{ element.name }}
                </div>
                <div v-if="
                    element.type === systemType ||
                    (!element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0)
                  "
                     class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                     :class="{ '!opacity-100': element.id === currentId }">
                  <div v-if="element.type === systemType" class="icon-button">
                    <span class="i-mage:plus-circle cursor-pointer"/>
                  </div>
                  <n-dropdown v-if=" !element.internal &&
                      (element.scopeId !== 'global' || !isGlobalDisable) &&
                      systemMoreAction.length > 0
                    " trigger="click" :options="systemMoreAction">
                    <span class="i-mage:dots-horizontal-circle"/>
                  </n-dropdown>
                </div>
              </div>
            </create-or-update-user-group>
          </div>

        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
.icon-button {
  display: flex;
  box-sizing: border-box;
  justify-content: center;
  align-items: center;
  width: 24px;
  height: 24px;
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

.list-item {
  padding: 7px 4px 7px 20px;
  height: 38px;
  border-radius: 2px;
  @apply flex cursor-pointer items-center;

  &:hover .list-item-action {
    opacity: 1;
  }
}
</style>