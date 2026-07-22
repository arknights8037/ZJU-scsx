<template>
  <div class="order-page" v-loading="loading">
    <div class="page-head">
      <div>
        <h2>📋 我的订单</h2>
        <p>查看订单明细、付款状态和后续履约进度。</p>
      </div>
      <el-button @click="loadOrders">刷新</el-button>
    </div>

    <el-alert
      v-if="highlightOrderNo"
      class="order-alert"
      type="success"
      show-icon
      :closable="false"
      :title="`订单 ${highlightOrderNo} 已提交，可在列表中查看详情或继续付款。`"
    />

    <el-tabs v-model="activeStatus" class="order-tabs">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="未付款" name="1" />
      <el-tab-pane label="已付款" name="2" />
      <el-tab-pane label="配送中" name="3" />
      <el-tab-pane label="已签收" name="4" />
      <el-tab-pane label="退款中" name="5" />
      <el-tab-pane label="已退款" name="6" />
      <el-tab-pane label="已完成" name="9" />
      <el-tab-pane label="退款被拒" name="-2" />
      <el-tab-pane label="已取消" name="-9" />
    </el-tabs>

    <div v-if="filteredOrders.length" class="table-wrap">
      <el-table :data="filteredOrders" :row-class-name="rowClassName">
        <el-table-column label="订单商品" min-width="310">
          <template #default="{ row }">
            <div class="order-product-cell">
              <el-image :src="assetUrl(firstDetail(row)?.goodsPicture)" class="order-thumb" fit="cover">
                <template #error><div class="thumb-placeholder">图</div></template>
              </el-image>
              <div class="order-product-copy">
                <strong>{{ firstDetail(row)?.goodsName || '商品信息加载中' }}</strong>
                <small>订单编号：{{ row.orderNo }}</small>
                <small v-if="firstDetail(row)">
                  {{ firstDetail(row).storeName || firstDetail(row).storeNo || '社区门店' }}
                  · 共 {{ itemCount(row) }} 件
                </small>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="总价" min-width="100">
          <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="stateType(row.orderState)">{{ stateText(row.orderState) }}</el-tag>
            <small v-if="autoCancelText(row)" class="auto-cancel-text">{{ autoCancelText(row) }}</small>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="170" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" class="order-action" @click="openDetails(row)">明细</el-button>
            <el-button v-if="row.orderState === 1" link type="success" class="order-action" @click="pay(row)">付款</el-button>
            <el-button v-if="row.orderState === 1" link type="danger" class="order-action" @click="cancel(row)">取消</el-button>
            <el-button v-if="canSignOrder(row)" link type="success" class="order-action" @click="sign(row)">
              确认签收
            </el-button>
            <el-button v-if="canConfirmReceipt(row)" link type="success" class="order-action" @click="confirmReceipt(row)">
              确认收货
            </el-button>
            <el-button v-if="canRefund(row)" link type="danger" class="order-action" @click="openRefund(row)">
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-else class="page-empty">
      <p>{{ orders.length ? '当前筛选下暂无订单' : '暂无订单' }}</p>
      <el-button type="primary" style="margin-top:16px" @click="$router.push('/portal')">去逛逛</el-button>
    </div>

    <el-drawer v-model="detailDrawer" title="订单明细" size="min(680px, 92vw)">
      <div v-if="activeOrder" class="drawer-summary">
        <p><span>订单编号</span><strong>{{ activeOrder.orderNo }}</strong></p>
        <p><span>订单状态</span><el-tag :type="stateType(activeOrder.orderState)">{{ stateText(activeOrder.orderState) }}</el-tag></p>
        <p><span>订单总价</span><strong class="price">¥{{ formatMoney(activeOrder.totalPrice) }}</strong></p>
        <p v-if="autoCancelText(activeOrder)"><span>自动取消</span><strong>{{ autoCancelText(activeOrder) }}</strong></p>
        <template v-if="activeOrder.refundReason">
          <p><span>退款原因</span><strong>{{ activeOrder.refundReason }}</strong></p>
          <p><span>退款金额</span><strong class="price">¥{{ formatMoney(activeOrder.refundAmount) }}</strong></p>
        </template>
      </div>
      <el-table :data="orderDetails">
        <el-table-column label="商品" min-width="230">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image :src="assetUrl(row.goodsPicture)" class="detail-thumb" fit="cover">
                <template #error><div class="thumb-placeholder">图</div></template>
              </el-image>
              <div>
                <strong>{{ row.goodsName || row.goodsNo || '商品' }}</strong>
                <small>商品编号：{{ row.goodsNo }}</small>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="门店" min-width="150">
          <template #default="{ row }">
            <div class="store-name">{{ row.storeName || row.storeNo || '门店' }}</div>
            <small>{{ row.storeNo }}</small>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="80">
          <template #default="{ row }">{{ row.goodsAmount }}</template>
        </el-table-column>
        <el-table-column label="小计" width="110">
          <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="activeOrder?.orderState === 1" class="drawer-actions">
        <el-button type="success" @click="pay(activeOrder)">立即付款</el-button>
        <el-button type="danger" plain @click="cancel(activeOrder)">取消订单</el-button>
      </div>
      <div v-else-if="canSignOrder(activeOrder)" class="drawer-actions">
        <el-button type="success" @click="sign(activeOrder)">确认签收</el-button>
      </div>
      <div v-else-if="canConfirmReceipt(activeOrder)" class="drawer-actions">
        <el-button type="success" @click="confirmReceipt(activeOrder)">确认收货</el-button>
      </div>
      <div v-if="canRefund(activeOrder)" class="drawer-actions">
        <el-button type="danger" plain @click="openRefund(activeOrder)">申请退款</el-button>
      </div>
    </el-drawer>

    <OrderPayDialog v-model="payDialogVisible" :order="payingOrder" @paid="paymentCompleted" />

    <el-dialog
      v-model="refundDialogVisible"
      title="申请退款"
      width="min(520px, 92vw)"
      :close-on-click-modal="false"
      @closed="refundOrder = null"
    >
      <el-form label-position="top" class="refund-form">
        <el-form-item label="退款订单">
          <el-input :model-value="refundOrder?.orderNo || ''" disabled />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input-number
            v-model="refundForm.refundAmount"
            :min="0.01"
            :max="Number(refundOrder?.totalPrice || 0)"
            :precision="2"
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-select v-model="refundForm.refundReason" placeholder="请选择退款原因" style="width: 100%">
            <el-option label="不想要了" value="不想要了" />
            <el-option label="买错商品/数量" value="买错商品/数量" />
            <el-option label="配送太慢" value="配送太慢" />
            <el-option label="商品问题" value="商品问题" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input
            v-model.trim="refundForm.refundDescription"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="可以填写退款情况、联系方式或希望物业处理的说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="refunding" @click="submitRefund">提交退款申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * Order - 门户端订单列表页
 *
 * 功能：
 * - 按订单状态 Tab 筛选展示（全部/未付款/已付款/配送中/已签收/退款中/已退款/已完成/已取消）
 * - 订单操作：付款、取消、确认签收、确认收货、申请退款
 * - 打开订单明细抽屉查看详情
 * - 高亮从外部跳转进来的订单（如支付完成回跳）
 * - 未付款订单显示自动取消倒计时
 *
 * 路由路径：/portal/order（支持 query 参数：orderNo 高亮指定订单）
 */

