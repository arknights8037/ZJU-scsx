<template>
  <div v-loading="loading" class="goods-detail">
    <template v-if="goods">
      <el-page-header @back="$router.back()" :content="goods.goodsName" />

      <section class="detail-main">
        <div class="gallery">
          <el-image :src="activeImage" fit="contain" class="cover-img" :preview-src-list="galleryImages">
            <template #error><div class="img-placeholder">暂无图片</div></template>
          </el-image>
          <div v-if="galleryImages.length > 1" class="thumbnail-list" aria-label="商品图片">
            <button
              v-for="image in galleryImages"
              :key="image"
              type="button"
              class="thumbnail-button"
              :class="{ active: image === activeImage }"
              @click="activeImage = image"
            >
              <el-image :src="image" fit="cover" />
            </button>
          </div>
        </div>

        <div class="detail-info">
          <p class="detail-label">社区好物</p>
          <h1>{{ goods.goodsName }}</h1>
          <p class="intro">{{ goods.goodsIntroduce || '暂无商品简介' }}</p>

          <div class="price-panel">
            <span class="price-label">到手价</span>
            <strong class="price">¥{{ displayPrice }}</strong>
            <del v-if="goods.promotionPrice != null && goods.goodsMarketPrice != null" class="original-price">
              ¥{{ formatPrice(goods.goodsMarketPrice) }}
            </del>
            <el-tag v-if="goods.promotionLabel" type="danger" effect="light">
              {{ goods.specialName || '限时优惠' }} · {{ goods.promotionLabel }}
            </el-tag>
            <span v-if="selectedStore" class="stock" :class="{ empty: availableStock <= 0 }">
              {{ availableStock > 0 ? `库存 ${availableStock} 件` : '暂时缺货' }}
            </span>
          </div>

          <div class="fulfillment-panel">
            <div class="panel-title-row">
              <div>
                <strong>履约方式</strong>
                <p>先查看配送或自提规则，实际履约以订单和门店确认为准。</p>
              </div>
              <el-tag type="success" effect="light">社区服务</el-tag>
            </div>
            <div class="fulfillment-options">
              <button
                v-for="item in fulfillmentModes"
                :key="item.value"
                type="button"
                class="fulfillment-card"
                :class="{ active: selectedFulfillment === item.value }"
                @click="selectedFulfillment = item.value"
              >
                <span>{{ item.title }}</span>
                <strong>{{ item.time }}</strong>
                <small>{{ item.desc }}</small>
              </button>
            </div>
          </div>

          <div v-if="storeOptions.length" class="store-field">
            <label for="store-select">{{ selectedFulfillmentText }}门店</label>
            <el-select id="store-select" v-model="selectedStoreNo" size="large" placeholder="请选择门店">
              <el-option
                v-for="item in storeOptions"
                :key="item.storeNo"
                :label="item.storeName"
                :value="item.storeNo"
                :disabled="item.goodsStock <= 0"
              >
                <span>{{ item.storeName }}</span>
                <span class="option-stock">库存 {{ item.goodsStock }}</span>
              </el-option>
            </el-select>
            <p v-if="selectedStore?.storeAddress" class="store-address">
              <el-icon><Location /></el-icon>{{ selectedStore.storeAddress }}
            </p>
          </div>
          <el-alert v-else title="该商品暂时没有可售门店" type="warning" :closable="false" show-icon />

          <div v-if="selectedStore" class="service-card">
            <div>
              <span>当前门店</span>
              <strong>{{ selectedStore.storeName }}</strong>
            </div>
            <div>
              <span>{{ selectedFulfillmentText }}说明</span>
              <strong>{{ fulfillmentTip }}</strong>
            </div>
            <div>
              <span>售后保障</span>
              <strong>支持缺货退款，签收/自提时请核对商品</strong>
            </div>
          </div>

          <div class="detail-actions">
            <el-input-number v-model="amount" :min="1" :max="maxAmount" size="large" />
            <el-button
              type="primary"
              size="large"
              :icon="ShoppingCart"
              :disabled="!selectedStore || availableStock <= 0"
              @click="addCart"
            >加入购物车</el-button>
            <el-button
              type="success"
              size="large"
              :disabled="!selectedStore || availableStock <= 0"
              :loading="buying"
              @click="buyNow"
            >直接付款</el-button>
            <el-button size="large" :icon="isFav ? StarFilled : Star" @click="toggleFavorite">
              {{ isFav ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </section>

      <section class="detail-content">
        <div class="section-heading">
          <div>
            <p class="detail-label">商品说明</p>
            <h2>商品详情</h2>
          </div>
        </div>
        <div class="content-layout">
          <div class="content-body" v-html="goods.goodsContent || '<p>暂无详情</p>'"></div>
          <aside class="purchase-guide">
            <h3>购买须知</h3>
            <ul>
              <li>社区配送：由所选门店安排配送，适合大件或不方便到店领取。</li>
              <li>门店自提：下单后可到所选服务站领取，建议保留订单编号。</li>
              <li>库存价格以当前选择门店为准，不同门店可能略有差异。</li>
              <li>如商品缺货，物业/门店可联系你调整或退款。</li>
            </ul>
          </aside>
        </div>
      </section>

      <section v-if="recommendations.length" class="recommend-section">
        <div class="section-heading">
          <div>
            <p class="detail-label">猜你喜欢</p>
            <h2>{{ isLoggedIn ? '根据你的浏览推荐' : '社区热门推荐' }}</h2>
          </div>
        </div>
        <div class="goods-grid">
          <article
            v-for="item in recommendations"
            :key="item.goodsNo"
            class="goods-card"
            @click="goDetail(item.goodsNo)"
          >
            <span v-if="item.badgeText || item.promotionLabel" class="recommend-badge">
              {{ item.badgeText || item.promotionLabel }}
            </span>
            <el-image :src="assetUrl(item.goodsPicture)" fit="cover" class="goods-img">
              <template #error><div class="img-placeholder">暂无图片</div></template>
            </el-image>
            <div class="goods-info">
              <p class="goods-name">{{ item.goodsName }}</p>
              <small v-if="item.specialName" class="recommend-promotion">
                {{ item.specialName }} · {{ item.promotionLabel }}
              </small>
              <p class="goods-price">¥{{ formatPrice(item.promotionPrice ?? item.goodsMarketPrice) }}</p>
              <del v-if="item.promotionPrice != null && item.goodsMarketPrice != null" class="recommend-original">
                ¥{{ formatPrice(item.goodsMarketPrice) }}
              </del>
            </div>
          </article>
        </div>
      </section>
    </template>

    <div v-else-if="!loading" class="page-empty">
      <p>商品不存在或已经下架</p>
      <el-button type="primary" @click="$router.push('/portal/goods')">返回商品列表</el-button>
    </div>

    <OrderPayDialog v-model="payDialogVisible" :order="pendingOrder" @paid="paymentCompleted" />
  </div>
</template>

<script setup>
/**
 * GoodsDetail - 门户端商品详情页
 *
 * 功能：
 * - 展示商品基本信息、图片画廊、价格信息
 * - 支持社区配送 / 门店自提两种履约方式选择
 * - 门店列表展示及地址信息
 * - 加入购物车、直接购买、收藏/取消收藏
 * - 猜你喜欢推荐商品列表
 * - 浏览历史记录（登录用户）
 *
 * 路由路径：/portal/goods/:goodsNo
 */

import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue'
import {
  addFavorite,
  addToCart,
  buyNowOrder,
  getFavorites,
  getGoodsDetail,
  getGoodsPictures,
  getGoodsRecommendations,
  getGoodsStores,
  getStores,
  recordGoodsClick,
  removeFavorite
} from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'
import OrderPayDialog from '@/portal/components/OrderPayDialog.vue'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const route = useRoute()
const router = useRouter()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const goods = ref(null)                 // 当前商品详情数据
const pictures = ref([])                // 商品图片列表
const goodsStores = ref([])             // 商品在各门店的库存/价格数据
const stores = ref([])                  // 全部门店信息（名称、地址等）
const recommendations = ref([])         // 推荐商品列表
const activeImage = ref('')             // 当前展示的主图 URL
const selectedStoreNo = ref('')         // 当前选中的门店编号
const selectedFulfillment = ref('delivery')  // 选中的履约方式：'delivery'（配送）或 'pickup'（自提）
const amount = ref(1)                   // 购买数量
const isFav = ref(false)                // 当前商品是否已收藏
const loading = ref(false)              // 页面加载状态
const buying = ref(false)               // 直接购买按钮加载状态
const payDialogVisible = ref(false)     // 支付弹窗是否可见
const pendingOrder = ref(null)          // 待支付的订单数据

// ========== 计算属性 ==========
/** 用户是否已登录 */
const isLoggedIn = computed(() => auth.isLoggedIn)

/** 履约方式选项配置 */
const fulfillmentModes = [
  { value: 'delivery', title: '社区配送', time: '预计当日/次日送达', desc: '适合送到家门口或物业暂存' },
  { value: 'pickup', title: '门店自提', time: '营业时间内可取', desc: '到社区服务站核对订单后领取' }
]

/**
 * 商品图片画廊列表
 * 主图和图片表可能有重复地址，Set 用来去重
 */
const galleryImages = computed(() => [...new Set([
  goods.value?.goodsPicture,
  ...pictures.value.map(item => item.pictureUrl)
].filter(Boolean).map(assetUrl))])

/**
 * 合并后的门店选项列表
 * goods_store 只保存门店编号、价格、库存；门店名称和地址来自 store 表，所以前端在这里合并展示
 */
const storeOptions = computed(() => {
  const storeMap = new Map(stores.value.map(item => [item.storeNo, item]))
  return goodsStores.value.map(item => {
    const store = storeMap.get(item.storeNo)
    return {
      ...item,
      storeName: store?.storeName || `门店 ${item.storeNo.slice(0, 6)}`,
      storeAddress: store?.storeAddress || ''
    }
  })
})

/** 当前选中的门店对象 */
const selectedStore = computed(() =>
  storeOptions.value.find(item => item.storeNo === selectedStoreNo.value)
)
/** 当前选中门店的可用库存 */
const availableStock = computed(() => selectedStore.value?.goodsStock || 0)
/** 最大可购买数量（不超过库存且不超过 99） */
const maxAmount = computed(() => Math.max(1, Math.min(availableStock.value, 99)))
/** 展示价格（优先使用门店价格，其次市场价） */
const displayPrice = computed(() => formatPrice(
  selectedStore.value?.goodsPrice ?? goods.value?.goodsMarketPrice
))
/** 履约方式文本描述 */
const selectedFulfillmentText = computed(() =>
  selectedFulfillment.value === 'pickup' ? '自提' : '配送'
)
/**
 * 履约方式提示文本
 * 当前版本订单表只有 deliveryType 字段，购物车暂不保存履约选择；这里先作为购买前说明展示
 */
const fulfillmentTip = computed(() => {
  if (selectedFulfillment.value === 'pickup') {
    return selectedStore.value?.storeAddress
      ? `请到 ${selectedStore.value.storeAddress} 自提`
      : '请到所选社区服务站自提'
  }
  return '门店接单后按社区配送路线送达'
})

// ========== 监听器 ==========
watch(() => route.params.goodsNo, loadDetail, { immediate: true })
watch(maxAmount, value => {
  if (amount.value > value) amount.value = value
})

/**
 * 加载商品详情
 * 切换商品详情时先清空旧数据，避免用户看到上一个商品残留的图片/推荐/收藏状态
 * @param {string} goodsNo - 商品编号
 */
async function loadDetail(goodsNo) {
  if (!goodsNo) return
  loading.value = true
  goods.value = null
  pictures.value = []
  goodsStores.value = []
  recommendations.value = []
  amount.value = 1
  isFav.value = false

  try {
    const detailRes = await getGoodsDetail(goodsNo)
    goods.value = detailRes?.data || null
    if (!goods.value) return

    // 只有登录用户才记个人行为；接口从 Token 识别用户，不采用 localStorage 的 userId
    if (isLoggedIn.value) {
      await recordGoodsClick(goodsNo).catch(() => {})
    }

    // 图片、门店、推荐都属于补充数据；其中一个失败时，商品主体仍然可以正常展示
    const [pictureResult, goodsStoreResult, storeResult, recommendResult] = await Promise.allSettled([
      getGoodsPictures(goodsNo),
      getGoodsStores(goodsNo),
      getStores(),
      getGoodsRecommendations({ excludeGoodsNo: goodsNo, size: 4 })
    ])
    pictures.value = resultData(pictureResult)
    goodsStores.value = resultData(goodsStoreResult)
    stores.value = resultData(storeResult)
    recommendations.value = resultData(recommendResult)
    activeImage.value = galleryImages.value[0] || ''

    // 默认选中第一个有库存的门店；如果全部无库存，也保留第一个门店用于展示地址和价格
    const available = storeOptions.value.find(item => item.goodsStock > 0)
    selectedStoreNo.value = available?.storeNo || storeOptions.value[0]?.storeNo || ''
    await checkFavorite()
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } catch (error) {
    goods.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 检查当前商品是否已被当前用户收藏
 */
async function checkFavorite() {
  if (!auth.isLoggedIn) return
  try {
    const res = await getFavorites()
    isFav.value = (res?.data || []).some(item => item.goodsNo === route.params.goodsNo)
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

/**
 * 加入购物车
 * 加入购物车需要登录，因为后端购物车记录按当前 JWT 用户保存
 */
async function addCart() {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  if (!selectedStore.value || availableStock.value <= 0) {
    return ElMessage.warning('请选择有库存的配送门店')
  }
  try {
    await addToCart({
      goodsNo: goods.value.goodsNo,
      storeNo: selectedStoreNo.value,
      amount: amount.value
    })
    await ElMessageBox.confirm(`商品已加入购物车（所选门店：${selectedStore.value.storeName}），要现在去结算吗？`, '加入成功', {
      confirmButtonText: '去购物车',
      cancelButtonText: '继续浏览',
      type: 'success'
    }).then(() => router.push('/portal/cart')).catch(() => {})
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

/**
 * 立即购买：生成订单并弹出支付对话框
 */
async function buyNow() {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  if (!selectedStore.value || availableStock.value <= 0) {
    return ElMessage.warning('请选择有库存的门店')
  }
  buying.value = true
  try {
    const res = await buyNowOrder({
      goodsNo: goods.value.goodsNo,
      storeNo: selectedStoreNo.value,
      amount: amount.value
    })
    pendingOrder.value = res?.data || null
    payDialogVisible.value = Boolean(pendingOrder.value)
    ElMessage.success('订单已生成，请选择支付方式')
  } finally {
    buying.value = false
  }
}

/**
 * 支付完成回调：跳转到订单页并定位到该订单
 * @param {Object} params - 回调参数
 * @param {string} params.orderNo - 支付完成的订单编号
 */
function paymentCompleted({ orderNo }) {
  router.push({ path: '/portal/order', query: { orderNo } })
}

/**
 * 切换收藏/取消收藏
 * 收藏同样是用户维度数据；未登录时先跳转登录，并带上回跳地址
 */
async function toggleFavorite() {
  if (!auth.isLoggedIn) {
    ElMessage.warning('请先登录')
    return router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
  }
  try {
    if (isFav.value) {
      await removeFavorite(goods.value.goodsNo)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(goods.value.goodsNo, selectedStoreNo.value || undefined)
      isFav.value = true
      ElMessage.success('已收藏')
    }
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

/**
 * 跳转到商品详情页（推荐商品使用）
 * @param {string} goodsNo - 商品编号
 */
function goDetail(goodsNo) {
  router.push({ name: 'GoodsDetail', params: { goodsNo } })
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
 * 提取 Promise.allSettled 成功状态的数据
 * @param {Object} result - Promise.allSettled 单个结果对象
 * @returns {Array} 数据数组（失败时返回空数组）
 */
function resultData(result) {
  return result.status === 'fulfilled' ? (result.value?.data || []) : []
}

</script>

<style scoped>
.goods-detail {
  min-height: 420px;
}

.detail-main {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  gap: 40px;
  margin-top: 24px;
}

.gallery {
  min-width: 0;
}

.cover-img {
  width: 100%;
  aspect-ratio: 4 / 3;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  overflow-x: auto;
  padding-bottom: 3px;
}

.thumbnail-button {
  width: 68px;
  height: 68px;
  flex: 0 0 68px;
  padding: 2px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--surface);
  cursor: pointer;
}

.thumbnail-button.active {
  border-color: var(--accent);
  box-shadow: 0 0 0 1px var(--accent);
}

.thumbnail-button .el-image {
  width: 100%;
  height: 100%;
}

.detail-info {
  min-width: 0;
  padding-top: 6px;
}

.detail-label {
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
}

.detail-info h1 {
  margin: 6px 0 12px;
  font-size: 28px;
  line-height: 1.35;
}

.intro {
  min-height: 48px;
  line-height: 1.7;
}

.price-panel {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin: 24px 0;
  padding: 18px 20px;
  background: var(--bg-secondary);
  border-left: 3px solid var(--price-color);
}

.price-label {
  font-size: 13px;
}

.price {
  color: var(--price-color);
  font-size: 30px;
  line-height: 1;
}

.stock {
  margin-left: auto;
  color: var(--success);
  font-size: 13px;
}

.stock.empty {
  color: var(--danger);
}

.original-price {
  color: var(--text-soft);
  font-size: 13px;
}

.recommend-section .goods-card {
  position: relative;
}

.recommend-badge {
  position: absolute;
  z-index: 2;
  top: 10px;
  left: 10px;
  padding: 3px 8px;
  border-radius: 4px;
  color: #fff;
  background: #c42b1c;
  font-size: 12px;
}

.recommend-promotion {
  display: block;
  margin: 5px 0;
  color: #c42b1c;
  font-size: 12px;
  font-weight: 600;
}

.recommend-original {
  display: block;
  margin-top: 3px;
  color: var(--text-soft);
  font-size: 12px;
}

.fulfillment-panel {
  margin: -8px 0 18px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.panel-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.panel-title-row strong {
  color: var(--text-h);
}

.panel-title-row p {
  margin-top: 3px;
  font-size: 13px;
}

.fulfillment-options {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.fulfillment-card {
  min-height: 92px;
  padding: 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-secondary);
  color: var(--text);
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, transform 0.2s;
}

.fulfillment-card:hover,
.fulfillment-card.active {
  border-color: var(--accent);
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.fulfillment-card span,
.fulfillment-card strong,
.fulfillment-card small {
  display: block;
}

.fulfillment-card span {
  color: var(--accent);
  font-size: 13px;
  font-weight: 700;
}

.fulfillment-card strong {
  margin: 6px 0 4px;
  color: var(--text-h);
}

.fulfillment-card small {
  line-height: 1.5;
}

.store-field {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  align-items: center;
  gap: 10px 12px;
}

.store-field label {
  color: var(--text-h);
  font-weight: 600;
}

.store-address {
  grid-column: 2;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
}

.option-stock {
  float: right;
  margin-left: 24px;
  color: var(--text);
  font-size: 12px;
}

.service-card {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1px;
  margin-top: 16px;
  overflow: hidden;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--border);
}

.service-card > div {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr);
  gap: 12px;
  padding: 11px 12px;
  background: var(--surface);
}

.service-card span {
  color: var(--text);
  font-size: 13px;
}

.service-card strong {
  color: var(--text-h);
  font-size: 13px;
  line-height: 1.5;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 26px;
  flex-wrap: wrap;
}

.detail-content,
.recommend-section {
  margin-top: 42px;
}

.content-body {
  min-height: 180px;
  padding: 24px;
  overflow: hidden;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  line-height: 1.8;
}

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
  align-items: start;
}

.purchase-guide {
  padding: 20px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.purchase-guide h3 {
  margin-bottom: 12px;
}

.purchase-guide ul {
  display: grid;
  gap: 9px;
  padding-left: 18px;
  line-height: 1.7;
}

.content-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius-sm);
}

.section-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-heading h2 {
  margin: 3px 0 0;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  color: var(--text);
}

.page-empty .el-button {
  margin-top: 16px;
}

@media (max-width: 900px) {
  .detail-main {
    grid-template-columns: 1fr;
    gap: 22px;
  }

  .detail-info h1 {
    font-size: 23px;
  }

  .content-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .price-panel {
    flex-wrap: wrap;
    padding: 15px;
  }

  .price {
    font-size: 25px;
  }

  .stock {
    width: 100%;
    margin-left: 0;
  }

  .store-field {
    grid-template-columns: 1fr;
  }

  .fulfillment-options {
    grid-template-columns: 1fr;
  }

  .store-address {
    grid-column: 1;
  }

  .detail-actions > * {
    flex: 1 1 auto;
  }

  .detail-actions .el-button {
    margin-left: 0;
  }

  .content-body {
    padding: 16px;
  }
}
</style>

