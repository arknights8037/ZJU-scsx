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
        <div class="logo" @click="$router.push('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 28 28" fill="none">
              <circle cx="14" cy="14" r="11" stroke="currentColor" stroke-width="2" opacity="0.4"/>
              <path d="M14 7l4 6-4 2-4-2z" fill="currentColor" opacity="0.7"/>
              <rect x="12" y="14" width="4" height="8" rx="1" fill="currentColor" opacity="0.9"/>
            </svg>
          </div>
          <span class="logo-text">智慧社区商城</span>
        </div>

        <!-- 导航链接（桌面端） -->
        <nav class="nav-links" :class="{ 'nav-open': mobileMenuOpen }">
          <router-link to="/" @click="mobileMenuOpen = false">首页</router-link>
          <router-link to="/goods" @click="mobileMenuOpen = false">商品</router-link>
          <router-link to="/cart" @click="mobileMenuOpen = false">
            购物车
          </router-link>
          <router-link to="/order" @click="mobileMenuOpen = false">订单</router-link>
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
            <button class="btn-nav" @click="$router.push('/login')">登录</button>
          </template>
          <template v-else>
            <el-dropdown trigger="click" popper-class="user-dropdown">
              <div class="user-chip">
                <span class="user-chip-avatar">{{ userPhone.charAt(0) }}</span>
                <span class="user-chip-name">{{ userPhone }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/order')">
                    <el-icon><Document /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/cart')">
                    <el-icon><ShoppingCart /></el-icon>购物车
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
          <span class="footer-logo-text">智慧社区商城</span>
          <span class="footer-tagline">SmartCommunity Mall</span>
        </div>
        <div class="footer-links">
          <span>首页</span>
          <span>商品列表</span>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Document, ShoppingCart, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const mobileMenuOpen = ref(false)

const isLoggedIn = computed(() => !!localStorage.getItem('token'))
const userPhone = computed(() => localStorage.getItem('phone') || '用户')

function onSearch() {
  if (searchKeyword.value.trim()) {
    mobileMenuOpen.value = false
    router.push({ name: 'GoodsList', query: { keyword: searchKeyword.value.trim() } })
  }
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('phone')
  window.location.reload()
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

/* ===== Header ===== */
.header {
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
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
  background: linear-gradient(135deg, #6366f1, #06b6d4);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo-icon svg {
  width: 16px;
  height: 16px;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-h);
  letter-spacing: -0.2px;
}

/* 导航 */
.nav-links {
  display: flex;
  gap: 2px;
  margin-left: 8px;
}

.nav-links a {
  text-decoration: none;
  color: var(--text);
  font-size: 14px;
  padding: 7px 14px;
  border-radius: 7px;
  transition: all 0.2s;
  font-weight: 450;
}

.nav-links a:hover {
  background: var(--bg-secondary);
  color: var(--text-h);
}

.nav-links a.router-link-active {
  background: var(--accent-bg);
  color: var(--accent);
  font-weight: 500;
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
  background: var(--bg-secondary, #f1f5f9);
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 0 12px;
  height: 36px;
  transition: all 0.25s;
  max-width: 260px;
  flex: 1;
}

.search-box:focus-within {
  border-color: var(--accent-border);
  background: var(--bg);
  box-shadow: 0 0 0 3px var(--accent-bg);
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
  background: none;
  padding: 7px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-h);
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  flex-shrink: 0;
}

.btn-nav:hover {
  border-color: var(--accent-border);
  color: var(--accent);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-chip:hover {
  background: var(--bg-secondary);
}

.user-chip-avatar {
  width: 28px;
  height: 28px;
  border-radius: 7px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
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
  padding: 28px 24px 40px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

/* ===== Footer ===== */
.footer {
  border-top: 1px solid var(--border);
  background: var(--bg);
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
  border-top: 1px solid var(--border);
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
    background: var(--bg);
    border-bottom: 1px solid var(--border);
    flex-direction: column;
    padding: 8px 14px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
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
