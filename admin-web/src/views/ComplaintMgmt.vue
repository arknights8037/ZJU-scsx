<template>
  <div class="page-complaint">
    <h2>报事报修</h2>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="userId" label="用户ID" min-width="100" />
        <el-table-column prop="complaintContent" label="内容" show-overflow-tooltip min-width="200" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.complaintStatus === 1 ? 'success' : 'warning'">
              {{ row.complaintStatus === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140">
          <template #default="{ row }">
            <el-button
              v-if="row.complaintStatus !== 1"
              size="small"
              type="primary"
              @click="resolve(row)"
            >
              标记已处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getComplaintPage, updateComplaint } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])

onMounted(async () => {
  const r = await getComplaintPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
})

async function resolve(row) {
  await updateComplaint(row.id, 1)
  row.complaintStatus = 1
  ElMessage.success('已处理')
}
</script>
