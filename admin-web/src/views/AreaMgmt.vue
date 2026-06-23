<template>
  <div class="page-area">
    <h2>区域管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增区域</el-button>
    </div>
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
import { ref, onMounted } from 'vue'
import { getAreaList, saveArea } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const res = await getAreaList()
  list.value = res?.data || []
}
function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}
async function save() {
  await saveArea(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}
</script>
