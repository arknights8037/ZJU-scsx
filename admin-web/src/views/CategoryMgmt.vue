<template>
  <div class="page-category">
    <h2>类别管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增类别</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="categoryName" label="类别名称" min-width="140" />
        <el-table-column prop="categoryType" label="类别类型" min-width="80" />
        <el-table-column prop="parentId" label="父级ID" min-width="80" />
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" title="类别" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.categoryName" /></el-form-item>
        <el-form-item label="类型"><el-input-number v-model="form.categoryType" /></el-form-item>
        <el-form-item label="父级ID"><el-input-number v-model="form.parentId" /></el-form-item>
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
import { getCategoryList, saveCategory, deleteCategory } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getCategoryList()
  list.value = r?.data || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveCategory(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteCategory(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