import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, confirmOrderReceipt, getOrderDetails, getOrderPage, requestOrderRefund, signOrder } from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'
import OrderPayDialog from '@/portal/components/OrderPayDialog.vue'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const orders = ref([])              // 订单列表
const orderDetails = ref([])        // 当前查看的订单详情（抽屉内容）
const orderDetailsMap = ref({})     // 订单编号 -> 订单详情列表的映射（用于快速查询）
const activeOrder = ref(null)       // 当前打开的订单（抽屉中）
const activeStatus = ref('all')     // 当前筛选的订单状态 Tab
const detailDrawer = ref(false)     // 订单明细抽屉可见性
const loading = ref(false)          // 订单列表加载状态
const payDialogVisible = ref(false) // 支付弹窗可见性
const payingOrder = ref(null)       // 正在支付的订单
const refundDialogVisible = ref(false)  // 退款弹窗可见性
const refunding = ref(false)        // 退款提交中
const refundOrder = ref(null)       // 当前申请退款的订单
const refundForm = reactive({
  refundAmount: 0,
  refundReason: '',
  refundDescription: ''
})
/** 需要高亮的订单编号（从 URL query 读取） */
const highlightOrderNo = ref(typeof route.query.orderNo === 'string' ? route.query.orderNo : '')

// ========== 计算属性 ==========
/** 按状态 Tab 筛选后的订单列表 */
const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') return orders.value
  return orders.value.filter(item => String(item.orderState) === activeStatus.value)
})

