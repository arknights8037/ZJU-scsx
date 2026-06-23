<template>
  <div class="page-parking">
    <h2>车位管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增车位</el-button>
    </div>
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
import { ref, onMounted } from 'vue'
import { getParkingPage, saveParking } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getParkingPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveParking(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}
</script>
