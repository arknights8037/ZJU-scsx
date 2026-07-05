<!--
  组件名称：ChargeMgmt
  功能描述：后台社区账单管理页面，展示缴费账单列表、缴费状态、费用明细
  路由路径：/admin/community/charge
-->
<template>
  <div class="page-charge">
    <!-- 页面标题与刷新按钮 -->
    <div class="page-heading">
      <div>
        <h2>社区账单</h2>
        <p>查看物业缴费账单、缴纳状态和费用明细。</p>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
    </div>

    <!-- 概览统计卡片 -->
    <div class="stat-grid">
      <section v-for="item in summaryCards" :key="item.label" class="stat-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.hint }}</small>
      </section>
    </div>

    <!-- 状态筛选工具栏 -->
    <div class="page-toolbar charge-toolbar">
      <el-select v-model="statusFilter" clearable placeholder="全部账单状态">
        <el-option label="未缴" :value="0" />
        <el-option label="已缴" :value="1" />
      </el-select>
      <span class="toolbar-tip">当前显示 {{ filteredList.length }} 条账单</span>
    </div>

    <!-- 账单列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="filteredList" stripe>
        <el-table-column label="账单" min-width="210">
          <template #default="{ row }">
            <div class="cell-main">{{ row.chargeName || '社区账单' }}</div>
            <div class="cell-sub">#{{ row.chargeNo || '--' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="业主ID" width="90" />
        <el-table-column label="金额" width="120" align="right">
          <template #default="{ row }">
            <span class="price">¥{{ formatMoney(row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.chargeStatus === 1 ? 'success' : 'warning'">
              {{ row.chargeStatus === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" min-width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Document" @click="showDetails(row)">查看明细</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无社区账单" />
        </template>
      </el-table>
    </div>

    <!-- 账单明细弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="账单明细"
      width="min(640px, 92vw)"
      :close-on-click-modal="false"
    >
      <!-- 账单概览 -->
      <div v-if="selectedCharge" class="bill-overview">
        <div>
          <span>账单名称</span>
          <strong>{{ selectedCharge.chargeName || '社区账单' }}</strong>
        </div>
        <div>
          <span>账单金额</span>
          <strong class="price">¥{{ formatMoney(selectedCharge.totalPrice) }}</strong>
        </div>
        <div>
          <span>缴费状态</span>
          <el-tag :type="selectedCharge.chargeStatus === 1 ? 'success' : 'warning'">
            {{ selectedCharge.chargeStatus === 1 ? '已缴' : '未缴' }}
          </el-tag>
        </div>
      </div>

      <!-- 明细表格 -->
      <el-table v-loading="detailLoading" :data="details" stripe>
        <el-table-column prop="chargeContent" label="费用项目" min-width="220" />
        <el-table-column label="金额" width="130" align="right">
          <template #default="{ row }">¥{{ formatMoney(row.chargePrice) }}</template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无账单明细" />
        </template>
      </el-table>

      <!-- 明细合计 -->
      <div class="detail-total">
        <span>明细合计</span>
        <strong>¥{{ formatMoney(detailTotal) }}</strong>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * ChargeMgmt - 后台社区账单管理
 *
 * 功能：
 * - 账单列表展示（含概览统计卡片）
 * - 按缴费状态筛选
 * - 查看账单明细
 */

import { computed, onMounted, ref } from 'vue'
import { Document, Refresh } from '@element-plus/icons-vue'
import { getChargeDetails, getChargePage } from '@/api'

const list = ref([])              // 账单列表
const detailVisible = ref(false)  // 明细弹窗可见性
const details = ref([])           // 账单明细数据
const selectedCharge = ref(null)  // 当前查看的账单
const loading = ref(false)        // 列表加载中
const detailLoading = ref(false)  // 明细加载中
const statusFilter = ref(null)    // 状态筛选

/** 按状态筛选后的列表 */
const filteredList = computed(() => list.value.filter(item => isAllStatus(statusFilter.value) || item.chargeStatus === statusFilter.value))
/** 未缴账单列表 */
const unpaidList = computed(() => list.value.filter(item => item.chargeStatus !== 1))
/** 已缴数量 */
const paidCount = computed(() => list.value.filter(item => item.chargeStatus === 1).length)
/** 未缴总金额 */
const unpaidAmount = computed(() => unpaidList.value.reduce((sum, item) => sum + Number(item.totalPrice || 0), 0))
/** 明细合计 */
const detailTotal = computed(() => details.value.reduce((sum, item) => sum + Number(item.chargePrice || 0), 0))
/** 概览统计卡片数据 */
const summaryCards = computed(() => [
  { label: '账单总数', value: list.value.length, hint: '最近 50 条账单' },
  { label: '未缴账单', value: unpaidList.value.length, hint: '居民端待支付' },
  { label: '已缴账单', value: paidCount.value, hint: '已完成缴费' },
  { label: '未缴金额', value: `¥${formatMoney(unpaidAmount.value)}`, hint: '需要重点跟进' }
])

onMounted(loadData)

/** 加载账单列表 */
async function loadData() {
  loading.value = true
  try {
    const response = await getChargePage({ page: 1, size: 50 })
    list.value = response?.data?.records || []
  } finally {
    loading.value = false
  }
}

/**
 * 查看账单明细
 * @param {Object} row - 账单行数据
 */
async function showDetails(row) {
  selectedCharge.value = row
  detailVisible.value = true
  details.value = []
  detailLoading.value = true
  try {
    const response = await getChargeDetails(row.chargeNo)
    details.value = response?.data || []
  } finally {
    detailLoading.value = false
  }
}

/** 判断是否"全部"状态筛选 */
function isAllStatus(value) {
  return value === null || value === undefined || value === ''
}

/** 格式化金额（保留两位小数） */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

/** 格式化日期 */
function formatDate(value) {
  if (!value) return '时间未记录'
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { font-size: 14px; color: var(--text-soft); }
.stat-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; margin-bottom: 16px; }
.stat-card { min-height: 96px; padding: 16px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--bg); box-shadow: var(--shadow-sm); }
.stat-card span, .stat-card small { display: block; color: var(--text-soft); font-size: 13px; }
.stat-card strong { display: block; margin: 8px 0 5px; color: var(--text-h); font-size: 26px; line-height: 1; }
.charge-toolbar .el-select { width: 160px; }
.toolbar-tip { color: var(--text-soft); font-size: 13px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.price { color: #a4262c; font-variant-numeric: tabular-nums; }
.table-wrap :deep(.el-table) { min-width: 760px; }
.bill-overview { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 1px; margin: -4px 0 18px; overflow: hidden; border: 1px solid var(--border); border-radius: var(--radius); background: var(--border); }
.bill-overview > div { display: flex; min-height: 72px; flex-direction: column; justify-content: center; gap: 5px; padding: 12px 14px; background: rgba(255, 255, 255, 0.86); }
.bill-overview span { color: var(--text-soft); font-size: 12px; }
.bill-overview strong { overflow: hidden; color: var(--text-h); text-overflow: ellipsis; white-space: nowrap; }
.detail-total { display: flex; justify-content: flex-end; gap: 16px; padding: 14px 4px 0; color: var(--text-soft); }
.detail-total strong { color: var(--text-h); }
@media (max-width: 760px) {
  .page-heading { flex-direction: column; }
  .stat-grid, .bill-overview { grid-template-columns: 1fr; }
  .charge-toolbar > * { width: 100% !important; }
}
</style>
