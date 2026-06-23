<template>
  <div class="page-notice">
    <h2>通知公告</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增通知</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="noticeTitle" label="标题" min-width="200" />
        <el-table-column prop="noticeStatus" label="状态" min-width="100" />
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="visible" title="通知" width="600px" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="标题">
          <el-input v-model="form.noticeTitle" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.noticeContent" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.noticeStatus" :active-value="1" :inactive-value="0" />
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
import { getNoticePage, saveNotice, deleteNotice } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getNoticePage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveNotice(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteNotice(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
