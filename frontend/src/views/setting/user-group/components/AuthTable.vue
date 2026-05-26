<script setup lang="ts">
import type {IAuthTableItem, IBaseUserGroup, ISavePermissions, IUserGroupAuthSetting} from "/@/typings/user-group.ts";
import {type DataTableColumns, NCheckbox, NCheckboxGroup} from "naive-ui";
import {useI18n} from "/@/composables/useI18n.ts";
import {useRequest} from "alova/client";
import {userGroupApi} from "/@/api/modules/user-group.ts";

const {showBottom = true, disabled = false, current = {id: ''}} = defineProps<{
  current: IBaseUserGroup;
  savePermission?: string[];
  showBottom?: boolean;
  disabled?: boolean;
}>()
// const systemType = inject<AuthScopeEnumType>('systemType');
const {t} = useI18n()
const tableData = ref<IAuthTableItem[]>();
const allChecked = ref(false);
const allIndeterminate = ref(false);
const columns: DataTableColumns<IAuthTableItem> = [
  {title: t('system.userGroup.function'), key: 'ability', width: 100},
  {title: t('system.userGroup.operationObject'), key: 'operationObject', width: 150},
  {
    title: () => {
      return h('div', {class: 'flex w-full flex-row justify-between'}, {
        default: () => {
          return [
            h('div', {}, {default: () => t('system.userGroup.auth')}),
            h(NCheckbox, {
              checked: allChecked.value, indeterminate: allIndeterminate.value,
              disabled: systemAdminDisabled.value || disabled,
              class: 'mr-[7px]',
              onUpdateChecked: handleAllAuthChangeByCheckbox
            }, {})
          ];
        }
      })
    }, key: 'children', render(row, rowIndex) {
      return h("div", {class: 'flex flex-row items-center justify-between'}, {
        default: () => [
          h(NCheckboxGroup, {
            value: row.perChecked,
            onUpdateValue: (value, meta) => handleCellAuthChange(value, meta, row, rowIndex)
          }, {
            default: () => {
              return row.permissions?.map(item => {
                return h(NCheckbox, {
                  value: item.id,
                  checked: item.enable,
                  disabled: systemAdminDisabled.value || disabled,
                }, {default: () => t(item.name)})
              })
            }
          }),
          h(NCheckbox, {
            class: 'mr-[7px]', checked: row.enable, indeterminate: row.indeterminate,
            disabled: systemAdminDisabled.value || disabled,
            onUpdateChecked: (value: boolean) => handleRowAuthChange(value, rowIndex)
          }, {})
        ]
      })
    }
  },
]
const canSave = ref(false);
const handleAllAuthChangeByCheckbox = () => {
  if (!tableData.value) return;
  allChecked.value = !allChecked.value;
  allIndeterminate.value = false;
  const tmpArr = tableData.value;
  tmpArr.forEach((item) => {
    item.enable = allChecked.value;
    item.indeterminate = false;
    item.perChecked = allChecked.value ? item.permissions?.map((ele) => ele.id) : [];
  });
  if (!canSave.value) canSave.value = true;
}
const handleRowAuthChange = (value: boolean, rowIndex: number) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  tmpArr[rowIndex].indeterminate = false;
  if (value) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].perChecked = tmpArr[rowIndex].permissions?.map((item) => item.id);
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].perChecked = [];
  }
  tableData.value = [...tmpArr];
  handleAllChange();
  if (!canSave.value) canSave.value = true;
};
const handleCellAuthChange = (_: Array<string | number>, meta: {
  actionType: 'check' | 'uncheck',
  value: string | number
}, record: IAuthTableItem, rowIndex: number) => {

  setAutoRead(record, meta.value as string);
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const length = tmpArr[rowIndex].permissions?.length || 0;
  if (record.perChecked?.length === length) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].indeterminate = false;
  } else if (record.perChecked?.length === 0) {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = false;
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = true;
  }
  handleAllChange();
};
const setAutoRead = (record: IAuthTableItem, currentValue: string) => {
  if (!record?.perChecked?.includes(currentValue)) {
    record?.perChecked?.push(currentValue);
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    const lastEditStr = currentValue.split('+')[1]; // 编辑类权限通过+号拼接
    const existRead = record?.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'READ'
    );
    const existCreate = record?.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'ADD'
    );
    if (!existRead && postStr !== 'READ') {
      record?.perChecked?.push(`${preStr}:READ`);
    }
    if (!existCreate && lastEditStr === 'IMPORT') {
      // 勾选导入时自动勾选新增和查询
      record?.perChecked?.push(`${preStr}:ADD`);
      record?.perChecked?.push(`${preStr}:READ+UPDATE`);
    }
  } else {
    // 删除权限值
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    if (postStr === 'READ') {
      // 当前是查询 那 移除所有相关的
      record.perChecked = record.perChecked.filter((item: string) => !item.includes(preStr));
    } else {
      record.perChecked.splice(record.perChecked.indexOf(currentValue), 1);
    }
  }
}
const {loading, send: fetchTableData} = useRequest(id => userGroupApi.getGlobalUSetting(id), {immediate: false});
const initData = (id: string) => {
  tableData.value = []
  fetchTableData(id).then(res => {
    tableData.value = transformData(res);
    handleAllChange(true);
  })
}
const transformData = (data: IUserGroupAuthSetting[]) => {
  const result: IAuthTableItem[] = [];
  data.forEach((item) => {
    result.push(...makeData(item));
  });
  return result;
}
const makeData = (item: IUserGroupAuthSetting) => {
  const result: IAuthTableItem[] = [];
  item.children?.forEach((child, index) => {
    const perChecked =
        child?.permissions?.reduce((acc: string[], cur) => {
          if (cur.enable) {
            acc.push(cur.id);
          }
          return acc;
        }, []) || [];
    const perCheckedLength = perChecked.length;
    let indeterminate = false;
    if (child?.permissions) {
      indeterminate = perCheckedLength > 0 && perCheckedLength < child?.permissions?.length;
    }
    result.push({
      id: child?.id,
      enable: child?.enable,
      permissions: child?.permissions,
      indeterminate,
      perChecked,
      ability: index === 0 ? item.name : undefined,
      operationObject: t(child.name),
      rowSpan: index === 0 ? item.children?.length || 1 : undefined,
    });
  });
  return result;
}
const handleAllChange = (isInit = false) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const {length: allLength} = tmpArr;
  const {length} = tmpArr.filter((item) => item.enable);
  if (length === allLength) {
    allChecked.value = true;
    allIndeterminate.value = false;
  } else if (length === 0) {
    allChecked.value = false;
    allIndeterminate.value = false;
  } else {
    allChecked.value = false;
    allIndeterminate.value = true;
  }
  if (!isInit && !canSave.value) canSave.value = true;
}
const handleReset = () => {
  if (current.id) {
    initData(current.id);
  }
};
const systemAdminDisabled = computed(() => {
  const adminArr = ['admin', 'org_admin', 'project_admin'];
  const {id} = current;
  if (adminArr.includes(id)) {
    // 系统管理员,组织管理员，项目管理员都不可编辑
    return true;
  }

  return disabled;
});
const {send: saveUG} = useRequest(param => userGroupApi.saveGlobalUSetting(param), {immediate: false})
const handleSave = () => {
  if (!tableData.value) return;
  const permissions: ISavePermissions[] = [];
  const tmpArr = tableData.value;
  tmpArr.forEach((item) => {
    item.permissions?.forEach((ele) => {
      ele.enable = item.perChecked?.includes(ele.id) || false;
      permissions.push({
        id: ele.id,
        enable: ele.enable,
      });
    });
  });
  saveUG({userRoleId: current.id, permissions}).then(() => {
    canSave.value = false;
    window.$message.success(t('common.saveSuccess'));
    initData(current.id)
  });
}
watchEffect(() => {
  if (current.id) {
    initData(current.id);
  }
});
defineExpose({
  canSave,
  handleSave,
  handleReset,
});
</script>