// ========== 生命周期 ==========
onMounted(() => {
  if (!auth.isLoggedIn) {
    router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
    return
  }
  loadOrders()
})

/**
 * 加载订单列表及所有订单的概要信息
 */
async function loadOrders() {
  loading.value = true
  try {
    const res = await getOrderPage({ page: 1, size: 50 })
    orders.value = res?.data?.records || []
    await loadOrderSummaries()
  } finally {
    loading.value = false
  }
}

/**
 * 批量加载所有订单的详情摘要（用于表格中展示首件商品和数量）
 */
async function loadOrderSummaries() {
  const result = {}
  await Promise.allSettled(orders.value.map(async order => {
    const res = await getOrderDetails(order.orderNo)
    result[order.orderNo] = res?.data || []
  }))
  orderDetailsMap.value = result
}

/**
 * 打开订单明细抽屉
 * @param {Object} row - 订单行数据
 */
async function openDetails(row) {
  activeOrder.value = row
  const res = await getOrderDetails(row.orderNo)
  orderDetails.value = res?.data || []
  detailDrawer.value = true
}

/**
 * 打开支付弹窗
 * @param {Object} row - 订单行数据
 */
async function pay(row) {
  payingOrder.value = row
  payDialogVisible.value = true
}

/**
 * 支付完成回调：更新本地订单状态为已付款
 * @param {Object} params - 回调参数，包含 orderNo
 */
function paymentCompleted({ orderNo }) {
    const targetOrder = orders.value.find(item => item.orderNo === orderNo)
    if (targetOrder) targetOrder.orderState = 2
    if (activeOrder.value?.orderNo === orderNo) activeOrder.value.orderState = 2
}

/**
 * 取消订单（仅未付款状态可取消）
 * @param {Object} row - 订单行数据
 */
