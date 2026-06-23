<template>
  <div class="order-page">
    <h2>📋 我的订单</h2>

    <div v-if="orders.length" class="table-wrap">
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单编号" min-width="220" />
        <el-table-column label="总价" min-width="100">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="stateType(row.orderState)">{{ stateText(row.orderState) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" min-width="160" />
      </el-table>
    </div>

    <div v-else class="page-empty">
      <p>暂无订单</p>
      <el-button type="primary" style="margin-top:16px" @click="$router.push('/')">去逛逛</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderPage } from '@/api'

const orders = ref([])

onMounted(async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await getOrderPage({ userId: Number(userId), page: 1, size: 50 })
    orders.value = res?.data?.records || []
  } catch (e) { /* handled */ }
})

function stateText(state) {
  const map = { 1: '未付款', 2: '已付款', 3: '已发货', 4: '已签收' }
  return map[state] || '未知'
}

function stateType(state) {
  const map = { 1: 'warning', 2: 'primary', 3: 'info', 4: 'success' }
  return map[state] || 'info'
}
</script>

<style scoped>
.order-page {
  /* constrained by parent */
}

.table-wrap {
  border-radius: var(--card-radius);
  overflow: hidden;
  border: 1px solid var(--border);
}
</style>
