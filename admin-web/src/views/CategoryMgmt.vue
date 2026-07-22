<!--
  组件名称：CategoryMgmt
  功能描述：后台商品类别管理页面，支持两级分类的新增/编辑/删除、搜索筛选
  路由路径：/admin/goods/category
-->
<template>
  <div class="page-category">
    <!-- 页面标题与新增按钮 -->
    <div class="page-heading">
      <div>
        <h2>类别管理</h2>
        <p>维护居民端和商品管理共同使用的中文分类</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增类别</el-button>
    </div>

    <!-- 搜索与筛选工具栏 -->
    <div class="page-toolbar category-toolbar">
      <el-input v-model.trim="keyword" clearable :prefix-icon="Search" placeholder="搜索类别名称" />
      <el-select v-model="typeFilter" clearable placeholder="全部层级">
        <el-option label="一级类别" :value="1" />
        <el-option label="二级类别" :value="2" />
      </el-select>
    </div>

    <!-- 类别列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="filteredList" stripe>
        <!-- 类别名称列 -->
        <el-table-column label="类别名称" min-width="190">
          <template #default="{ row }">
            <div class="category-cell">
              <span class="category-icon"><FolderOpened v-if="row.categoryType === 1" /><Document v-else /></span>
              <div>
                <strong>{{ row.categoryName }}</strong>
                <small>{{ row.categoryType === 1 ? '顶级分类' : '子分类' }}</small>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类别层级" width="110">
          <template #default="{ row }">
            <el-tag :type="row.categoryType === 1 ? 'primary' : 'info'" effect="light">
              {{ row.categoryType === 1 ? '一级类别' : '二级类别' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属父级" min-width="160">
          <template #default="{ row }">
            {{ row.categoryType === 1 ? '无（顶级）' : parentName(row.parentId) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="112" fixed="right" align="center">
          <template #default="{ row }">
            <el-tooltip content="编辑类别" placement="top">
              <el-button circle text :icon="Edit" @click="openEdit(row)" />
            </el-tooltip>
            <el-tooltip content="删除类别" placement="top">
              <el-button circle text type="danger" :icon="Delete" @click="remove(row)" />
            </el-tooltip>
          </template>
        </el-table-column>
        <template #empty><el-empty description="没有找到符合条件的类别" /></template>
      </el-table>
    </div>

    <!-- 新增/编辑类别弹窗 -->
    <el-dialog
      v-model="visible"
      :title="form.id ? '编辑类别' : '新增类别'"
      :close-on-click-modal="false"
      width="min(520px, 92vw)"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="类别名称" prop="categoryName">
          <el-input v-model.trim="form.categoryName" maxlength="100" placeholder="请输入中文类别名称" />
        </el-form-item>
        <el-form-item label="类别层级" prop="categoryType">
          <el-radio-group v-model="form.categoryType" @change="onTypeChange">
            <el-radio-button :value="1">一级类别</el-radio-button>
            <el-radio-button :value="2">二级类别</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <!-- 选择父级（仅二级类别） -->
        <el-form-item v-if="form.categoryType === 2" label="所属父级" prop="parentId">
          <el-select v-model="form.parentId" filterable placeholder="请选择一级类别" class="parent-select">
            <el-option v-for="item in topCategories" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存类别</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * CategoryMgmt - 后台商品类别管理
 *
 * 功能：
 * - 两级分类列表（一级/二级）
 * - 新增/编辑/删除类别
 * - 按名称搜索和按层级筛选
 */

import { computed, onMounted, ref } from 'vue'
import { Delete, Document, Edit, FolderOpened, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCategory, getCategoryList, saveCategory } from '@/api'

const list = ref([])          // 全部分类列表
const keyword = ref('')       // 搜索关键词
const typeFilter = ref(null)  // 层级筛选
const loading = ref(false)    // 列表加载中
const saving = ref(false)     // 保存中
const visible = ref(false)    // 编辑弹窗可见性
const formRef = ref()         // el-form 引用
const form = ref(createEmptyForm())  // 表单数据

/** 一级分类列表（供二级选择父级） */
const topCategories = computed(() => list.value.filter(item => item.categoryType === 1 || !item.parentId))
/** 按关键词和层级筛选后的列表 */
const filteredList = computed(() => list.value.filter(item => {
  const matchesKeyword = !keyword.value || item.categoryName?.includes(keyword.value)
  const matchesType = typeFilter.value == null || item.categoryType === typeFilter.value
  return matchesKeyword && matchesType
}))

/** 表单校验规则 */
const rules = {
  categoryName: [{ required: true, message: '请输入类别名称', trigger: 'blur' }],
  categoryType: [{ required: true, message: '请选择类别层级', trigger: 'change' }],
  parentId: [{ required: true, message: '请选择所属父级', trigger: 'change' }]
}

onMounted(loadData)

/** 创建空白表单 */
function createEmptyForm() {
  return { id: null, categoryName: '', categoryType: 1, parentId: 0 }
}

/** 加载全部分类 */
async function loadData() {
  loading.value = true
  try {
    const response = await getCategoryList()
    list.value = response?.data || []
  } finally {
    loading.value = false
  }
}

/**
 * 打开新增/编辑分类弹窗
 * @param {Object} row - 分类数据
 */
function openEdit(row) {
  form.value = row ? { ...row } : createEmptyForm()
  visible.value = true
  formRef.value?.clearValidate()
}

/** 层级切换时重置父级 */
function onTypeChange(value) {
  form.value.parentId = value === 1 ? 0 : null
  formRef.value?.clearValidate('parentId')
}

/** 保存分类 */
async function save() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await saveCategory(form.value)
    visible.value = false
    ElMessage.success(form.value.id ? '类别信息已更新' : '类别创建成功')
    await loadData()
  } finally {
    saving.value = false
  }
}

/** 删除分类 */
async function remove(row) {
  await ElMessageBox.confirm(`确定删除"${row.categoryName}"吗？`, '删除类别', {
    type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
  })
  await deleteCategory(row.id)
  ElMessage.success('类别已删除')
  await loadData()
}

/** 根据父级 ID 获取父级名称 */
function parentName(parentId) {
  return list.value.find(item => item.id === parentId)?.categoryName || '父级已失效'
}

/** 格式化时间 */
function formatTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : '未记录'
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { color: var(--text-soft); font-size: 14px; }
.category-toolbar .el-input { width: min(280px, 100%); }
.category-toolbar .el-select { width: 150px; }
.table-wrap :deep(.el-table) { min-width: 730px; }
.category-cell { display: flex; align-items: center; gap: 10px; }
.category-icon { display: grid; width: 32px; height: 32px; flex: 0 0 32px; place-items: center; border-radius: 6px; color: var(--accent); background: var(--accent-bg); }
.category-icon .el-icon { width: 17px; }
.category-cell strong, .category-cell small { display: block; }
.category-cell strong { color: var(--text-h); }
.category-cell small { margin-top: 1px; color: var(--text-soft); font-size: 12px; }
.parent-select { width: 100%; }
@media (max-width: 760px) {
  .page-heading { align-items: stretch; flex-direction: column; }
  .page-heading > .el-button, .category-toolbar > * { width: 100% !important; }
}
</style>
