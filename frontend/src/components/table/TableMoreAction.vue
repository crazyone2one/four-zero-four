<script setup lang="ts">
import type {DropdownOption} from "naive-ui";

const props = defineProps<{
  list: DropdownOption[];
  trigger?: 'hover' | 'click' | 'focus' | 'manual';
}>();
const emit = defineEmits(['select', 'close', 'open']);
const show = ref(false);
const handleSelect = (key: string) => {
  emit('select', key)
}
const renderLabel = (option: DropdownOption) => {
  return h('div', {class: option.key === 'delete' ? 'text-red hover:text-red-500' : ''}, {default: () => option.label})
}
watch(
    () => show.value,
    (val) => {
      if (val) {
        emit('open');
      }
    }
);
</script>

<template>
  <n-dropdown v-model:show="show" :trigger="props.trigger || 'click'" :options="list"
              :render-label="renderLabel"
              @select="handleSelect">
    <n-button text @click="show = !show">
      <template #icon>
        <div class="i-mage:dots-horizontal-circle"/>
      </template>
    </n-button>
  </n-dropdown>
</template>

<style scoped>

</style>