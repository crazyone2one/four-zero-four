<script setup lang="ts">
import Logo from './logo/index.vue'
import Action from './action/index.vue';
import {useAppStore, useUserStore} from "/@/stores";
import AddProjectModal from "/@/views/setting/project/components/AddProjectModal.vue";
import {projectApi} from "/@/api/modules/project-api.ts";

const appStore = useAppStore()
const userStore = useUserStore()
const projectVisible = ref(false)

const handleSwitch = async (value: string) => {
  try {
    appStore.showLoading();
    await projectApi.switchProject({projectId: value, userId: userStore.user.id || ''})
    await userStore.checkIsLogin(true)
  } catch (e) {
    console.log(e)
  } finally {
    appStore.hideLoading()
  }
}
watch(() => appStore.appStore.currentOrgId, async () => {
  await appStore.initProjectList()
}, {immediate: true})
</script>

<template>
  <div class="flex items-center wh-full">
    <logo/>
    <div class=" flex flex-1 items-center  px-4 py-3.5">
      <div class="flex h-9 flex-1 items-center">
        <div class="w-[200px]">
          <n-select
              v-model:value="appStore.appStore.currentProjectId"
              :options="appStore.appStore.projectList"
              label-field="name" value-field="id"
              @update-value="handleSwitch">
            <template #action>
              <n-button text class="mb-[4px] h-[28px] w-full justify-start pl-[7px] pr-0"
                        @click="projectVisible = true">
                <template #icon>
                  <n-icon>
                    <div class="i-mage:plus-circle"/>
                  </n-icon>
                </template>
                {{ $t('settings.navbar.createProject') }}
              </n-button>
            </template>
          </n-select>
        </div>

      </div>
      <Action class="gap-x-3 pl-4"/>
    </div>
    <add-project-modal v-model:show-modal="projectVisible" @cancel="projectVisible=false"/>
  </div>
</template>

<style scoped>

</style>