<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': !sidebarOpen }">
    <!-- 移动端遮罩 -->
    <div v-if="sidebarOpen && isMobile" class="sidebar-overlay" @click="sidebarOpen = false" />

    <el-container>
      <!-- 侧边栏 -->
      <aside :class="['sidebar', { 'sidebar-mobile-open': sidebarOpen }]" :style="{ width: isMobile ? '260px' : '232px' }">
        <!-- Logo -->
        <div class="sidebar-brand" @click="$router.push('/dashboard')">
          <div class="brand-icon">
            <svg viewBox="0 0 32 32" fill="none">
              <rect x="4" y="4" width="10" height="10" rx="2.5" fill="currentColor" opacity="0.7"/>
              <rect x="17" y="4" width="11" height="10" rx="2.5" fill="currentColor" opacity="0.9"/>
              <rect x="4" y="17" width="11" height="11" rx="2.5" fill="currentColor" opacity="0.9"/>
              <rect x="17" y="17" width="10" height="11" rx="2.5" fill="currentColor" opacity="0.5"/>
            </svg>
          </div>
          <div class="brand-text">
            <span class="brand-name">智慧社区</span>
            <span class="brand-sub">管理平台</span>
          </div>
        </div>

        <!-- 菜单 -->
        <nav class="sidebar-nav">
          <el-menu
            router
            :default-active="route.path"
            class="sidebar-menu"
          >
            <template v-for="menu in menus" :key="menu.id">
              <el-sub-menu v-if="menu.children && menu.children.length" :index="'g-' + menu.id" class="nav-group">
                <template #title>
                  <span class="nav-icon"><el-icon><component :is="menu.menuIcon" /></el-icon></span>
                  <span class="nav-label">{{ menu.menuName }}</span>
                </template>
                <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.menuPath" class="nav-sub-item">
                  {{ child.menuName }}
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item v-else :index="menu.menuPath" class="nav-item">
                <span class="nav-icon"><el-icon><component :is="menu.menuIcon" /></el-icon></span>
                <span class="nav-label">{{ menu.menuName }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </nav>

        <!-- 侧边栏底部 -->
        <div class="sidebar-footer">
          <div class="user-mini" @click="logout">
            <span class="user-avatar">{{ userName.charAt(0) || 'A' }}</span>
            <span class="user-name-mini">{{ userName || '管理员' }}</span>
          </div>
        </div>
      </aside>

      <el-container class="main-area">
        <!-- 顶栏 -->
        <header class="topbar">
          <div class="topbar-left">
            <button class="menu-toggle" @click="sidebarOpen = !sidebarOpen">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M3 5h12M3 9h12M3 13h8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              </svg>
            </button>
            <div class="breadcrumb">
              <span class="breadcrumb-current">{{ currentTitle }}</span>
            </div>
          </div>
          <div class="topbar-right">
            <span class="header-user">{{ userName }}</span>
            <button class="btn-logout" @click="logout">退出</button>
          </div>
        </header>

        <!-- 主体 -->
        <main class="main-content">
          <router-view />
        </main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfo } from '@/api'

const router = useRouter()
const route = useRoute()
const menus = ref([])
const userName = ref('')
const sidebarOpen = ref(true)
const windowWidth = ref(window.innerWidth)

const isMobile = computed(() => windowWidth.value <= 1024)
const currentTitle = computed(() => route.meta?.title || '首页')

function onResize() {
  windowWidth.value = window.innerWidth
  sidebarOpen.value = windowWidth.value > 1024
}

onMounted(async () => {
  window.addEventListener('resize', onResize)
  onResize()
  try {
    const res = await getUserInfo()
    if (res?.data) {
      menus.value = res.data.menus || []
      userName.value = res.data.user?.userName || ''
    }
  } catch (e) {
    router.push('/login')
  }
})

onUnmounted(() => window.removeEventListener('resize', onResize))

function logout() {
  localStorage.removeItem('admin_token')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
  background: transparent;
}

/* ===== 侧边栏 ===== */
.sidebar {
  background: rgba(246, 250, 254, 0.86);
  border-right: 1px solid rgba(255, 255, 255, 0.82);
  box-shadow: 8px 0 28px rgba(37, 52, 68, 0.07), 1px 0 0 rgba(78, 96, 116, 0.10);
  backdrop-filter: blur(26px) saturate(1.25);
  -webkit-backdrop-filter: blur(26px) saturate(1.25);
  height: 100vh;
  position: relative;
  overflow: hidden;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
}

/* Logo */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px 18px;
  cursor: pointer;
  border-bottom: 1px solid rgba(78, 96, 116, 0.10);
  user-select: none;
}

