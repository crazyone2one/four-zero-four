<script setup lang="ts">
import type {ICreateOrUpdateSystemProjectParams} from "/@/typings/project.ts";
import type {FormInst} from "naive-ui";
import {useForm} from "alova/client";
import {projectApi} from "/@/api/modules/project-api.ts";
import {toRefsUserStore, useAppStore} from "/@/stores";
import {useI18n} from "vue-i18n";
import {UserRequestTypeEnum} from "/@/components/user-selector/utils.ts";

const showModal = defineModel("showModal", {type: Boolean, default: false});
const props = defineProps<{
  currentProject?: ICreateOrUpdateSystemProjectParams;
}>();
const {t} = useI18n()
const appStore = useAppStore()
const {user} = toRefsUserStore()
const formRef = ref<FormInst | null>(null)
const isEdit = computed(() => !!(props.currentProject && props.currentProject.id));
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void;
}>();
const rules = {
  name: [
    {required: true, message: t('system.project.projectNameRequired')},
    {maxLength: 255, message: t('common.nameIsTooLang')},
  ],
  num: [
    {required: true, message: t('system.project.projectNumRequired')},
    {maxLength: 255, message: t('common.nameIsTooLang')},
  ],
  userIds: [{required: true, message: t('system.project.projectAdminIsNotNull')}]
}
const {loading, form, reset, send} = useForm(formData => projectApi.createProject(formData), {
  initialForm: {
    name: '',
    num: '',
    description: '',
    enable: true,
    userIds: user.value.id ? [user.value.id] : [],
    organizationId: appStore.appStore.currentOrgId
  },
  resetAfterSubmiting: true,
})
const handleCancel = (shouldSearch: boolean) => {
  reset()
  formRef.value?.restoreValidation()
  showModal.value = false
  emit('cancel', shouldSearch);
}
const handleSubmit = () => {
  formRef.value?.validate(err => {
    if (!err) {
      send().then(() => {
        appStore.initProjectList()
        handleCancel(true)
        window.$message.success(t(isEdit.value ? 'system.project.updateProjectSuccess' : 'system.project.createProjectSuccess'))
      })
    }
  })
}
watchEffect(() => {
  if (props.currentProject?.id) {
    form.value = {
      id: props.currentProject.id,
      name: props.currentProject.name,
      num: props.currentProject.num,
      description: props.currentProject.description,
      enable: props.currentProject.enable,
      userIds: props.currentProject.userIds,
      organizationId: appStore.appStore.currentOrgId
    }
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" :mask-closable="false"
           :auto-focus="false" @close="handleCancel(false)">
    <template #header>
      <div v-if="isEdit">{{ $t('system.project.updateProject') }}</div>
      <div v-else>{{ $t('system.project.createProject') }}</div>
    </template>
    <div class="form">
      <n-form ref="formRef" :model="form" :rules="rules" class="rounded-[4px]" label-placement="top">
        <n-form-item :label="$t('system.project.name')" path="name">
          <n-input v-model:value="form.name" :placeholder="$t('system.project.projectNamePlaceholder')" clearable/>
        </n-form-item>
        <n-form-item :label="$t('system.project.num')" path="num">
          <n-input v-model:value="form.num" :placeholder="$t('system.project.projectNumPlaceholder')" clearable/>
        </n-form-item>
        <n-form-item :label="$t('system.project.projectAdmin')" path="userIds">
          <user-selector v-model:model-value="form.userIds"
                         :type="UserRequestTypeEnum.SYSTEM_PROJECT_ADMIN"/>
        </n-form-item>
        <n-form-item :label="$t('common.desc')" path="description">
          <n-input v-model:value="form.description" type="textarea"
                   :placeholder="$t('system.project.descriptionPlaceholder')" clearable
                   :maxlength="1000" :autosize="{minRows:1}"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row items-center gap-[4px]">
          <n-switch v-model:value="form.enable" size="small"/>
          <span>{{ $t('system.organization.status') }}</span>
        </div>
        <div class="flex flex-row gap-[14px]">
          <n-button secondary :loading="loading" @click="handleCancel(false)">{{ $t('common.cancel') }}</n-button>
          <n-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isEdit ? $t('common.confirm') : $t('common.create') }}
          </n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>