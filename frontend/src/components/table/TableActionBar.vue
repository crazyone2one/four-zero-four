<script setup lang="ts">
defineOptions({name: 'TableActionBar'});
import type {DropdownOption} from "naive-ui";
import {useI18n} from "/@/composables/useI18n.ts";
import type {IBatchActionConfig} from "/@/components/table/types.ts";
import BasePagination from "/@/components/base-pagination/index.vue";

const {selectRowCount = 0, actionConfig = {baseAction: [], moreAction: []}} = defineProps<{
  selectRowCount?: number,
  actionConfig?: IBatchActionConfig
  total?: number
}>()
const {t} = useI18n()
const emit = defineEmits<{
  (e: 'batchAction', value: string): void;
  (e: 'clear'): void;
}>();
const baseAction = ref<Array<DropdownOption>>([]);
const moreAction = ref<Array<DropdownOption>>([]);
const page = defineModel<number>('page', {type: Number, default: 1});
const pageSize = defineModel<number>('pageSize', {type: Number, default: 10});
const handleSelect = (key: string) => {
  emit('batchAction', key);
}
const showBatchAction = computed(() => {
  return selectRowCount > 0 && actionConfig;
});
watch(() => actionConfig, (newValue) => {
  if (newValue) {
    baseAction.value = [...newValue.baseAction]
    if (newValue.moreAction) {
      moreAction.value = [...newValue.moreAction]
    }
  }
}, {immediate: true})
</script>

<template>
  <n-flex justify="space-between" class="items-center">
    <n-flex>
      <span>
        <slot name="count">
          {{ t('msTable.batch.selected', {count: selectRowCount}) }}
        </slot>
        <n-button text class="clear-btn ml-[12px] px-2"
                  :disabled="!(selectRowCount>0)"
                  @click="emit('clear')">
          {{ t('msTable.batch.clear') }}
        </n-button>
      </span>
      <div v-if="showBatchAction" class="flex flex-grow items-center">
        <div v-if="actionConfig" class="flex flex-row flex-nowrap items-center">
          <template v-for="(element) in baseAction" :key="element.label">
            <n-divider v-if="element.isDivider" class="divider mx-0 my-[6px]"/>
            <n-button v-if="!element.isDivider && !element.children" class="ml-[8px]" size="tiny">
              {{ t(element.label as string) }}
            </n-button>
            <n-dropdown v-if="!element.isDivider && element.children" :options="element.children">
              <n-button>{{ t(element.label as string) }}</n-button>
            </n-dropdown>
          </template>
          <div v-if="moreAction.length > 0" class="drop-down ml-[8px] flex items-center">
            <n-dropdown :options="moreAction" placement="bottom-start" @select="handleSelect">
              <n-button text>
                <template #icon>
                  <div class="i-mage:dots-horizontal-circle"/>
                </template>
              </n-button>
            </n-dropdown>
          </div>
        </div>
      </div>
    </n-flex>
    <base-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
  </n-flex>

</template>

<style scoped>

</style>