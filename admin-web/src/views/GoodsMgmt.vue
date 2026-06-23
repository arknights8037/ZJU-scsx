<template>
  <div class="page-goods">
    <h2>商品管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增商品</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="goodsNo" label="商品编号" min-width="120" />
        <el-table-column prop="goodsName" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="categoryId" label="类别ID" min-width="80" />
        <el-table-column prop="goodsMarketPrice" label="市场价" min-width="90" />
        <el-table-column prop="goodsState" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.goodsState === 1 ? 'success' : 'info'">{{ row.goodsState === 1 ? '上架' : '下架' }}</el-tag>
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

    <el-dialog v-model="visible" title="商品" :close-on-click-modal="false" width="540px">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.goodsName" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.goodsNo" /></el-form-item>
        <el-form-item label="类别ID"><el-input-number v-model="form.categoryId" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.goodsIntroduce" type="textarea" rows="2" /></el-form-item>
        <el-form-item label="市场价"><el-input-number v-model="form.goodsMarketPrice" :precision="2" /></el-form-item>
        <el-form-item label="图片路径"><el-input v-model="form.goodsPicture" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.goodsState" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
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
import { getGoodsPage, saveGoods, deleteGoods } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = ref({})

onMounted(loadData)
async function loadData() {
  const r = await getGoodsPage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveGoods(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteGoods(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
