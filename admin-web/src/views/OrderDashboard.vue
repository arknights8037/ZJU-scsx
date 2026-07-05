<!--
  组件名称：OrderDashboard
  功能描述：后台订单仪表盘页面，展示订单统计数据、图表分布（ECharts）、履约进度分析
  路由路径：/admin/order/dashboard
-->
<template>
  <div class="order-dashboard">
    <!-- 页面标题与跳转列表按钮 -->
    <div class="page-heading">
      <div>
        <h2>订单仪表盘</h2>
        <p>汇总订单数量、成交金额和履约状态，快速判断今日处理重点。</p>
      </div>
      <el-button type="primary" @click="$router.push('/admin/order/index')">查看订单列表</el-button>
    </div>

    <!-- 刷新按钮 -->
    <div class="page-toolbar dashboard-toolbar">
      <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadData">刷新统计</el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-grid" v-loading="loading">
      <section v-for="item in dashboardCards" :key="item.label" class="stat-card" :class="item.tone">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </section>
    </div>

    <!-- ECharts 图表区域 -->
    <div class="chart-grid">
      <section class="chart-card">
        <div class="card-head">
          <div>
            <h3>订单状态分布</h3>
            <p>按当前筛选范围统计各状态订单占比。</p>
          </div>
        </div>
        <div ref="statusChartRef" class="chart-box" />
      </section>
      <section class="chart-card">
        <div class="card-head">
          <div>
            <h3>履约处理量</h3>
            <p>对比待处理、配送中与已完成订单量。</p>
          </div>
        </div>
        <div ref="fulfillmentChartRef" class="chart-box" />
      </section>
    </div>

    <!-- 履约提醒面板 -->
    <div class="insight-grid">
      <section class="insight-card">
        <h3>履约提醒</h3>
        <p>待付款、已付款和退款申请需要优先关注，避免居民下单后长时间无反馈。</p>
        <div class="insight-row">
          <span>待付款</span>
          <strong>{{ dashboard.pendingPaymentCount || 0 }} 单</strong>
        </div>
        <div class="insight-row">
          <span>等待履约</span>
          <strong>{{ dashboard.paidCount || 0 }} 单</strong>
        </div>
        <div class="insight-row">
          <span>退款待处理</span>
          <strong>{{ dashboard.refundingCount || 0 }} 单</strong>
        </div>
      </section>
      <section class="insight-card">
        <h3>履约进度</h3>
        <p>配送中、已签收、已完成可以反映订单是否顺利闭环。</p>
        <div class="progress-line">
          <span :style="{ width: progressWidth }"></span>
        </div>
        <small>已签收 + 已完成占比 {{ progressText }}</small>
      </section>
    </div>
  </div>
</template>

<script setup>
/**
 * OrderDashboard - 后台订单仪表盘
 *
 * 功能：
 * - 统计卡片展示订单总数、成交金额、各状态数量
 * - ECharts 饼图/柱状图分析订单状态分布
 * - 履约提醒与进度面板
 */

import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getOrderDashboard } from '@/api'

