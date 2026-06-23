<template>
  <div class="page-visitor">
    <h2>访客记录</h2>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="userId" label="用户ID" min-width="100" />
        <el-table-column prop="visitorObjective" label="来访目的" min-width="160" show-overflow-tooltip />
        <el-table-column prop="visitorTime" label="来访时间" min-width="160" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.visitorStatus === 1 ? 'success' : 'info'">
              {{ row.visitorStatus === 1 ? '已完成' : '待访' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVisitorPage } from '@/api'

const list = ref([])

onMounted(async () => {
  const r = await getVisitorPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
})
</script>
