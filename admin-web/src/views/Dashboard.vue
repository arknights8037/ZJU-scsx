<template>
  <div class="dashboard">
    <div class="page-header">
      <div>
        <h2>工作台</h2>
        <p class="subtitle">欢迎使用智慧社区管理平台，以下是快捷入口</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(15,108,189,0.1); color: #0f6cbd;">
          <svg viewBox="0 0 20 20" fill="none" width="20" height="20">
            <path d="M10 4a4 4 0 100 8 4 4 0 000-8zM3 16c0-2.8 3.1-5 7-5s7 2.2 7 5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">用户管理</span>
          <span class="stat-label">用户与角色配置</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(16,185,129,0.1); color: #10b981;">
          <svg viewBox="0 0 20 20" fill="none" width="20" height="20">
            <rect x="2" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="2" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="2" y="11" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
            <rect x="11" y="11" width="7" height="7" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">商品管理</span>
          <span class="stat-label">商品分类与上下架</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(245,158,11,0.1); color: #f59e0b;">
          <svg viewBox="0 0 20 20" fill="none" width="20" height="20">
            <path d="M3 5h14l-1.5 9H4.5L3 5z" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/>
            <circle cx="7" cy="17" r="1.5" stroke="currentColor" stroke-width="1.6"/>
            <circle cx="14" cy="17" r="1.5" stroke="currentColor" stroke-width="1.6"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">订单管理</span>
          <span class="stat-label">订单查看与处理</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(239,68,68,0.1); color: #ef4444;">
          <svg viewBox="0 0 20 20" fill="none" width="20" height="20">
            <path d="M4 7h12M4 11h8M4 15h5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
            <circle cx="16" cy="16" r="2.5" stroke="currentColor" stroke-width="1.6"/>
            <path d="M18 18l1.5 1.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">报事报修</span>
          <span class="stat-label">社区工单处理</span>
        </div>
      </div>
    </div>

    <!-- 快捷导航卡片 -->
    <h3 class="section-title">快捷功能</h3>
    <div class="quick-grid">
      <div v-for="card in cards" :key="card.title" class="quick-card" @click="$router.push(card.path)">
        <div class="quick-card-icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="quick-card-text">
          <span class="quick-card-title">{{ card.title }}</span>
          <span class="quick-card-desc">{{ card.desc }}</span>
        </div>
        <svg class="quick-card-arrow" viewBox="0 0 16 16" width="14" height="14">
          <path d="M6 4l4 4-4 4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Bell, Document, Goods, House, Location, Lock, Shop, User } from '@element-plus/icons-vue'

const cards = [
  { icon: User, title: '用户管理', desc: '管理系统用户与角色权限', path: '/auth/user' },
  { icon: Lock, title: '角色管理', desc: '配置角色与菜单权限', path: '/auth/role' },
  { icon: Goods, title: '商品管理', desc: '管理商城商品信息', path: '/goods/index' },
  { icon: Document, title: '订单管理', desc: '查看与处理订单', path: '/order/index' },
  { icon: House, title: '区域管理', desc: '管理社区区域划分', path: '/area/index' },
  { icon: Shop, title: '门店管理', desc: '管理门店与状态', path: '/area/store' },
  { icon: Location, title: '车位管理', desc: '管理社区车位资源', path: '/community/parking' },
  { icon: Bell, title: '通知公告', desc: '发布社区通知', path: '/community/notice' },
]
</script>

<style scoped>
.dashboard {
  max-width: 100%;
}

.page-header {
  margin-bottom: 28px;
}

.page-header h2 {
  margin-bottom: 4px;
}

.subtitle {
  color: var(--text);
  font-size: 14px;
}

/* ===== 统计卡片 ===== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 32px;
}

.stat-card {
  background: var(--surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(18px) saturate(1.2);
  cursor: pointer;
  transition: all 0.25s;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--accent-border);
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-h);
}

.stat-label {
  font-size: 12px;
  color: var(--text);
}

/* ===== 快捷导航 ===== */
.section-title {
  margin-bottom: 14px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}

.quick-card {
  background: var(--surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(18px) saturate(1.2);
}

.quick-card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--accent-border);
  transform: translateY(-1px);
}

.quick-card-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius);
  background: var(--accent-bg);
  color: var(--accent);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.quick-card-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.quick-card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-h);
}

.quick-card-desc {
  font-size: 12px;
  color: var(--text);
}

.quick-card-arrow {
  color: var(--text);
  opacity: 0.4;
  flex-shrink: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .quick-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
