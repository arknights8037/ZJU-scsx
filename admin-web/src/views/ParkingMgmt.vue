<!--
  组件名称：ParkingMgmt
  功能描述：后台车位管理页面，支持车位列表展示、新增/编辑车位
  路由路径：/admin/community/parking
-->
<template>
  <div class="page-parking">
    <h2>车位管理</h2>
    <!-- 操作工具栏 -->
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增车位</el-button>
    </div>
    <!-- 车位列表 -->
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="parkingName" label="车位名称" min-width="120" />
        <el-table-column prop="parkingType" label="类型" min-width="100" />
        <el-table-column prop="parkingStatus" label="状态" min-width="100" />
        <el-table-column label="操作" min-width="100">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑车位弹窗 -->
    <el-dialog v-model="visible" title="车位" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="名称">
          <el-input v-model="form.parkingName" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input-number v-model="form.parkingType" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.parkingStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * ParkingMgmt - 后台车位管理
 *
 * 功能：
 * - 车位列表展示
 * - 新增/编辑车位（名称、类型、状态）
 */

import { ref, onMounted } from 'vue'
import { getParkingPage, saveParking } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])      // 车位列表
const visible = ref(false) // 编辑弹窗可见性
const form = ref({})       // 编辑表单数据

onMounted(loadData)

/** 加载车位列表 */
async function loadData() {
  const r = await getParkingPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

/**
 * 打开新增/编辑车位弹窗
 * @param {Object} row - 车位数据
 */
function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

/** 保存车位 */
async function save() {
  await saveParking(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}
</script>
