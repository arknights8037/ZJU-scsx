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
            <span v-if="selectedStore" class="stock" :class="{ empty: availableStock <= 0 }">
              {{ availableStock > 0 ? `库存 ${availableStock} 件` : '暂时缺货' }}
            </span>
          </div>

          <div v-if="storeOptions.length" class="store-field">
            <label for="store-select">配送门店</label>
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

          <div class="detail-actions">
            <el-input-number v-model="amount" :min="1" :max="maxAmount" size="large" />
            <el-button
              type="primary"
              size="large"
              :icon="ShoppingCart"
              :disabled="!selectedStore || availableStock <= 0"
              @click="addCart"
            >加入购物车</el-button>
            <el-button size="large" :icon="isFav ? StarFilled : Star" @click="toggleFavorite">
              {{ isFav ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </section>

      <section class="detail-content">
        <h2>商品详情</h2>
        <div class="content-body" v-html="goods.goodsContent || '<p>暂无详情</p>'"></div>
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
            <el-image :src="item.goodsPicture" fit="cover" class="goods-img">
              <template #error><div class="img-placeholder">暂无图片</div></template>
            </el-image>
            <div class="goods-info">
              <p class="goods-name">{{ item.goodsName }}</p>
              <p class="goods-price">¥{{ formatPrice(item.goodsMarketPrice) }}</p>
            </div>
          </article>
        </div>
      </section>
    </template>

    <div v-else-if="!loading" class="page-empty">
      <p>商品不存在或已经下架</p>
      <el-button type="primary" @click="$router.push('/goods')">返回商品列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, ShoppingCart, Star, StarFilled } from '@element-plus/icons-vue'
import {
  addFavorite,
  addToCart,
  getFavorites,
  getGoodsDetail,
  getGoodsPictures,
  getGoodsRecommendations,
  getGoodsStores,
  getStores,
  recordGoodsClick,
  removeFavorite
} from '@/api'

const route = useRoute()
const router = useRouter()
const goods = ref(null)
const pictures = ref([])
const goodsStores = ref([])
const stores = ref([])
const recommendations = ref([])
const activeImage = ref('')
const selectedStoreNo = ref('')
const amount = ref(1)
const isFav = ref(false)
const loading = ref(false)

const isLoggedIn = computed(() => Boolean(localStorage.getItem('token')))

// 主图和图片表可能有重复地址，Set 用来去重。
const galleryImages = computed(() => [...new Set([
  goods.value?.goodsPicture,
  ...pictures.value.map(item => item.pictureUrl)
].filter(Boolean))])

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

const selectedStore = computed(() =>
  storeOptions.value.find(item => item.storeNo === selectedStoreNo.value)
)
const availableStock = computed(() => selectedStore.value?.goodsStock || 0)
const maxAmount = computed(() => Math.max(1, Math.min(availableStock.value, 99)))
const displayPrice = computed(() => formatPrice(
  selectedStore.value?.goodsPrice ?? goods.value?.goodsMarketPrice
))

watch(() => route.params.goodsNo, loadDetail, { immediate: true })
watch(maxAmount, value => {
  if (amount.value > value) amount.value = value
})

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

    // 只有登录用户才记个人行为；接口从 Token 识别用户，不采用 localStorage 的 userId。
    if (isLoggedIn.value) {
      await recordGoodsClick(goodsNo).catch(() => {})
    }

    // 图片、门店、推荐都属于补充数据；其中一个失败时，商品主体仍然可以正常展示。
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

async function checkFavorite() {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await getFavorites(userId)
    isFav.value = (res?.data || []).some(item => item.goodsNo === route.params.goodsNo)
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

async function addCart() {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  if (!selectedStore.value || availableStock.value <= 0) {
    return ElMessage.warning('请选择有库存的配送门店')
  }
  try {
    await addToCart({
      userId: Number(userId),
      goodsNo: goods.value.goodsNo,
      storeNo: selectedStoreNo.value,
      amount: amount.value
    })
    ElMessage.success('已加入购物车')
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

async function toggleFavorite() {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    return router.push('/login')
  }
  try {
    if (isFav.value) {
      await removeFavorite(userId, goods.value.goodsNo)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(userId, goods.value.goodsNo, selectedStoreNo.value || undefined)
      isFav.value = true
      ElMessage.success('已收藏')
    }
  } catch (error) { /* 请求错误由统一拦截器提示 */ }
}

function goDetail(goodsNo) {
  router.push({ name: 'GoodsDetail', params: { goodsNo } })
}

function formatPrice(value) {
  return value == null ? '暂无标价' : Number(value).toFixed(2)
}

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
