<template>
  <div class="page-special">
    <h2>促销管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增促销</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="specialName" label="促销名称" min-width="160" />
        <el-table-column prop="specialStatus" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.specialStatus === 1 ? 'success' : 'info'">{{ row.specialStatus === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" title="促销" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.specialName" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.specialStatus" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
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
import { ref, onMounted } from 'vue'
import { getSpecialList, saveSpecial, deleteSpecial } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getSpecialList()
  list.value = r?.data || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveSpecial(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteSpecial(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
