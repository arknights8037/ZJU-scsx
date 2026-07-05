<!--
  组件名称：SystemLog
  功能描述：后台系统日志页面，展示管理端和居民端接口访问记录，支持搜索与筛选
  路由路径：/admin/system/log
-->
<template>
  <div class="system-log-page">
    <!-- 页面标题 -->
    <div class="page-heading">
      <div>
        <h2>系统日志</h2>
        <p>查看管理端和居民端接口访问记录，便于追踪异常和操作过程。</p>
      </div>
    </div>

    <!-- 查询工具栏 -->
    <div class="page-toolbar log-toolbar">
      <el-input
        v-model.trim="query.keyword"
        clearable
        placeholder="搜索路径、模块、IP、错误信息"
        :prefix-icon="Search"
        @keyup.enter="search"
      />
      <el-select v-model="query.success" clearable placeholder="全部结果">
        <el-option label="成功" :value="true" />
        <el-option label="失败" :value="false" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="search">查询</el-button>
      <el-button :icon="RefreshLeft" @click="resetQuery">重置</el-button>
    </div>

    <!-- 日志列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="时间" min-width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="模块" min-width="110">
          <template #default="{ row }">
            <el-tag effect="light">{{ row.operationModule || '系统接口' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请求" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="cell-main">{{ row.requestMethod }} {{ row.requestUri }}</div>
            <div class="cell-sub">{{ row.queryString || '无参数' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="用户/IP" min-width="140">
          <template #default="{ row }">
            <div class="cell-main">{{ row.userId ? `用户 ${row.userId}` : '未登录/游客' }}</div>
            <div class="cell-sub">{{ row.clientIp || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.success ? 'success' : 'danger'">{{ row.success ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态/耗时" width="110" align="right">
          <template #default="{ row }">
            <div class="cell-main">{{ row.statusCode || '-' }}</div>
            <div class="cell-sub">{{ row.costMs || 0 }} ms</div>
          </template>
        </el-table-column>
        <el-table-column label="错误信息" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.errorMessage || '-' }}</template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无系统日志" />
        </template>
      </el-table>

      <!-- 分页栏 -->
      <div class="pagination-row">
        <span>共 {{ total }} 条日志</span>
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * SystemLog - 后台系统日志
 *
 * 功能：
 * - 分页展示系统接口访问日志
 * - 按关键词和成功/失败状态筛选
 * - 查看请求方法、URI、用户IP、状态码、耗时、错误信息
 */

import { onMounted, reactive, ref } from 'vue'
import { RefreshLeft, Search } from '@element-plus/icons-vue'
import { getSystemLogPage } from '@/api'

const query = reactive({ page: 1, size: 10, keyword: '', success: null })
const list = ref([])
const total = ref(0)
const loading = ref(false)

onMounted(loadData)

/** 加载日志列表 */
async function loadData() {
  loading.value = true
  try {
    const res = await getSystemLogPage(query)
    list.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

/** 查询 */
function search() {
  query.page = 1
  loadData()
}

/** 重置查询条件 */
function resetQuery() {
  query.keyword = ''
  query.success = null
  search()
}

/** 每页条数变化 */
function handleSizeChange() {
  query.page = 1
  loadData()
}

/** 格式化日期时间 */
function formatDate(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}
</script>

<style scoped>
.page-heading { margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { color: var(--text-soft); font-size: 14px; }
.log-toolbar .el-input { width: min(340px, 100%); }
.log-toolbar .el-select { width: 140px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.table-wrap :deep(.el-table) { min-width: 1080px; }
.pagination-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 14px 16px; border-top: 1px solid var(--border); color: var(--text-soft); font-size: 13px; }
@media (max-width: 760px) {
  .log-toolbar > * { width: 100% !important; }
  .pagination-row { align-items: flex-start; flex-direction: column; }
}
</style>