async function cancel(row) {
  if (!row?.orderNo) return
  try {
    await ElMessageBox.confirm('确认取消该未付款订单吗？取消后会释放商品库存。', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    await cancelOrder(row.orderNo)
    const targetOrder = orders.value.find(item => item.orderNo === row.orderNo)
    if (targetOrder) targetOrder.orderState = -9
    if (activeOrder.value?.orderNo === row.orderNo) activeOrder.value.orderState = -9
    ElMessage.success('订单已取消')
  } catch (error) {
    if (error !== 'cancel') {
      // 业务异常由请求拦截器统一提示
    }
  }
}

/**
 * 判断订单是否可签收（已付款或配送中状态）
 * @param {Object} row - 订单行数据
 * @returns {boolean}
 */
function canSignOrder(row) {
  return row && (row.orderState === 2 || row.orderState === 3)
}

/**
 * 判断订单是否可确认收货（已签收状态）
 * @param {Object} row - 订单行数据
 * @returns {boolean}
 */
function canConfirmReceipt(row) {
  return row && row.orderState === 4
}

/**
 * 判断订单是否可申请退款
 * @param {Object} row - 订单行数据
 * @returns {boolean}
 */
function canRefund(row) {
  // 已付款/配送中/已签收/已完成 可申请；退款被拒后也可重新申请
  return row && [2, 3, 4, 9, -2].includes(row.orderState)
}

/**
 * 确认签收订单
 * @param {Object} row - 订单行数据
 */
async function sign(row) {
  if (!row?.orderNo) return
  try {
    await ElMessageBox.confirm('确认已经签收该订单吗？签收后可手动确认收货，或 7 天后自动确认收货。', '确认签收', {
      confirmButtonText: '确认签收',
      cancelButtonText: '再等等',
      type: 'success'
    })
    await signOrder(row.orderNo)
    const targetOrder = orders.value.find(item => item.orderNo === row.orderNo)
    if (targetOrder) targetOrder.orderState = 4
    if (activeOrder.value?.orderNo === row.orderNo) activeOrder.value.orderState = 4
    ElMessage.success('已确认签收')
  } catch (error) {
    if (error !== 'cancel') {
      // 业务异常由请求拦截器统一提示
    }
  }
}

/**
 * 确认收货，订单标记为已完成
 * @param {Object} row - 订单行数据
 */
async function confirmReceipt(row) {
  if (!row?.orderNo) return
  try {
    await ElMessageBox.confirm('确认该订单已完成收货吗？确认后订单会更新为”已完成”。', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '再等等',
      type: 'success'
    })
    await confirmOrderReceipt(row.orderNo)
    const targetOrder = orders.value.find(item => item.orderNo === row.orderNo)
    if (targetOrder) targetOrder.orderState = 9
    if (activeOrder.value?.orderNo === row.orderNo) activeOrder.value.orderState = 9
    ElMessage.success('已确认收货，订单已完成')
  } catch (error) {
    if (error !== 'cancel') {
      // 业务异常由请求拦截器统一提示
    }
  }
}

/**
 * 打开退款申请弹窗
 * @param {Object} row - 订单行数据
 */
function openRefund(row) {
  if (!row?.orderNo) return
  refundOrder.value = row
  refundForm.refundAmount = Number(row.totalPrice || 0)
  refundForm.refundReason = ''
  refundForm.refundDescription = ''
  refundDialogVisible.value = true
}

/**
 * 提交退款申请
 */
async function submitRefund() {
  if (!refundOrder.value?.orderNo) return
  if (!refundForm.refundReason) {
    ElMessage.warning('请选择退款原因')
    return
  }
  refunding.value = true
  try {
    await requestOrderRefund(refundOrder.value.orderNo, {
      refundAmount: refundForm.refundAmount,
      refundReason: refundForm.refundReason,
      refundDescription: refundForm.refundDescription
    })
    // 更新本地订单状态为退款中
    const targetOrder = orders.value.find(item => item.orderNo === refundOrder.value.orderNo)
    if (targetOrder) {
      targetOrder.orderState = 5
      targetOrder.refundAmount = refundForm.refundAmount
      targetOrder.refundReason = refundForm.refundReason
      targetOrder.refundDescription = refundForm.refundDescription
    }
    if (activeOrder.value?.orderNo === refundOrder.value.orderNo) {
      activeOrder.value.orderState = 5
      activeOrder.value.refundAmount = refundForm.refundAmount
      activeOrder.value.refundReason = refundForm.refundReason
      activeOrder.value.refundDescription = refundForm.refundDescription
    }
    refundDialogVisible.value = false
    ElMessage.success('退款申请已提交，等待后台处理')
  } finally {
    refunding.value = false
  }
}

/**
 * 订单状态转文本
 * @param {number|string} state - 订单状态数字
 * @returns {string} 中文描述
 */
function stateText(state) {
  const map = { 1: '未付款', 2: '已付款', 3: '配送中', 4: '已签收', 5: '退款中', 6: '已退款', 9: '已完成', '-2': '退款被拒', '-9': '已取消' }
  return map[state] || '未知'
}

/**
 * 订单状态对应的 Tag 样式
 * @param {number|string} state - 订单状态数字
 * @returns {string} Element Plus Tag 类型
 */
function stateType(state) {
  const map = { 1: 'warning', 2: 'primary', 3: 'info', 4: 'success', 5: 'danger', 6: 'info', 9: 'success', '-2': 'danger', '-9': 'info' }
  return map[state] || 'info'
}

