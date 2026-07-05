<!--
  组件名称：Dashboard
  功能描述：后台管理首页/工作台，展示快捷导航、待办事项概览（退款/报修/访客/账单）
  路由路径：/admin/dashboard
-->
<template>
  <div class="dashboard">
    <!-- 页面标题区 -->
    <div class="page-header">
      <div>
        <h2>工作台</h2>
        <p class="subtitle">欢迎使用智慧社区管理平台，以下是快捷入口</p>
      </div>
    </div>

    <!-- 统计卡片：四个管理模块的快捷入口 -->
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

    <!-- 待办事件面板 -->
    <section class="todo-panel">
      <div class="panel-head">
        <div>
          <h3>社区业务待办</h3>
          <p>实时汇总退款、报修、访客和账单等需要管理员跟进的业务事件。</p>
        </div>
        <!-- 刷新待办按钮 -->
        <el-button :icon="Refresh" :loading="todoLoading" @click="loadTodos">刷新待办</el-button>
      </div>

      <!-- 待办分类标签栏 -->
      <div class="todo-summary">
        <button
          v-for="summary in todoSummaries"
          :key="summary.type"
          type="button"
          :class="{ active: activeTodoType === summary.type }"
          @click="activeTodoType = summary.type"
        >
          <span>{{ summary.label }}</span>
          <strong>{{ summary.count }}</strong>
          <small>{{ summary.hint }}</small>
        </button>
      </div>

      <!-- 待办列表 -->
      <div v-loading="todoLoading" class="todo-list">
        <div v-if="filteredTodos.length" class="todo-section">
          <article
            v-for="todo in filteredTodos"
            :key="`${todo.type}:${todo.id}`"
            class="todo-item"
            :class="[`priority-${todo.priority}`, `type-${todo.type.toLowerCase()}`]"
          >
            <!-- 待办类型图标 -->
            <div class="todo-type-icon">
              <el-icon><component :is="todoIcon(todo.type)" /></el-icon>
            </div>
            <div class="todo-copy">
              <div class="todo-title-line">
                <el-tag size="small" :type="todoTagType(todo.type)" effect="light">{{ todo.typeName }}</el-tag>
                <strong>{{ todo.title }}</strong>
              </div>
              <small>{{ todo.description }}</small>
              <em>提交时间：{{ formatDate(todo.eventTime) }}</em>
            </div>
            <!-- 高优先级标记 -->
            <el-tag v-if="todo.priority === 3" size="small" type="danger">优先处理</el-tag>
            <!-- 操作按钮 -->
            <el-button
              :type="todo.type === 'REFUND' ? 'danger' : 'primary'"
              plain
              :loading="handlingKey === `${todo.type}:${todo.id}`"
              @click="handleTodo(todo)"
            >
              {{ todo.actionLabel }}
            </el-button>
          </article>
        </div>
        <el-empty v-else description="当前分类暂无待处理业务" />
      </div>
    </section>

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
/**
 * Dashboard - 后台工作台首页
 *
 * 功能：
 * - 展示快捷导航卡片，点击跳转至对应管理模块
 * - 展示待办事项（退款审核、报修处理、访客事项、账单跟进）
 * - 支持按待办类型分类筛选和操作处理
 */

import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Document, Goods, House, Location, Lock, Notebook, Refresh, Service, Shop, Tickets, User, Wallet } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTodoList, updateComplaint, updateVisitorStatus } from '@/api'

const router = useRouter()

/** 快捷导航卡片数据 */
const cards = [
  { icon: User, title: '用户管理', desc: '管理系统用户与角色权限', path: '/admin/auth/user' },
  { icon: Lock, title: '角色管理', desc: '配置角色与菜单权限', path: '/admin/auth/role' },
  { icon: Notebook, title: '系统日志', desc: '查看全系统接口访问记录', path: '/admin/system/log' },
  { icon: Goods, title: '商品管理', desc: '管理商城商品信息', path: '/admin/goods/index' },
  { icon: Document, title: '订单管理', desc: '查看订单仪表盘与列表', path: '/admin/order/dashboard' },
  { icon: House, title: '区域管理', desc: '管理社区区域划分', path: '/admin/area/index' },
  { icon: Shop, title: '门店管理', desc: '管理门店与状态', path: '/admin/area/store' },
  { icon: Location, title: '车位管理', desc: '管理社区车位资源', path: '/admin/community/parking' },
  { icon: Bell, title: '通知公告', desc: '发布社区通知', path: '/admin/community/notice' },
]

/** 待办事项概览数据（总数及各分类计数） */
const todoOverview = ref({
  totalCount: 0,
  refundCount: 0,
  repairCount: 0,
  visitorCount: 0,
  unpaidChargeCount: 0,
  items: []
})
const todoLoading = ref(false)      // 待办列表加载状态
const activeTodoType = ref('ALL')    // 当前选中的待办分类
const handlingKey = ref('')          // 正在操作中的待办项标识（用于按钮 loading）

