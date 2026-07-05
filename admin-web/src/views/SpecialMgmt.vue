<!--
  组件名称：SpecialMgmt（促销管理）
  功能描述：后台促销活动管理页面，支持活动列表、新增/编辑/删除促销、配置优惠规则与参与商品
  路由路径：/admin/special/index
-->
<template>
  <div class=”page-special”>
    <!-- 页面标题与新增按钮 -->
    <div class=”page-heading”>
      <div>
        <h2>促销管理</h2>
        <p>配置活动时间、优惠规则和参与商品</p>
      </div>
      <el-button type=”primary” :icon=”Plus” @click=”openEdit()”>新增促销</el-button>
    </div>

    <!-- 概览统计条 -->
    <div class=”summary-strip”>
      <div><span>活动总数</span><strong>{{ list.length }}</strong></div>
      <div><span>进行中</span><strong>{{ activeCount }}</strong></div>
      <div><span>已配置商品</span><strong>{{ goodsTotal }}</strong></div>
    </div>

    <!-- 促销活动列表 -->
    <div class=”table-wrap”>
      <el-table v-loading=”loading” :data=”list” stripe>
        <el-table-column label=”促销活动” min-width=”210”>
          <template #default=”{ row }”>
            <div class=”cell-main”>{{ row.specialName }}</div>
            <div class=”cell-sub”>{{ row.specialSubtitle || '暂无活动说明' }}</div>
          </template>
        </el-table-column>
        <el-table-column label=”优惠规则” min-width=”150”>
          <template #default=”{ row }”>
            <div class=”rule-line”>
              <el-tag size=”small” effect=”plain”>{{ row.badgeText || '优惠' }}</el-tag>
              <strong>{{ ruleText(row) }}</strong>
            </div>
          </template>
        </el-table-column>
        <el-table-column label=”活动时间” min-width=”220”>
          <template #default=”{ row }”>
            <div class=”cell-main time-text”>{{ formatTime(row.startTime, '开始时间不限') }}</div>
            <div class=”cell-sub”>至 {{ formatTime(row.endTime, '结束时间不限') }}</div>
          </template>
        </el-table-column>
        <el-table-column label=”活动商品” width=”105” align=”center”>
          <template #default=”{ row }”><strong>{{ row.goodsCount || 0 }}</strong> 件</template>
        </el-table-column>
        <el-table-column label=”当前状态” width=”105”>
          <template #default=”{ row }”>
            <el-tag :type=”statusMeta(row.effectiveStatus).type” effect=”light”>
              {{ statusMeta(row.effectiveStatus).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label=”首页排序” width=”90” align=”center”>
          <template #default=”{ row }”>{{ row.sortOrder }}</template>
        </el-table-column>
        <el-table-column label=”操作” width=”112” fixed=”right” align=”center”>
          <template #default=”{ row }”>
            <el-tooltip content=”编辑促销” placement=”top”>
              <el-button circle text :icon=”Edit” @click=”openEdit(row)” />
            </el-tooltip>
            <el-tooltip content=”删除促销” placement=”top”>
              <el-button circle text type=”danger” :icon=”Delete” @click=”remove(row)” />
            </el-tooltip>
          </template>
        </el-table-column>
        <template #empty><el-empty description=”暂未配置促销活动” /></template>
      </el-table>
    </div>

    <!-- 新增/编辑促销弹窗 -->
    <el-dialog
      v-model=”visible”
      :title=”form.id ? '编辑促销' : '新增促销'”
      :close-on-click-modal=”false”
      width=”min(760px, 94vw)”
      class=”special-dialog”
    >
      <el-form ref=”formRef” :model=”form” :rules=”rules” label-position=”top”>
        <!-- ====== 基础信息 ====== -->
        <div class=”section-title”>基础信息</div>
        <div class=”form-grid”>
          <el-form-item label=”活动名称” prop=”specialName”>
            <el-input v-model.trim=”form.specialName” maxlength=”50” placeholder=”例如：夏日清凉季” />
          </el-form-item>
          <el-form-item label=”活动角标”>
            <el-input v-model.trim=”form.badgeText” maxlength=”10” placeholder=”例如：限时折扣” />
          </el-form-item>
        </div>
        <el-form-item label=”活动副标题”>
          <el-input v-model.trim=”form.specialSubtitle” maxlength=”100” show-word-limit placeholder=”用于居民端首页的活动说明” />
        </el-form-item>

        <!-- ====== 优惠规则 ====== -->
        <div class=”section-title”>优惠规则</div>
        <div class=”form-grid rule-grid”>
          <el-form-item label=”优惠方式”>
            <el-radio-group v-model=”form.promotionType”>
              <el-radio-button value=”DISCOUNT”>按折扣</el-radio-button>
              <el-radio-button value=”REDUCE”>按金额直减</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label=”form.promotionType === 'DISCOUNT' ? '折扣力度' : '每件直减金额'” prop=”discountValue”>
            <el-input-number
              v-model=”form.discountValue”
              :min=”0.01”
              :max=”form.promotionType === 'DISCOUNT' ? 9.99 : 99999”
              :precision=”2”
              :step=”form.promotionType === 'DISCOUNT' ? 0.1 : 1”
              controls-position=”right”
            />
            <span class=”input-unit”>{{ form.promotionType === 'DISCOUNT' ? '折' : '元' }}</span>
          </el-form-item>
        </div>
        <!-- 活动有效时间范围 -->
        <el-form-item label=”活动有效期”>
          <el-date-picker
            v-model=”form.activeTime”
            type=”datetimerange”
            start-placeholder=”开始时间（可不填）”
            end-placeholder=”结束时间（可不填）”
            range-separator=”至”
            value-format=”YYYY-MM-DDTHH:mm:ss”
            format=”YYYY-MM-DD HH:mm”
            unlink-panels
          />
        </el-form-item>

        <!-- ====== 展示设置 ====== -->
        <div class=”section-title”>展示设置</div>
        <div class=”form-grid display-grid”>
          <el-form-item label=”启用活动”>
            <div class=”switch-row”>
              <el-switch v-model=”form.specialStatus” :active-value=”1” :inactive-value=”2” />
              <span>{{ form.specialStatus === 1 ? '已启用' : '已停用' }}</span>
            </div>
          </el-form-item>
          <el-form-item label=”首页排序”>
            <el-input-number v-model=”form.sortOrder” :min=”0” :max=”999” controls-position=”right” />
            <span class=”field-tip”>数值越小越靠前</span>
          </el-form-item>
          <el-form-item label=”最多展示商品”>
            <el-input-number v-model=”form.maxItems” :min=”1” :max=”20” controls-position=”right” />
            <span class=”field-tip”>1 至 20 件</span>
          </el-form-item>
        </div>

        <!-- ====== 活动商品选择 ====== -->
        <div class=”section-title products-title”>
          <span>活动商品</span>
          <small>已选择 {{ form.goodsNos.length }} 件</small>
        </div>
        <el-form-item prop=”goodsNos”>
          <el-select
            v-model=”form.goodsNos”
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags=”3”
            placeholder=”输入商品名称搜索并选择”
            class=”goods-select”
          >
            <el-option
              v-for=”goods in goodsOptions”
              :key=”goods.goodsNo”
              :label=”goods.goodsName”
              :value=”goods.goodsNo”
            >
              <div class=”goods-option”>
                <span>{{ goods.goodsName }}</span>
                <small>¥{{ formatPrice(goods.goodsMarketPrice) }}</small>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <!-- 已选商品列表 -->
        <div v-if=”selectedGoods.length” class=”selected-goods”>
          <div v-for=”goods in selectedGoods” :key=”goods.goodsNo” class=”selected-row”>
            <el-image :src=”goods.goodsPicture” fit=”cover”>
              <template #error><div class=”image-fallback”><Picture /></div></template>
            </el-image>
            <div>
              <strong>{{ goods.goodsName }}</strong>
              <span v-if=”goods.goodsMarketPrice != null”>
                原价 ¥{{ formatPrice(goods.goodsMarketPrice) }} · 促销价约 ¥{{ previewPrice(goods.goodsMarketPrice) }}
              </span>
              <span v-else>商品暂未定价</span>
            </div>
            <el-button text circle :icon=”Close” aria-label=”移除商品” @click=”removeGoods(goods.goodsNo)” />
          </div>
        </div>
      </el-form>

      <template #footer>
        <el-button @click=”visible = false”>取消</el-button>
        <el-button type=”primary” :loading=”saving” @click=”save”>保存促销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * SpecialMgmt - 后台促销管理
 *
 * 功能：
 * - 促销活动列表展示（含概览统计）
 * - 新增/编辑促销活动（基础信息、优惠规则、展示设置、关联商品）
 * - 删除促销活动
 * - 实时预览促销价
 */

import { computed, onMounted, ref } from 'vue'
import { Close, Delete, Edit, Picture, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteSpecial,
  getGoodsPage,
  getSpecialDetail,
  getSpecialList,
  saveSpecial
} from '@/api'

const list = ref([])          // 促销活动列表
const goodsOptions = ref([])  // 全部商品列表（供选择）
const loading = ref(false)    // 列表加载中
const saving = ref(false)     // 保存中
const visible = ref(false)    // 编辑弹窗可见性
const formRef = ref()         // el-form 引用
const form = ref(createEmptyForm())  // 表单数据

/** 进行中的活动数量 */
const activeCount = computed(() => list.value.filter(item => item.effectiveStatus === 'ACTIVE').length)
/** 全部活动配置的商品总数 */
const goodsTotal = computed(() => list.value.reduce((sum, item) => sum + Number(item.goodsCount || 0), 0))
/** 当前表单中已选择的商品详情 */
const selectedGoods = computed(() => {
  const selected = new Set(form.value.goodsNos)
  return goodsOptions.value.filter(item => selected.has(item.goodsNo))
})

/** 表单校验规则 */
const rules = {
  specialName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  discountValue: [{ required: true, message: '请填写优惠值', trigger: 'change' }],
  goodsNos: [{ type: 'array', required: true, min: 1, message: '请至少选择一件活动商品', trigger: 'change' }]
}

onMounted(async () => {
  await Promise.all([loadData(), loadGoodsOptions()])
})

/** 创建空的促销表单结构 */
function createEmptyForm() {
  return {
    id: null,
    specialName: '',
    specialSubtitle: '社区精选优惠商品',
    badgeText: '限时优惠',
    specialStatus: 1,
    promotionType: 'DISCOUNT',
    discountValue: 9,
    activeTime: [],
    sortOrder: 0,
    maxItems: 4,
    goodsNos: []
  }
}

/** 加载促销活动列表 */
async function loadData() {
  loading.value = true
  try {
    const response = await getSpecialList()
    list.value = response?.data || []
  } finally {
    loading.value = false
  }
}

/** 加载全量商品作为活动商品选项 */
async function loadGoodsOptions() {
  const firstResponse = await getGoodsPage({ page: 1, size: 100 })
  const firstPage = firstResponse?.data
  const allGoods = [...(firstPage?.records || [])]
  const pageCount = Number(firstPage?.pages || 1)
  if (pageCount > 1) {
    // 商品接口单页上限是100，继续读取剩余页，避免后半部分商品无法参加促销。
    const rest = await Promise.all(
      Array.from({ length: pageCount - 1 }, (_, index) => getGoodsPage({ page: index + 2, size: 100 }))
    )
    rest.forEach(response => allGoods.push(...(response?.data?.records || [])))
  }
  goodsOptions.value = allGoods
}

/**
 * 打开新增/编辑促销弹窗
 * @param {Object} row - 促销数据（为空时表示新增）
 */
async function openEdit(row) {
  form.value = createEmptyForm()
  visible.value = true
  formRef.value?.clearValidate()
  if (!row?.id) return
  try {
    const response = await getSpecialDetail(row.id)
    const detail = response?.data || row
    form.value = {
      ...createEmptyForm(),
      ...detail,
      activeTime: detail.startTime || detail.endTime ? [detail.startTime, detail.endTime] : [],
      goodsNos: detail.goodsNos || []
    }
  } catch (error) {
    visible.value = false
  }
}

/** 保存促销活动 */
async function save() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  const [startTime, endTime] = form.value.activeTime || []
  saving.value = true
  try {
    await saveSpecial({ ...form.value, startTime: startTime || null, endTime: endTime || null, activeTime: undefined })
    visible.value = false
    ElMessage.success(form.value.id ? '促销配置已更新' : '促销活动已创建')
    await loadData()
  } finally {
    saving.value = false
  }
}

/**
 * 删除促销活动
 * @param {Object} row - 促销数据
 */
async function remove(row) {
  await ElMessageBox.confirm(`删除”${row.specialName}”后，商品关联也会清除。`, '删除促销', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await deleteSpecial(row.id)
  ElMessage.success('促销活动已删除')
  await loadData()
}

/** 移除已选商品 */
function removeGoods(goodsNo) {
  form.value.goodsNos = form.value.goodsNos.filter(item => item !== goodsNo)
}

/** 生成优惠规则文本 */
function ruleText(row) {
  return row.promotionType === 'REDUCE' ? `每件立减 ¥${formatPrice(row.discountValue)}` : `${Number(row.discountValue)} 折`
}

/** 获取活动当前状态的标签样式 */
function statusMeta(value) {
  return ({
    ACTIVE: { text: '进行中', type: 'success' },
    NOT_STARTED: { text: '未开始', type: 'primary' },
    ENDED: { text: '已结束', type: 'info' },
    STOPPED: { text: '已停用', type: 'warning' }
  })[value] || { text: '未知', type: 'info' }
}

/** 格式化时间 */
function formatTime(value, fallback) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : fallback
}

/** 格式化金额（保留两位小数） */
function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

/** 预览促销价格 */
function previewPrice(marketPrice) {
  const price = Number(marketPrice || 0)
  const value = Number(form.value.discountValue || 0)
  const result = form.value.promotionType === 'REDUCE' ? Math.max(0.01, price - value) : price * value / 10
  return result.toFixed(2)
}
</script>

<style scoped>
.page-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 18px; }
.page-heading h2 { margin-bottom: 2px; }
.page-heading p { color: var(--text-soft); font-size: 14px; }
.summary-strip { display: flex; gap: 1px; margin-bottom: 16px; overflow: hidden; border: 1px solid var(--border); border-radius: var(--radius); background: var(--border); }
.summary-strip > div { display: flex; min-width: 150px; flex: 1; align-items: center; justify-content: space-between; padding: 12px 16px; background: rgba(255, 255, 255, 0.78); }
.summary-strip span { color: var(--text-soft); font-size: 13px; }
.summary-strip strong { color: var(--text-h); font-size: 18px; }
.table-wrap :deep(.el-table) { min-width: 940px; }
.cell-main { color: var(--text-h); font-weight: 600; line-height: 1.45; }
.cell-sub { margin-top: 2px; color: var(--text-soft); font-size: 12px; line-height: 1.45; }
.time-text { font-size: 13px; }
.rule-line { display: flex; align-items: center; gap: 8px; color: var(--text-h); }
.section-title { margin: 6px 0 14px; padding-bottom: 8px; border-bottom: 1px solid var(--border); color: var(--text-h); font-weight: 650; }
.form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
.display-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.rule-grid :deep(.el-input-number) { width: 150px; }
.input-unit { margin-left: 8px; color: var(--text); }
.field-tip { display: block; margin-top: 4px; color: var(--text-soft); font-size: 12px; }
.switch-row { display: flex; align-items: center; gap: 10px; }
.products-title { display: flex; align-items: center; justify-content: space-between; }
.products-title small { color: var(--text-soft); font-weight: 400; }
.goods-select { width: 100%; }
.goods-option { display: flex; align-items: center; justify-content: space-between; gap: 20px; }
.goods-option small { color: #a4262c; }
.selected-goods { max-height: 230px; margin-top: -8px; overflow-y: auto; border: 1px solid var(--border); border-radius: var(--radius); }
.selected-row { display: grid; grid-template-columns: 44px minmax(0, 1fr) 34px; align-items: center; gap: 10px; min-height: 62px; padding: 8px 10px; border-bottom: 1px solid var(--border); }
.selected-row:last-child { border-bottom: 0; }
.selected-row .el-image { width: 44px; height: 44px; border-radius: 6px; background: var(--surface-muted); }
.selected-row strong, .selected-row span { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.selected-row strong { color: var(--text-h); font-size: 13px; }
.selected-row span { margin-top: 2px; color: var(--text-soft); font-size: 12px; }
.image-fallback { display: grid; width: 100%; height: 100%; place-items: center; color: var(--text-soft); }
@media (max-width: 760px) {
  .page-heading { align-items: stretch; flex-direction: column; }
  .page-heading > .el-button { width: 100%; }
  .summary-strip { overflow-x: auto; }
  .summary-strip > div { flex: 0 0 145px; }
  .form-grid, .display-grid { grid-template-columns: 1fr; gap: 0; }
  .selected-row span { white-space: normal; }
}
</style>
