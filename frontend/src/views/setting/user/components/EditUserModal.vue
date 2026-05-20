<script setup lang="ts">
import type {IFormItemModel} from "/@/components/batch-form/types.ts";
import {useI18n} from "/@/composables/useI18n.ts";
import BatchForm from '/@/components/batch-form/index.vue'
import {cloneDeep} from "lodash-es";
import type {ICreateUserParams, ISimpleUserInfo, ISystemRole} from "/@/typings/user.ts";
import type {FormInst} from "naive-ui";
import {userApi} from "/@/api/modules/user-api.ts";
import {validateEmail, validatePhone} from "/@/utils/validate.ts";

interface UserForm {
  list: ISimpleUserInfo[];
  userGroup: string[];
}

const showModal = defineModel('showModal', {default: false})
const {userFormMode = 'create'} = defineProps<{ userFormMode: 'create' | 'edit' }>()
const {t} = useI18n()
const emit = defineEmits(['load']);
const batchFormRef = ref<InstanceType<typeof BatchForm>>();
const userGroupOptions = ref<ISystemRole[]>([]);
const batchFormModels: Ref<IFormItemModel[]> = ref([
  {
    field: 'name',
    type: 'input',
    label: 'system.user.createUserName',
    rules: [{required: true, message: t('system.user.createUserNameNotNull')}, {
      validator(_, value: string) {
        if (value.length > 255) {
          return new Error('system.user.createUserNameOverLength')
        }
        return true
      }
    }],
    placeholder: 'system.user.createUserNamePlaceholder',
  },
  {
    field: 'email',
    type: 'input',
    label: 'system.user.createUserEmail',
    rules: [
      {notRepeat: true, message: 'system.user.createUserEmailNoRepeat'},
      {
        required: true,
        validator(_, value: string) {
          if (!value) {
            return new Error(t('system.user.createUserEmailNotNull'))
          } else if (!validateEmail(value)) {
            return new Error(t('system.user.createUserEmailErr'))
          }
          return true
        }
      }
    ],
    placeholder: 'system.user.createUserEmailPlaceholder',
  },
  {
    field: 'phone',
    type: 'input',
    label: 'system.user.createUserPhone',
    rules: [{
      validator(_, value: string) {
        if (value && !validatePhone(value)) {
          return new Error(t('system.user.createUserPhoneErr'))
        }
        return true
      }
    }],
    placeholder: 'system.user.createUserPhonePlaceholder',
  },
]);

const defaultUserForm = {
  list: [
    {
      name: '',
      email: '',
      phone: '',
    },
  ],
  userGroup: [],
};
const userFormRef = ref<FormInst | null>(null)
const userForm = ref<UserForm>(cloneDeep(defaultUserForm));
const isBatchFormChange = ref(false);
const handleBatchFormChange = () => {
  isBatchFormChange.value = true;
}
const userFormValidate = (cb: () => Promise<any>) => {
  userFormRef.value?.validate(err => {
    if (err) {
      return
    }
    batchFormRef.value?.formValidate(async (list: any) => {
      userForm.value.list = [...list];
      // console.log(userForm.value)
      await cb();
    })
  })
}
const resetUserForm = () => {
  userForm.value.list = [];
  userFormRef.value?.restoreValidation();
}
const cancelCreate = () => {
  showModal.value = false;
  resetUserForm();
}
const createUser = async (isContinue?: boolean) => {
  const params: ICreateUserParams = {
    userInfoList: userForm.value.list,
    userRoleIdList: userForm.value.userGroup,
  };
  const res = await userApi.batchCreateUser(params);
  if (res.errorEmails !== null) {
    const errData: Record<string, Record<string, string>> = {};
    Object.keys(res.errorEmails).forEach((key) => {
      const filedIndex = userForm.value.list.findIndex((e) => e.email === key);
      if (filedIndex > -1) {
        errData[`list[${filedIndex}].email`] = {
          status: 'error',
          message: t('system.user.createUserEmailExist'),
        };
      }
    });
  } else {
    window.$message.success(t('system.user.addUserSuccess'));
    if (!isContinue) {
      cancelCreate();
    }
    emit('load');
  }
}
const handleSaveUser = () => {
  if (userFormMode === 'create') {
    userFormValidate(createUser)
  }
}
const init = async () => {
  userGroupOptions.value = await userApi.getSystemRoles();
  if (userGroupOptions.value.length) {
    userForm.value.userGroup = userGroupOptions.value.filter((e: ISystemRole) => e.selected).map((e: ISystemRole) => e.id);
  }
}
const handleClose = () => {
  if (isBatchFormChange.value) {
    window.$dialog.warning({
      title: t('common.tip'),
      content: t('system.user.closeTip'),
      positiveText: t('common.close'),
      maskClosable: false,
      onPositiveClick: () => {
        cancelCreate()
      }
    })
  } else {
    cancelCreate()
  }
}
watch(() => showModal.value, (newValue) => {
  if (newValue) {
    init()
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog"
           :mask-closable="false" :closable="false"
           :title="userFormMode === 'create' ? $t('system.user.createUserModalTitle') : $t('system.user.editUserModalTitle')"
           :style="{ width:  '30%' }">
    <n-alert :show-icon="false" class="mb-[16px]">
      {{ $t('system.user.createUserTip') }}
    </n-alert>
    <div>
      <n-form v-if="showModal" ref="userFormRef" :model="userForm">
        <batch-form ref="batchFormRef"
                    :models="batchFormModels"
                    :form-mode="userFormMode"
                    :default-vals="userForm.list"
                    add-text="system.user.addUser"
                    max-height="250px"
                    @change="handleBatchFormChange"/>
        <n-form-item :label="$t('system.user.createUserUserGroup')" path="userGroup"
                     :rule="[{ required: true, message: t('system.user.createUserUserGroupNotNull') }]">
          <n-select v-model:value="userForm.userGroup" :options="userGroupOptions"
                    multiple :placeholder="$t('system.user.createUserUserGroupPlaceholder')"
                    label-field="name" value-field="id"
          ></n-select>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-flex>
        <n-button secondary @click="handleClose">{{ $t('system.user.editUserModalCancelCreate') }}</n-button>
        <n-button v-if="userFormMode === 'create'" secondary>{{
            $t('system.user.editUserModalSaveAndContinue')
          }}
        </n-button>
        <n-button type="primary" @click="handleSaveUser">
          {{
            $t(userFormMode === 'create' ? 'system.user.editUserModalCreateUser' : 'system.user.editUserModalEditUser')
          }}
        </n-button>
      </n-flex>
    </template>
  </n-modal>
</template>

<style scoped>

</style>