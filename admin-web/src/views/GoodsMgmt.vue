<template>
  <div class="page-goods">
    <h2>商品管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增商品</el-button>
    </div>
    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="主图" width="82">
          <template #default="{ row }">
            <el-image
              v-if="row.goodsPicture"
              :src="row.goodsPicture"
              :preview-src-list="[row.goodsPicture]"
              preview-teleported
              fit="cover"
              class="goods-thumb"
            >
              <template #error><span class="image-empty">失效</span></template>
            </el-image>
            <span v-else class="image-empty">无图</span>
          </template>
        </el-table-column>
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

    <div v-if="total" class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        background
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 50]"
        :pager-count="5"
        :total="total"
        @current-change="loadData"
        @size-change="onSizeChange"
      />
    </div>

    <el-dialog v-model="visible" title="商品" :close-on-click-modal="false" width="620px">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.goodsName" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.goodsNo" /></el-form-item>
        <el-form-item label="类别ID"><el-input-number v-model="form.categoryId" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.goodsIntroduce" type="textarea" rows="2" /></el-form-item>
        <el-form-item label="市场价"><el-input-number v-model="form.goodsMarketPrice" :precision="2" /></el-form-item>
        <el-form-item label="商品主图">
          <div class="goods-upload-row">
            <el-upload
              accept=".jpg,.jpeg,.png,.webp,.gif"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="uploadImage"
            >
              <div class="goods-upload-box" v-loading="uploading">
                <img v-if="form.goodsPicture" :src="form.goodsPicture" alt="商品图片预览" />
                <el-icon v-else><Plus /></el-icon>
              </div>
            </el-upload>
            <div class="upload-help">
              <p>支持 JPG、PNG、WebP、GIF，大小不超过 5MB。</p>
              <el-button v-if="form.goodsPicture" text type="danger" :icon="Delete" @click="form.goodsPicture = ''">
                移除图片
              </el-button>
            </div>
          </div>
        </el-form-item>
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
import { getGoodsPage, saveGoods, deleteGoods, uploadGoodsImage } from '@/api'
import { ElMessage } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'

const list = ref([])
const visible = ref(false)
const form = ref({})
const uploading = ref(false)
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(loadData)
async function loadData() {
  loading.value = true
  try {
    const r = await getGoodsPage({ page: page.value, size: size.value })
    list.value = r?.data?.records || []
    total.value = Number(r?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

function onSizeChange() {
  page.value = 1
  loadData()
}

function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

async function save() {
  await saveGoods(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  page.value = 1
  loadData()
}

async function uploadImage(options) {
  uploading.value = true
  try {
    const res = await uploadGoodsImage(options.file)
    form.value.goodsPicture = res?.data?.url || ''
    ElMessage.success('商品图片上传成功')
  } finally {
    uploading.value = false
  }
}

function beforeImageUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']
  if (!allowed.includes(file.type)) {
    ElMessage.warning('请选择 JPG、PNG、WebP 或 GIF 图片')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return false
  }
  return true
}

async function del(id) {
  await deleteGoods(id)
  ElMessage.success('已删除')
  if (list.value.length === 1 && page.value > 1) page.value--
  loadData()
}
</script>

<style scoped>
.goods-thumb {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  border: 1px solid var(--border);
}

.image-empty {
  color: var(--text);
  font-size: 12px;
}

.goods-thumb :deep(.el-image__error) {
  color: var(--danger);
  background: var(--danger-bg);
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
  overflow-x: auto;
}

.goods-upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.goods-upload-box {
  width: 120px;
  height: 120px;
  border: 1px dashed var(--accent-border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
  color: var(--accent);
  font-size: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
}

.goods-upload-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-help p {
  font-size: 12px;
  margin-bottom: 6px;
}
</style>