/**
 * 金额格式化，保留两位小数
 * @param {number} value - 金额数值
 * @returns {string} 格式化后的金额字符串
 */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

/**
 * 表格行自定义类名：高亮指定订单
 * @param {Object} params - 行参数
 * @returns {string} 行 CSS 类名
 */
function rowClassName({ row }) {
  return row.orderNo === highlightOrderNo.value ? 'highlight-order-row' : ''
}

/**
 * 获取订单中第一个商品的概要信息
 * @param {Object} row - 订单行数据
 * @returns {Object|null} 商品概要对象
 */
function firstDetail(row) {
  return orderDetailsMap.value[row.orderNo]?.[0] || null
}

/**
 * 统计订单中商品总件数
 * @param {Object} row - 订单行数据
 * @returns {number} 商品总件数
 */
function itemCount(row) {
  return (orderDetailsMap.value[row.orderNo] || [])
    .map(item => Number(item.goodsAmount || 0))
    .reduce((sum, value) => sum + value, 0)
}

/**
 * 计算未付款订单的自动取消倒计时文本
 * @param {Object} row - 订单行数据
 * @returns {string} 倒计时文本（超时时返回提示信息）
 */
function autoCancelText(row) {
  if (!row || row.orderState !== 1 || !row.createTime) return ''
  const createdAt = new Date(String(row.createTime).replace(' ', 'T')).getTime()
  if (!Number.isFinite(createdAt)) return ''
  const deadline = createdAt + 24 * 60 * 60 * 1000
  const remain = deadline - Date.now()
  if (remain <= 0) return '已超过 24 小时，系统将自动取消'
  const hours = Math.floor(remain / (60 * 60 * 1000))
  const minutes = Math.ceil((remain % (60 * 60 * 1000)) / (60 * 1000))
  return `${hours}小时${minutes}分钟后自动取消`
}

</script>

<style scoped>
.order-page {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.order-alert {
  border-radius: var(--radius);
}

.order-tabs {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 0 14px;
}

.table-wrap {
  border-radius: var(--card-radius);
  overflow: hidden;
  border: 1px solid var(--border);
}

:deep(.highlight-order-row) {
  --el-table-tr-bg-color: var(--accent-bg);
}

.drawer-summary {
  display: grid;
  gap: 10px;
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface-muted);
}

.drawer-summary p {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.drawer-summary span {
  color: var(--text);
}

.drawer-summary strong {
  color: var(--text-h);
  overflow-wrap: anywhere;
}

.drawer-summary .price {
  color: var(--price-color);
}

.drawer-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.order-action {
  padding: 0 4px;
  background: transparent !important;
  border-color: transparent !important;
}

.order-product-cell,
.goods-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.order-thumb {
  width: 52px;
  height: 52px;
  flex: 0 0 52px;
  border-radius: 8px;
  background: var(--surface-muted);
}

.order-product-copy {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.order-product-copy strong {
  color: var(--text-h);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-product-copy small,
.auto-cancel-text {
  display: block;
  color: var(--text);
  font-size: 12px;
}

.auto-cancel-text {
  margin-top: 4px;
  color: #ca5010;
}

.detail-thumb {
  width: 46px;
  height: 46px;
  flex: 0 0 46px;
  border-radius: 6px;
}

.thumb-placeholder {
  width: 46px;
  height: 46px;
  display: grid;
  place-items: center;
  border-radius: 6px;
  background: var(--surface-muted);
  color: var(--text);
  font-size: 12px;
}

.goods-cell strong,
.goods-cell small,
.store-name {
  display: block;
}

.goods-cell strong,
.store-name {
  color: var(--text-h);
  font-weight: 600;
}

.goods-cell small,
.el-table small {
  color: var(--text);
  font-size: 12px;
}

.refund-form :deep(.el-form-item__label) {
  color: var(--text-h);
  font-weight: 600;
}

@media (max-width: 720px) {
  .page-head {
    flex-direction: column;
  }
}
</style>
