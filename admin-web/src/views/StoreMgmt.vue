<template>
  <div class="page-store">
    <div class="page-heading">
      <div>
        <h2>门店管理</h2>
        <p>维护社区服务门店、所属区域与营业状态</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增门店</el-button>
    </div>

    <div class="page-toolbar store-toolbar">
      <el-input
        v-model.trim="query.keyword"
        clearable
        placeholder="搜索门店名称、编号或地址"
        :prefix-icon="Search"
        @keyup.enter="search"
      />
      <el-select v-model="query.areaId" clearable filterable placeholder="全部社区">
        <el-option v-for="area in areas" :key="area.id" :label="area.areaName" :value="area.id" />
      </el-select>
      <el-select v-model="query.status" clearable placeholder="全部状态">
        <el-option label="正常营业" :value="1" />
        <el-option label="暂停营业" :value="2" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="search">查询</el-button>
      <el-button :icon="RefreshLeft" @click="resetQuery">重置</el-button>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="门店" min-width="190">
          <template #default="{ row }">
            <div class="cell-main">{{ row.storeName || '未命名门店' }}</div>
            <div class="cell-sub" :title="row.storeNo">编号 #{{ shortCode(row.storeNo) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="所属社区" min-width="140">
          <template #default="{ row }">
            <div class="area-cell"><Location class="area-icon" />{{ row.areaName || '未绑定社区' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="storeAddress" label="门店地址" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">{{ row.storeAddress || '暂未填写地址' }}</template>
        </el-table-column>
        <el-table-column label="营业时间" min-width="135">
          <template #default="{ row }">{{ businessHours(row) }}</template>
        </el-table-column>
        <el-table-column label="营业状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.storeStatus === 1 ? 'success' : 'info'" effect="light">
              {{ row.storeStatus === 1 ? '正常营业' : '暂停营业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="112" fixed="right" align="center">
          <template #default="{ row }">
            <el-tooltip content="编辑门店" placement="top">
              <el-button circle text :icon="Edit" @click="openEdit(row)" />
            </el-tooltip>
            <el-tooltip content="删除门店" placement="top">
              <el-button circle text type="danger" :icon="Delete" @click="remove(row)" />
            </el-tooltip>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="没有找到符合条件的门店" />
        </template>
      </el-table>

      <div class="pagination-row">
        <span>共 {{ total }} 家门店</span>
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-dialog
      v-model="visible"
      :title="form.id ? '编辑门店' : '新增门店'"
      :close-on-click-modal="false"
      width="min(620px, 92vw)"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="门店名称" prop="storeName">
            <el-input v-model.trim="form.storeName" placeholder="例如：龙湖社区生活服务站" />
          </el-form-item>
          <el-form-item label="所属社区" prop="areaId">
            <el-select v-model="form.areaId" filterable placeholder="请选择社区">
              <el-option v-for="area in areas" :key="area.id" :label="area.areaName" :value="area.id" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="门店编号">
          <el-input v-model.trim="form.storeNo" :disabled="Boolean(form.id)" placeholder="留空则由系统自动生成" />
        </el-form-item>
        <el-form-item label="门店地址" prop="storeAddress">
          <el-input v-model.trim="form.storeAddress" placeholder="请输入便于居民识别的详细地址" />
        </el-form-item>
        <el-form-item label="门店介绍">
          <el-input v-model="form.storeIntroduce" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="简要介绍服务内容" />
        </el-form-item>
        <el-form-item label="营业状态">
          <div class="switch-row">
            <el-switch v-model="form.storeStatus" :active-value="1" :inactive-value="2" />
            <span>{{ form.storeStatus === 1 ? '正常营业' : '暂停营业' }}</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存门店</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Location, Plus, RefreshLeft, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteStore, getAreaList, getStorePage, saveStore } from '@/api'

const query = reactive({ page: 1, size: 10, keyword: '', areaId: null, status: null })
const list = ref([])
const total = ref(0)
const areas = ref([])
const loading = ref(false)
const visible = ref(false)
const saving = ref(false)
const formRef = ref()
const form = ref(createEmptyForm())

const rules = {
  storeName: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  areaId: [{ required: true, message: '请选择所属社区', trigger: 'change' }],
  storeAddress: [{ required: true, message: '请输入门店地址', trigger: 'blur' }]
}

onMounted(async () => {
  await Promise.all([loadAreas(), loadData()])
})

function createEmptyForm() {
  return { storeName: '', storeNo: '', storeAddress: '', areaId: null, storeIntroduce: '', storeStatus: 1 }
}

async function loadAreas() {
  const response = await getAreaList()
  areas.value = response?.data || []
}

// 后端返回分页对象，records 是当前页数据，total 是数据库中的总条数。
async function loadData() {
  loading.value = true
  try {
    const response = await getStorePage(query)
    list.value = response?.data?.records || []
    total.value = Number(response?.data?.total || 0)
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  loadData()
}

function resetQuery() {
  query.keyword = ''
  query.areaId = null
  query.status = null
  search()
}

function handleSizeChange() {
  query.page = 1
  loadData()
}

function openEdit(row) {
  form.value = row ? { ...row } : createEmptyForm()
  visible.value = true
  formRef.value?.clearValidate()
}

async function save() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await saveStore(form.value)
    visible.value = false
    ElMessage.success(form.value.id ? '门店信息已更新' : '门店创建成功')
    await loadData()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除“${row.storeName}”吗？`, '删除门店', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await deleteStore(row.id)
  ElMessage.success('门店已删除')
  if (list.value.length === 1 && query.page > 1) query.page -= 1
  await loadData()
}

function shortCode(value) {
  if (!value) return '--'
  const text = String(value)
  return text.length > 10 ? text.slice(-8).toUpperCase() : text
}

function businessHours(row) {
  if (!row.startTime || !row.closeTime) return '暂未设置'
  return `${formatTime(row.startTime)} - ${formatTime(row.closeTime)}`
}

function formatTime(value) {
  const text = String(value)
  return text.includes('T') ? text.split('T')[1].slice(0, 5) : text.slice(0, 5)
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { font-size: 14px; color: var(--text-soft); }
.store-toolbar .el-input { width: min(300px, 100%); }
.store-toolbar .el-select { width: 160px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.area-cell { display: flex; align-items: center; gap: 6px; color: var(--text-h); }
.area-icon { width: 16px; color: var(--accent); }
.table-wrap :deep(.el-table) { min-width: 920px; }
.pagination-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 14px 16px; border-top: 1px solid var(--border); color: var(--text-soft); font-size: 13px; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
.form-grid .el-select { width: 100%; }
.switch-row { display: flex; align-items: center; gap: 10px; }
@media (max-width: 760px) {
  .page-heading { align-items: stretch; flex-direction: column; }
  .page-heading > .el-button { width: 100%; }
  .store-toolbar > * { width: 100% !important; }
  .pagination-row { align-items: flex-start; flex-direction: column; }
  .form-grid { grid-template-columns: 1fr; gap: 0; }
}
</style>
