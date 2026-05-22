<script setup lang="ts">
import initOptionsFunc, {UserRequestTypeEnum, type UserRequestTypeEnumType} from "/@/components/user-selector/utils.ts";
import type {IUserOption} from "/@/typings/user.ts";
import type {SelectOption} from "naive-ui";

defineOptions({name: 'UserSelector'});

export interface IUserSelectorOption {
  id: string;
  name: string;
  email: string;
  disabled?: boolean;

  [key: string]: string | number | boolean | undefined;
}

const props = withDefaults(
    defineProps<{
      disabled?: boolean; // 是否禁用
      disabledKey?: string; // 禁用的key
      valueKey?: string; // value的key
      placeholder?: string;
      firstLabelKey?: string; // 首要的的字段key
      secondLabelKey?: string; // 次要的字段key
      loadOptionParams?: Record<string, any>; // 加载选项的参数
      type?: UserRequestTypeEnumType; // 加载选项的类型
      atLeastOne?: boolean; // 是否至少选择一个
    }>(),
    {
      disabled: false,
      disabledKey: 'disabled',
      valueKey: 'id',
      firstLabelKey: 'name',
      secondLabelKey: 'email',
      type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
      atLeastOne: false,
    }
);
const currentValue = defineModel<(string | number)[]>('modelValue', {default: []});
const options = ref<Array<IUserOption>>([])
const handleRemoteSearch = async (keyword: string) => {
  const list = (await initOptionsFunc(props.type, {keyword})) || [];
  options.value = list as Array<IUserOption>;
}
const renderLabel = (option: SelectOption) => {
  return h('div', {}, {default: () => `${option.name}（${option.email}）`})
}
onMounted(async () => {
  const key = '';
  await handleRemoteSearch(key);
})
</script>

<template>
  <n-select v-model:value="currentValue"
            :options=" options"
            multiple filterable remote
            value-field="id"
            label-field="name"
            :placeholder="$t(props.placeholder || 'common.pleaseSelectMember')"
            :render-label="renderLabel"
            @search="handleRemoteSearch"/>
</template>

<style scoped>

</style>