<template>
  <div class="page-order">
    <div class="page-heading">
      <div>
        <h2>订单管理</h2>
        <p>查看社区订单、购买商品与履约门店</p>
      </div>
    </div>

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

    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="订单" min-width="176">
          <template #default="{ row }">
            <div class="cell-main" :title="row.orderNo">订单 #{{ shortCode(row.orderNo) }}</div>
            <div class="cell-sub">{{ formatDate(row.createTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="下单用户" min-width="150">
          <template #default="{ row }">
            <div class="cell-main">{{ row.userName || '未知用户' }}</div>
            <div class="cell-sub">{{ row.userPhone || `用户 ID ${row.userId}` }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="cell-main cell-ellipsis">{{ row.goodsSummary || '商品信息已失效' }}</div>
            <div class="cell-sub">共 {{ row.itemCount || 0 }} 件</div>
          </template>
        </el-table-column>
        <el-table-column label="履约门店" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="cell-main cell-ellipsis">{{ row.storeSummary || '暂未分配门店' }}</div>
            <div class="cell-sub">{{ deliveryText(row.deliveryType) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="实付金额" min-width="110" align="right">
          <template #default="{ row }">
            <span class="price">{{ formatPrice(row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" min-width="105">
          <template #default="{ row }">{{ paymentText(row.paymentType) }}</template>
        </el-table-column>
        <el-table-column label="订单状态" min-width="105">
          <template #default="{ row }">
            <el-tag :type="stateType(row.orderState)" effect="light">{{ stateText(row.orderState) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="86" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link class="detail-action" :icon="View" @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="没有找到符合条件的订单" />
        </template>
      </el-table>

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

    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      :close-on-click-modal="false"
      width="min(820px, 92vw)"
    >
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

      <el-table v-loading="detailLoading" :data="details" stripe>
        <el-table-column label="商品" min-width="230">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image class="goods-image" :src="row.goodsPicture" fit="cover">
                <template #error><div class="image-fallback"><Picture /></div></template>
              </el-image>
              <div class="goods-copy">
                <div class="cell-main">{{ row.goodsName || '商品信息已失效' }}</div>
                <div class="cell-sub" :title="row.goodsNo">商品 #{{ shortCode(row.goodsNo) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="履约门店" min-width="145">
          <template #default="{ row }">{{ row.storeName || '门店信息已失效' }}</template>
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
import { onMounted, reactive, ref } from 'vue'
import { Picture, RefreshLeft, Search, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getOrderDetails, getOrderPage } from '@/api'

const stateOptions = [
  { label: '待付款', value: 1 },
  { label: '已付款', value: 2 },
  { label: '配送中', value: 3 },
  { label: '已签收', value: 4 },
  { label: '已完成', value: 9 },
  { label: '已关闭', value: -1 },
  { label: '已取消', value: -9 }
]

const query = reactive({ page: 1, size: 10, keyword: '', orderState: null })
const list = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const details = ref([])
const selectedOrder = ref(null)

onMounted(loadData)

// 列表参数都放在 query 中，翻页和筛选时只需要重复调用这一个方法。
async function loadData() {
  loading.value = true
  try {
    const response = await getOrderPage(query)
    list.value = response?.data?.records || []
    total.value = Number(response?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  loadData()
}

function resetQuery() {
  query.keyword = ''
  query.orderState = null
  search()
}

function handleSizeChange() {
  query.page = 1
  loadData()
}

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

function shortCode(value) {
  if (!value) return '--'
  const text = String(value)
  return text.length > 10 ? text.slice(-8).toUpperCase() : text
}

function formatPrice(value) {
  return `¥${Number(value || 0).toFixed(2)}`
}

function formatDate(value) {
  if (!value) return '时间未记录'
  return String(value).replace('T', ' ').slice(0, 16)
}

function paymentText(value) {
  return ({ 1: '在线支付', 2: '余额支付' })[value] || '未设置'
}

function deliveryText(value) {
  return ({ 1: '社区配送', 2: '门店自提' })[value] || '配送方式未设置'
}

function stateText(value) {
  return ({ 1: '待付款', 2: '已付款', 3: '配送中', 4: '已签收', 9: '已完成', '-1': '已关闭', '-9': '已取消' })[value] || '未知状态'
}

function stateType(value) {
  return ({ 1: 'warning', 2: 'primary', 3: 'warning', 4: 'success', 9: 'success', '-1': 'info', '-9': 'danger' })[value] || 'info'
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
.table-wrap :deep(.el-table) { min-width: 1060px; }
.pagination-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 14px 16px; border-top: 1px solid var(--border); color: var(--text-soft); font-size: 13px; }
.order-overview { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 1px; margin: -4px 0 18px; overflow: hidden; border: 1px solid var(--border); border-radius: var(--radius); background: var(--border); }
.order-overview > div { display: flex; min-height: 72px; flex-direction: column; justify-content: center; gap: 5px; padding: 12px 14px; background: rgba(255, 255, 255, 0.8); }
.order-overview span { color: var(--text-soft); font-size: 12px; }
.order-overview strong { overflow: hidden; color: var(--text-h); text-overflow: ellipsis; white-space: nowrap; }
.goods-cell { display: flex; align-items: center; gap: 10px; min-width: 0; }
.goods-image { width: 44px; height: 44px; flex: 0 0 44px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface-muted); }
.goods-copy { min-width: 0; }
.image-fallback { display: grid; width: 100%; height: 100%; place-items: center; color: var(--text-soft); }
.image-fallback .el-icon { font-size: 20px; }
@media (max-width: 760px) {
  .order-toolbar > * { width: 100% !important; }
  .pagination-row { align-items: flex-start; flex-direction: column; }
  .order-overview { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
</style>