echarts.use([BarChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const loading = ref(false)
const statusChartRef = ref(null)          // 状态分布图容器
const fulfillmentChartRef = ref(null)     // 履约处理量图容器
let statusChart = null                    // 状态饼图实例
let fulfillmentChart = null               // 履约柱状图实例

/** 仪表盘数据 */
const dashboard = ref({
  totalOrders: 0,
  totalAmount: 0,
  pendingPaymentCount: 0,
  paidCount: 0,
  shippingCount: 0,
  signedCount: 0,
  completedCount: 0,
  refundingCount: 0,
  refundedCount: 0,
  closedCount: 0,
  canceledCount: 0
})

/** 统计卡片列表 */
const dashboardCards = computed(() => [
  { label: '订单总数', value: dashboard.value.totalOrders || 0, hint: '当前筛选范围', tone: 'neutral' },
  { label: '成交金额', value: formatPrice(dashboard.value.totalAmount), hint: '已纳入订单金额', tone: 'money' },
  { label: '待付款', value: dashboard.value.pendingPaymentCount || 0, hint: '需要催付/关注', tone: 'warning' },
  { label: '已付款', value: dashboard.value.paidCount || 0, hint: '等待门店履约', tone: 'primary' },
  { label: '配送中', value: dashboard.value.shippingCount || 0, hint: '正在履约', tone: 'info' },
  { label: '退款中', value: dashboard.value.refundingCount || 0, hint: `已退款 ${dashboard.value.refundedCount || 0} 单`, tone: 'danger' },
  { label: '已完成', value: dashboard.value.completedCount || 0, hint: `已签收待确认 ${dashboard.value.signedCount || 0} 单`, tone: 'success' }
])

/** 履约完成率（已签收 + 已完成占比） */
const completionRate = computed(() => {
  const total = Number(dashboard.value.totalOrders || 0)
  if (!total) return 0
  return Math.min(100, Math.round(((Number(dashboard.value.completedCount || 0) + Number(dashboard.value.signedCount || 0)) / total) * 100))
})
const progressWidth = computed(() => `${completionRate.value}%`)
const progressText = computed(() => `${completionRate.value}%`)

onMounted(() => {
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  statusChart?.dispose()
  fulfillmentChart?.dispose()
})

/** 仪表盘数据变化时重绘图表 */
watch(dashboard, () => renderCharts(), { deep: true })

/** 加载仪表盘数据 */
async function loadData() {
  loading.value = true
  try {
    const response = await getOrderDashboard()
    dashboard.value = { ...dashboard.value, ...(response?.data || {}) }
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

/** 格式化金额 */
function formatPrice(value) {
  return `¥${Number(value || 0).toFixed(2)}`
}

/** 渲染所有图表 */
function renderCharts() {
  renderStatusChart()
  renderFulfillmentChart()
}

/** 渲染订单状态分布饼图 */
function renderStatusChart() {
  if (!statusChartRef.value) return
  statusChart ||= echarts.init(statusChartRef.value)
  const data = [
    { name: '待付款', value: dashboard.value.pendingPaymentCount || 0 },
    { name: '已付款', value: dashboard.value.paidCount || 0 },
    { name: '配送中', value: dashboard.value.shippingCount || 0 },
    { name: '已签收', value: dashboard.value.signedCount || 0 },
    { name: '已完成', value: dashboard.value.completedCount || 0 },
    { name: '退款中', value: dashboard.value.refundingCount || 0 },
    { name: '已退款', value: dashboard.value.refundedCount || 0 },
    { name: '已关闭/取消', value: Number(dashboard.value.closedCount || 0) + Number(dashboard.value.canceledCount || 0) }
  ]
  statusChart.setOption({
    color: ['#ca5010', '#0f6cbd', '#64748b', '#107c10', '#13a10e', '#c42b1c', '#8a8886', '#605e5c'],
    tooltip: { trigger: 'item', formatter: '{b}: {c} 单 ({d}%)' },
    legend: { bottom: 0, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['46%', '70%'],
      center: ['50%', '43%'],
      avoidLabelOverlap: true,
      label: { formatter: '{b}\n{c}单' },
      data
    }]
  })
}

/** 渲染履约处理量柱状图 */
function renderFulfillmentChart() {
  if (!fulfillmentChartRef.value) return
  fulfillmentChart ||= echarts.init(fulfillmentChartRef.value)
  fulfillmentChart.setOption({
    color: ['#0f6cbd'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 36, right: 18, top: 28, bottom: 36 },
    xAxis: {
      type: 'category',
      data: ['待付款', '已付款', '配送中', '已签收', '已完成', '退款中'],
      axisTick: { show: false }
    },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      barMaxWidth: 34,
      itemStyle: { borderRadius: [6, 6, 0, 0] },
      data: [
        dashboard.value.pendingPaymentCount || 0,
        dashboard.value.paidCount || 0,
        dashboard.value.shippingCount || 0,
        dashboard.value.signedCount || 0,
        dashboard.value.completedCount || 0,
        dashboard.value.refundingCount || 0
      ]
    }]
  })
}

/** 窗口 resize 时自适应图表 */
function resizeCharts() {
  statusChart?.resize()
  fulfillmentChart?.resize()
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { color: var(--text-soft); font-size: 14px; }
.dashboard-toolbar { justify-content: flex-end; }
.stat-grid { display: grid; grid-template-columns: repeat(6, minmax(0, 1fr)); gap: 12px; margin-bottom: 16px; }
.stat-card { min-height: 118px; padding: 18px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--bg); box-shadow: var(--shadow-sm); }
.stat-card span, .stat-card small { display: block; color: var(--text-soft); font-size: 13px; }
.stat-card strong { display: block; margin: 10px 0 7px; color: var(--text-h); font-size: 28px; line-height: 1; }
.stat-card.money strong { color: var(--price-color); }
.stat-card.warning strong { color: var(--warning); }
.stat-card.primary strong { color: var(--accent); }
.stat-card.success strong { color: var(--success); }
.stat-card.danger strong { color: #c42b1c; }
.chart-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; margin-bottom: 16px; }
.chart-card { padding: 18px; border: 1px solid var(--border-light); border-radius: var(--radius); background: var(--surface); box-shadow: var(--shadow-sm); }
.card-head { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 8px; }
.card-head h3 { margin-bottom: 2px; }
.card-head p { color: var(--text-soft); font-size: 13px; }
.chart-box { width: 100%; height: 320px; }
.insight-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; }
.insight-card { padding: 20px; border: 1px solid var(--border-light); border-radius: var(--radius); background: var(--surface); box-shadow: var(--shadow-sm); }
.insight-card h3 { margin-bottom: 6px; }
.insight-card p { margin-bottom: 16px; color: var(--text-soft); }
.insight-row { display: flex; justify-content: space-between; padding: 10px 0; border-top: 1px solid var(--border); }
.progress-line { height: 10px; overflow: hidden; border-radius: 999px; background: var(--surface-muted); }
.progress-line span { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, var(--accent), var(--success)); }
.insight-card small { display: block; margin-top: 10px; color: var(--text-soft); }
@media (max-width: 1100px) {
  .stat-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
}
@media (max-width: 760px) {
  .page-heading, .pagination-row { align-items: flex-start; flex-direction: column; }
  .dashboard-toolbar { justify-content: flex-start; }
  .stat-grid, .chart-grid, .insight-grid { grid-template-columns: 1fr; }
  .chart-box { height: 280px; }
}
</style>
