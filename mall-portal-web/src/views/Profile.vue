<template>
  <div class="profile-page" v-loading="loading">
    <!-- 左侧账号摘要：让用户快速确认当前登录的是哪个账号 -->
    <aside class="profile-summary">
      <el-avatar :size="88" :src="profileForm.avatar || undefined" class="profile-avatar">
        {{ avatarText }}
      </el-avatar>
      <h2>{{ profileForm.userName || '社区居民' }}</h2>
      <p>{{ profileForm.phone || '未绑定手机号' }}</p>

      <div class="summary-list">
        <div class="summary-item">
          <el-icon><Phone /></el-icon>
          <span>登录账号</span>
          <strong>{{ profileForm.phone || '--' }}</strong>
        </div>
        <div class="summary-item">
          <el-icon><Message /></el-icon>
          <span>联系邮箱</span>
          <strong>{{ profileForm.mail || '未填写' }}</strong>
        </div>
      </div>
    </aside>

    <section class="profile-main">
      <div class="page-head">
        <div>
          <h1>个人中心</h1>
          <p>维护居民基础资料和登录密码。</p>
        </div>
      </div>

      <!-- 登录信息来自后端 User 表，最近登录时间会在每次登录成功后更新 -->
      <div class="account-grid">
        <div class="account-item">
          <span>登录手机号</span>
          <strong>{{ profileForm.phone || '--' }}</strong>
        </div>
        <div class="account-item">
          <span>账号状态</span>
          <strong class="status-normal">{{ accountStatus }}</strong>
        </div>
        <div class="account-item">
          <span>最近登录</span>
          <strong>{{ formatDate(profileForm.lastLoginTime) }}</strong>
        </div>
        <div class="account-item">
          <span>注册时间</span>
          <strong>{{ formatDate(profileForm.createTime) }}</strong>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="基本资料" name="basic">
          <el-form label-position="top" class="profile-form">
            <div class="form-grid">
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" disabled>
                  <template #prefix><el-icon><Phone /></el-icon></template>
                </el-input>
                <p class="field-help">手机号是登录账号，暂不支持自行修改。</p>
              </el-form-item>

              <el-form-item label="用户名">
                <el-input v-model="profileForm.userName" maxlength="30" show-word-limit>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item label="性别">
                <el-segmented v-model="profileForm.sex" :options="sexOptions" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="profileForm.mail" placeholder="例如：name@example.com">
                  <template #prefix><el-icon><Message /></el-icon></template>
                </el-input>
              </el-form-item>
            </div>

            <el-form-item label="个人头像">
              <div class="avatar-upload-row">
                <el-upload
                  class="avatar-uploader"
                  accept=".jpg,.jpeg,.png,.webp,.gif"
                  :show-file-list="false"
                  :before-upload="beforeImageUpload"
                  :http-request="uploadAvatar"
                >
                  <div class="avatar-upload-box" v-loading="avatarUploading">
                    <img v-if="profileForm.avatar" :src="profileForm.avatar" alt="头像预览" />
                    <el-icon v-else><Plus /></el-icon>
                  </div>
                </el-upload>
                <div class="upload-help">
                  <strong>上传头像</strong>
                  <p>支持 JPG、PNG、WebP、GIF，大小不超过 5MB。</p>
                  <el-button v-if="profileForm.avatar" text type="danger" :icon="Delete" @click="profileForm.avatar = ''">
                    移除头像
                  </el-button>
                </div>
              </div>
            </el-form-item>

            <div class="form-actions">
              <el-button :icon="RefreshLeft" @click="loadProfile">恢复</el-button>
              <el-button type="primary" :icon="Check" :loading="saving" @click="saveProfile">
                保存资料
              </el-button>
            </div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="账户安全" name="security">
          <div class="security-intro">
            <span class="security-icon"><el-icon><Lock /></el-icon></span>
            <div>
              <h3>修改登录密码</h3>
              <p>新密码至少 6 位，修改成功后需要重新登录。</p>
            </div>
          </div>

          <el-form label-position="top" class="password-form" @keyup.enter="savePassword">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="current-password" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" />
            </el-form-item>
            <el-button type="primary" :icon="Lock" :loading="passwordSaving" @click="savePassword">
              修改密码
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Delete, Lock, Message, Phone, Plus, RefreshLeft, User } from '@element-plus/icons-vue'
import { changeUserPassword, getUserProfile, updateUserProfile, uploadUserImage } from '@/api'

const router = useRouter()
const activeTab = ref('basic')
const loading = ref(false)
const saving = ref(false)
const passwordSaving = ref(false)
const avatarUploading = ref(false)

