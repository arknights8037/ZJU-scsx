<template>
  <div class="goods-list-page">
    <section class="shop-hero">
      <div>
        <span class="eyebrow">Community Mall</span>
        <h2>{{ pageTitle }}</h2>
        <p>按分类、价格、库存和排序快速找到合适商品，也可以直接查看当前生效的优惠商品。</p>
      </div>
      <div class="shop-summary">
        <strong>{{ total }}</strong>
        <span>{{ viewMode === 'promotion' ? '件优惠商品' : '件在售商品' }}</span>
      </div>
    </section>

    <section class="filter-panel">
      <div class="mode-row">
        <el-button :type="viewMode === 'all' ? 'primary' : 'default'" :icon="Goods" @click="switchMode('all')">
          全部商品
        </el-button>
        <el-button :type="viewMode === 'promotion' ? 'primary' : 'default'" :icon="Present" @click="switchMode('promotion')">
          优惠商品
        </el-button>
      </div>

      <div class="filter-main">
        <el-input
          v-model.trim="filters.keyword"
          :prefix-icon="Search"
          clearable
          placeholder="搜索商品名称"
          @keyup.enter="applyFilters"
        />
        <el-select v-model="filters.categoryId" clearable filterable placeholder="全部分类">
          <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
        </el-select>
        <el-select v-model="filters.sort" placeholder="排序">
          <el-option label="最新上架" value="newest" />
          <el-option label="价格从低到高" value="priceAsc" />
          <el-option label="价格从高到低" value="priceDesc" />
        </el-select>
      </div>

      <div class="preset-row">
        <span class="preset-label">价格</span>
        <el-tag
          v-for="item in pricePresets"
          :key="item.key"
          :effect="activePricePreset === item.key ? 'dark' : 'plain'"
          round
          @click="choosePricePreset(item)"
        >
          {{ item.label }}
        </el-tag>
      </div>

      <div v-if="viewMode === 'promotion' && specials.length" class="preset-row">
        <span class="preset-label">活动</span>
        <el-tag
          :effect="activeSpecialId === null ? 'dark' : 'plain'"
          round
          @click="chooseSpecial(null)"
        >
          全部优惠
        </el-tag>
        <el-tag
          v-for="special in specials"
          :key="special.id"
          :effect="activeSpecialId === special.id ? 'dark' : 'plain'"
          round
          @click="chooseSpecial(special.id)"
        >
          {{ special.specialName }}
        </el-tag>
      </div>

      <div class="tag-action-row">
        <div v-if="quickCategories.length" class="quick-cats">
          <span class="preset-label">分类</span>
          <el-tag
            v-for="cat in quickCategories"
            :key="cat.id"
            :effect="filters.categoryId === cat.id ? 'dark' : 'plain'"
            round
            @click="chooseCategory(cat.id)"
          >
            {{ cat.categoryName }}
          </el-tag>
        </div>
        <div class="filter-actions">
          <el-switch v-model="filters.hasStock" active-text="只看有货" />
          <el-button type="primary" :icon="Search" @click="applyFilters">筛选</el-button>
          <el-button :icon="Refresh" @click="resetFilters">重置</el-button>
        </div>
      </div>
    </section>

    <div class="goods-grid" v-if="goodsList.length">
      <div v-for="item in goodsList" :key="item.goodsNo" class="goods-card" @click="goDetail(item)">
        <span v-if="item.badgeText || item.promotionLabel" class="promotion-badge">
          {{ item.badgeText || item.promotionLabel }}
        </span>
        <el-image :src="assetUrl(item.goodsPicture)" fit="cover" class="goods-img">
          <template #error><div class="img-placeholder">暂无图片</div></template>
        </el-image>
        <div class="goods-info">
          <p class="goods-name">{{ item.goodsName }}</p>
          <small v-if="item.specialName" class="promotion-meta">{{ item.specialName }} · {{ item.promotionLabel }}</small>
          <div class="goods-meta-row" @click.stop>
            <p class="goods-price">
              ¥{{ formatPrice(item.promotionPrice ?? item.goodsMarketPrice) }}
              <del v-if="item.promotionPrice != null && item.goodsMarketPrice != null">¥{{ formatPrice(item.goodsMarketPrice) }}</del>
            </p>
            <el-button
              class="quick-icon-btn"
              circle
              text
              type="primary"
              :icon="ShoppingCart"
              :loading="busyKey === `${item.goodsNo}:cart`"
              aria-label="加入购物车"
              title="加入购物车"
              @click="quickAddCart(item)"
            />
            <el-button
              class="quick-icon-btn"
              circle
              text
              type="primary"
              :icon="Star"
              :loading="busyKey === `${item.goodsNo}:fav`"
              aria-label="收藏"
              title="收藏"
              @click="quickFavorite(item)"
            />
          </div>
        </div>
      </div>
    </div>

    <div v-else class="page-empty">
      <p>暂无商品</p>
    </div>

    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        background
        layout="prev, pager, next"
        :pager-count="5"
        :total="total"
        :page-size="pageSize"
        @current-change="loadGoods"
      />
    </div>
  </div>
