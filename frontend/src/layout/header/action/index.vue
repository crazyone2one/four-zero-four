<script setup lang="ts">

import PreferencesDrawer from "/@/layout/header/action/PreferencesDrawer.vue";
import {LOCALE_OPTIONS} from "/@/i18n";
import useLocale from "/@/i18n/use-locale.ts";
import type {DropdownOption} from "naive-ui";
import {userApi} from "/@/api/modules/user-api.ts";

const {changeLocale, currentLocale} = useLocale();
const renderDropdownIcon = (option: DropdownOption) => {
  if (currentLocale.value === option.key) {
    return h("div", {class: 'i-mage:check-circle text-green'})
  }
}
const handleSwitchLanguage = async (key: string) => {
  await userApi.updateLanguage({language: key});
  await changeLocale(key as LocaleType);
}
</script>

<template>
  <div class="flex items-center">
    <button-animation :animation="false"
                      tag="a"
                      href="https://github.com/crazyone2one/four-zero-four"
                      target="_blank"
                      rel="noopener noreferrer"
    >
      <span class="i-mage:github"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:notification-bell-snooze"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:dashboard-bar-notification"/>
    </button-animation>
    <button-animation :animation="false">
      <span class="i-mage:gem-stone"/>
    </button-animation>
    <n-dropdown trigger="click" :options="[...LOCALE_OPTIONS]"
                :render-icon="renderDropdownIcon"
                @select="handleSwitchLanguage">
      <button-animation :animation="false" :title="$t('settings.language')">
        <span class="i-fzf:translate"/>
      </button-animation>
    </n-dropdown>

    <preferences-drawer/>
  </div>
</template>

<style scoped>

</style>