// profileForm 对应后端 UserProfileForm；phone 只用于展示，不会提交修改。
const profileForm = reactive({
  phone: '',
  userName: '',
  sex: '',
  mail: '',
  avatar: '',
  userStatus: null,
  lastLoginTime: '',
  createTime: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const sexOptions = [
  { label: '未设置', value: '' },
  { label: '男', value: '男' },
  { label: '女', value: '女' }
]

const avatarText = computed(() => (profileForm.userName || profileForm.phone || '用').charAt(0))
const accountStatus = computed(() => profileForm.userStatus === 1 ? '正常' : '停用')

onMounted(() => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  loadProfile()
})

// 从后端重新读取资料，也用于“恢复”尚未保存的表单改动。
async function loadProfile() {
  loading.value = true
  try {
    const res = await getUserProfile()
    const user = res?.data || {}
    profileForm.phone = user.phone || ''
    profileForm.userName = user.userName || ''
    profileForm.sex = user.sex || ''
    profileForm.mail = user.mail || ''
    profileForm.avatar = user.avatar || ''
    profileForm.userStatus = user.userStatus
    profileForm.lastLoginTime = user.lastLoginTime || getTokenLoginTime()
    profileForm.createTime = user.createTime || ''
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  if (!profileForm.userName.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    const res = await updateUserProfile({
      userName: profileForm.userName.trim(),
      sex: profileForm.sex,
      mail: profileForm.mail.trim(),
      avatar: profileForm.avatar.trim()
    })
    localStorage.setItem('userName', res?.data?.userName || profileForm.userName.trim())
    localStorage.setItem('avatar', res?.data?.avatar || '')
    window.dispatchEvent(new Event('profile-updated'))
    ElMessage.success('个人资料已保存')
  } finally {
    saving.value = false
  }
}

// el-upload 的自定义上传函数：先上传文件，成功后把后端返回的 URL 放进个人资料表单。
async function uploadAvatar(options) {
  avatarUploading.value = true
  try {
    const res = await uploadUserImage(options.file)
    profileForm.avatar = res?.data?.url || ''
    ElMessage.success('头像上传成功，请保存资料')
  } finally {
    avatarUploading.value = false
  }
}

function beforeImageUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']
  if (!allowed.includes(file.type)) {
    ElMessage.warning('请选择 JPG、PNG、WebP 或 GIF 图片')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return false
  }
  return true
}

async function savePassword() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写原密码和新密码')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码不能少于6位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }

  passwordSaving.value = true
  try {
    await changeUserPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    localStorage.removeItem('token')
    localStorage.removeItem('phone')
    localStorage.removeItem('userId')
    localStorage.removeItem('userName')
    localStorage.removeItem('avatar')
    router.replace('/login')
  } finally {
    passwordSaving.value = false
  }
}

// 后端返回 ISO 时间字符串，页面上统一转换为中国本地时间。
function formatDate(value) {
  if (!value) return '暂无记录'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

// 旧会话可能还没有写入 lastLoginTime，此时从 JWT 的 iat（签发时间）中取本次登录时间。
function getTokenLoginTime() {
  try {
    const token = localStorage.getItem('token')
    const payloadPart = token?.split('.')[1]
    if (!payloadPart) return ''
    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/')
    const payload = JSON.parse(atob(base64))
    return payload.iat ? new Date(payload.iat * 1000).toISOString() : ''
  } catch (e) {
    return ''
  }
}
</script>

<style scoped>
.profile-page {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}

.profile-summary,
.profile-main {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.profile-summary {
  padding: 28px 20px 20px;
  text-align: center;
}

.profile-avatar {
  background: var(--accent);
  color: #fff;
  font-size: 30px;
  border: 4px solid var(--accent-bg);
}

.profile-summary h2 {
  margin: 14px 0 2px;
}

.summary-list {
  margin-top: 24px;
  border-top: 1px solid var(--border);
  padding-top: 10px;
}

.summary-item {
  display: grid;
  grid-template-columns: 22px 72px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  min-height: 44px;
  text-align: left;
  font-size: 13px;
}

.summary-item strong {
  color: var(--text-h);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-main {
  padding: 24px;
  min-width: 0;
}

.page-head h1 {
  margin-bottom: 4px;
}

.account-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-top: 18px;
}

.account-item {
  min-height: 72px;
  padding: 13px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
  box-sizing: border-box;
}

.account-item span,
.account-item strong {
  display: block;
}

.account-item span {
  font-size: 12px;
  color: var(--text);
  margin-bottom: 7px;
}

.account-item strong {
  color: var(--text-h);
  font-size: 14px;
  overflow-wrap: anywhere;
}

.account-item .status-normal {
  color: var(--success);
}

.profile-tabs {
  margin-top: 18px;
}

.profile-form {
  max-width: 760px;
  padding-top: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

.field-help {
  font-size: 12px;
  margin-top: 5px;
}

.avatar-upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-upload-box {
  width: 104px;
  height: 104px;
  border: 1px dashed var(--accent-border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
  color: var(--accent);
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;
}

.avatar-upload-box:hover {
  border-color: var(--accent);
}

.avatar-upload-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-help strong {
  display: block;
  color: var(--text-h);
  margin-bottom: 4px;
}

.upload-help p {
  font-size: 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 8px;
}

.security-intro {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 16px 0 22px;
  max-width: 520px;
}

.security-icon {
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius);
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 20px;
}

.security-intro h3 {
  margin-bottom: 3px;
}

.password-form {
  max-width: 460px;
}

@media (max-width: 1024px) {
  .profile-page {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .account-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
