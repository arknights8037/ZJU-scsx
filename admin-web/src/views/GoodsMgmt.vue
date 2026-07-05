<!--
  组件名称：GoodsMgmt
  功能描述：后台商品管理页面，支持商品列表分页展示、新增/编辑/删除商品、上传商品图片
  路由路径：/admin/goods/index
-->
<template>
  <div class="page-goods">
    <h2>商品管理</h2>
    <!-- 操作工具栏 -->
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增商品</el-button>
    </div>
    <!-- 商品列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <!-- 商品主图列 -->
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
        <!-- 商品类别名称 -->
        <el-table-column prop="categoryName" label="商品类别" min-width="130">
          <template #default="{ row }">
            <span class="category-name">{{ row.categoryName || '未分类' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="goodsMarketPrice" label="市场价" min-width="90" />
        <!-- 上架/下架状态标签 -->
        <el-table-column prop="goodsState" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.goodsState === 1 ? 'success' : 'info'">{{ row.goodsState === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页器 -->
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

    <!-- 新增/编辑商品弹窗 -->
    <el-dialog v-model="visible" title="商品" :close-on-click-modal="false" width="620px">
      <el-form :model="form">
        <el-form-item label="名称"><el-input v-model="form.goodsName" /></el-form-item>
        <el-form-item label="编号"><el-input v-model="form.goodsNo" /></el-form-item>
        <!-- 商品类别下拉选择（带层级显示） -->
        <el-form-item label="商品类别">
          <el-select v-model="form.categoryId" filterable placeholder="请选择中文类别" class="category-select">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="categoryLabel(category)"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="简介"><el-input v-model="form.goodsIntroduce" type="textarea" rows="2" /></el-form-item>
        <el-form-item label="市场价"><el-input-number v-model="form.goodsMarketPrice" :precision="2" /></el-form-item>
        <!-- 商品主图上传 -->
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
        <!-- 上架/下架开关 -->
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
/**
 * GoodsMgmt - 后台商品管理
 *
 * 功能：
 * - 商品列表分页展示
 * - 新增/编辑商品（含商品类别选择、图片上传）
 * - 删除商品
 */

import { ref, onMounted } from 'vue'
import { getCategoryList, getGoodsPage, saveGoods, deleteGoods, uploadGoodsImage } from '@/api'
import { ElMessage } from 'element-plus'
import { Delete, Plus } from '@element-plus/icons-vue'

const list = ref([])         // 商品列表
const categories = ref([])   // 商品分类列表
const visible = ref(false)   // 编辑弹窗可见性
const form = ref({})         // 编辑表单数据
const uploading = ref(false) // 图片上传中
const loading = ref(false)   // 列表加载中
const page = ref(1)          // 当前页码
const size = ref(10)         // 每页条数
const total = ref(0)         // 总记录数

onMounted(async () => {
  await Promise.all([loadCategories(), loadData()])
})

/** 加载商品分类列表 */
async function loadCategories() {
  const response = await getCategoryList()
  categories.value = response?.data || []
}

/** 加载商品列表（分页） */
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

/** 每页条数变化时重置到第一页 */
function onSizeChange() {
  page.value = 1
  loadData()
}

/**
 * 打开新增/编辑商品弹窗
 * @param {Object} row - 商品数据
 */
function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

/** 保存商品 */
async function save() {
  await saveGoods(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  page.value = 1
  loadData()
}

/**
 * 自定义上传商品图片
 * @param {Object} options - 上传组件提供的文件对象
 */
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

/**
 * 上传前校验文件类型和大小
 * @param {File} file - 待上传文件
 * @returns {boolean} 是否允许上传
 */
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

/**
 * 删除商品
 * @param {number|string} id - 商品 ID
 */
async function del(id) {
  await deleteGoods(id)
  ElMessage.success('已删除')
  if (list.value.length === 1 && page.value > 1) page.value--
  loadData()
}

/**
 * 生成带父级名称的分类标签
 * @param {Object} category - 分类对象
 * @returns {string} 例如"食品 / 零食"
 */
function categoryLabel(category) {
  const parent = categories.value.find(item => item.id === category.parentId)
  return parent ? `${parent.categoryName} / ${category.categoryName}` : category.categoryName
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

.category-name {
  color: var(--text-h);
  font-weight: 600;
}

.category-select {
  width: 100%;
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
