<script setup lang="ts">
import type {InputInst} from "naive-ui";

defineOptions({name: 'ShowOrEdit'});

interface Props {
  value: string
  // onUpdateValue?: (v: string) => void
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update', value: string): void
}>()
const isEdit = ref(false)
const inputRef = ref<InputInst | null>(null)
const inputValue = ref(props.value)
const handleOnClick = () => {
  isEdit.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}
const handleUpdate = (v: string) => {
  inputValue.value = v
}
const handleChange = () => {
  emit('update', inputValue.value)
  isEdit.value = false
}
</script>

<template>
  <div style="min-height: 22px" @click="handleOnClick">
    <n-input v-if="isEdit" ref="inputRef" v-model:value="inputValue"
             @update:value="handleUpdate"
             @blur="handleChange"/>
    <span v-else>{{ props.value }}</span>
  </div>
</template>

<style scoped>

</style>