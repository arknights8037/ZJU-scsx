<template>
  <div class="page-charge">
    <h2>缴纳记录</h2>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="chargeNo" label="账单编号" min-width="160" />
        <el-table-column prop="chargeName" label="账单名称" min-width="140" />
        <el-table-column prop="totalPrice" label="总价" min-width="100" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.chargeStatus === 1 ? 'success' : 'warning'">
              {{ row.chargeStatus === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template #default="{ row }">
            <el-button size="small" @click="showDetails(row)">明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="detailVisible" title="账单明细" width="500px" :close-on-click-modal="false">
      <el-table :data="details" stripe>
        <el-table-column prop="chargeContent" label="内容" min-width="160" />
        <el-table-column prop="chargePrice" label="金额" min-width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getChargePage, getChargeDetails } from '@/api'

const list = ref([])
const detailVisible = ref(false)
const details = ref([])

onMounted(async () => {
  const r = await getChargePage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
})

async function showDetails(row) {
  const r = await getChargeDetails(row.chargeNo)
  details.value = r?.data || []
  detailVisible.value = true
}
</script>
