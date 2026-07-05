<!--
  组件名称：ComplaintMgmt
  功能描述：后台报修工单管理页面，展示报修列表、按紧急程度/状态筛选、标记处理完成
  路由路径：/admin/community/complaint
-->
<template>
  <div class="page-complaint">
    <!-- 页面标题与刷新按钮 -->
    <div class="page-heading">
      <div>
        <h2>报修后台</h2>
        <p>集中查看居民报修，优先处理紧急工单和待处理事项。</p>
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

    <!-- 筛选工具栏 -->
    <div class="page-toolbar complaint-toolbar">
      <el-select v-model="statusFilter" clearable placeholder="全部处理状态">
        <el-option label="待处理" :value="0" />
        <el-option label="已处理" :value="1" />
      </el-select>
      <el-select v-model="urgencyFilter" clearable placeholder="全部紧急程度">
        <el-option label="普通" value="普通" />
        <el-option label="较急" value="较急" />
        <el-option label="紧急" value="紧急" />
      </el-select>
      <span class="toolbar-tip">当前显示 {{ filteredList.length }} 条</span>
    </div>

    <!-- 报修工单列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="filteredList" stripe>
        <el-table-column label="工单" min-width="170">
          <template #default="{ row }">
            <div class="cell-main">{{ parseComplaint(row).repairType }}</div>
            <div class="cell-sub">{{ formatDate(row.createTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="urgencyType(parseComplaint(row).urgency)" effect="light">
              {{ parseComplaint(row).urgency }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="问题位置" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">{{ parseComplaint(row).location || '未填写' }}</template>
        </el-table-column>
        <el-table-column label="联系人" min-width="145">
          <template #default="{ row }">
            <div class="cell-main">{{ parseComplaint(row).contactName || `用户 ${row.userId}` }}</div>
            <div class="cell-sub">{{ parseComplaint(row).contactPhone || '未留电话' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="问题描述" min-width="260" show-overflow-tooltip>
          <template #default="{ row }">{{ parseComplaint(row).description }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.complaintStatus === 1 ? 'success' : 'warning'">
              {{ row.complaintStatus === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="row.complaintStatus !== 1"
              link
              type="success"
              :loading="resolvingId === row.id"
              @click="resolve(row)"
            >
              标记已处理
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无报修工单" />
        </template>
      </el-table>
    </div>

    <!-- 报修详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="报修详情"
      width="min(680px, 92vw)"
      :close-on-click-modal="false"
    >
      <div v-if="selectedComplaint" class="repair-detail">
        <!-- 详情头部 -->
        <div class="detail-head">
          <div>
            <h3>{{ selectedParsed.repairType }}</h3>
            <p>{{ formatDate(selectedComplaint.createTime) }}</p>
          </div>
          <div class="detail-tags">
            <el-tag :type="urgencyType(selectedParsed.urgency)">{{ selectedParsed.urgency }}</el-tag>
            <el-tag :type="selectedComplaint.complaintStatus === 1 ? 'success' : 'warning'">
              {{ selectedComplaint.complaintStatus === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </div>
        </div>

        <!-- 详细信息描述列表 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ selectedComplaint.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="问题位置">{{ selectedParsed.location || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedParsed.contactName || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedParsed.contactPhone || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="期望上门" :span="2">
            {{ selectedParsed.preferredTime ? formatDate(selectedParsed.preferredTime) : '未填写' }}
          </el-descriptions-item>
          <el-descriptions-item label="问题描述" :span="2">
            <p class="detail-desc">{{ selectedParsed.description }}</p>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 待处理时的操作按钮 -->
        <div v-if="selectedComplaint.complaintStatus !== 1" class="dialog-actions">
          <el-button type="success" :loading="resolvingId === selectedComplaint.id" @click="resolve(selectedComplaint)">
            标记已处理
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * ComplaintMgmt - 后台报修工单管理
 *
 * 功能：
 * - 报修工单列表展示（含概览统计）
 * - 按处理状态和紧急程度筛选
 * - 查看工单详情
 * - 标记工单为已处理
 */

import { computed, onMounted, ref } from 'vue'
import { Refresh, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getComplaintPage, updateComplaint } from '@/api'

const list = ref([])              // 报修工单列表
const loading = ref(false)        // 列表加载中
const detailVisible = ref(false)  // 详情弹窗可见性
const selectedComplaint = ref(null) // 当前查看工单
const resolvingId = ref(null)     // 正在处理中的工单 ID
const statusFilter = ref(null)    // 处理状态筛选
const urgencyFilter = ref('')     // 紧急程度筛选

/** 已解析的工单列表 */
const parsedList = computed(() => list.value.map(row => ({ row, parsed: parseComplaint(row) })))
/** 按筛选条件过滤后的工单列表 */
const filteredList = computed(() => parsedList.value
  .filter(item => isAllStatus(statusFilter.value) || item.row.complaintStatus === statusFilter.value)
  .filter(item => !urgencyFilter.value || item.parsed.urgency === urgencyFilter.value)
  .map(item => item.row))
/** 待处理数量 */
const pendingCount = computed(() => list.value.filter(item => item.complaintStatus !== 1).length)
/** 已处理数量 */
const processedCount = computed(() => list.value.filter(item => item.complaintStatus === 1).length)
/** 紧急且待处理的数量 */
const urgentCount = computed(() => parsedList.value.filter(item => item.parsed.urgency === '紧急' && item.row.complaintStatus !== 1).length)
/** 当前选中工单的解析结果 */
const selectedParsed = computed(() => selectedComplaint.value ? parseComplaint(selectedComplaint.value) : {})
/** 概览统计卡片数据 */
const summaryCards = computed(() => [
  { label: '报修总数', value: list.value.length, hint: '最近 50 条工单' },
  { label: '待处理', value: pendingCount.value, hint: '需要后台跟进' },
  { label: '已处理', value: processedCount.value, hint: '已完成闭环' },
  { label: '紧急待处理', value: urgentCount.value, hint: '建议优先查看' }
])

onMounted(loadData)

/** 加载工单列表 */
async function loadData() {
  loading.value = true
  try {
    const response = await getComplaintPage({ page: 1, size: 50 })
    list.value = response?.data?.records || []
  } finally {
    loading.value = false
  }
}

/** 打开工单详情弹窗 */
function openDetail(row) {
  selectedComplaint.value = row
  detailVisible.value = true
}

/** 标记工单为已处理 */
async function resolve(row) {
  resolvingId.value = row.id
  try {
    await updateComplaint(row.id, 1)
    row.complaintStatus = 1
    ElMessage.success('报修已标记为已处理')
  } finally {
    resolvingId.value = null
  }
}

/**
 * 解析工单内容文本
 * 从 complaintContent 中提取结构化字段（格式： 【字段名】值）
 * @param {Object} row - 工单行数据
 * @returns {Object} 解析后的工单信息
 */
function parseComplaint(row) {
  const text = stripHtml(row.complaintContent || '')
  const data = {}
  for (const line of text.split(/\r?\n/)) {
    const match = line.match(/^【(.+?)】(.*)$/)
    if (match) data[match[1]] = match[2].trim()
  }
  return {
    repairType: data['报修类型'] || '报事报修',
    urgency: data['紧急程度'] || '普通',
    location: data['问题位置'] || '',
    contactName: cleanEmpty(data['联系人']),
    contactPhone: cleanEmpty(data['联系电话']),
    preferredTime: cleanEmpty(data['期望上门']),
    description: data['问题描述'] || text || '暂无描述'
  }
}

/** 清理空值 */
function cleanEmpty(value) {
  return value && value !== '未填写' ? value : ''
}

/** 判断是否"全部"状态 */
function isAllStatus(value) {
  return value === null || value === undefined || value === ''
}

/** 去除 HTML 标签，保留换行 */
function stripHtml(value) {
  return String(value)
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/p>/gi, '\n')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .trim()
}

/** 紧急程度对应的 el-tag 类型 */
function urgencyType(value) {
  return ({ '紧急': 'danger', '较急': 'warning', '普通': 'info' })[value] || 'info'
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
.stat-card strong { display: block; margin: 8px 0 5px; color: var(--text-h); font-size: 28px; line-height: 1; }
.complaint-toolbar .el-select { width: 160px; }
.toolbar-tip { color: var(--text-soft); font-size: 13px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.table-wrap :deep(.el-table) { min-width: 980px; }
.detail-head { display: flex; justify-content: space-between; gap: 16px; margin: -4px 0 18px; }
.detail-head h3 { margin-bottom: 4px; color: var(--text-h); }
.detail-head p { color: var(--text-soft); font-size: 13px; }
.detail-tags { display: flex; align-items: flex-start; gap: 8px; }
.detail-desc { margin: 0; line-height: 1.8; white-space: pre-wrap; }
.dialog-actions { display: flex; justify-content: flex-end; margin-top: 18px; }
@media (max-width: 760px) {
  .page-heading { flex-direction: column; }
  .stat-grid { grid-template-columns: 1fr; }
  .complaint-toolbar > * { width: 100% !important; }
  .detail-head { flex-direction: column; }
}
</style>
