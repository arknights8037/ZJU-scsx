<template>
  <div class="main-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <!-- 移动端菜单按钮 -->
        <button class="menu-toggle" @click="mobileMenuOpen = !mobileMenuOpen">
          <span></span><span></span><span></span>
        </button>

        <!-- Logo -->
        <div class="logo" @click="$router.push('/portal')">
          <div class="logo-icon">
            <svg viewBox="0 0 28 28" fill="none">
              <circle cx="14" cy="14" r="11" stroke="currentColor" stroke-width="2" opacity="0.4"/>
              <path d="M14 7l4 6-4 2-4-2z" fill="currentColor" opacity="0.7"/>
              <rect x="12" y="14" width="4" height="8" rx="1" fill="currentColor" opacity="0.9"/>
            </svg>
          </div>
          <span class="logo-text">智慧社区生活</span>
        </div>

        <!-- 导航链接（桌面端） -->
        <nav class="nav-links" :class="{ 'nav-open': mobileMenuOpen }">
          <router-link
            v-for="item in navItems"
            :key="item.key"
            :to="item.to"
            :class="{ 'nav-active': isNavActive(item) }"
            @click="mobileMenuOpen = false"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </router-link>
        </nav>

        <!-- 右侧操作 -->
        <div class="header-right">
          <!-- 搜索 -->
          <div class="search-box" @click="mobileMenuOpen = false">
            <svg class="search-icon" viewBox="0 0 18 18" fill="none">
              <circle cx="8" cy="8" r="5" stroke="currentColor" stroke-width="1.6"/>
              <path d="M12 12l3.5 3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
            </svg>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索..."
              class="search-input"
              @keyup.enter="onSearch"
            />
          </div>

          <!-- 用户 -->
          <template v-if="!isLoggedIn">
            <button class="btn-nav" @click="$router.push('/portal/login')">登录</button>
          </template>
          <template v-else>
            <el-dropdown trigger="click" popper-class="user-dropdown">
              <div class="user-chip">
                <span class="user-chip-avatar">
                  <img v-if="auth.user.avatar" :src="assetUrl(auth.user.avatar)" alt="用户头像" />
                  <template v-else>{{ userDisplay.charAt(0) }}</template>
                </span>
                <span class="user-chip-name">{{ userDisplay }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/portal/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/portal/order')">
                    <el-icon><Document /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/portal/favorite')">
                    <el-icon><Star /></el-icon>我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/portal/cart')">
                    <el-icon><ShoppingCart /></el-icon>购物车
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/portal/community')">
                    <el-icon><OfficeBuilding /></el-icon>社区服务
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-logo-text">智慧社区生活</span>
          <span class="footer-tagline">SmartCommunity Portal</span>
        </div>
        <div class="footer-links">
          <span>首页</span>
          <span>商品列表</span>
          <span>社区服务</span>
          <span>购物车</span>
          <span>我的订单</span>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2025 SmartCommunity. All rights reserved.</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
/**
 * MainLayout - 门户端主布局组件
 *
 * 功能：
 * - 顶部导航栏（Logo、导航链接、搜索框、用户下拉菜单）
 * - 移动端响应式：汉堡菜单控制导航展开/收起
 * - 用户登录状态控制（登录前显示"登录"按钮，登录后显示用户下拉菜单）
 * - 页脚显示
 *
 * 路由路径：/portal/* 下的所有子路由均使用此布局
 */

import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Goods, HomeFilled, OfficeBuilding, ShoppingCart, Star, SwitchButton, User } from '@element-plus/icons-vue'
import { usePortalAuthStore } from '@/portal/stores/auth'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const searchKeyword = ref('')         // 搜索关键词，绑定搜索输入框
const mobileMenuOpen = ref(false)     // 移动端导航菜单展开/收起状态

// 导航菜单项配置：key、标签、图标、路由地址、匹配函数
const navItems = [
  { key: 'home', label: '首页', icon: HomeFilled, to: '/portal', match: path => path === '/portal' },
  { key: 'goods', label: '商品', icon: Goods, to: '/portal/goods', match: path => path.startsWith('/portal/goods') },
  { key: 'community', label: '社区服务', icon: OfficeBuilding, to: '/portal/community', match: path => path.startsWith('/portal/community') },
  { key: 'favorite', label: '收藏', icon: Star, to: '/portal/favorite', match: path => path.startsWith('/portal/favorite') },
  { key: 'cart', label: '购物车', icon: ShoppingCart, to: '/portal/cart', match: path => path.startsWith('/portal/cart') },
  { key: 'order', label: '订单', icon: Document, to: '/portal/order', match: path => path.startsWith('/portal/order') }
]

// ========== 计算属性 ==========
/** 用户是否已登录 */
const isLoggedIn = computed(() => auth.isLoggedIn)
/** 用户显示名称（用于头像和下拉菜单展示） */
const userDisplay = computed(() => auth.displayName)

/**
 * 判断导航项是否处于激活状态
 * @param {Object} item - 导航菜单项
 * @param {Function} item.match - 匹配函数，接收当前路径，返回布尔值
 * @returns {boolean} 是否匹配当前路由
 */
function isNavActive(item) {
  return item.match(route.path)
}

/**
 * 执行搜索：将关键词作为 query 参数跳转到商品列表页
 */
function onSearch() {
  if (searchKeyword.value.trim()) {
    mobileMenuOpen.value = false
    router.push({ name: 'GoodsList', query: { keyword: searchKeyword.value.trim() } })
  }
}

/**
 * 退出登录：清除用户认证状态，跳转到门户首页
 */
