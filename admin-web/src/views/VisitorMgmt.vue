<!--
  组件名称：VisitorMgmt
  功能描述：后台访客记录管理页面，展示访客记录列表，支持确认完成操作
  路由路径：/admin/community/visitor
-->
<template>
  <div class="page-visitor">
    <h2>访客记录</h2>
    <div class="table-wrap">
      <!-- 访客记录列表 -->
      <el-table :data="list" stripe>
        <el-table-column prop="userId" label="用户ID" min-width="100" />
        <el-table-column prop="visitorObjective" label="来访目的" min-width="160" show-overflow-tooltip />
        <el-table-column prop="visitorTime" label="来访时间" min-width="160" />
        <!-- 访客状态标签列 -->
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.visitorStatus === 1 ? 'success' : 'info'">
              {{ row.visitorStatus === 1 ? '已完成' : '待访' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label="操作" width="130" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.visitorStatus !== 1"
              link
              type="success"
              :loading="updatingId === row.id"
              @click="completeVisitor(row)"
            >
              确认完成
            </el-button>
            <span v-else class="done-text">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
/**
 * VisitorMgmt - 后台访客记录管理
 *
 * 功能：
 * - 访客记录列表展示（用户ID、来访目的、来访时间、状态）
 * - 将待访记录标记为"已完成"
 */

import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVisitorPage, updateVisitorStatus } from '@/api'

const list = ref([])           // 访客记录列表
const updatingId = ref(null)   // 正在确认完成的访客 ID（用于按钮 loading）

onMounted(loadData)

/** 加载访客记录列表 */
async function loadData() {
  const r = await getVisitorPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

/**
 * 确认访客事项已完成
 * @param {Object} row - 访客记录行数据
 */
async function completeVisitor(row) {
  try {
    await ElMessageBox.confirm('确认该访客事项已经完成吗？', '确认完成', {
      confirmButtonText: '确认完成',
      cancelButtonText: '取消',
      type: 'warning'
    })
    updatingId.value = row.id
    await updateVisitorStatus(row.id, 1)
    row.visitorStatus = 1              // 更新本地状态为已完成
    ElMessage.success('访客事项已完成')
  } catch (error) {
    if (error !== 'cancel') throw error
  } finally {
    updatingId.value = null
  }
}
</script>

<style scoped>
.done-text { color: var(--text-soft); font-size: 12px; }
</style>
