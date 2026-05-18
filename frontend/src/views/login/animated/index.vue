<script setup lang="ts">
import styles from './index.module.css';
import AnimatedCharacters from '/@/components/animated-characters/index.vue'

const isTyping = ref(false)
const showPassword = ref(false)

const model = ref({
  username: '',
  password: '',
})
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
        <n-form ref="formRef"
                :model="model" label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging"
                size="large"
                :class="styles.form">
          <n-form-item label="账号" :content-class="styles.fieldLabel">
            <n-input v-model:value="model.username" type="text" placeholder="请输入用户名"
                     @blur="()=>isTyping=false"
                     @focus="()=>isTyping=true">
            </n-input>
          </n-form-item>
          <n-form-item label="密码">
            <n-input v-model:value="model.password" :type="showPassword ? 'text' : 'password'" placeholder="请输入密码">
              <template #suffix>
                <n-icon @click="showPassword = !showPassword">
                  <div :class="showPassword?'i-mage:eye':'i-mage:eye-off'"/>
                  <!--                  <span class="i-mage:eye-off" v-else/>-->
                </n-icon>
              </template>
            </n-input>
          </n-form-item>
          <n-form-item style="{margin-bottom: 0}">
            <n-button type="primary" block>{{ isTyping ? '登录中...' : '登录' }}</n-button>
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