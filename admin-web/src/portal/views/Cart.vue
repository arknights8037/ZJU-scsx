<template>
  <div class="cart-page">
    <h2>🛒 购物车</h2>

    <div v-if="cartItems.length" class="table-wrap">
      <el-table ref="tableRef" :data="cartItems" style="width:100%" @selection-change="selectedItems = $event">
        <el-table-column type="selection" width="48" />
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image :src="itemImage(row)" class="thumb" fit="cover">
                <template #error><div class="thumb-placeholder">图</div></template>
              </el-image>
              <span class="goods-cell-name">{{ itemName(row) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" min-width="100">
          <template #default="{ row }">¥{{ formatMoney(itemPrice(row)) }}</template>
        </el-table-column>
        <el-table-column label="库存" min-width="90">
          <template #default="{ row }">
            <el-tag :type="itemStock(row) > 0 ? 'success' : 'danger'">
              {{ itemStock(row) > 0 ? itemStock(row) : '缺货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数量" min-width="140">
          <template #default="{ row }">
            <el-input-number
              v-model="row.amount"
              :min="1"
              :max="Math.max(1, Math.min(itemStock(row) || 99, 99))"
              size="small"
              @change="updateAmount(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" min-width="100">
          <template #default="{ row }">¥{{ formatMoney(itemPrice(row) * (row.amount || 1)) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="90">
          <template #default="{ row }">
            <el-button type="danger" size="small" text @click="removeItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-else class="page-empty">
      <p>购物车是空的</p>
      <el-button type="primary" style="margin-top:16px" @click="$router.push('/portal')">去逛逛</el-button>
    </div>

    <div v-if="cartItems.length" class="cart-footer">
      <span class="cart-total">
        已选 <strong>{{ selectedItems.length }}</strong> / {{ cartItems.length }} 件，
        合计：<span class="total-price">¥{{ totalPrice }}</span>
      </span>
      <el-button type="primary" size="large" :loading="checkingOut" @click="checkout">提交并付款</el-button>
    </div>

    <OrderPayDialog v-model="payDialogVisible" :order="pendingOrder" @paid="paymentCompleted" />
  </div>
</template>

<script setup>
/**
 * Cart - 门户端购物车页面
 *
 * 功能：
 * - 展示购物车商品列表（带多选、数量调整、删除）
 * - 自动全选所有商品
 * - 计算选中商品总价
 * - 提交选中商品生成订单并跳转支付
 *
 * 路由路径：/portal/cart
 */

import { ref, computed, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCartList, updateCartAmount, removeFromCart, createOrder } from '@/portal/api'
import { ElMessage } from 'element-plus'
import { usePortalAuthStore } from '@/portal/stores/auth'
import OrderPayDialog from '@/portal/components/OrderPayDialog.vue'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const cartItems = ref([])            // 购物车商品列表
const selectedItems = ref([])        // 用户选中的商品列表
const tableRef = ref()               // el-table 组件引用（用于全选操作）
const checkingOut = ref(false)       // 结算中加载状态
const payDialogVisible = ref(false)  // 支付弹窗可见性
const pendingOrder = ref(null)       // 待支付的订单数据

// ========== 计算属性 ==========
/** 选中商品的总价合计 */
const totalPrice = computed(() => {
  return formatMoney(selectedItems.value
    .reduce((sum, i) => sum + itemPrice(i) * (i.amount || 1), 0))
})

// ========== 生命周期 ==========
onMounted(loadCart)

/**
 * 加载购物车列表
 * 加载完成后默认全选所有商品
 */
async function loadCart() {
  if (!auth.isLoggedIn) {
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  try {
    const res = await getCartList()
    cartItems.value = res?.data || []
    await nextTick()
    tableRef.value?.toggleAllSelection()
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 更新商品数量
 * @param {Object} row - 购物车商品行数据
 */
async function updateAmount(row) {
  try {
    await updateCartAmount(row.id, row.amount)
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 从购物车删除商品
 * @param {Object} row - 购物车商品行数据
 */
async function removeItem(row) {
  try {
    await removeFromCart(row.id)
    cartItems.value = cartItems.value.filter(i => i.id !== row.id)
    ElMessage.success('已删除')
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 结算：生成订单并打开支付弹窗
 * 校验登录状态、选中商品、库存是否充足
 */
async function checkout() {
  if (!auth.isLoggedIn) {
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  if (!selectedItems.value.length) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  const invalid = selectedItems.value.find(item => itemStock(item) <= 0)
  if (invalid) {
    ElMessage.warning(`”${itemName(invalid)}”库存不足，暂不能结算`)
    return
  }
  try {
    checkingOut.value = true
    const res = await createOrder(selectedItems.value.map(item => item.id))
    pendingOrder.value = res?.data || null
    payDialogVisible.value = Boolean(pendingOrder.value)
    ElMessage.success('订单已生成，请选择支付方式')
    await loadCart()
  } catch (e) {
    /* cancel or handled */
  } finally {
    checkingOut.value = false
  }
}

/**
 * 支付完成回调：跳转到订单页并定位到该订单
 * @param {Object} params - 回调参数
 * @param {string} params.orderNo - 支付完成的订单编号
 */
function paymentCompleted({ orderNo }) {
  router.push({ path: '/portal/order', query: { orderNo } })
}

/**
 * 获取商品名称
 * @param {Object} item - 购物车商品
 * @returns {string} 商品名称
 */
function itemName(item) {
  return item.goods?.goodsName || item.goodsName || item.goodsNo
}

/**
 * 获取商品图片 URL
 * @param {Object} item - 购物车商品
 * @returns {string} 图片 URL
 */
function itemImage(item) {
  return assetUrl(item.goods?.goodsPicture || item.goodsPicture)
}

/**
 * 获取商品单价
 * @param {Object} item - 购物车商品
 * @returns {number} 单价数值
 */
function itemPrice(item) {
  return Number(item.goodsStore?.goodsPrice ?? item.goods?.goodsMarketPrice ?? item.goodsMarketPrice ?? 0)
}

/**
 * 获取商品库存
 * @param {Object} item - 购物车商品
 * @returns {number} 库存数量（默认 99）
 */
function itemStock(item) {
  return Number(item.goodsStore?.goodsStock ?? 99)
}

/**
 * 金额格式化，保留两位小数
 * @param {number} value - 金额数值
 * @returns {string} 格式化后的金额字符串
 */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

</script>

<style scoped>
.cart-page {
  /* constrained by parent */
}

.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.thumb {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  flex-shrink: 0;
}

.thumb-placeholder {
  width: 60px;
  height: 60px;
  background: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text);
  border-radius: 6px;
  font-size: 12px;
}

.goods-cell-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-wrap {
  border-radius: var(--card-radius);
  overflow: hidden;
  border: 1px solid var(--border);
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 24px;
  padding: 16px 0;
}

.cart-total {
  font-size: 16px;
  color: var(--text);
}

.total-price {
  color: var(--price-color);
  font-size: 22px;
  font-weight: bold;
}

@media (max-width: 1024px) {
  .cart-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .cart-total {
    text-align: center;
  }

  .cart-footer .el-button {
    width: 100%;
  }

  .goods-cell-name {
    max-width: 120px;
  }
}
</style>