function logout() {
  auth.logout()
  router.push('/portal')
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: transparent;
}

/* ===== Header ===== */
.header {
  background: rgba(249, 252, 255, 0.76);
  backdrop-filter: blur(24px) saturate(1.3);
  -webkit-backdrop-filter: blur(24px) saturate(1.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.78);
  box-shadow: 0 1px 0 rgba(78, 96, 116, 0.10), 0 5px 18px rgba(37, 52, 68, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 58px;
  gap: 20px;
  padding: 0 24px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  user-select: none;
}

.logo-icon {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #0f6cbd, #3a96dd);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(15, 108, 189, 0.24), 0 1px 0 rgba(255, 255, 255, 0.35) inset;
}

.logo-icon svg {
  width: 16px;
  height: 16px;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-h);
  letter-spacing: 0;
}

/* 导航 */
.nav-links {
  display: flex;
  gap: 2px;
  margin-left: 8px;
}

.nav-links a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: var(--text);
  font-size: 14px;
  padding: 7px 14px;
  border-radius: 7px;
  border: 1px solid transparent;
  transition: background-color var(--motion-fast), border-color var(--motion-fast), color var(--motion-fast);
  font-weight: 450;
}

.nav-links a .el-icon {
  font-size: 15px;
}

.nav-links a:hover {
  background: rgba(255, 255, 255, 0.66);
  border-color: var(--border-light);
  color: var(--text-h);
}

.nav-links a.nav-active {
  background: var(--accent-bg);
  border-color: rgba(15, 108, 189, 0.13);
  color: var(--accent);
  font-weight: 600;
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.6) inset;
}

/* 右侧 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: flex-end;
}

/* 搜索 */
.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.62);
  border: 1px solid var(--border-light);
  border-radius: 8px;
  padding: 0 12px;
  height: 36px;
  box-shadow: 0 1px 2px rgba(37, 52, 68, 0.04);
  transition: background-color var(--motion), border-color var(--motion), box-shadow var(--motion);
  max-width: 260px;
  flex: 1;
}

.search-box:focus-within {
  border-color: var(--accent-border);
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 0 0 3px var(--accent-bg), 0 6px 16px rgba(37, 52, 68, 0.08);
}

.search-icon {
  width: 16px;
  height: 16px;
  color: var(--text);
  flex-shrink: 0;
}

.search-input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: var(--text-h);
  flex: 1;
  min-width: 0;
  font-family: inherit;
}

.search-input::placeholder {
  color: var(--text);
}

/* 用户 */
.btn-nav {
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.58);
  padding: 7px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-h);
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: background-color var(--motion-fast), border-color var(--motion-fast), color var(--motion-fast), box-shadow var(--motion-fast);
  font-family: inherit;
  flex-shrink: 0;
}

.btn-nav:hover {
  border-color: var(--accent-border);
  color: var(--accent);
  background: rgba(255, 255, 255, 0.88);
  box-shadow: var(--shadow);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: background-color var(--motion-fast), border-color var(--motion-fast);
}

.user-chip:hover {
  background: rgba(255, 255, 255, 0.66);
  border-color: var(--border-light);
}

.user-chip-avatar {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  background: linear-gradient(135deg, #0f6cbd, #3a96dd);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  overflow: hidden;
}

.user-chip-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-chip-name {
  font-size: 13px;
  color: var(--text-h);
  font-weight: 500;
}

/* 汉堡菜单 */
.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  border-radius: 6px;
}

.menu-toggle:hover {
  background: var(--bg-secondary);
}

.menu-toggle span {
  display: block;
  width: 18px;
  height: 1.8px;
  background: var(--text-h);
  border-radius: 2px;
  transition: all 0.2s;
}

/* ===== Main ===== */
.main-content {
  flex: 1;
  padding: 30px 24px 44px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

/* ===== Footer ===== */
.footer {
  border-top: 1px solid rgba(255, 255, 255, 0.72);
  background: rgba(248, 251, 255, 0.66);
  backdrop-filter: blur(20px);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.footer-logo-text {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-h);
}

.footer-tagline {
  font-size: 12px;
  color: var(--text);
}

.footer-links {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: var(--text);
}

.footer-links span {
  cursor: pointer;
  transition: color 0.2s;
}

.footer-links span:hover {
  color: var(--text-h);
}

.footer-bottom {
  border-top: 1px solid rgba(78, 96, 116, 0.12);
  text-align: center;
  padding: 14px 24px;
  font-size: 12px;
  color: var(--text);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .header-content {
    gap: 12px;
    padding: 0 14px;
  }

  .logo-text {
    display: none;
  }

  .menu-toggle {
    display: flex;
    flex-shrink: 0;
  }

  .nav-links {
    position: absolute;
    top: 58px;
    left: 0;
    right: 0;
    background: rgba(249, 252, 255, 0.94);
    backdrop-filter: blur(24px) saturate(1.25);
    border-bottom: 1px solid var(--border);
    flex-direction: column;
    padding: 8px 14px;
    box-shadow: var(--shadow-md);
    z-index: 99;
    display: none;
  }

  .nav-links.nav-open {
    display: flex;
  }

  .nav-links a {
    padding: 11px 14px;
    font-size: 15px;
    border-radius: 8px;
  }

  .main-content {
    padding: 18px 14px 32px;
  }

  .footer-content {
    flex-direction: column;
    gap: 20px;
    padding: 24px 14px;
  }

  .footer-links {
    flex-wrap: wrap;
    gap: 16px;
  }
}
</style>

