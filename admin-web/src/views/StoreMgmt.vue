<template>
  <div class="page-store">
    <!-- 页面标题与新增按钮 -->
    <div class="page-heading">
      <div>
        <h2>门店管理</h2>
        <p>维护社区服务门店、所属区域与营业状态</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openEdit()">新增门店</el-button>
    </div>

    <!-- 搜索与筛选工具栏 -->
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

    <!-- 门店列表 -->
    <div class="table-wrap">
      <el-table v-loading="loading" :data="list" stripe>
        <!-- 门店名称与编号列 -->
        <el-table-column label="门店" min-width="190">
          <template #default="{ row }">
            <div class="cell-main">{{ row.storeName || '未命名门店' }}</div>
            <div class="cell-sub" :title="row.storeNo">编号 #{{ shortCode(row.storeNo) }}</div>
          </template>
        </el-table-column>
        <!-- 所属社区列 -->
        <el-table-column label="所属社区" min-width="140">
          <template #default="{ row }">
            <div class="area-cell"><Location class="area-icon" />{{ row.areaName || '未绑定社区' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="storeAddress" label="门店地址" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">{{ row.storeAddress || '暂未填写地址' }}</template>
        </el-table-column>
        <!-- 营业时间列 -->
        <el-table-column label="营业时间" min-width="135">
          <template #default="{ row }">{{ businessHours(row) }}</template>
        </el-table-column>
        <!-- 营业状态标签列 -->
        <el-table-column label="营业状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.storeStatus === 1 ? 'success' : 'info'" effect="light">
              {{ row.storeStatus === 1 ? '正常营业' : '暂停营业' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 商品概况列（商品数量和库存） -->
        <el-table-column label="商品概况" width="130">
          <template #default="{ row }">
            <div class="cell-main">{{ row.goodsCount || 0 }} 个商品</div>
            <div class="cell-sub">库存 {{ row.totalStock || 0 }}</div>
          </template>
        </el-table-column>
        <!-- 操作列（编辑、删除、商品管理） -->
        <el-table-column label="操作" width="154" fixed="right" align="center">
          <template #default="{ row }">
            <el-tooltip content="维护店铺商品" placement="top">
              <el-button circle text :icon="GoodsIcon" @click="openStoreGoods(row)" />
            </el-tooltip>
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

    <!-- 分页栏 -->
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

    <!-- 新增/编辑门店弹窗 -->
    <el-dialog
      v-model="visible"
      :title="form.id ? '编辑门店' : '新增门店'"
      :close-on-click-modal="false"
      width="min(620px, 92vw)"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <!-- 门店名称输入 -->
          <el-form-item label="门店名称" prop="storeName">
            <el-input v-model.trim="form.storeName" placeholder="例如：龙湖社区生活服务站" />
          </el-form-item>
          <!-- 所属社区下拉选择 -->
          <el-form-item label="所属社区" prop="areaId">
            <el-select v-model="form.areaId" filterable placeholder="请选择社区">
              <el-option v-for="area in areas" :key="area.id" :label="area.areaName" :value="area.id" />
            </el-select>
          </el-form-item>
        </div>
        <!-- 门店编号输入（编辑时不可修改） -->
        <el-form-item label="门店编号">
          <el-input v-model.trim="form.storeNo" :disabled="Boolean(form.id)" placeholder="留空则由系统自动生成" />
        </el-form-item>
        <el-form-item label="门店地址" prop="storeAddress">
          <el-input v-model.trim="form.storeAddress" placeholder="请输入便于居民识别的详细地址" />
        </el-form-item>
        <!-- 门店介绍文本域 -->
        <el-form-item label="门店介绍">
          <el-input v-model="form.storeIntroduce" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="简要介绍服务内容" />
        </el-form-item>
        <!-- 营业状态开关 -->
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

    <!-- 门店商品关系管理抽屉 -->
    <el-drawer
      v-model="goodsDrawerVisible"
      :title="selectedStore ? `${selectedStore.storeName} · 商品关系` : '店铺商品关系'"
      size="min(880px, 96vw)"
      :close-on-click-modal="false"
    >
      <div v-if="selectedStore" class="store-goods-drawer">
        <!-- 门店商品概览统计 -->
        <section class="store-goods-summary">
          <div>
            <span>门店编号</span>
            <strong>{{ selectedStore.storeNo }}</strong>
          </div>
          <div>
            <span>关联商品</span>
            <strong>{{ storeGoodsList.length }} 个</strong>
          </div>
          <div>
            <span>总库存</span>
            <strong>{{ storeGoodsList.reduce((sum, item) => sum + Number(item.goodsStock || 0), 0) }}</strong>
          </div>
        </section>

        <!-- 添加/编辑门店商品表单 -->
        <section class="relation-editor">
          <div class="section-title">
            <h3>{{ relationForm.id ? '编辑门店商品' : '添加门店商品' }}</h3>
            <p>这里维护的是该门店自己的销售价和库存，不会改商品主档的市场价。</p>
          </div>
          <div class="relation-form">
            <!-- 商品选择下拉 -->
            <el-select
              v-model="relationForm.goodsNo"
              filterable
              clearable
              :disabled="Boolean(relationForm.id)"
              placeholder="选择商品"
              @change="onRelationGoodsChange"
            >
              <el-option
                v-for="goods in allGoods"
                :key="goods.goodsNo"
                :label="goods.goodsName"
                :value="goods.goodsNo"
              >
                <span>{{ goods.goodsName }}</span>
                <span class="option-code">{{ goods.goodsNo }}</span>
              </el-option>
            </el-select>
            <!-- 门店价与库存输入 -->
            <el-input-number v-model="relationForm.goodsPrice" :min="0.01" :precision="2" controls-position="right" placeholder="门店价" />
            <el-input-number v-model="relationForm.goodsStock" :min="0" :max="999999" :step="10" controls-position="right" placeholder="库存" />
            <el-button type="primary" :loading="relationSaving" @click="saveRelation">
              {{ relationForm.id ? '保存关系' : '添加商品' }}
            </el-button>
            <el-button v-if="relationForm.id" @click="resetRelationForm">取消编辑</el-button>
          </div>
        </section>

        <!-- 门店商品关联列表 -->
        <el-table v-loading="storeGoodsLoading" :data="storeGoodsList" stripe>
          <el-table-column label="商品" min-width="240">
            <template #default="{ row }">
              <div class="goods-cell">
                <el-image v-if="row.goodsPicture" :src="row.goodsPicture" fit="cover" class="goods-thumb" />
                <span v-else class="goods-thumb empty">图</span>
                <div>
                  <div class="cell-main">{{ row.goodsName }}</div>
                  <div class="cell-sub">{{ row.goodsNo }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="110" />
          <el-table-column label="商品状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.goodsState === 1 ? 'success' : 'info'" effect="light">
                {{ row.goodsState === 1 ? '上架' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="门店价" width="110">
            <template #default="{ row }">¥{{ formatMoney(row.goodsPrice) }}</template>
          </el-table-column>
          <el-table-column prop="goodsStock" label="库存" width="90" />
          <el-table-column label="操作" width="130" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="editRelation(row)">编辑</el-button>
              <el-button link type="danger" @click="removeRelation(row)">移除</el-button>
            </template>
          </el-table-column>
          <template #empty><el-empty description="该门店还没有关联商品" /></template>
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
/**
 * StoreMgmt - 后台门店管理
 *
 * 功能：
 * - 门店列表分页展示，支持按关键词/社区/营业状态搜索筛选
 * - 新增/编辑/删除门店（名称、地址、所属社区、营业状态）
 * - 门店商品关系维护（添加/编辑/移除商品、设置门店价和库存）
 * - 门店商品价格与库存独立管理，不影响商品主档数据
 */
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Delete, Edit, Goods as GoodsIcon, Location, Plus, RefreshLeft, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteStore, deleteStoreGoods, getAreaList, getGoodsPage, getStoreGoods, getStorePage, saveStore, saveStoreGoods } from '@/api'

const route = useRoute()

/** 门店列表查询参数（页码、每页条数、关键词、所属社区、营业状态） */
const query = reactive({
  page: 1,
  size: 10,
  keyword: typeof route.query.keyword === 'string' ? route.query.keyword : '',
  areaId: null,
  status: null
})
const list = ref([])                     // 门店列表数据
const total = ref(0)                     // 门店总数（分页用）
const areas = ref([])                    // 社区列表（供筛选下拉使用）
const loading = ref(false)               // 列表加载状态
const visible = ref(false)               // 门店编辑弹窗可见性
const saving = ref(false)                // 门店保存中
const goodsDrawerVisible = ref(false)    // 门店商品抽屉可见性
const storeGoodsLoading = ref(false)     // 门店商品列表加载中
const relationSaving = ref(false)        // 商品关系保存中
const selectedStore = ref(null)          // 当前选中的门店（用于商品管理抽屉）
const storeGoodsList = ref([])           // 当前门店的商品列表
const allGoods = ref([])                 // 全部商品列表（供添加门店商品时选择）
const formRef = ref()                    // 门店表单 el-form 引用
const form = ref(createEmptyForm())      // 门店编辑表单数据
const relationForm = reactive(createEmptyRelationForm())  // 门店商品关系表单数据

/** 门店表单校验规则 */
const rules = {
  storeName: [{ required: true, message: '请输入门店名称', trigger: 'blur' }],
  areaId: [{ required: true, message: '请选择所属社区', trigger: 'change' }],
  storeAddress: [{ required: true, message: '请输入门店地址', trigger: 'blur' }]
}

onMounted(async () => {
  // 同时加载社区列表、门店列表和全部商品，互不依赖，并行请求提高性能
  await Promise.all([loadAreas(), loadData(), loadAllGoods()])
})

/** 监听路由参数 keyword 变化，自动重新搜索 */
watch(() => route.query.keyword, (keyword) => {
  const nextKeyword = typeof keyword === 'string' ? keyword : ''
  if (nextKeyword === query.keyword) return
  query.keyword = nextKeyword
  query.page = 1
  loadData()
})

/** 创建空白门店表单结构 */
function createEmptyForm() {
  return { storeName: '', storeNo: '', storeAddress: '', areaId: null, storeIntroduce: '', storeStatus: 1 }
}

/** 创建空白门店商品关系表单结构 */
function createEmptyRelationForm() {
  return { id: null, goodsNo: '', goodsPrice: 0, goodsStock: 0, goodsType: 1 }
}

/** 加载社区列表（供门店表单和搜索筛选使用） */
async function loadAreas() {
  const response = await getAreaList()
  areas.value = response?.data || []
}

/** 加载门店列表（根据当前查询参数分页） */
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

/** 加载全部商品列表（供门店商品关系抽屉中选择商品使用，上限 300 条） */
async function loadAllGoods() {
  const response = await getGoodsPage({ page: 1, size: 300 })
  allGoods.value = response?.data?.records || []
}

/** 触发搜索（重置到第一页） */
function search() {
  query.page = 1
  loadData()
}

/** 重置查询条件 */
function resetQuery() {
  query.keyword = ''
  query.areaId = null
  query.status = null
  search()
}

/** 每页条数变化时重置到第一页 */
function handleSizeChange() {
  query.page = 1
  loadData()
}

/**
 * 打开新增/编辑门店弹窗
 * @param {Object} row - 门店数据（空对象时表示新增）
 */
function openEdit(row) {
  form.value = row ? { ...row } : createEmptyForm()
  visible.value = true
  formRef.value?.clearValidate()
}

/** 保存门店（新增或编辑） */
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

/** 删除门店 */
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

/**
 * 打开门店商品关系管理抽屉
 * @param {Object} row - 门店数据
 */
async function openStoreGoods(row) {
  selectedStore.value = row
  goodsDrawerVisible.value = true
  resetRelationForm()
  await loadStoreGoods()
}

/** 加载当前门店的商品关联列表 */
async function loadStoreGoods() {
  if (!selectedStore.value?.storeNo) return
  storeGoodsLoading.value = true
  try {
    const response = await getStoreGoods(selectedStore.value.storeNo)
    storeGoodsList.value = response?.data || []
  } finally {
    storeGoodsLoading.value = false
  }
}

/** 选择商品后自动填充门店价为商品市场价（仅首次未填时） */
function onRelationGoodsChange(goodsNo) {
  const goods = allGoods.value.find(item => item.goodsNo === goodsNo)
  if (goods && !relationForm.goodsPrice) relationForm.goodsPrice = Number(goods.goodsMarketPrice || 0)
}

/** 保存门店-商品关系（新增或更新） */
async function saveRelation() {
  if (!selectedStore.value?.storeNo) return
  if (!relationForm.goodsNo) {
    ElMessage.warning('请先选择商品')
    return
  }
  relationSaving.value = true
  try {
    await saveStoreGoods(selectedStore.value.storeNo, relationForm)
    ElMessage.success(relationForm.id ? '门店商品关系已更新' : '商品已添加到门店')
    resetRelationForm()
    await loadStoreGoods()
    await loadData()
  } finally {
    relationSaving.value = false
  }
}

/** 编辑门店商品关系（回填已有数据到表单） */
function editRelation(row) {
  Object.assign(relationForm, {
    id: row.id,
    goodsNo: row.goodsNo,
    goodsPrice: Number(row.goodsPrice || 0),
    goodsStock: Number(row.goodsStock || 0),
    goodsType: row.goodsType || 1
  })
}

/** 重置门店商品关系表单 */
function resetRelationForm() {
  Object.assign(relationForm, createEmptyRelationForm())
}

/** 移除门店中的商品关联 */
async function removeRelation(row) {
  await ElMessageBox.confirm(`确认从门店移除“${row.goodsName}”吗？`, '移除商品', {
    type: 'warning',
    confirmButtonText: '移除',
    cancelButtonText: '取消'
  })
  await deleteStoreGoods(selectedStore.value.storeNo, row.id)
  ElMessage.success('门店商品关系已移除')
  await loadStoreGoods()
  await loadData()
}

/** 截取字符串末尾 8 位作为短码显示 */
function shortCode(value) {
  if (!value) return '--'
  const text = String(value)
  return text.length > 10 ? text.slice(-8).toUpperCase() : text
}

/** 获取门店营业时间文本 */
function businessHours(row) {
  if (!row.startTime || !row.closeTime) return '暂未设置'
  return `${formatTime(row.startTime)} - ${formatTime(row.closeTime)}`
}

/** 格式化时间（只取 HH:mm 部分） */
function formatTime(value) {
  const text = String(value)
  return text.includes('T') ? text.split('T')[1].slice(0, 5) : text.slice(0, 5)
}

/** 格式化金额（保留两位小数） */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
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
.store-goods-drawer { display: grid; gap: 16px; }
.store-goods-summary { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 10px; }
.store-goods-summary > div { padding: 14px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--bg-secondary); }
.store-goods-summary span, .store-goods-summary strong { display: block; }
.store-goods-summary span { margin-bottom: 6px; color: var(--text-soft); font-size: 12px; }
.store-goods-summary strong { color: var(--text-h); overflow-wrap: anywhere; }
.relation-editor { padding: 16px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface); }
.section-title { margin-bottom: 12px; }
.section-title h3 { margin-bottom: 4px; }
.section-title p { color: var(--text-soft); font-size: 13px; }
.relation-form { display: grid; grid-template-columns: minmax(220px, 1fr) 150px 140px auto auto; gap: 10px; align-items: center; }
.option-code { float: right; color: var(--text-soft); font-size: 12px; }
.goods-cell { display: flex; align-items: center; gap: 10px; }
.goods-thumb { width: 44px; height: 44px; border: 1px solid var(--border); border-radius: 8px; flex: 0 0 44px; }
.goods-thumb.empty { display: grid; place-items: center; background: var(--bg-secondary); color: var(--text-soft); font-size: 12px; }
@media (max-width: 760px) {
  .page-heading { align-items: stretch; flex-direction: column; }
  .page-heading > .el-button { width: 100%; }
  .store-toolbar > * { width: 100% !important; }
  .pagination-row { align-items: flex-start; flex-direction: column; }
  .form-grid { grid-template-columns: 1fr; gap: 0; }
  .store-goods-summary { grid-template-columns: 1fr; }
  .relation-form { grid-template-columns: 1fr; }
}
</style>
