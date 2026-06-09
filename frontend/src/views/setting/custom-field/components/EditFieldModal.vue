<script setup lang="ts">

import type {FormItemType, IAddOrUpdateField, IFieldIconAndNameModal, IFieldOptions} from "/@/typings/custom-field.ts";
import type {FormInst} from "naive-ui";
import {useForm, useRequest} from "alova/client";
import {customFieldApi} from "/@/api/modules/custom-field.ts";
import {useI18n} from "/@/composables/useI18n.ts";
import type {IFormItemModel} from "/@/components/batch-form/types.ts";
import {cloneDeep} from "lodash-es";
import {dateOptions, fieldIconAndName, numberTypeOptions} from "/@/views/setting/custom-field/field-setting.ts";
import BatchForm from '/@/components/batch-form/index.vue'
import {getGenerateId} from "/@/utils";

const {t} = useI18n()
const showModal = defineModel("showModal", {type: Boolean, default: false});
const {projectId = ''} = defineProps<{ projectId: string }>()
const isEdit = ref<boolean>(false);
const formRef = ref<FormInst | null>(null)
const selectNumber = ref<FormItemType>('INT');
const selectFormat = ref<FormItemType>();
const fieldType = ref<FormItemType>();
const isMultipleSelectMember = ref<boolean | undefined>(false);
const batchFormRef = ref<InstanceType<typeof BatchForm>>();
const emit = defineEmits(['success']);
const initFieldForm: IAddOrUpdateField = {
  name: '',
  used: false,
  type: undefined,
  remark: '',
  scopeId: projectId,
  scene: 'COMMON',
  options: [],
  enableOptionKey: false,
  fieldKey: ''
};
const {form, loading, reset, send} = useForm((formData) => {
  const formCopy = cloneDeep(formData);
  if (selectFormat.value) {
    formCopy.type = selectFormat.value;
  }
  if (formCopy.type === 'NUMBER') {
    formCopy.type = selectNumber.value;
  }
  const {id, name, options, scene, type, remark, enableOptionKey, fieldKey} = formCopy;
  const params: IAddOrUpdateField = {
    name,
    used: false,
    options,
    scopeId: projectId,
    scene,
    type,
    remark,
    enableOptionKey, fieldKey
  };
  if (id) {
    params.id = id;
  }
  return customFieldApi.addOrUpdateOrdField(params)
}, {
  initialForm: {...initFieldForm},
  resetAfterSubmiting: true,
});
const resetForm = () => {
  form.value = {...initFieldForm};
  selectFormat.value = undefined;
  isMultipleSelectMember.value = false;
  fieldType.value = undefined;
  reset()
  batchFormRef.value?.resetForm()
};
const handleCancel = () => {
  formRef.value?.restoreValidation();
  showModal.value = false
  resetForm()
}
const handleSubmit = async () => {
  formRef.value?.validate(async (err) => {
    if (!err) {
      if (showOptionsSelect.value) {
        await new Promise<void>(resolve => {
          batchFormRef.value?.formValidate((list: any) => {
            fieldDefaultValues.value = [...list];
            if (showOptionsSelect.value) {
              let startPos = 1;
              form.value.options = (batchFormRef.value?.getFormResult() || []).map(item => {
                console.log(item)
                const currentItem: IFieldOptions = {
                  text: item.text,
                  value: item.text || getGenerateId(),
                  pos: startPos,
                };
                if (item.fieldId) {
                  currentItem.fieldId = item.fieldId;
                }
                startPos += 1;
                return currentItem;
              })
            }
            resolve()
          })
        })
      }
      send().then(res => {
        window.$message.success(isEdit.value ? t('common.updateSuccess') : t('common.newSuccess'));
        resetForm();
        handleCancel()
        emit('success', isEdit.value, res.id);
      })
    }
  })
}

