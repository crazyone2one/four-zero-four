<script setup lang="ts">
import type {AuthScopeEnumType} from "/@/typings/common";
import type {IUserGroupItem} from "/@/typings/user-group.ts";
import {useForm} from "alova/client";
import {userGroupApi} from "/@/api/modules/user-group.ts";
import type {FormInst, FormRules} from "naive-ui";
import {useI18n} from "/@/composables/useI18n.ts";


// const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{
  id?: string;
  list: IUserGroupItem[];
  defaultName?: string;
  // 权限范围
  authScope: AuthScopeEnumType;
  visible: boolean;
}>();
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void;
  (e: 'submit', currentId: string): void;
}>();
const {t} = useI18n()
const show = ref(props.visible)
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
  name: {
    validator(_, value: string) {
      if (!value) {
        return new Error(t('system.userGroup.userGroupNameIsNotNone'));
      } else {
        const isExist = props.list.some((item) => item.name === value);
        if (isExist) {
          return new Error(t('system.userGroup.userGroupNameIsExist', {name: value}))
        }
      }
    }
  }
}
const {form, reset, loading, send: sendUpdate} = useForm(formData => userGroupApi.updateOrAddUserGroup(formData), {
  initialForm: {
    name: '',
    id: props.id,
    type: props.authScope
  }
})
const handleSubmit = () => {
  formRef.value?.validate(err => {
    if (err) {
      return false
    }
    sendUpdate().then(res => {
      if (res) {
        emit('submit', res.id)
        window.$message.success(props.id ? t('system.userGroup.updateUserGroupSuccess') : t('system.userGroup.addUserGroupSuccess'))
        handleCancel()
      }
    })
  })
}
const handleCancel = () => {
  reset()
  form.value.name = ''
  emit('cancel', false);
}
const handleOutsideClick = () => {
  if (show.value) {
    handleCancel()
  }
}
watchEffect(() => {
  show.value = props.visible
  form.value.name = props.defaultName || ''
})
</script>

<template>
  <n-popover trigger="click" :show="show"
             :content-class="props.id ? 'move-left' : ''"
             @clickoutside="handleOutsideClick">
    <template #trigger>
      <slot></slot>
    </template>
    <div>
      <n-form ref="formRef" v-model:model="form" :rules="rules">
        <div class="mb-[8px] text-[14px] font-medium text-[#242633]">
          {{ props.id ? $t('system.userGroup.rename') : $t('system.userGroup.createUserGroup') }}
        </div>
        <n-form-item path="name">
          <n-input v-model:value="form.name" :placeholder="$t('system.userGroup.searchHolder')" clearable
                   :maxlength="255"/>
          <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium text-gray">
                {{ $t('system.userGroup.createUserGroupTip') }}
              </span>
        </n-form-item>
      </n-form>
    </div>
    <div class="flex flex-row flex-nowrap justify-end gap-2">
      <n-button secondary size="tiny" :disabled="loading" @click="handleCancel">{{ $t('common.cancel') }}</n-button>
      <n-button type="primary" size="tiny" :disabled="form.name.length === 0"
                :loading="loading" @click="handleSubmit">
        {{ props.id ? $t('common.confirm') : $t('common.create') }}
      </n-button>
    </div>
  </n-popover>
</template>

<style scoped>
.move-left {
  position: relative;
  right: 22px;
}
</style>