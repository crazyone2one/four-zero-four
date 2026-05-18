<script setup lang="ts">
defineProps({
  size: { type: Number, default: 12 },
  maxDistance: { type: Number, default: 5 },
  pupilColor: { type: String, default: 'black' }
})
</script>

<template>
  <div
      class="pupil"
      :style="{
      width: size + 'px',
      height: size + 'px',
      borderRadius: '50%',
      backgroundColor: pupilColor,
      willChange: 'transform'
    }"
      :data-max-distance="maxDistance"
  />
</template>

<style scoped>

</style>