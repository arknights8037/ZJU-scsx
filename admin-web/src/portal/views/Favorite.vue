<template>
  <div class="favorite-page" v-loading="loading">
    <div class="page-head">
      <div>
        <h2>我的收藏</h2>
        <p>保存感兴趣的商品，之后可以直接加入购物车或查看详情。</p>
      </div>
      <el-button :icon="Refresh" @click="loadFavorites">刷新</el-button>
    </div>

    <div v-if="favorites.length" class="goods-grid">
      <div v-for="item in favorites" :key="item.goodsNo" class="goods-card" @click="goDetail(item.goodsNo)">
        <span v-if="item.badgeText || item.promotionLabel" class="favorite-badge">
          {{ item.badgeText || item.promotionLabel }}
        </span>
        <el-image :src="assetUrl(item.goodsPicture)" fit="cover" class="goods-img">
          <template #error><div class="img-placeholder">暂无图片</div></template>
        </el-image>
        <div class="goods-info">
          <p class="goods-name">{{ item.goodsName || item.goodsNo }}</p>
          <small v-if="item.specialName" class="favorite-promotion">
            {{ item.specialName }} · {{ item.promotionLabel }}
          </small>
          <small class="favorite-meta">
            {{ item.goodsStock > 0 ? `库存 ${item.goodsStock}` : '暂无库存' }}
          </small>
          <div class="favorite-row" @click.stop>
            <div class="favorite-price">
              <p class="goods-price">¥{{ formatPrice(item.goodsPrice ?? item.promotionPrice ?? item.goodsMarketPrice) }}</p>
              <del v-if="item.promotionPrice != null && item.goodsMarketPrice != null">¥{{ formatPrice(item.goodsMarketPrice) }}</del>
            </div>
            <el-button
              circle
              text
              type="primary"
              :icon="ShoppingCart"
              :loading="busyKey === `${item.goodsNo}:cart`"
              title="加入购物车"
              @click="addCart(item)"
            />
            <el-button
              circle
              text
              type="danger"
              :icon="Delete"
              :loading="busyKey === `${item.goodsNo}:remove`"
              title="取消收藏"
              @click="remove(item)"
            />
          </div>
        </div>
      </div>
    </div>

    <div v-else class="page-empty">
      <p>还没有收藏商品</p>
      <el-button type="primary" style="margin-top: 16px" @click="$router.push('/portal/goods')">去逛商品</el-button>
    </div>
  </div>
</template>

<script setup>
/**
 * Favorite - 门户端收藏夹页面
 *
 * 功能：
 * - 展示用户已收藏的商品列表
 * - 支持从收藏夹直接加入购物车
 * - 支持取消收藏（删除）
 * - 点击商品跳转详情页
 *
 * 路由路径：/portal/favorite
 */

import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Delete, Refresh, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { addToCart, getFavorites, removeFavorite } from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const route = useRoute()
const router = useRouter()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const loading = ref(false)        // 收藏列表加载状态
const favorites = ref([])         // 收藏商品列表
const busyKey = ref('')           // 当前正在操作的商品标识（用于按钮 loading）

// ========== 生命周期 ==========
onMounted(() => {
  if (!auth.isLoggedIn) {
    router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
    return
  }
  loadFavorites()
})

/**
 * 加载收藏列表
 */
async function loadFavorites() {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = res?.data || []
  } finally {
    loading.value = false
  }
}

/**
 * 从收藏夹将商品加入购物车
 * @param {Object} item - 收藏的商品对象
 */
async function addCart(item) {
  if (!item.storeNo) {
    ElMessage.warning('该商品暂无可售门店')
    return
  }
  busyKey.value = `${item.goodsNo}:cart`
  try {
    await addToCart({ goodsNo: item.goodsNo, storeNo: item.storeNo, amount: 1 })
    ElMessage.success('已加入购物车')
  } finally {
    busyKey.value = ''
  }
}

/**
 * 取消收藏（从收藏列表中移除）
 * @param {Object} item - 要取消收藏的商品对象
 */
async function remove(item) {
  busyKey.value = `${item.goodsNo}:remove`
  try {
    await removeFavorite(item.goodsNo)
    favorites.value = favorites.value.filter(favorite => favorite.goodsNo !== item.goodsNo)
    ElMessage.success('已取消收藏')
  } finally {
    busyKey.value = ''
  }
}

/**
 * 跳转到商品详情页
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
</script>

<style scoped>
.favorite-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-head p,
.favorite-meta {
  color: var(--text);
}

.favorite-meta {
  display: block;
  margin-top: 4px;
  font-size: 12px;
}

.favorite-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.favorite-row .goods-price {
  min-width: 0;
}

.goods-card {
  position: relative;
}

.favorite-badge {
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

.favorite-promotion {
  display: block;
  margin-top: 5px;
  color: #c42b1c;
  font-size: 12px;
  font-weight: 600;
}

.favorite-price {
  flex: 1;
  min-width: 0;
}

.favorite-price del {
  display: block;
  margin-top: 3px;
  color: var(--text-soft);
  font-size: 12px;
}

@media (max-width: 720px) {
  .page-head {
    flex-direction: column;
  }
}
</style>
