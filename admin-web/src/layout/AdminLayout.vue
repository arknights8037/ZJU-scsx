<!--
  组件名称：AdminLayout
  功能描述：后台管理系统的整体布局组件，包含侧边栏菜单、顶部导航栏、全局搜索面板
  路由路径：/admin/* 下的所有子路由均使用此布局
  children 通过 <router-view /> 渲染
-->
<template>
  <div class="admin-layout" :class="{ 'sidebar-collapsed': !sidebarOpen }">
    <!-- 移动端遮罩：侧边栏展开时点击遮罩可收起 -->
    <div v-if="sidebarOpen && isMobile" class="sidebar-overlay" @click="sidebarOpen = false" />

    <el-container>
      <!-- 侧边栏区域 -->
      <aside :class="['sidebar', { 'sidebar-mobile-open': sidebarOpen }]" :style="{ width: isMobile ? '260px' : '232px' }">
        <!-- Logo 品牌区，点击跳转至仪表盘 -->
        <div class="sidebar-brand" @click="$router.push('/admin/dashboard')">
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

        <!-- 动态菜单导航：根据后端返回的 menus 数据渲染 -->
        <nav class="sidebar-nav">
          <el-menu
            router
            :default-active="route.path"
            class="sidebar-menu"
          >
            <template v-for="menu in menus" :key="menu.id">
              <!-- 有子菜单时渲染 el-sub-menu -->
              <el-sub-menu v-if="menu.children && menu.children.length" :index="'g-' + menu.id" class="nav-group">
                <template #title>
                  <span class="nav-icon"><el-icon><component :is="menu.menuIcon" /></el-icon></span>
                  <span class="nav-label">{{ menu.menuName }}</span>
                </template>
              <el-menu-item v-for="child in menu.children" :key="child.id" :index="adminRoutePath(child.menuPath)" class="nav-sub-item">
                  {{ child.menuName }}
                </el-menu-item>
              </el-sub-menu>
              <!-- 无子菜单时直接渲染 el-menu-item -->
              <el-menu-item v-else :index="adminRoutePath(menu.menuPath)" class="nav-item">
                <span class="nav-icon"><el-icon><component :is="menu.menuIcon" /></el-icon></span>
                <span class="nav-label">{{ menu.menuName }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </nav>

        <!-- 侧边栏底部：当前登录用户信息与退出入口 -->
        <div class="sidebar-footer">
          <div class="user-mini" @click="logout">
            <span class="user-avatar">{{ userName.charAt(0) || 'A' }}</span>
            <span class="user-name-mini">{{ userName || '管理员' }}</span>
          </div>
        </div>
      </aside>

      <el-container class="main-area">
        <!-- 顶部导航栏 -->
        <header class="topbar">
          <div class="topbar-left">
            <!-- 移动端侧边栏展开/收起按钮 -->
            <button class="menu-toggle" @click="sidebarOpen = !sidebarOpen">
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M3 5h12M3 9h12M3 13h8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
              </svg>
            </button>
            <!-- 面包屑：显示当前页面标题 -->
            <div class="breadcrumb">
              <span class="breadcrumb-current">{{ currentTitle }}</span>
            </div>
          </div>
          <div class="topbar-right">
            <!-- 全局搜索组件 -->
            <div class="global-search" @click.stop>
              <el-input
                v-model.trim="globalKeyword"
                class="global-search-input"
                clearable
                :prefix-icon="Search"
                placeholder="搜索用户、商品、订单、报修..."
                @focus="openGlobalSearch"
                @input="scheduleGlobalSearch"
                @keyup.enter="runGlobalSearch"
                @clear="clearGlobalSearch"
              />
              <!-- 全局搜索下拉面板 -->
              <div v-if="globalSearchOpen" class="global-search-panel">
                <div class="global-search-panel-head">
                  <span>全局搜索</span>
                  <small v-if="globalKeyword">{{ globalResults.length }} 条结果</small>
                </div>
                <div v-loading="globalSearchLoading" class="global-search-results">
                  <button
                    v-for="item in globalResults"
                    :key="`${item.type}:${item.path}:${item.title}`"
                    type="button"
                    class="global-search-item"
                    @click="openSearchResult(item)"
                  >
                    <el-tag size="small" effect="light">{{ item.typeName }}</el-tag>
                    <span class="global-result-copy">
                      <strong>{{ item.title }}</strong>
                      <small>{{ item.subtitle }}</small>
                    </span>
                  </button>
                  <!-- 搜索无结果时的空状态 -->
                  <el-empty
                    v-if="globalKeyword && !globalSearchLoading && !globalResults.length"
                    :image-size="52"
                    description="没有找到相关内容"
                  />
                  <!-- 未输入关键词时的提示 -->
                  <p v-if="!globalKeyword" class="global-search-hint">输入关键词搜索全后台数据和功能菜单</p>
                </div>
              </div>
            </div>
            <!-- 当前登录用户名 -->
            <span class="header-user">{{ userName }}</span>
            <!-- 退出登录按钮 -->
            <button class="btn-logout" @click="logout">退出</button>
          </div>
        </header>

        <!-- 主体内容区：子路由页面在此渲染 -->
        <main class="main-content">
          <router-view />
        </main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
/**
 * 后台管理布局组件
 *
 * 功能：
 * - 根据后端返回的 menus 数据动态渲染侧边栏菜单
 * - 顶部全局搜索（支持用户、商品、订单等）
 * - 响应式：小屏时侧边栏收起为浮动层
 */

