<script setup lang="ts">
import {useRequest} from "alova/client";
import {customFieldApi} from "/@/api/modules/custom-field.ts";
import {NInput, NRadio, NRadioGroup, NSelect, type TransferOption} from "naive-ui";
import type {ICustomField} from "/@/typings/custom-field.ts";

const active = defineModel('active', {default: false});
const customFieldOptions = ref<Array<TransferOption>>([])
const selectedCustomFieldIds = ref<Array<string>>([])
const customFields = ref<Array<ICustomField>>()
const selectedCustomFields = ref<Array<ICustomField>>([])
const formValues = ref<Record<string, any>>({})

const {send: fetchCustomFieldList} = useRequest(scene => customFieldApi.getFieldListByScene(scene), {immediate: false});
const getFieldValue = (fieldId: string) => {
  return formValues.value[fieldId]
}

const setFieldValue = (fieldId: string, value: any) => {
  formValues.value[fieldId] = value
}
const renderFormItem = (field: ICustomField) => {
  const fieldValue = getFieldValue(field.fieldKey)
  switch (field.type) {
    case 'INPUT':
      return h(NInput, {value: fieldValue, onUpdateValue: (v: string) => setFieldValue(field.fieldKey, v)}, {});
    case 'SELECT':
      return h(NSelect, {
        value: fieldValue,
        options: field.options.map(opt => ({label: opt.text, value: opt.value})) || [],
        onUpdateValue: (v: string) => setFieldValue(field.id, v)
      }, {});
    case 'RADIO':
      return h(NRadioGroup, {value: fieldValue, onUpdateValue: (v: any) => setFieldValue(field.fieldKey, v)}, {
        default: () => field.options.map((opt => h(NRadio, {value: opt.value}, {default: () => opt.text})))
      })
    default:
      return h(NInput, {value: fieldValue, onUpdateValue: (v: string) => setFieldValue(field.fieldKey, v)}, {});
  }
}
const handleConfirm = () => {
  console.log(formValues.value)
}
watch(() => selectedCustomFieldIds.value, (newValue) => {
  if (newValue && customFields.value) {
    selectedCustomFields.value = newValue.map(id =>
        customFields.value!.find(c => c.id === id)
    ).filter((cf): cf is ICustomField => cf !== undefined)
  } else {
    selectedCustomFields.value = []
  }
})
onBeforeMount(() => {
  fetchCustomFieldList("COMMON").then(res => {
    res.forEach(item => {
      customFieldOptions.value.push({
        label: item.name,
        value: item.id,
      })
    })
    customFields.value = res
  })
})
</script>

<template>
  <n-drawer v-model:show="active" :width="800">
    <n-drawer-content title="任务配置">
      <n-transfer
          v-model:value="selectedCustomFieldIds"
          virtual-scroll
          :options="customFieldOptions"
          source-filterable
      />
      <n-divider/>
      <n-form v-if="selectedCustomFields.length > 0" v-model:model="formValues" label-placement="left">
        <n-form-item v-for="item in selectedCustomFields" :key="item.id"
                     :label="item.name">
          <component :is="renderFormItem(item)"/>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button secondary @click="handleConfirm">{{ $t('common.confirm') }}</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>