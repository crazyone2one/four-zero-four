<script setup lang="ts">
defineProps({
  size: {type: Number, default: 48},
  pupilSize: {type: Number, default: 16},
  maxDistance: {type: Number, default: 10},
  eyeColor: {type: String, default: 'white'},
  pupilColor: {type: String, default: 'black'}
})
</script>

<template>
  <div class="eyeball"
       :style="{
      width: size + 'px',
      height: size + 'px',
      borderRadius: '50%',
      backgroundColor: eyeColor,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      overflow: 'hidden',
      willChange: 'height'
    }"
       :data-max-distance="maxDistance">
    <div class="eyeball-pupil"
         :style="{
        width: pupilSize + 'px',
        height: pupilSize + 'px',
        borderRadius: '50%',
        backgroundColor: pupilColor,
        willChange: 'transform'
      }">

    </div>
  </div>
</template>

<style scoped>

</style>