<template>
  <div class="page-order">
    <h2>订单管理</h2>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="orderNo" label="订单编号" min-width="160" />
        <el-table-column prop="userId" label="用户ID" min-width="80" />
        <el-table-column prop="totalPrice" label="总价" min-width="90" />
        <el-table-column prop="paymentType" label="支付方式" min-width="90" />
        <el-table-column prop="orderState" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.orderState === 1 ? 'success' : row.orderState === 2 ? 'warning' : 'info'">
              {{ row.orderState === 1 ? '已完成' : row.orderState === 2 ? '待支付' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" :close-on-click-modal="false" width="600px">
      <el-table :data="details" stripe>
        <el-table-column prop="goodsNo" label="商品编号" min-width="140" />
        <el-table-column prop="storeNo" label="门店编号" min-width="120" />
        <el-table-column prop="goodsAmount" label="数量" min-width="70" />
        <el-table-column prop="goodsPrice" label="单价" min-width="80" />
        <el-table-column prop="totalPrice" label="小计" min-width="80" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderPage, getOrderDetails } from '@/api'

const list = ref([])
const detailVisible = ref(false)
const details = ref([])

onMounted(loadData)
async function loadData() {
  const r = await getOrderPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

async function viewDetails(row) {
  const r = await getOrderDetails(row.orderNo)
  details.value = r?.data || []
  detailVisible.value = true
}
</script>
