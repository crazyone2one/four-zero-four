<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import type {SelectOption} from "naive-ui";

type ICronSelectorProps = {
  class?: string;
  disabled?: boolean;
  size?: 'tiny' | 'small' | 'medium' | 'large'
}
const cron = defineModel<string>('modelValue', {required: true,});
const loading = defineModel<boolean>('loading', {required: false,});
const props = withDefaults(defineProps<ICronSelectorProps>(), {size: 'small'});
const emit = defineEmits<{ (e: 'changeCron', value: string): void; }>();
const {t} = useI18n()
const options: Array<SelectOption> = [
  {label: t('ms.cron.select.timeTaskHour'), value: '0 0 0/1 * * ?'},
  {label: t('ms.cron.select.timeTaskSixHour'), value: '0 0 0/6 * * ?'},
  {label: t('ms.cron.select.timeTaskTwelveHour'), value: '0 0 0/12 * * ?'},
  {label: t('ms.cron.select.timeTaskDay'), value: '0 0 0 * * ?'},
]
// const renderLabel = (option: SelectOption) => {
//   return h('div', {class: 'flex items-center'}, {
//     default: () => [
//       h('span', {}, {default: () => option.value}),
//       h('span', {class: 'ml-[4px] text-red'}, {default: () => option.label})
//     ]
//   })
// }

const handleUpdateValue = (v: string) => {
  emit('changeCron', v)
}
</script>

<template>
  <n-select v-model:value="cron" :options="options"
            :loading="loading"
            :disabled="props.disabled"
            :class="props.class"
            :placeholder="t('ms.cron.select.placeholder')"
            :size="props.size"
            filterable
            tag
            @update:value="handleUpdateValue"/>
</template>

<style scoped>

</style>