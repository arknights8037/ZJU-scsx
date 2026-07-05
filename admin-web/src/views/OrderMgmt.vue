<!--
  组件名称：OrderMgmt
  功能描述：后台订单管理页面，支持订单列表查询筛选、订单详情查看、退款审核操作
  路由路径：/admin/order/list
-->
<template>
  <div class="page-order">
    <!-- 页面标题区 -->
    <div class="page-heading">
      <div>
        <h2>订单列表</h2>
        <p>查看订单详情、购买商品、履约门店，以及居民签收、确认收货和退款处理状态。</p>
      </div>
    </div>

    <!-- 查询工具栏 -->
    <div class="page-toolbar order-toolbar">
      <el-input
        v-model.trim="query.keyword"
        clearable
        placeholder="搜索订单、用户或手机号"
        :prefix-icon="Search"
        @keyup.enter="search"
      />
      <el-select v-model="query.orderState" clearable placeholder="全部订单状态">
        <el-option v-for="item in stateOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="search">查询</el-button>
      <el-button :icon="RefreshLeft" @click="resetQuery">重置</el-button>
    </div>

    <!-- 订单列表表格 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <!-- 订单编号列 -->
        <el-table-column label="订单" min-width="176">
          <template #default="{ row }">
            <div class="cell-main" :title="row.orderNo">订单 #{{ shortCode(row.orderNo) }}</div>
            <div class="cell-sub">{{ formatDate(row.createTime) }}</div>
          </template>
        </el-table-column>
        <!-- 下单用户 -->
        <el-table-column label="下单用户" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">{{ row.userName || '未知用户' }}</div>
            <div class="cell-sub">{{ row.userPhone || `用户 ID ${row.userId}` }}</div>
          </template>
        </el-table-column>
        <!-- 商品信息 -->
        <el-table-column label="商品" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="cell-main cell-ellipsis">{{ row.goodsSummary || '商品待关联' }}</div>
            <div class="cell-sub">共 {{ row.itemCount || 0 }} 件</div>
          </template>
        </el-table-column>
        <!-- 履约门店 -->
        <el-table-column label="履约门店" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="cell-main cell-ellipsis">{{ row.storeSummary || '暂未分配门店' }}</div>
            <div class="cell-sub">{{ deliveryText(row.deliveryType) }}</div>
          </template>
        </el-table-column>
        <!-- 实付金额 -->
        <el-table-column label="实付金额" min-width="110" align="right">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <!-- 支付方式 -->
        <el-table-column label="支付方式" min-width="105">
          <template #default="{ row }">{{ paymentText(row.paymentType) }}</template>
        </el-table-column>
        <!-- 订单状态标签 -->
        <el-table-column label="订单状态" min-width="105">
          <template #default="{ row }">
            <el-tag :type="stateType(row.orderState)" effect="light">{{ stateText(row.orderState) }}</el-tag>
            <div v-if="row.refundReason" class="cell-sub refund-sub">退款：{{ row.refundReason }}</div>
          </template>
        </el-table-column>
        <!-- 操作按钮 -->
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link class="detail-action" :icon="View" @click="viewDetails(row)">详情</el-button>
            <el-button
              v-if="row.orderState === 5"
              link
              type="danger"
              class="detail-action"
              :icon="CircleCheck"
              @click="approveRefund(row)"
            >
              同意退款
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="没有找到符合条件的订单" />
        </template>
      </el-table>

      <!-- 分页栏 -->
      <div class="pagination-row">
        <span>共 {{ total }} 笔订单</span>
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      :close-on-click-modal="false"
      width="min(820px, 92vw)"
    >
      <!-- 订单概览 -->
      <div v-if="selectedOrder" class="order-overview">
        <div>
          <span>订单号</span>
          <strong :title="selectedOrder.orderNo">#{{ shortCode(selectedOrder.orderNo) }}</strong>
        </div>
        <div>
          <span>下单用户</span>
          <strong>{{ selectedOrder.userName || '未知用户' }}</strong>
        </div>
        <div>
          <span>实付金额</span>
          <strong class="price">{{ formatPrice(selectedOrder.totalPrice) }}</strong>
        </div>
        <div>
          <span>订单状态</span>
          <el-tag :type="stateType(selectedOrder.orderState)">{{ stateText(selectedOrder.orderState) }}</el-tag>
        </div>
      </div>

      <!-- 退款信息 -->
      <div v-if="selectedOrder?.refundReason" class="refund-overview">
        <div>
          <span>退款原因</span>
          <strong>{{ selectedOrder.refundReason }}</strong>
        </div>
        <div>
          <span>退款金额</span>
          <strong class="price">{{ formatPrice(selectedOrder.refundAmount) }}</strong>
        </div>
        <div>
          <span>申请时间</span>
          <strong>{{ formatDate(selectedOrder.refundTime) }}</strong>
        </div>
        <div>
          <span>处理时间</span>
          <strong>{{ formatDate(selectedOrder.refundHandledTime) }}</strong>
        </div>
        <p v-if="selectedOrder.refundDescription">{{ selectedOrder.refundDescription }}</p>
      </div>

      <!-- 订单商品明细 -->
      <el-table v-loading="detailLoading" :data="details" stripe>
        <el-table-column label="商品" min-width="230">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image class="goods-image" :src="row.goodsPicture" fit="cover">
                <template #error><div class="image-fallback"><Picture /></div></template>
              </el-image>
              <div class="goods-copy">
                <div class="cell-main">{{ row.goodsName || row.goodsNo || '商品待关联' }}</div>
                <div class="cell-sub" :title="row.goodsNo">商品 #{{ shortCode(row.goodsNo) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="履约门店" min-width="145">
          <template #default="{ row }">{{ row.storeName || row.storeNo || '门店待关联' }}</template>
        </el-table-column>
        <el-table-column prop="goodsAmount" label="数量" width="72" align="center" />
        <el-table-column label="单价" width="100" align="right">
          <template #default="{ row }">{{ formatPrice(row.goodsPrice) }}</template>
        </el-table-column>
        <el-table-column label="小计" width="105" align="right">
          <template #default="{ row }"><strong>{{ formatPrice(row.totalPrice) }}</strong></template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * OrderMgmt - 后台订单管理
 *
 * 功能：
 * - 订单列表分页展示与查询（关键词 + 状态筛选）
 * - 查看订单详情（含商品明细）
 * - 同意退款操作
 */

