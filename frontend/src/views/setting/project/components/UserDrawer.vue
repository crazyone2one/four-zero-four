<script setup lang="ts">
export interface projectDrawerProps {
  organizationId?: string;
  projectId?: string;
  currentName: string;
}

const keyword = ref('')
const props = defineProps<projectDrawerProps>();
const active = defineModel("active", {default: false})
const emit = defineEmits<{
  (e: 'cancel'): void;
  (e: 'requestFetchData'): void;
}>();
const handleCancel = () => {
  keyword.value = '';
  active.value = false;
  emit('cancel');
};
</script>

<template>
  <n-drawer v-model:show="active" :width="800" @update:show="handleCancel">
    <n-drawer-content closable>
      <template #header>
        {{ $t('system.memberList') }}
        <span class="ml-[8px] text-gray">
          ({{ props.currentName }})
        </span>
      </template>
      <div>
        <n-flex justify="space-between">
          <n-button>{{ $t('system.organization.addMember') }}</n-button>
          <div>
            <n-input v-model:value="keyword" :placeholder="$t('system.organization.searchUserPlaceholder')"
                     class="w-[280px]"/>
          </div>
        </n-flex>
      </div>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>