.brand-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f6cbd, #3a96dd);
  border-radius: 8px;
  color: #fff;
  box-shadow: 0 5px 14px rgba(15, 108, 189, 0.24), 0 1px 0 rgba(255, 255, 255, 0.34) inset;
  flex-shrink: 0;
}

.brand-icon svg {
  width: 18px;
  height: 18px;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-h);
  letter-spacing: 0;
}

.brand-sub {
  font-size: 11px;
  color: var(--text-soft);
  letter-spacing: 0;
}

/* 导航 */
.sidebar-nav {
  position: absolute;
  top: 73px;
  bottom: 58px;
  left: 0;
  right: 0;
  padding: 12px 10px;
}

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
  height: 100%;
  overflow-y: auto;
}

.sidebar-menu :deep(.el-sub-menu),
.sidebar-menu :deep(.el-menu-item) {
  margin-bottom: 2px;
}

/* 一级菜单项 */
.nav-item,
.nav-group :deep(.el-sub-menu__title) {
  height: 40px;
  line-height: 40px;
  border-radius: 8px;
  color: #4f5966;
  font-size: 14px;
  border: 1px solid transparent;
  transition: background-color var(--motion-fast), border-color var(--motion-fast), color var(--motion-fast);
}

.nav-item:hover,
.nav-group :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.62);
  border-color: var(--border-light);
  color: var(--text-h);
}

.nav-item.is-active {
  background: var(--accent-bg) !important;
  border-color: rgba(15, 108, 189, 0.12) !important;
  color: var(--accent) !important;
  box-shadow: 3px 0 0 var(--accent) inset;
}

.nav-icon {
  display: inline-flex;
  align-items: center;
  margin-right: 10px;
  width: 20px;
  justify-content: center;
  font-size: 17px;
}

.nav-label {
  font-size: 13.5px;
}

/* 子菜单 */
.sidebar-menu :deep(.el-sub-menu .el-menu) {
  background: transparent !important;
}

.nav-sub-item {
  height: 36px !important;
  line-height: 36px !important;
  border-radius: 6px;
  padding-left: 50px !important;
  font-size: 13px;
  color: #65707d;
  background: transparent !important;
}

.nav-sub-item:hover {
  background: rgba(255, 255, 255, 0.58) !important;
  color: var(--text-h);
}

.nav-sub-item.is-active {
  background: var(--accent-bg) !important;
  color: var(--accent) !important;
}

/* 底部 */
.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 14px;
  border-top: 1px solid rgba(78, 96, 116, 0.10);
}

.user-mini {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: background-color var(--motion-fast), border-color var(--motion-fast);
}

.user-mini:hover {
  background: rgba(255, 255, 255, 0.64);
  border-color: var(--border-light);
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, #0f6cbd, #3a96dd);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-name-mini {
  font-size: 13px;
  color: var(--text-h);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ===== 主区域 ===== */
.main-area {
  flex-direction: column;
  overflow: hidden;
}

/* ===== 顶栏 ===== */
.topbar {
  height: 56px;
  background: rgba(249, 252, 255, 0.74);
  border-bottom: 1px solid rgba(255, 255, 255, 0.78);
  box-shadow: 0 1px 0 rgba(78, 96, 116, 0.10), 0 5px 18px rgba(37, 52, 68, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
  backdrop-filter: blur(24px) saturate(1.3);
  -webkit-backdrop-filter: blur(24px) saturate(1.3);
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-toggle {
  display: none;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: none;
  background: none;
  border-radius: 8px;
  cursor: pointer;
  color: var(--text-h);
  transition: background 0.2s;
}

.menu-toggle:hover {
  background: var(--bg-secondary, #f1f5f9);
}

.breadcrumb-current {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-h);
}

.header-user {
  font-size: 13px;
  color: var(--text);
}

.btn-logout {
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.58);
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.btn-logout:hover {
  border-color: var(--accent-border);
  color: var(--accent);
  background: var(--accent-bg);
}

/* ===== 主体 ===== */
.main-content {
  padding: 28px 28px 40px;
  background: transparent;
  overflow-y: auto;
  flex: 1;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .menu-toggle {
    display: flex;
  }

  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 1001;
    transform: translateX(-100%);
    box-shadow: 0 0 0 1px rgba(0,0,0,0.05), 0 8px 32px rgba(0,0,0,0.15);
  }

  .sidebar-mobile-open {
    transform: translateX(0);
  }

  .sidebar-overlay {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.4);
    z-index: 1000;
    backdrop-filter: blur(2px);
  }

  .main-content {
    padding: 16px 14px 32px;
  }

  .topbar {
    padding: 0 14px;
  }
}
</style>
