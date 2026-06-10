<script setup lang="ts">
import {useI18n} from "/@/composables/useI18n.ts";
import {useRequest} from "alova/client";
import {projectApi} from "/@/api/modules/project-api.ts";
import type {IProjectParams} from "/@/typings/project.ts";
import UpdateDatabaseModal from "/@/views/setting/project/params/components/UpdateDatabaseModal.vue";

const active = defineModel('active', {default: false})
const props = defineProps<{ projectId: string; }>();
const {t} = useI18n();
const currentId = ref('');
const addVisible = ref(false);
const paramList = ref<Array<IProjectParams>>([])
const {send: fetchParams} = useRequest(id => projectApi.getProjectParamsByProjectId(id), {immediate: false})
const fetchData = () => {
  fetchParams(props.projectId).then(res => {
    paramList.value = res
  })
}
const handleAdd = () => {
  currentId.value = '';
  addVisible.value = true
}

const dataSourceParams = computed(() => {
  return paramList.value.filter(item => item.paramType == 'datasource')[0];
})
const hostParams = computed(() => {
  return paramList.value.filter(item => item.paramType == 'host')[0];
})
const handleEdit = (type: string) => {
  if (type == 'database') {
    currentId.value = dataSourceParams.value.id;
    addVisible.value = true
  } else {
    console.log('other')
  }
}
watch(() => active.value, (newValue) => {
  if (newValue) {
    fetchData()
  }
})
</script>

<template>
  <n-drawer v-model:show="active" :width="502" :mask-closable="false">
    <n-drawer-content :title="$t('project.environmental.title')" closable>
      <n-tabs :bar-width="28" type="line" class="custom-tabs">
        <n-tab-pane name="database" :tab="t('project.environmental.database.title')">
          <div v-if="paramList.length==0" class="flex w-full items-center justify-center text-grey">
            <span>{{ t('common.tableNoData') }}</span>
            <n-button type="primary" text class="ml-[8px]" @click="handleAdd">
              {{ t('project.environmental.database.addDatabase') }}
            </n-button>
          </div>
          <div v-else class="mt-2">
            <n-flex vertical>
              <div>
                <n-descriptions bordered label-placement="left" :column="1">
                  <n-descriptions-item>
                    <template #label>
                      {{ $t('project.environmental.database.name') }}
                    </template>
                    {{ dataSourceParams.parameters.dataSource }}
                  </n-descriptions-item>
                  <n-descriptions-item :label="$t('project.environmental.database.url')">
                    {{ dataSourceParams.parameters.dbUrl }}
                  </n-descriptions-item>
                  <n-descriptions-item :label="$t('project.environmental.database.username')">
                    {{ dataSourceParams.parameters.username }}
                  </n-descriptions-item>
                  <n-descriptions-item :label="$t('project.environmental.database.password')">
                    {{ dataSourceParams.parameters.password }}
                  </n-descriptions-item>
                </n-descriptions>
              </div>
              <div class="mt-2">
                <n-button type="primary" text @click="handleEdit('database')">{{ $t('common.edit') }}</n-button>
              </div>
            </n-flex>
          </div>
        </n-tab-pane>
        <n-tab-pane name="host" :tab="t('project.environmental.host.config')">
          <div v-if="!hostParams" class="flex w-full items-center justify-center text-grey">
            <span>{{ t('common.tableNoData') }}</span>
            <n-button type="primary" text class="ml-[8px]" @click="handleAdd">
              {{ t('common.add') }}
            </n-button>
          </div>
        </n-tab-pane>
        <!--        <n-tab-pane name="jay chou" tab="周杰伦">-->
        <!--          七里香-->
        <!--        </n-tab-pane>-->
      </n-tabs>
    </n-drawer-content>
  </n-drawer>
  <update-database-modal v-model:show-modal="addVisible" :current-id="currentId" :project-id="props.projectId"
                         :ds="dataSourceParams" @cancel="fetchData"/>
</template>

<style scoped>

</style>