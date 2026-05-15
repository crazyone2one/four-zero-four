<script setup lang="ts">
import {toRefsPreferencesStore} from "/@/stores";
import {ButtonAnimation} from "/@/components/button-animation";

const {sidebarMenu} = toRefsPreferencesStore()

</script>

<template>
  <div class="flex cursor-pointer items-center hover:bg-gray-185" :class="
      sidebarMenu.collapsed
        ? 'mx-2 rounded-naive px-2 py-1.5'
        : 'mx-4 rounded-xl bg-neutral-150 py-3.5 pr-2.5 pl-3.5 dark:bg-neutral-800'
    ">
    <user-dropdown placement="right-end"
                   :disabled="!sidebarMenu.collapsed">
      <div
          class="grid aspect-square place-items-center overflow-hidden rounded-full transition-[margin]"
          :class="{
          'mr-2': !sidebarMenu.collapsed,
        }"
      >
        <div
            class="flex items-center justify-center overflow-hidden transition-[height,width]"
            :class="sidebarMenu.collapsed ? 'w-8' : 'w-10'"
        >
          <user-avatar/>
        </div>
      </div>
    </user-dropdown>
    <Transition
        type="transition"
        enter-active-class="transition-[grid-template-columns] duration-150"
        leave-active-class="transition-[grid-template-columns] duration-150"
        enter-from-class="grid-cols-[0fr]"
        leave-to-class="grid-cols-[0fr]"
        enter-to-class="grid-cols-[1fr]"
        leave-from-class="grid-cols-[1fr]"
    >
      <div
          class="flex flex-1 items-center justify-between overflow-hidden"
          v-show="!sidebarMenu.collapsed"
      >
        <div class="flex flex-col gap-y-px overflow-hidden">
          <span class="truncate text-sm">
            FzF
          </span>
          <span class="truncate text-xs text-gray-450 dark:text-gray-500">
            这里或许可以写点什么
          </span>
        </div>

        <UserDropdown placement="top">
          <button-animation animation="rotate" title="设置">
            <span class="i-mage:settings text-gray-500 dark:text-gray-450"/>
          </button-animation>
        </UserDropdown>
      </div>
    </Transition>
  </div>
</template>

<style scoped>

</style>