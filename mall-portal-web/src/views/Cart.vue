<template>
  <div class="cart-page">
    <h2>🛒 购物车</h2>

    <div v-if="cartItems.length" class="table-wrap">
      <el-table :data="cartItems" style="width:100%">
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">
            <div class="goods-cell">
              <el-image :src="row.goodsPicture" class="thumb" fit="cover">
                <template #error><div class="thumb-placeholder">图</div></template>
              </el-image>
              <span class="goods-cell-name">{{ row.goodsName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" min-width="100">
          <template #default="{ row }">¥{{ row.goodsMarketPrice || '--' }}</template>
        </el-table-column>
        <el-table-column label="数量" min-width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.amount" :min="1" :max="99" size="small" @change="updateAmount(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" min-width="100">
          <template #default="{ row }">¥{{ ((row.goodsMarketPrice || 0) * (row.amount || 1)).toFixed(2) }}</template>
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
      <el-button type="primary" style="margin-top:16px" @click="$router.push('/')">去逛逛</el-button>
    </div>

    <div v-if="cartItems.length" class="cart-footer">
      <span class="cart-total">
        共 <strong>{{ cartItems.length }}</strong> 件，
        合计：<span class="total-price">¥{{ totalPrice }}</span>
      </span>
      <el-button type="primary" size="large" @click="checkout">去结算</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartAmount, removeFromCart, createOrder } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartItems = ref([])

const totalPrice = computed(() => {
  return cartItems.value
    .reduce((sum, i) => sum + (i.goodsMarketPrice || 0) * (i.amount || 1), 0)
    .toFixed(2)
})

onMounted(loadCart)

async function loadCart() {
  const userId = localStorage.getItem('userId')
  if (!userId) return router.push('/login')
  try {
    const res = await getCartList(Number(userId))
    cartItems.value = res?.data || []
  } catch (e) { /* handled */ }
}

async function updateAmount(row) {
  try {
    await updateCartAmount(row.id, row.amount)
  } catch (e) { /* handled */ }
}

async function removeItem(row) {
  try {
    await removeFromCart(row.id)
    cartItems.value = cartItems.value.filter(i => i.id !== row.id)
    ElMessage.success('已删除')
  } catch (e) { /* handled */ }
}

async function checkout() {
  const userId = Number(localStorage.getItem('userId'))
  if (!userId) return router.push('/login')
  const items = cartItems.value.map(i => ({
    goodsNo: i.goodsNo,
    storeNo: i.storeNo,
    goodsAmount: i.amount,
    goodsPrice: i.goodsMarketPrice || 0,
    totalPrice: (i.goodsMarketPrice || 0) * i.amount
  }))
  try {
    await createOrder(userId, items)
    ElMessage.success('下单成功')
    cartItems.value = []
    router.push('/order')
  } catch (e) { /* handled */ }
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
