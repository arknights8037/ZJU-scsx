<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="24" cy="24" r="20" stroke="rgba(255,255,255,0.2)" stroke-width="1.5" />
            <path d="M24 10 L30 20 L24 18 L18 20 Z" fill="rgba(255,255,255,0.5)" />
            <rect x="20" y="20" width="8" height="16" rx="2" fill="rgba(255,255,255,0.35)" />
            <circle cx="24" cy="28" r="2" fill="#fff" />
          </svg>
        </div>
        <h1>智慧社区商城</h1>
        <p class="brand-desc">便捷的社区生活服务平台<br/>足不出户，乐享生活</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>社区优选商品
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>便捷在线购物
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>快速配送到家
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <span>© 2025 SmartCommunity Mall</span>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-main">
      <div class="login-form-wrap">
        <div class="form-header">
          <h2>{{ activeTab === 'login' ? '欢迎回来' : '创建账号' }}</h2>
          <p>{{ activeTab === 'login' ? '登录您的账号继续购物' : '注册新账号开始购物' }}</p>
        </div>

        <!-- Tab 切换 -->
        <div class="tab-switch">
          <button
            :class="['tab-btn', { active: activeTab === 'login' }]"
            @click="activeTab = 'login'"
          >登录</button>
          <button
            :class="['tab-btn', { active: activeTab === 'register' }]"
            @click="activeTab = 'register'"
          >注册</button>
        </div>

        <!-- 登录表单 -->
        <el-form v-if="activeTab === 'login'" :model="loginForm" label-width="0" class="auth-form" @keyup.enter="doLogin">
          <div class="input-group">
            <label class="input-label">手机号</label>
            <el-input v-model="loginForm.phone" placeholder="请输入手机号" size="large" class="custom-input">
              <template #prefix><el-icon><Phone /></el-icon></template>
            </el-input>
          </div>
          <div class="input-group">
            <label class="input-label">密码</label>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" size="large" show-password class="custom-input">
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </div>
          <el-button type="primary" size="large" class="submit-btn" @click="doLogin" :loading="loading" round>
            登 录
          </el-button>
        </el-form>

        <!-- 注册表单 -->
        <el-form v-else :model="registerForm" label-width="0" class="auth-form">
          <div class="input-group">
            <label class="input-label">手机号</label>
            <el-input v-model="registerForm.phone" placeholder="请输入手机号" size="large" class="custom-input">
              <template #prefix><el-icon><Phone /></el-icon></template>
            </el-input>
          </div>
          <div class="input-group">
            <label class="input-label">用户名</label>
            <el-input v-model="registerForm.userName" placeholder="请输入用户名" size="large" class="custom-input">
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </div>
          <div class="input-group">
            <label class="input-label">密码</label>
            <el-input v-model="registerForm.userPassword" type="password" placeholder="请输入密码" size="large" show-password class="custom-input">
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </div>
          <el-button type="primary" size="large" class="submit-btn" @click="doRegister" :loading="regLoading" round>
            注 册
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api'
import { ElMessage } from 'element-plus'
import { Phone, Lock, User } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const regLoading = ref(false)
const activeTab = ref('login')
const loginForm = ref({ phone: '', password: '' })
const registerForm = ref({ phone: '', userName: '', userPassword: '' })

async function doLogin() {
  if (!loginForm.value.phone || !loginForm.value.password) {
    ElMessage.warning('请输入手机号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm.value.phone, loginForm.value.password)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('phone', loginForm.value.phone)
    router.push('/')
  } catch (e) {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

async function doRegister() {
  if (!registerForm.value.phone || !registerForm.value.userName || !registerForm.value.userPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }
  regLoading.value = true
  try {
    await register(registerForm.value)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.value.phone = registerForm.value.phone
  } catch (e) {
    /* handled by interceptor */
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  overflow: hidden;
}

/* ===== 左侧品牌区 ===== */
.login-brand {
  flex: 0 0 44%;
  background: linear-gradient(135deg, #0c3483 0%, #0a5c8a 40%, #1e7e5e 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px 48px;
  position: relative;
  overflow: hidden;
  color: #fff;
}

.login-brand::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.06) 0%, transparent 70%);
  top: -100px;
  right: -150px;
}

.login-brand::after {
  content: '';
  position: absolute;
  width: 350px;
  height: 350px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.04) 0%, transparent 70%);
  bottom: -80px;
  left: -80px;
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 420px;
}

.brand-icon {
  margin-bottom: 28px;
}

.brand-icon svg {
  width: 56px;
  height: 56px;
}

.login-brand h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 14px;
  color: #fff;
  letter-spacing: -0.5px;
}

.brand-desc {
  color: rgba(255,255,255,0.65);
  font-size: 16px;
  line-height: 1.7;
  margin: 0;
}

.brand-features {
  margin-top: 36px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: rgba(255,255,255,0.75);
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
  flex-shrink: 0;
}

.brand-footer {
  position: absolute;
  bottom: 28px;
  left: 48px;
  font-size: 12px;
  color: rgba(255,255,255,0.35);
}

/* ===== 右侧表单区 ===== */
.login-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg);
  padding: 40px;
}

.login-form-wrap {
  width: 400px;
  max-width: 100%;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 8px;
  color: var(--text-h);
}

.form-header p {
  font-size: 15px;
  color: var(--text);
  margin: 0;
}

/* ===== Tab 切换 ===== */
.tab-switch {
  display: flex;
  background: var(--bg-secondary, #f1f3f5);
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 24px;
}

.tab-btn {
  flex: 1;
  border: none;
  background: transparent;
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
  cursor: pointer;
  transition: all 0.25s;
  font-family: inherit;
}

.tab-btn.active {
  background: #fff;
  color: var(--text-h);
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

/* ===== 表单 ===== */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.input-group {
  margin-bottom: 8px;
}

.input-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-h);
  margin-bottom: 6px;
  letter-spacing: 0.3px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px var(--border) inset;
  transition: box-shadow 0.25s;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--accent-border) inset;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--accent-border) inset;
}

.submit-btn {
  width: 100%;
  margin-top: 16px;
  height: 46px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .login-brand {
    flex: 0 0 40%;
    padding: 40px 32px;
  }

  .login-brand h1 {
    font-size: 26px;
  }
}

@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }

  .login-brand {
    flex: none;
    padding: 40px 24px 32px;
    text-align: center;
  }

  .login-brand h1 {
    font-size: 24px;
  }

  .brand-desc br {
    display: none;
  }

  .brand-features {
    display: none;
  }

  .brand-footer {
    display: none;
  }

  .login-main {
    padding: 28px 20px;
  }
}
</style>
