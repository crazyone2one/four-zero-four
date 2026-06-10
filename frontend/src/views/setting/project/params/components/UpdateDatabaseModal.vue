<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import {useForm} from "alova/client";
import {projectApi} from "/@/api/modules/project-api.ts";
import type {FormInst} from "naive-ui";
import type {IProjectParams} from "/@/typings/project.ts";

const showModal = defineModel('showModal', {default: false});
const props = defineProps<{ currentId: string; projectId: string; ds?: IProjectParams }>();
const {t} = useI18n();
const formRef = ref<FormInst | null>(null)
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const rules = {
  parameters: {
    dataSource: [
      {required: true, message: t('project.environmental.database.nameIsRequire')}
    ],
    dbUrl: [
      {required: true, message: t('project.environmental.database.urlIsRequire')}
    ],
    username: [
      {required: true, message: t('project.environmental.database.usernameIsRequire')}
    ],
    password: [
      {required: true, message: t('project.environmental.database.password')}
    ]
  }
}
const {form, send, reset, loading} = useForm(formData => {
  if (props.currentId) {
    formData.id = props.currentId
  }
  formData.projectId = props.projectId
  return projectApi.saveOrUpdateProjectParma(formData)
}, {
  initialForm: {
    id: '',
    projectId: '',
    paramType: 'datasource',
    parameters: {dataSource: '', dbUrl: '', username: '', password: ''},
  },
  resetAfterSubmiting: true
})
const handleCancel = (shouldSearch: boolean) => {
  showModal.value = false
  reset()
  emit('cancel', shouldSearch);
}
const handleConfirm = () => {
  formRef.value?.validate(err => {
    if (!err) {
      send().then(() => {
        handleCancel(true)
      })
    }
  })
}
watchEffect(() => {
  if (props.currentId && props.ds) {
    form.value.parameters = props.ds.parameters
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <span v-if="props.currentId">
        {{ t('project.environmental.database.updateDatabase') }}
      </span>
      <span v-else>
        {{ t('project.environmental.database.addDatabase') }}
      </span>
    </template>
    <div>
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item :label="t('project.environmental.database.name')" path="parameters.dataSource">
          <n-input v-model:value="form.parameters.dataSource"
                   clearable
                   :placeholder="t('project.environmental.database.namePlaceholder')"/>
        </n-form-item>
        <n-form-item :label="t('project.environmental.database.url')" path="parameters.dbUrl">
          <n-input v-model:value="form.parameters.dbUrl" :placeholder="t('common.pleaseInput')"/>
        </n-form-item>
        <n-form-item :label="t('project.environmental.database.username')" path="parameters.username">
          <n-input v-model:value="form.parameters.username" :placeholder="t('common.pleaseInput')"/>
        </n-form-item>
        <n-form-item :label="t('project.environmental.database.password')" path="parameters.password">
          <n-input v-model:value="form.parameters.password" :placeholder="t('common.pleaseInput')"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-flex>
        <n-button secondary :loading="loading" @click="handleCancel(false)">{{ t('common.cancel') }}</n-button>
        <n-button type="primary" :loading="loading" @click="handleConfirm">
          {{ props.currentId ? t('common.confirm') : t('common.add') }}
        </n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>