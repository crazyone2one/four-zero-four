<script setup lang="ts">
import styles from './index.module.css';
import AnimatedCharacters from '/@/components/animated-characters/index.vue'
import {useForm} from "alova/client";
import {authApi} from "/@/api/modules/auth";
import {setToken} from "/@/utils/storage.ts";
import type {FormInst} from "naive-ui";
import router from "/@/router";
import {useAppStore, useUserStore} from "/@/stores";

const {appStore} = useAppStore()
const isTyping = ref(false)
const showPassword = ref(false)
const loginFormRef = ref<FormInst | null>(null)
const {form: model, loading, send} = useForm(formData => authApi.login(formData), {
  initialForm: {
    username: '',
    password: ''
  }
})
const toLayout = async () => {
  const {redirect, ...othersQuery} = router.currentRoute.value.query;
  await router.push({
    name: redirect ? redirect as string : 'Dashboard',
    query: {
      ...othersQuery,
    },
  });
}
const handleLogin = () => {
  loginFormRef.value?.validate(err => {
    if (!err) {
      send().then(async res => {
        const {accessToken, refreshToken, user} = res
        setToken(accessToken, refreshToken)
        useUserStore().user = user
        appStore.currentOrgId = user.lastOrganizationId || '';
        appStore.currentProjectId = user.lastProjectId || '';
        await toLayout()
      })
    }
  })
}
</script>

<template>
  <div :class="styles.container">
    <!--  左侧：品牌视觉区-->
    <div :class="styles.leftPanel">
      <div :class="styles.leftTop">
        <div :class="styles.brandMark">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <rect
                width="28"
                height="28"
                rx="7"
                fill="white"
                fill-opacity="0.15"
            />
            <path
                d="M7 14L12 9L17 14L12 19L7 14Z"
                fill="white"
                fill-opacity="0.9"
            />
            <path
                d="M13 14L18 9L21 12V16L18 19L13 14Z"
                fill="white"
                fill-opacity="0.5"
            />
          </svg>
        </div>
        <span :class="styles.brandName">FzF</span>
      </div>
      <div :class="styles.charactersArea">
        <animated-characters :is-typing="isTyping" :show-password="showPassword"
                             :password-length="model.password.length"/>
      </div>
      <div :class="styles.leftFooter">
        <a href="#">帮助中心</a>
        <a href="#">隐私政策</a>
      </div>

      <div :class="styles.decorBlur1"/>
      <div :class="styles.decorBlur2"/>
      <div :class="styles.decorGrid"/>
    </div>
    <!--    右侧：登录表单-->
    <div :class="styles.rightPanel">
      <div :class="styles.formWrapper">
        <div :class="styles.mobileLogo">
          <div :class="styles.mobileLogoIcon">
            <svg width="20" height="20" viewBox="0 0 28 28" fill="none">
              <path
                  d="M7 14L12 9L17 14L12 19L7 14Z"
                  fill="#1E40AF"
                  fill-opacity="0.9"
              />
              <path
                  d="M13 14L18 9L21 12V16L18 19L13 14Z"
                  fill="#3B82F6"
                  fill-opacity="0.7"
              />
            </svg>
          </div>
          <span>Fzf 平台</span>
        </div>
        <div :class="styles.formHeader">
          <h1 :class="styles.formHeader">登录到工作台</h1>
        </div>
        <n-form ref="loginFormRef"
                :model="model"
                label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging"
                size="large"
                :class="styles.form">
          <n-form-item :class="styles.fieldLabel" path="username"
                       :rule="[{required: true, message: $t('login.form.userName.errMsg'), trigger: ['input']}]">
            <n-input v-model:value="model.username" type="text" :placeholder="$t('login.form.userName.placeholder')"
                     @blur="()=>isTyping=false"
                     @focus="()=>isTyping=true">
              <template #prefix>
                <n-icon>
                  <div class="i-mage:user"/>
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="password"
                       :rule="[{required: true, message: $t('login.form.password.errMsg'), trigger: ['input']}]">
            <n-input v-model:value="model.password" :type="showPassword ? 'text' : 'password'"
                     :placeholder="$t('login.form.password.placeholder')">
              <template #prefix>
                <n-icon>
                  <div class="i-mage:lock"/>
                </n-icon>
              </template>
              <template #suffix>
                <n-icon @click="showPassword = !showPassword">
                  <div :class="showPassword?'i-mage:eye':'i-mage:eye-off'"/>
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item style="{margin-bottom: 0}">
            <n-button type="primary" block :loading="loading" :class="styles.submitBtn"
                      @click="handleLogin">
              {{ loading ? $t('login.form.loading') : $t('login.form.login') }}
            </n-button>
          </n-form-item>
        </n-form>
        <div :class="styles.signupRow">
          暂无账号？{' '}
          <a href="#" :class="styles.signupLink">
            联系管理员申请开通
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>