/** 待办分类标签栏数据 */
const todoSummaries = computed(() => [
  { type: 'ALL', label: '全部待办', count: todoOverview.value.totalCount || 0, hint: '需要管理员跟进' },
  { type: 'REFUND', label: '退款审核', count: todoOverview.value.refundCount || 0, hint: '核对申请与金额' },
  { type: 'REPAIR', label: '报修处理', count: todoOverview.value.repairCount || 0, hint: '居民报修工单' },
  { type: 'VISITOR', label: '访客事项', count: todoOverview.value.visitorCount || 0, hint: '待完成访客记录' },
  { type: 'CHARGE', label: '账单跟进', count: todoOverview.value.unpaidChargeCount || 0, hint: '未缴社区账单' }
])

/** 根据当前选中的分类过滤待办列表 */
const filteredTodos = computed(() => {
  const items = todoOverview.value.items || []
  return activeTodoType.value === 'ALL' ? items : items.filter(item => item.type === activeTodoType.value)
})

onMounted(loadTodos)

/**
 * 加载待办事项列表
 */
async function loadTodos() {
  todoLoading.value = true
  try {
    const res = await getTodoList()
    todoOverview.value = res?.data || todoOverview.value
  } finally {
    todoLoading.value = false
  }
}

/**
 * 处理单个待办事项
 * - REFUND / CHARGE：跳转到对应详情页
 * - REPAIR：确认后完成报修
 * - VISITOR：确认后完成访客记录
 * @param {Object} todo - 待办事项对象
 */
async function handleTodo(todo) {
  if (todo.type === 'REFUND' || todo.type === 'CHARGE') {
    await router.push({
      path: todo.path,
      query: todo.keyword ? { keyword: todo.keyword } : {}
    })
    return
  }

  const isRepair = todo.type === 'REPAIR'
  const message = isRepair ? '确认该报修工单已经处理完成吗？' : '确认该访客事项已经完成吗？'
  try {
    await ElMessageBox.confirm(message, todo.actionLabel, {
      confirmButtonText: todo.actionLabel,
      cancelButtonText: '取消',
      type: 'warning'
    })
    handlingKey.value = `${todo.type}:${todo.id}`
    if (isRepair) await updateComplaint(todo.id, 1)
    else await updateVisitorStatus(todo.id, 1)
    ElMessage.success(isRepair ? '报修工单已处理' : '访客事项已完成')
    await loadTodos()
  } catch (error) {
    if (error !== 'cancel') throw error
  } finally {
    handlingKey.value = ''
  }
}

/** 获取待办类型对应的图标组件 */
function todoIcon(type) {
  return ({ REFUND: Wallet, REPAIR: Service, VISITOR: User, CHARGE: Tickets })[type] || Bell
}

/** 获取待办类型对应的 el-tag 类型 */
function todoTagType(type) {
  return ({ REFUND: 'danger', REPAIR: 'warning', VISITOR: 'primary', CHARGE: 'info' })[type] || 'info'
}

/** 格式化日期：将 ISO 日期转为 "YYYY-MM-DD HH:mm" 格式 */
function formatDate(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}
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

.todo-panel {
  margin-bottom: 32px;
  padding: 18px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  background: var(--surface);
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(18px) saturate(1.2);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.panel-head h3 {
  margin-bottom: 4px;
}

.panel-head p {
  color: var(--text);
  font-size: 13px;
}

.todo-summary {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1px;
  margin-bottom: 14px;
  overflow: hidden;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--border);
}

.todo-summary > button {
  display: grid;
  gap: 4px;
  padding: 12px;
  border: 0;
  background: rgba(255, 255, 255, 0.78);
  color: inherit;
  text-align: left;
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s;
}

.todo-summary > button:hover,
.todo-summary > button.active {
  position: relative;
  background: var(--accent-bg);
  box-shadow: inset 0 -3px var(--accent);
}

.todo-summary strong {
  color: var(--text-h);
  font-size: 22px;
  line-height: 1;
}

.todo-summary span,
.todo-summary small {
  color: var(--text);
  font-size: 12px;
}

.todo-summary small { color: var(--text-soft); }

.todo-list {
  min-height: 92px;
}

.todo-section {
  display: grid;
  gap: 10px;
}

.todo-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border: 1px solid var(--border);
  border-left: 4px solid var(--accent);
  border-radius: var(--radius);
  background: rgba(255, 255, 255, 0.72);
}

.todo-type-icon {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 10px;
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 18px;
}

.type-refund .todo-type-icon { background: rgba(196, 43, 28, 0.1); color: #c42b1c; }
.type-repair .todo-type-icon { background: rgba(202, 80, 16, 0.1); color: #ca5010; }
.type-charge .todo-type-icon { background: rgba(91, 95, 99, 0.1); color: #5b5f63; }

.todo-title-line {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.todo-title-line strong {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-item.priority-2 {
  border-left-color: #ca5010;
}

.todo-item.priority-3 {
  border-left-color: #c42b1c;
}

.todo-item.done {
  opacity: 0.76;
}

.todo-copy {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.todo-copy strong {
  color: var(--text-h);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.todo-copy small,
.todo-copy em {
  color: var(--text);
  font-size: 12px;
  font-style: normal;
}

.done-collapse {
  margin-top: 10px;
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

  .todo-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .stats-row {
    grid-template-columns: 1fr;
  }

  .panel-head,
  .todo-item {
    grid-template-columns: 1fr;
  }

  .todo-summary {
    grid-template-columns: 1fr;
  }

  .todo-item {
    display: flex;
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .todo-copy { flex: 1 1 calc(100% - 52px); }
}
</style>
