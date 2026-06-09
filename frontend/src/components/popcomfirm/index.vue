<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import type {FormInst, FormRules} from "naive-ui";

const {t} = useI18n();

interface FieldConfig {
  field?: string;
  rules?: FormRules;
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
  nameExistTipText?: string; // 添加重复提示文本
}

export interface ConfirmValue {
  field: string;
  id?: string;
}

const props = withDefaults(defineProps<{
  visible?: boolean;
  title: string;
  subTitleTip?: string;
  isDelete?: boolean;
  fieldConfig?: FieldConfig; okText?: string; nodeId?: string; cancelText?: string;
  loading?: boolean;
}>(), {isDelete: true, okText: 'common.remove'})
const showPopover = ref(props.visible || false);
const attrs = useAttrs();
const emits = defineEmits<{
  (e: 'confirm', formValue: ConfirmValue, cancel?: () => void): void;
  (e: 'cancel'): void;
  (e: 'update:visible', visible: boolean): void;
}>();
const formRef = ref<FormInst | null>(null);
const form = ref({
  field: props.fieldConfig?.field || '',
});
const titleClass = computed(() => {
  return props.isDelete
      ? 'ml-2 font-medium text-[var(--color-text-1)] text-[14px]'
      : 'mb-[8px] font-medium text-[var(--color-text-1)] text-[14px] leading-[22px]';
});
const reset = () => {
  form.value.field = '';
  formRef.value?.restoreValidation()
}
const handleCancel = () => {
  showPopover.value = false;
  emits('cancel');
  reset()
}
const emitConfirm = () => emits('confirm', {...form.value, id: props.nodeId}, handleCancel);
const handleConfirm = () => {
  if (!formRef.value) {
    emitConfirm();
    return;
  }
  formRef.value.validate(err => {
    if (!err) {
      emitConfirm();
    }
  })
}
watch(
    () => props.fieldConfig?.field,
    (val) => {
      form.value.field = val || '';
    }
);
watch(
    () => props.visible,
    (val) => {
      showPopover.value = val;
    }
);

watch(
    () => showPopover.value,
    (val) => {
      if (!val) {
        emits('cancel');
      }
      emits('update:visible', val);
    }
);
</script>

<template>
  <n-popover v-bind="attrs"
             trigger="click"
             :show="showPopover"
             :position="props.isDelete ? 'right-end' : 'bottom'">
    <template #trigger>
      <slot></slot>
    </template>
    <div>
      <div class="flex flex-row flex-nowrap items-center">
        <slot name="icon">
        </slot>
        <div :class="[titleClass]">
          {{ props.title || '' }}
        </div>
      </div>
      <div v-if="props.subTitleTip" class="ml-8 mt-2 text-sm text-[var(--color-text-2)]">
        {{ props.subTitleTip }}
      </div>
      <n-form v-else ref="formRef" :model="form">
        <n-form-item path="field">
          <n-input v-model:value="form.field" />
        </n-form-item>
      </n-form>
      <div class="mb-1 mt-4 flex flex-row flex-nowrap justify-end gap-2">
        <n-button secondary size="tiny" :disabled="props.loading" @click="handleCancel">
          {{ props.cancelText || t('common.cancel') }}
        </n-button>
        <n-button type="primary" size="tiny" :loading="props.loading" @click="handleConfirm">
          {{ t(props.okText) || t('common.confirm') }}
        </n-button>
      </div>
    </div>
  </n-popover>
</template>

<style scoped>

</style>