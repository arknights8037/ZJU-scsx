<template>
  <div class="page-category">
    <div class="page-heading">
      <div>
        <h2>类别管理</h2>
        <p>维护居民端和商品管理共同使用的中文分类</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增类别</el-button>
    </div>

    <div class="page-toolbar category-toolbar">
      <el-input v-model.trim="keyword" clearable :prefix-icon="Search" placeholder="搜索类别名称" />
      <el-select v-model="typeFilter" clearable placeholder="全部层级">
        <el-option label="一级类别" :value="1" />
        <el-option label="二级类别" :value="2" />
      </el-select>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="filteredList" stripe>
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
import { computed, onMounted, ref } from 'vue'
import { Delete, Document, Edit, FolderOpened, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCategory, getCategoryList, saveCategory } from '@/api'

const list = ref([])
const keyword = ref('')
const typeFilter = ref(null)
const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const formRef = ref()
const form = ref(createEmptyForm())

const topCategories = computed(() => list.value.filter(item => item.categoryType === 1 || !item.parentId))
const filteredList = computed(() => list.value.filter(item => {
  const matchesKeyword = !keyword.value || item.categoryName?.includes(keyword.value)
  const matchesType = typeFilter.value == null || item.categoryType === typeFilter.value
  return matchesKeyword && matchesType
}))

const rules = {
  categoryName: [{ required: true, message: '请输入类别名称', trigger: 'blur' }],
  categoryType: [{ required: true, message: '请选择类别层级', trigger: 'change' }],
  parentId: [{ required: true, message: '请选择所属父级', trigger: 'change' }]
}

onMounted(loadData)

function createEmptyForm() {
  return { id: null, categoryName: '', categoryType: 1, parentId: 0 }
}

async function loadData() {
  loading.value = true
  try {
    const response = await getCategoryList()
    list.value = response?.data || []
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  form.value = row ? { ...row } : createEmptyForm()
  visible.value = true
  formRef.value?.clearValidate()
}

function onTypeChange(value) {
  form.value.parentId = value === 1 ? 0 : null
  formRef.value?.clearValidate('parentId')
}

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

async function remove(row) {
  await ElMessageBox.confirm(`确定删除“${row.categoryName}”吗？`, '删除类别', {
    type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消'
  })
  await deleteCategory(row.id)
  ElMessage.success('类别已删除')
  await loadData()
}

function parentName(parentId) {
  return list.value.find(item => item.id === parentId)?.categoryName || '父级已失效'
}

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