<template>
  <div class="flex h-full flex-col gap-[16px] overflow-hidden">
    <div class="group-auth-table">
      <n-data-table :columns="columns" :data="tableData" :loading="loading"/>
    </div>
    <div v-if="showBottom && current.id !== 'admin' && !systemAdminDisabled" class="footer">
      <n-button secondary :disabled="!canSave" @click="handleReset">{{ t('system.userGroup.reset') }}</n-button>
      <n-button type="primary" :disabled="!canSave" @click="handleSave">{{ t('system.userGroup.save') }}</n-button>
    </div>
  </div>
</template>

<style scoped>
.group-auth-table {
  @apply flex-1 overflow-hidden;

  padding: 0 16px 16px;

  div,
  span {
    color: #e3e6f3;
  }

  :deep(.arco-table-container) {
    border-top: 1px solid #434552 !important;
    border-left: 1px solid #434552 !important;
  }

  :deep(.arco-table-th-title) {
    width: 100%;
  }

  :deep(.arco-table-th) {
    background-color: #2e313d;
    line-height: normal;
  }
}

:deep(.auth-type-class) {
  background-color: #2e313d;
}

.footer {
  display: flex;
  justify-content: flex-end;
  padding: 24px;

  box-shadow: 0 -1px 4px rgb(2 2 2 / 10%);
  gap: 16px;
}
</style>