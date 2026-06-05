<script setup lang="ts">
import {useForm} from "alova/client";
import {systemTaskApi} from "/@/api/modules/task/system.ts";
import {useAppStore} from "/@/stores";
import type {FormInst} from "naive-ui";

const showModal = defineModel('showModal', {default: false});
const appStore = useAppStore()
const formRef = ref<FormInst | null>(null)
const {form, reset, send, loading} = useForm(formdata => {
  return systemTaskApi.saveOrUpdateTask(formdata)
}, {
  initialForm: {
    name: '',
    projectId: appStore.appStore.currentProjectId,
    value: '',
    executorHandler: '',
    job: '',
    enable: true,
    resourceType: ''
  },
  resetAfterSubmiting: true
})
const rules = {}
const handleCancel = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
}
const handleSubmit = () => {
  formRef.value?.validate(err => {
    if (!err) {
      send().then(() => {
        handleCancel()
      })
    }
  })
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" :auto-focus="false" @close="handleCancel">
    <template #header>
      <div>标题</div>
    </template>
    <div>
      <n-form ref="formRef" :model="form" :rules="rules" class="rounded-[4px]" label-placement="top">
        <n-form-item path="name" :label="$t('ms.taskCenter.taskName')">
          <n-input v-model:value="form.name"/>
        </n-form-item>
        <n-form-item path="executorHandler" label="executorHandler">
          <n-input v-model:value="form.executorHandler"/>
        </n-form-item>
        <n-form-item path="job" label="job">
          <n-input v-model:value="form.job"/>
        </n-form-item>
        <n-form-item path="value" label="cron">
          <cron-select v-model:model-value="form.value"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-flex>
        <n-button secondary :disabled="loading" @click="handleCancel">{{ $t('common.cancel') }}</n-button>
        <n-button type="primary" :loading="loading" @click="handleSubmit">{{ $t('common.confirm') }}</n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>