const rules = {
  name: [{required: true, message: t('system.orgTemplate.fieldNameRules')}],
  type: [{required: true, message: t('system.orgTemplate.typeEmptyTip')}],
  optionsModels: [{message: t('system.orgTemplate.optionContentRules')}],
}
const optionsModels: Ref<IFormItemModel[]> = ref([]);
const fieldDefaultValues = ref<IFormItemModel[]>([]);
const showOptionsSelect = computed(() => {
  const showOptionsType: FormItemType[] = ['RADIO', 'CHECKBOX', 'SELECT', 'MULTIPLE_SELECT'];
  return showOptionsType.includes(form.value.type);
});
const onlyOptions: Ref<IFormItemModel> = ref({
  field: 'text',
  type: 'input',
  label: '',
  rules: [
    {required: true, message: t('system.orgTemplate.optionContentRules')},
    {notRepeat: true, message: t('system.orgTemplate.optionsContentNoRepeat')},
  ],
  placeholder: t('system.orgTemplate.optionsPlaceholder'),
  hideAsterisk: true,
  hideLabel: true,
});
const fieldOptions = ref<IFieldIconAndNameModal[]>([]);
const handleFieldChange = (_: string) => {
  optionsModels.value = [{...onlyOptions.value}]
  fieldDefaultValues.value = [];
}
const {send: fetchDetail} = useRequest(id => customFieldApi.getDetail(id), {immediate: false})
const editHandler = (item: IAddOrUpdateField) => {
  showModal.value = true
  if (item.id) {
    fetchDetail(item.id).then(res => {
      form.value = {
        ...res
      }
      fieldDefaultValues.value = res.options?.map((item: any) => {
        return {...item}
      }) || []
    })
  }
};
watch(() => form.value.enableOptionKey, () => {
  optionsModels.value = [{...onlyOptions.value}]
}, {immediate: true})
watchEffect(() => {
  isEdit.value = !!form.value.id;
});
onMounted(() => {
  const excludeOptions = ['MULTIPLE_MEMBER', 'DATETIME', 'SYSTEM', 'INT', 'FLOAT'];
  fieldOptions.value = fieldIconAndName.filter((item: IFieldIconAndNameModal) => excludeOptions.indexOf(item.value as string) < 0);
});
defineExpose({
  editHandler,
});
</script>

<template>
  <n-drawer v-model:show="showModal" :width="500">
    <n-drawer-content>
      <template #header>
        <div v-if="isEdit">{{ $t('system.orgTemplate.update') }}</div>
        <div v-else>{{ $t('system.orgTemplate.addField') }}</div>
      </template>
      <div class="form">
        <n-form ref="formRef" :model="form" :rules="rules" class="rounded-[4px]" label-placement="left"
                label-width="auto"
                label-align="right">
          <n-form-item :label="$t('system.orgTemplate.fieldName')" path="name">
            <n-input v-model:value="form.name"
                     :disabled="form.internal"
                     :placeholder="$t('system.orgTemplate.fieldNamePlaceholder')"
                     clearable :maxlength="255"/>
          </n-form-item>
          <n-form-item :label="$t('system.orgTemplate.optionKeyValue')" path="fieldKey">
            <n-input v-model:value="form.fieldKey"
                     :disabled="form.internal"
                     :placeholder="$t('system.orgTemplate.optionKeyValue')"
                     clearable :maxlength="255"/>
          </n-form-item>
          <n-form-item :label="$t('common.desc')" path="remark">
            <n-input v-model:value="form.remark" type="textarea"
                     :placeholder="$t('system.orgTemplate.resDescription')" clearable
                     :maxlength="1000" :autosize="{minRows:1}"/>
          </n-form-item>
          <n-form-item :label="$t('system.orgTemplate.fieldType')" path="type">
            <n-select v-model:value="form.type" :options="fieldOptions"
                      :placeholder="t('system.orgTemplate.fieldTypePlaceholder')"
                      :disabled="isEdit" @update-value="handleFieldChange"/>
          </n-form-item>
          <n-form-item v-if="form.type === 'MEMBER'" :label="$t('system.orgTemplate.allowMultiMember')" path="name">
            <n-switch v-model:value="isMultipleSelectMember" size="small" :disabled="isEdit"/>
          </n-form-item>
          <n-form-item v-if="showOptionsSelect" path="optionsModels" :label="$t('system.orgTemplate.optionContent')"
                       :class="[!form?.enableOptionKey ? 'max-w-[340px]' : 'w-full']">
            <batch-form ref="batchFormRef" :models="optionsModels" form-mode="create"
                        add-text="system.orgTemplate.addOptions"
                        :form-width="!form?.enableOptionKey ? '360px' : ''"
                        :default-vals="fieldDefaultValues"/>
          </n-form-item>
          <n-form-item v-if="form.type === 'NUMBER'" :label="$t('system.orgTemplate.numberFormat')" path="selectNumber">
            <n-select v-model:value="selectNumber" :options="numberTypeOptions"
                      :placeholder="t('system.orgTemplate.formatPlaceholder')"
                      :disabled="isEdit"/>
          </n-form-item>
          <n-form-item v-if="form.type === 'DATE'" :label="$t('system.orgTemplate.dateFormat')" path="selectFormat">
            <n-select v-model:value="selectFormat" :options="dateOptions"
                      :placeholder="t('system.orgTemplate.formatPlaceholder')"
                      :disabled="isEdit"/>
          </n-form-item>
        </n-form>

      </div>
      <template #footer>
        <div class="flex flex-row gap-[14px]">
          <n-button secondary :loading="loading" @click="handleCancel">{{ $t('common.cancel') }}</n-button>
          <n-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEdit ? $t('system.orgTemplate.update') : $t('system.orgTemplate.addField') }}
          </n-button>
        </div>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>