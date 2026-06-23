<template>
  <div class="page-store">
    <h2>门店管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增门店</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="storeNo" label="门店编号" min-width="120" />
        <el-table-column prop="storeName" label="门店名称" min-width="140" />
        <el-table-column prop="storeAddress" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="storeStatus" label="状态" min-width="80" />
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" title="门店" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.storeName" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.storeNo" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.storeAddress" /></el-form-item>
        <el-form-item label="区域ID"><el-input-number v-model="form.areaId" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.storeStatus" :active-value="1" :inactive-value="2" />
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
import { getStorePage, saveStore, deleteStore } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getStorePage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveStore(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteStore(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
