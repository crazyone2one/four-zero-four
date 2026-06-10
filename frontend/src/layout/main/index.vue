<script setup lang="ts">
import {useInjection} from "/@/composables/useInjection.ts";
import {layoutInjectionKey} from "/@/injection";

const navigationTransitionName = ref('scale')
const {shouldRefreshRoute} = useInjection(layoutInjectionKey)

watch(
    () => shouldRefreshRoute.value,
    (shouldRefresh) => {
      if (shouldRefresh) {
        navigationTransitionName.value = 'shake'
        nextTick(() => {
          shouldRefreshRoute.value = false
        })
      }
    },
)
</script>

<template>
  <!--  <router-view v-if="navigationTransition.enable"-->
  <!--               v-slot="{ Component, route }">-->
  <!--    <Transition :type="navigationTransitionName === 'shake' ? 'animation' : 'transition'"-->
  <!--                :name="navigationTransitionName">-->
  <!--      <keep-alive>-->
  <!--        <component :is="Component"-->
  <!--                   v-if="isMounted && !shouldRefreshRoute"-->
  <!--                   :key="route.path + JSON.stringify(route.query)"/>-->
  <!--      </keep-alive>-->
  <!--    </Transition>-->
  <!--  </router-view>-->
  <router-view/>
</template>

<style scoped>

</style>