import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getUserInfo, globalSearch } from '@/api'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()

// ========== 响应式状态 ==========
const menus = ref([])               // 菜单树数据（从后端获取）
const userName = ref('')            // 当前登录用户名
const sidebarOpen = ref(true)       // 侧边栏展开状态
const windowWidth = ref(window.innerWidth)  // 窗口宽度（用于响应式判断）
const globalKeyword = ref('')       // 全局搜索关键词
const globalResults = ref([])       // 全局搜索结果列表
const globalSearchOpen = ref(false) // 全局搜索面板是否展开
const globalSearchLoading = ref(false)       // 全局搜索加载中
let globalSearchTimer = null        // 搜索防抖定时器句柄

// ========== 计算属性 ==========
/** 是否为移动端（<= 1024px） */
const isMobile = computed(() => windowWidth.value <= 1024)
/** 当前路由的标题，默认'首页' */
const currentTitle = computed(() => route.meta?.title || '首页')

// ========== 窗口尺寸变化处理 ==========
/** 窗口尺寸变化时更新 windowWidth，并在小屏时自动收起侧边栏 */
function onResize() {
  windowWidth.value = window.innerWidth
  sidebarOpen.value = windowWidth.value > 1024
}

// ========== 生命周期 ==========
onMounted(async () => {
  window.addEventListener('resize', onResize)
  document.addEventListener('click', closeGlobalSearch)
  onResize()
  try {
    const res = await getUserInfo()
    if (res?.data) {
      menus.value = res.data.menus || []
      userName.value = res.data.user?.userName || ''
    }
  } catch (e) {
    router.push('/admin/login')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
  document.removeEventListener('click', closeGlobalSearch)
  if (globalSearchTimer) window.clearTimeout(globalSearchTimer)
})

// ========== 菜单与退出 ==========
/** 退出登录：清除 token 并跳转到登录页 */
function logout() {
  localStorage.removeItem('admin_token')
  router.push('/admin/login')
}

/**
 * 补全管理后台路由路径
 * @param {string} path - 原始路径
 * @returns {string} 补全后的 /admin 开头的路径
 */
function adminRoutePath(path) {
  if (!path) return ''
  return path.startsWith('/admin') ? path : `/admin${path.startsWith('/') ? path : '/' + path}`
}

// ========== 全局搜索 ==========
/** 打开搜索面板，若有关键词则立即搜索 */
function openGlobalSearch() {
  globalSearchOpen.value = true
  if (globalKeyword.value && !globalResults.value.length) runGlobalSearch()
}

/** 关闭搜索面板 */
function closeGlobalSearch() {
  globalSearchOpen.value = false
}

/** 清空搜索关键词后重新展开面板（用户点击清空按钮时触发） */
function clearGlobalSearch() {
  globalResults.value = []
  globalSearchOpen.value = true
}

/** 输入防抖：260ms 后自动执行搜索 */
function scheduleGlobalSearch() {
  globalSearchOpen.value = true
  if (globalSearchTimer) window.clearTimeout(globalSearchTimer)
  globalSearchTimer = window.setTimeout(runGlobalSearch, 260)
}

/** 执行全局搜索请求 */
async function runGlobalSearch() {
  if (globalSearchTimer) window.clearTimeout(globalSearchTimer)
  const keyword = globalKeyword.value.trim()
  if (!keyword) {
    globalResults.value = []
    return
  }
  globalSearchOpen.value = true
  globalSearchLoading.value = true
  try {
    const response = await globalSearch(keyword)
    globalResults.value = response?.data || []
  } finally {
    globalSearchLoading.value = false
  }
}

/**
 * 选中搜索结果后跳转
 * @param {Object} item - 搜索结果项，包含 path/type/keyword
 */
function openSearchResult(item) {
  const searchableTypes = ['ORDER', 'STORE']
  router.push({
    path: item.path,
    query: searchableTypes.includes(item.type) ? { keyword: item.keyword } : {}
  })
  globalSearchOpen.value = false
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
  background: transparent;
}

.admin-layout > .el-container {
  height: 100%;
  min-height: 0;
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
  min-width: 0;
  min-height: 0;
  height: 100%;
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

.global-search {
  position: relative;
  width: min(360px, 36vw);
}

.global-search-input :deep(.el-input__wrapper) {
  border: 1px solid var(--border-light);
  background: rgba(255, 255, 255, 0.72);
  box-shadow: none;
}

.global-search-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent-border);
  box-shadow: 0 0 0 3px var(--accent-bg);
}

