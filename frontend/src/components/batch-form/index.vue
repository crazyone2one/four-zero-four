<script setup lang="ts">
import type {FormMode, IFormItemModel} from "/@/components/batch-form/types.ts";
import type {FormInst} from "naive-ui";
import {useI18n} from "/@/composables/useI18n.ts";

export interface BatchFormProps {
  models: IFormItemModel[];
  formMode: FormMode;
  addText?: string;
  maxHeight?: string;
  defaultVals?: any[]; // 当外层是编辑状态时，可传入已填充的数据
  isShowDrag?: boolean; // 是否可以拖拽
  formWidth?: string; // 自定义表单区域宽度
  showEnable?: boolean; // 是否显示启用禁用switch状态
  hideAdd?: boolean; // 是否隐藏添加按钮
  addToolTip?: string;
  enableType?: 'circle' | 'round' | 'line';
}

const props = withDefaults(defineProps<BatchFormProps>(), {
  maxHeight: '30vh',
  isShowDrag: false,
  hideAdd: false,
  enableType: 'line',
});
const {t} = useI18n();
const defaultForm = {
  list: [] as Record<string, any>[],
};
const emit = defineEmits(['change']);
const formRef = ref<FormInst | null>(null)
const form = ref<Record<string, any>>({list: [...defaultForm.list]});
const formItem: Record<string, any> = {};
const getFormResult = () => {
  return unref<Record<string, any>[]>(form.value.list);
}
const formValidate = (cb: (res?: Record<string, any>[]) => void, isSubmit = true) => {
  formRef.value?.validate(err => {
    if (err) {
      return;
    }
    if (typeof cb === 'function') {
      if (isSubmit) {
        cb(getFormResult());
        return;
      }
      cb();
    }
  })
}
const addField = () => {
  const item = [{...formItem}];
  item[0].type = [];
  formValidate(() => {
    form.value.list.push(item[0]); // 序号自增，不会因为删除而重复
  }, false);
}
const removeField = (i: number) => {
  form.value.list.splice(i, 1);
}
watchEffect(() => {
  props.models.forEach(e => {
    let value: string | number | boolean | string[] | number[] | undefined;
    if (e.type === 'inputNumber') {
      value = undefined;
    } else if (e.type === 'tagInput') {
      value = [];
    } else {
      value = e.defaultValue;
    }
    formItem[e.field] = value;
    if (props.showEnable) {
      // 如果有开启关闭状态，将默认禁用
      formItem.enable = false;
    }
    e.children?.forEach((child) => {
      formItem[child.field] = child.type === 'inputNumber' ? null : child.defaultValue;
    });
  })
  form.value.list = [{...formItem}];
  if (props.defaultVals?.length) {
    // 取出defaultVals的表单 field
    form.value.list = props.defaultVals.map((e) => e);
  }
})
const resetForm = () => {
  formRef.value?.restoreValidation();
}
const fieldNotRepeat = (value: string | undefined,
                        index: number,
                        field: string,
                        msg?: string) => {
  if (!value) {
    return
  }
  // 遍历其他同 field 名的输入框的值，检查是否与当前输入框的值重复
  for (let i = 0; i < form.value.list.length; i++) {
    if (i !== index && form.value.list[i][field].trim() === value) {
      return new Error(t(`${msg}` || ''));
    }
  }
}
defineExpose({
  formValidate,
  getFormResult,
  resetForm,
  // setFields,
});
</script>

<template>
  <n-form ref="formRef" :model="form">
    <div class="mb-[16px] overflow-y-auto rounded-[4px] border border-gray p-[12px]"
         :style="{ width: props.formWidth || '100%' }">
      <n-scrollbar>
        <n-flex v-for="(element, index) in form.list"
                :key="`${element.field}${index}`"
                class="w-full gap-[8px] py-[6px] pr-[8px]"
                :class="[props.isShowDrag ? 'cursor-move' : '']">
          <n-form-item v-for="model of props.models"
                       :key="`${model.field}${index}`"
                       :path="`list[${index}].${model.field}`"
                       :class="index > 0 ? 'hidden-item' : 'mb-0 flex-1'"
                       :rule="model.rules?.map(e=>{
            if(e.notRepeat===true){
              return {validator(_, value: string) {
                return fieldNotRepeat(value, index ,model.field,e.message as string)
              }}
            }
            return e;
          })">
            <template #label>
              <div class="inline-flex flex-row">
                <div v-if="index === 0 && model.label">{{ $t(model.label) }}</div>
              </div>
            </template>
            <n-input v-if="model.type === 'input'" v-model:value="element[model.field]"
                     class="flex-1" :placeholder="$t(model.placeholder || '')"
                     clearable @change="emit('change')"/>
          </n-form-item>
          <div v-if="showEnable">
            <n-switch class="mt-[8px]" :style="{ 'margin-top': index === 0 && !props.isShowDrag ? '36px' : '' }"/>
          </div>
          <div v-if="!hideAdd" v-show="form.list.length > 1" class="cursor-pointer"
               :style="{ 'margin-top': index === 0 && !props.isShowDrag ? '35px' : '10px' }"
               @click="removeField(index)">
            <div class="i-mage:minus-circle"/>
          </div>
        </n-flex>
      </n-scrollbar>
      <div v-if="props.formMode === 'create' && !props.hideAdd" class="w-full">
        <n-button text class="px-0" @click="addField">
          <template #icon>
            <div class="i-mage:plus-circle"/>
          </template>
          {{ $t(props.addText || 'common.add') }}
        </n-button>
      </div>
    </div>
  </n-form>
</template>

<style scoped>
.hidden-item {
  @apply mb-0 flex-1;
}

.hidden-item :deep(.n-form-item-label) {
  display: none !important;
}
.n-form-item.n-form-item--top-labelled {
  grid-template-rows: unset !important;
}
</style>