import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { CircleCheck, Picture, RefreshLeft, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { approveOrderRefund, getOrderDetails, getOrderPage } from '@/api'

/** 订单状态选项列表 */
const stateOptions = [
  { label: '待付款', value: 1 },
  { label: '已付款', value: 2 },
  { label: '配送中', value: 3 },
  { label: '已签收', value: 4 },
  { label: '退款中', value: 5 },
  { label: '已退款', value: 6 },
  { label: '已完成', value: 9 },
  { label: '已关闭', value: -1 },
  { label: '已取消', value: -9 }
]

const route = useRoute()

// ========== 查询参数与列表状态 ==========
const query = reactive({
  page: 1,
  size: 10,
  keyword: typeof route.query.keyword === 'string' ? route.query.keyword : '',
  orderState: null
})
const list = ref([])      // 订单列表
const total = ref(0)      // 总订单数
const loading = ref(false) // 列表加载状态

// ========== 详情弹窗状态 ==========
const detailVisible = ref(false)
const detailLoading = ref(false)
const details = ref([])           // 订单商品明细
const selectedOrder = ref(null)   // 当前查看的订单

onMounted(loadData)

/** 监听路由 keyword 参数变化，自动重新加载 */
watch(() => route.query.keyword, (keyword) => {
  const nextKeyword = typeof keyword === 'string' ? keyword : ''
  if (nextKeyword === query.keyword) return
  query.keyword = nextKeyword
  query.page = 1
  loadData()
})

/**
 * 加载订单列表
 * 列表参数都放在 query 中，翻页和筛选时只需要重复调用这一个方法。
 */
async function loadData() {
  loading.value = true
  try {
    const pageResponse = await getOrderPage(query)
    list.value = pageResponse?.data?.records || []
    total.value = Number(pageResponse?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

/** 触发查询（重置到第一页） */
function search() {
  query.page = 1
  loadData()
}

/** 重置查询条件 */
function resetQuery() {
  query.keyword = ''
  query.orderState = null
  search()
}

/** 每页条数变化时重置到第一页 */
function handleSizeChange() {
  query.page = 1
  loadData()
}

/**
 * 查看订单详情
 * @param {Object} row - 订单行数据
 */
async function viewDetails(row) {
  selectedOrder.value = row
  details.value = []
  detailVisible.value = true
  detailLoading.value = true
  try {
    const response = await getOrderDetails(row.orderNo)
    details.value = response?.data || []
  } catch (error) {
    ElMessage.error(error?.message || '订单详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

/**
 * 同意退款审核
 * @param {Object} row - 退款中的订单行数据
 */
async function approveRefund(row) {
  try {
    await ElMessageBox.confirm(
      `确认同意订单 #${shortCode(row.orderNo)} 的退款申请吗？`,
      '同意退款',
      {
        confirmButtonText: '同意退款',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await approveOrderRefund(row.orderNo)
    row.orderState = 6
    row.refundHandledTime = new Date().toISOString()
    if (selectedOrder.value?.orderNo === row.orderNo) {
      selectedOrder.value.orderState = 6
      selectedOrder.value.refundHandledTime = row.refundHandledTime
    }
    ElMessage.success('已同意退款，订单更新为已退款')
  } catch (error) {
    if (error !== 'cancel') {
      // 请求拦截器会提示业务异常。
    }
  }
}

/** 截取字符串末尾 8 位作为短码显示 */
function shortCode(value) {
  if (!value) return '--'
  const text = String(value)
  return text.length > 10 ? text.slice(-8).toUpperCase() : text
}

/** 格式化金额为 ¥xx.xx */
function formatPrice(value) {
  return `¥${Number(value || 0).toFixed(2)}`
}

/** 格式化日期时间 */
function formatDate(value) {
  if (!value) return '时间未记录'
  return String(value).replace('T', ' ').slice(0, 16)
}

/** 支付方式文本映射 */
function paymentText(value) {
  return ({ 1: '在线支付', 2: '余额支付' })[value] || '未设置'
}

/** 配送方式文本映射 */
function deliveryText(value) {
  return ({ 1: '社区配送', 2: '门店自提' })[value] || '配送方式未设置'
}

/** 订单状态文本映射 */
function stateText(value) {
  return ({ 1: '待付款', 2: '已付款', 3: '配送中', 4: '已签收', 5: '退款中', 6: '已退款', 9: '已完成', '-1': '已关闭', '-9': '已取消' })[value] || '未知状态'
}

/** 订单状态对应 el-tag 类型 */
function stateType(value) {
  return ({ 1: 'warning', 2: 'primary', 3: 'warning', 4: 'success', 5: 'danger', 6: 'info', 9: 'success', '-1': 'info', '-9': 'danger' })[value] || 'info'
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { font-size: 14px; color: var(--text-soft); }
.order-toolbar .el-input { width: min(300px, 100%); }
.order-toolbar .el-select { width: 170px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.cell-ellipsis { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.price { color: #a4262c; font-variant-numeric: tabular-nums; }
.detail-action { color: var(--accent); }
.detail-action:hover { color: var(--accent-hover); }
.refund-sub { color: var(--danger, #c42b1c); }
.table-wrap :deep(.el-table) { min-width: 1060px; }
.pagination-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 14px 16px; border-top: 1px solid var(--border); color: var(--text-soft); font-size: 13px; }
.order-overview { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 1px; margin: -4px 0 18px; overflow: hidden; border: 1px solid var(--border); border-radius: var(--radius); background: var(--border); }
.order-overview > div { display: flex; min-height: 72px; flex-direction: column; justify-content: center; gap: 5px; padding: 12px 14px; background: rgba(255, 255, 255, 0.8); }
.order-overview span { color: var(--text-soft); font-size: 12px; }
.order-overview strong { overflow: hidden; color: var(--text-h); text-overflow: ellipsis; white-space: nowrap; }
.refund-overview { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 1px; margin: -6px 0 18px; overflow: hidden; border: 1px solid rgba(196, 43, 28, 0.18); border-radius: var(--radius); background: rgba(196, 43, 28, 0.18); }
.refund-overview > div { display: flex; min-height: 70px; flex-direction: column; justify-content: center; gap: 5px; padding: 12px 14px; background: rgba(255, 247, 247, 0.88); }
.refund-overview span { color: var(--text-soft); font-size: 12px; }
.refund-overview strong { color: var(--text-h); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.refund-overview p { grid-column: 1 / -1; margin: 0; padding: 12px 14px; background: rgba(255, 247, 247, 0.88); color: var(--text); }
.goods-cell { display: flex; align-items: center; gap: 10px; min-width: 0; }
.goods-image { width: 44px; height: 44px; flex: 0 0 44px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface-muted); }
.goods-copy { min-width: 0; }
.image-fallback { display: grid; width: 100%; height: 100%; place-items: center; color: var(--text-soft); }
.image-fallback .el-icon { font-size: 20px; }
@media (max-width: 760px) {
  .order-toolbar > * { width: 100% !important; }
  .pagination-row { align-items: flex-start; flex-direction: column; }
  .order-overview { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .refund-overview { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
</style>