</template>

<script setup>
/**
 * GoodsList - 门户端商品列表页
 *
 * 功能：
 * - 支持全部商品 / 优惠商品两种视图模式切换
 * - 多维度筛选：关键词、分类、价格区间、排序、仅看有货
 * - 优惠商品模式支持按活动筛选，前端内存分页
 * - 快速加入购物车和收藏（需登录）
 *
 * 路由路径：/portal/goods（支持 query 参数：keyword, categoryId, minPrice, maxPrice, hasStock, sort, promotion）
 */

import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods, Present, Refresh, Search, ShoppingCart, Star } from '@element-plus/icons-vue'
import { addFavorite, addToCart, getCategories, getGoodsPage, getGoodsStores, getSpecialGoods, getSpecialList } from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const route = useRoute()
const router = useRouter()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const goodsList = ref([])              // 当前展示的商品列表数据
const categories = ref([])             // 全部分类列表
const specials = ref([])               // 促销活动列表
const activeSpecialId = ref(null)      // 当前选中的促销活动 ID
const viewMode = ref(route.query.promotion === 'true' ? 'promotion' : 'all')  // 视图模式：'all'（全部商品）或 'promotion'（优惠商品）
const currentPage = ref(1)             // 当前页码
const pageSize = ref(12)               // 每页条数
const total = ref(0)                   // 商品总数
const busyKey = ref('')                // 当前正在操作的商品标识（用于按钮 loading）

// 价格快捷预设选项
const pricePresets = [
  { key: 'all', label: '不限', min: null, max: null },
  { key: '0-10', label: '¥10以下', min: 0, max: 10 },
  { key: '10-30', label: '¥10-30', min: 10, max: 30 },
  { key: '30-50', label: '¥30-50', min: 30, max: 50 },
  { key: '50-100', label: '¥50-100', min: 50, max: 100 },
  { key: '100+', label: '¥100以上', min: 100, max: null }
]
const activePricePreset = ref('all')   // 当前激活的价格预设 key

// 筛选条件，从 URL query 中初始化
const filters = ref({
  keyword: textQuery('keyword'),
  categoryId: numberQuery('categoryId'),
  minPrice: numberQuery('minPrice'),
  maxPrice: numberQuery('maxPrice'),
  hasStock: route.query.hasStock === 'true',
  sort: textQuery('sort') || 'newest'
})

// ========== 计算属性 ==========
/** 可展示的分类选项（排除仅作父分类的条目） */
const categoryOptions = computed(() => categories.value.filter(cat => cat.categoryType !== 1 || !hasChildren(cat.id)))
/** 快捷分类标签（取前 8 个分类） */
const quickCategories = computed(() => categoryOptions.value.slice(0, 8))
/** 页面标题，根据视图模式和搜索关键词动态显示 */
const pageTitle = computed(() => {
  if (viewMode.value === 'promotion') return '优惠商品'
  return route.query.keyword ? `搜索: "${route.query.keyword}"` : '社区商品'
})

