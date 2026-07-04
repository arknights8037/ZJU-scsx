<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="6" y="6" width="16" height="16" rx="3" fill="rgba(255,255,255,0.3)" />
            <rect x="26" y="6" width="16" height="16" rx="3" fill="rgba(255,255,255,0.5)" />
            <rect x="6" y="26" width="16" height="16" rx="3" fill="rgba(255,255,255,0.5)" />
            <rect x="26" y="26" width="16" height="16" rx="3" fill="rgba(255,255,255,0.2)" />
          </svg>
        </div>
        <h1>智慧社区管理平台</h1>
        <p class="brand-desc">一站式社区运营管理解决方案<br/>高效 · 智能 · 便捷</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-dot"></span>数据可视化大屏
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>智能物业管理
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>精细化用户运营
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <span>© 2025 SmartCommunity</span>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-main">
      <div class="login-form-wrap">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请使用管理员账号登录系统</p>
        </div>

        <el-form :model="form" label-width="0" class="login-form" @keyup.enter="doLogin">
          <div class="input-group">
            <label class="input-label">手机号</label>
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="input-group">
            <label class="input-label">密码</label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              class="custom-input"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            @click="doLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const form = ref({ phone: '', password: '' })

async function doLogin() {
  if (!form.value.phone || !form.value.password) {
    ElMessage.warning('请输入手机号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value.phone, form.value.password)
    localStorage.setItem('admin_token', res.data.token)
    router.push('/dashboard')
  } catch (e) {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #f9fcff 0%, #eaf4fd 52%, #f5f2fa 100%);
}

/* ===== 左侧品牌区 ===== */
.login-brand {
  flex: 0 0 44%;
  background:
    linear-gradient(115deg, rgba(8, 35, 59, 0.90), rgba(15, 108, 189, 0.64), rgba(0, 120, 128, 0.34)),
    url('/smartcommunitybackground.webp') center/cover;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px 48px;
  position: relative;
  overflow: hidden;
  color: #fff;
  box-shadow: 12px 0 36px rgba(37, 52, 68, 0.12);
}

.login-brand::before {
  content: none;
}

.login-brand::after {
  content: none;
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 420px;
}

.brand-icon {
  margin-bottom: 28px;
  width: 72px;
  height: 72px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.30);
  border-radius: var(--radius);
  box-shadow: 0 14px 30px rgba(4, 23, 39, 0.16), 0 1px 0 rgba(255, 255, 255, 0.22) inset;
  backdrop-filter: blur(18px);
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
  letter-spacing: 0;
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
  background: #7dc3ff;
  box-shadow: 0 0 0 4px rgba(125, 195, 255, 0.16);
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
  background: transparent;
  padding: 40px;
}

.login-form-wrap {
  width: 400px;
  max-width: 100%;
  padding: 32px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.82);
  border-radius: var(--radius);
  box-shadow: var(--shadow-lg);
  backdrop-filter: blur(26px) saturate(1.25);
  -webkit-backdrop-filter: blur(26px) saturate(1.25);
}

.form-header {
  margin-bottom: 36px;
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

/* ===== 表单 ===== */
.login-form {
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
  letter-spacing: 0;
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

.login-btn {
  width: 100%;
  margin-top: 16px;
  height: 46px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0;
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

  .login-form-wrap {
    padding: 24px 20px;
  }
}
</style>
