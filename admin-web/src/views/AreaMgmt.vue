<!--
  组件名称：AreaMgmt
  功能描述：后台区域管理页面，支持区域列表展示、新增/编辑区域
  路由路径：/admin/area/index
-->
<template>
  <div class="page-area">
    <h2>区域管理</h2>
    <!-- 操作工具栏 -->
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增区域</el-button>
    </div>
    <!-- 区域列表 -->
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="areaName" label="区域名称" min-width="120" />
        <el-table-column prop="areaNo" label="区域编号" min-width="120" />
        <el-table-column prop="areaType" label="区域类型" min-width="100" />
        <el-table-column label="操作" min-width="100">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑区域弹窗 -->
    <el-dialog v-model="visible" title="编辑区域" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.areaName" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.areaNo" /></el-form-item>
        <el-form-item label="父级ID"><el-input-number v-model="form.parentId" :min="0" /></el-form-item>
        <el-form-item label="类型"><el-input-number v-model="form.areaType" :min="1" :max="3" /></el-form-item>
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
 * AreaMgmt - 后台区域管理
 *
 * 功能：
 * - 区域列表展示
 * - 新增/编辑区域（名称、编号、父级ID、类型）
 */

import { ref, onMounted } from 'vue'
import { getAreaList, saveArea } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])      // 区域列表数据
const visible = ref(false) // 编辑弹窗可见性
const form = ref({})       // 编辑表单数据

onMounted(loadData)

/** 加载区域列表 */
async function loadData() {
  const res = await getAreaList()
  list.value = res?.data || []
}

/**
 * 打开新增/编辑区域弹窗
 * @param {Object} row - 区域数据
 */
function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

/** 保存区域 */
async function save() {
  await saveArea(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}
</script>