// ========== 生命周期 ==========
onMounted(async () => {
  activePricePreset.value = matchingPricePreset()
  await Promise.all([loadCategories(), loadSpecials()])
  loadGoods()
})

// ========== 路由查询参数变化监听 ==========
watch(() => route.query, query => {
  viewMode.value = query.promotion === 'true' ? 'promotion' : 'all'
  filters.value.keyword = typeof query.keyword === 'string' ? query.keyword : ''
  filters.value.categoryId = numberQuery('categoryId')
  filters.value.minPrice = numberQuery('minPrice')
  filters.value.maxPrice = numberQuery('maxPrice')
  filters.value.hasStock = query.hasStock === 'true'
  filters.value.sort = textQuery('sort') || 'newest'
  activePricePreset.value = matchingPricePreset()
  currentPage.value = 1
  loadGoods()
}, { deep: true })

/**
 * 加载商品列表
 * - 校验价格范围合法性
 * - 根据 viewMode 区分普通分页查询和优惠商品前端分页查询
 */
async function loadGoods() {
  try {
    // 校验最低价不能高于最高价
    if (filters.value.minPrice != null && filters.value.maxPrice != null && filters.value.minPrice > filters.value.maxPrice) {
      ElMessage.warning('最低价不能高于最高价')
      return
    }
    if (viewMode.value === 'promotion') {
      await loadPromotionGoods()
      return
    }
    const res = await getGoodsPage({
      page: currentPage.value,
      size: pageSize.value,
      keyword: filters.value.keyword || undefined,
      categoryId: filters.value.categoryId || undefined,
      minPrice: filters.value.minPrice ?? undefined,
      maxPrice: filters.value.maxPrice ?? undefined,
      hasStock: filters.value.hasStock || undefined,
      sort: filters.value.sort
    })
    if (res?.data) {
      goodsList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 加载促销活动列表
 */
async function loadSpecials() {
  try {
    const res = await getSpecialList()
    specials.value = res?.data || []
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 加载优惠商品（前端做内存分页和筛选）
 * - 根据 activeSpecialId 过滤目标活动
 * - 拉取活动下的商品数据，按 goodsNo 去重
 * - 前端执行关键词、分类、价格筛选和排序
 * - 在前端进行分页
 */
async function loadPromotionGoods() {
  if (!specials.value.length) {
    goodsList.value = []
    total.value = 0
    return
  }
  // 确定需要加载的活动列表
  const targetSpecials = activeSpecialId.value == null
    ? specials.value
    : specials.value.filter(item => item.id === activeSpecialId.value)
  const results = await Promise.allSettled(targetSpecials.map(async special => {
    const res = await getSpecialGoods(special.id)
    return (res?.data || []).map(goods => ({ ...goods, specialId: special.id, specialName: special.specialName }))
  }))
  // 用 Map 按 goodsNo 去重
  const map = new Map()
  for (const result of results) {
    if (result.status !== 'fulfilled') continue
    for (const goods of result.value) {
      if (!map.has(goods.goodsNo)) map.set(goods.goodsNo, goods)
    }
  }
  let list = Array.from(map.values())
  // 前端筛选
  if (filters.value.keyword) {
    list = list.filter(item => (item.goodsName || '').includes(filters.value.keyword))
  }
  if (filters.value.categoryId != null) {
    list = list.filter(item => item.categoryId === filters.value.categoryId)
  }
  if (filters.value.minPrice != null) {
    list = list.filter(item => Number(item.promotionPrice ?? item.goodsMarketPrice ?? 0) >= filters.value.minPrice)
  }
  if (filters.value.maxPrice != null) {
    list = list.filter(item => Number(item.promotionPrice ?? item.goodsMarketPrice ?? 0) <= filters.value.maxPrice)
  }
  // 排序
  if (filters.value.sort === 'priceAsc') {
    list.sort((a, b) => Number(a.promotionPrice ?? a.goodsMarketPrice ?? 0) - Number(b.promotionPrice ?? b.goodsMarketPrice ?? 0))
  } else if (filters.value.sort === 'priceDesc') {
    list.sort((a, b) => Number(b.promotionPrice ?? b.goodsMarketPrice ?? 0) - Number(a.promotionPrice ?? a.goodsMarketPrice ?? 0))
  }
  total.value = list.length
  // 前端分页
  const start = (currentPage.value - 1) * pageSize.value
  goodsList.value = list.slice(start, start + pageSize.value)
}

/**
 * 加载全部分类数据
 */
async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res?.data || []
  } catch (e) { /* 错误已处理 */ }
}

/**
 * 判断分类是否有子分类
 * @param {number|string} id - 分类 ID
 * @returns {boolean} 是否有子分类
 */
function hasChildren(id) {
  return categories.value.some(cat => cat.parentId === id)
}

/**
 * 应用当前筛选条件并重新加载
 */
function applyFilters() {
  currentPage.value = 1
  activePricePreset.value = matchingPricePreset()
  loadGoods()
}

/**
 * 重置所有筛选条件
 */
function resetFilters() {
  filters.value = {
    keyword: '',
    categoryId: null,
    minPrice: null,
    maxPrice: null,
    hasStock: false,
    sort: 'newest'
  }
  activePricePreset.value = 'all'
  currentPage.value = 1
  router.replace({ name: 'GoodsList' })
  loadGoods()
}

/**
 * 切换全部商品/优惠商品视图模式
 * @param {string} mode - 'all' 或 'promotion'
 */
function switchMode(mode) {
  viewMode.value = mode
  currentPage.value = 1
  if (mode === 'promotion') {
    router.replace({ name: 'GoodsList', query: { ...route.query, promotion: 'true' } })
  } else {
    const query = { ...route.query }
    delete query.promotion
    router.replace({ name: 'GoodsList', query })
  }
}

/**
 * 选择指定促销活动
 * @param {number|null} id - 活动 ID，null 表示全部优惠
 */
function chooseSpecial(id) {
  activeSpecialId.value = id
  currentPage.value = 1
  loadGoods()
}

/**
 * 选择/取消分类筛选
 * @param {number|string} categoryId - 分类 ID
 */
function chooseCategory(categoryId) {
  filters.value.categoryId = filters.value.categoryId === categoryId ? null : categoryId
  applyFilters()
}

/**
 * 选择价格快捷预设
 * @param {Object} item - 价格预设对象（包含 key, min, max）
 */
function choosePricePreset(item) {
  activePricePreset.value = item.key
  filters.value.minPrice = item.min
  filters.value.maxPrice = item.max
  applyFilters()
}

/**
 * 匹配当前价格筛选对应的预设 key
 * @returns {string} 匹配到的预设 key，无匹配返回空字符串
 */
function matchingPricePreset() {
  const preset = pricePresets.find(item => item.min === filters.value.minPrice && item.max === filters.value.maxPrice)
  return preset?.key || ''
}

/**
 * 从 URL query 中读取字符串类型参数
 * @param {string} name - 参数名
 * @returns {string} 参数值（不存在则返回空字符串）
 */
function textQuery(name) {
  return typeof route.query[name] === 'string' ? route.query[name] : ''
}

/**
 * 从 URL query 中读取数值类型参数
 * @param {string} name - 参数名
 * @returns {number|null} 参数数值（非法或空则返回 null）
 */
function numberQuery(name) {
  const value = route.query[name]
  if (typeof value !== 'string' || value === '') return null
  const number = Number(value)
  return Number.isFinite(number) ? number : null
}

/**
 * 跳转到商品详情页
 * @param {Object} item - 商品对象
 */
function goDetail(item) {
  router.push({ name: 'GoodsDetail', params: { goodsNo: item.goodsNo } })
}

/**
 * 格式化价格显示
 * @param {number|null} value - 价格数值
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(value) {
  return value == null ? '暂无标价' : Number(value).toFixed(2)
}

/**
 * 快速加入购物车（需登录）
 * @param {Object} item - 商品对象
 */
async function quickAddCart(item) {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  busyKey.value = `${item.goodsNo}:cart`
  try {
    const store = await firstAvailableStore(item.goodsNo)
    if (!store) return ElMessage.warning('该商品暂时没有可售门店')
    await addToCart({ goodsNo: item.goodsNo, storeNo: store.storeNo, amount: 1 })
    ElMessage.success('已加入购物车')
  } finally {
    busyKey.value = ''
  }
}

/**
 * 快速收藏商品（需登录）
 * @param {Object} item - 商品对象
 */
async function quickFavorite(item) {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  busyKey.value = `${item.goodsNo}:fav`
  try {
    await addFavorite(item.goodsNo)
    ElMessage.success('已收藏')
  } finally {
    busyKey.value = ''
  }
}

/**
 * 查找商品第一个有库存的门店
 * @param {string} goodsNo - 商品编号
 * @returns {Object|undefined} 有库存的门店对象，若无则返回第一个门店
 */
async function firstAvailableStore(goodsNo) {
  const res = await getGoodsStores(goodsNo)
  const stores = res?.data || []
  return stores.find(item => Number(item.goodsStock || 0) > 0) || stores[0]
}
</script>

<style scoped>
.goods-list-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.shop-hero {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  gap: 18px;
  padding: 24px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  background:
    radial-gradient(circle at 12% 18%, rgba(15, 108, 189, 0.14), transparent 32%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.86), rgba(239, 248, 255, 0.72));
  box-shadow: var(--shadow-sm);
}

.shop-hero h2 {
  margin: 4px 0 8px;
}

.shop-hero p {
  max-width: 640px;
  color: var(--text);
}

.eyebrow {
  color: var(--accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.shop-summary {
  display: grid;
  min-width: 140px;
  place-items: center;
  padding: 18px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: rgba(255, 255, 255, 0.72);
}

.shop-summary strong {
  color: var(--price-color);
  font-size: 32px;
  line-height: 1;
}

.shop-summary span {
  color: var(--text);
  font-size: 13px;
}

.filter-panel {
  display: grid;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  background: var(--surface);
  box-shadow: var(--shadow-sm);
}

.filter-main {
  display: grid;
  grid-template-columns: minmax(240px, 1.4fr) minmax(180px, 0.9fr) minmax(160px, 0.7fr);
  gap: 12px;
}

.mode-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preset-row,
.tag-action-row,
.quick-cats,
.filter-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-action-row {
  justify-content: space-between;
}

.filter-actions {
  justify-content: flex-end;
  margin-left: auto;
}

.preset-label {
  color: var(--text-soft);
  font-size: 13px;
}

.quick-cats .el-tag,
.preset-row .el-tag {
  cursor: pointer;
}

.goods-card {
  position: relative;
}

.promotion-badge {
  position: absolute;
  z-index: 2;
  top: 10px;
  left: 10px;
  max-width: calc(100% - 20px);
  padding: 3px 8px;
  overflow: hidden;
  border-radius: 4px;
  color: #fff;
  background: #c42b1c;
  box-shadow: 0 2px 8px rgba(196, 43, 28, 0.24);
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.promotion-meta {
  display: block;
  margin-top: 4px;
  color: #c42b1c;
  font-size: 12px;
  font-weight: 600;
}

.goods-meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.goods-meta-row .goods-price {
  flex: 1;
  min-width: 0;
}

.goods-price del {
  display: block;
  margin-top: 2px;
  color: var(--text-soft);
  font-size: 12px;
  font-weight: 400;
}

.quick-icon-btn {
  width: 30px;
  height: 30px;
  flex: 0 0 30px;
  color: var(--accent);
}

.quick-icon-btn + .quick-icon-btn {
  margin-left: 0;
}

@media (max-width: 980px) {
  .shop-hero,
  .filter-actions {
    align-items: flex-start;
    flex-direction: column;
  }

  .filter-main {
    grid-template-columns: 1fr;
  }
}
</style>