.global-search-panel {
  position: absolute;
  z-index: 1200;
  top: calc(100% + 10px);
  right: 0;
  width: min(520px, 86vw);
  overflow: hidden;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  background: rgba(250, 252, 255, 0.98);
  box-shadow: 0 18px 50px rgba(37, 52, 68, 0.18);
  backdrop-filter: blur(24px) saturate(1.2);
}

.global-search-panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-bottom: 1px solid var(--border);
  color: var(--text-h);
  font-size: 13px;
  font-weight: 600;
}

.global-search-panel-head small {
  color: var(--text-soft);
  font-weight: 400;
}

.global-search-results {
  max-height: min(520px, 68vh);
  min-height: 86px;
  overflow-y: auto;
  padding: 8px;
}

.global-search-item {
  display: grid;
  grid-template-columns: 58px minmax(0, 1fr);
  align-items: center;
  gap: 10px;
  width: 100%;
  min-height: 58px;
  padding: 8px 10px;
  border: 1px solid transparent;
  border-radius: var(--radius);
  background: transparent;
  color: inherit;
  cursor: pointer;
  text-align: left;
}

.global-search-item:hover {
  border-color: var(--border-light);
  background: var(--accent-bg);
}

.global-result-copy {
  display: grid;
  gap: 3px;
  min-width: 0;
}

.global-result-copy strong,
.global-result-copy small {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.global-result-copy strong {
  color: var(--text-h);
  font-size: 13px;
}

.global-result-copy small,
.global-search-hint {
  color: var(--text-soft);
  font-size: 12px;
}

.global-search-hint {
  padding: 18px 10px;
  text-align: center;
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
  min-width: 0;
  min-height: 0;
  flex: 1 1 0;
  scrollbar-gutter: stable;
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

  .global-search {
    width: min(300px, 50vw);
  }

  .header-user {
    display: none;
  }
}

@media (max-width: 640px) {
  .global-search {
    width: 54vw;
  }

  .global-search-input :deep(input::placeholder) {
    color: transparent;
  }
}
</style>
