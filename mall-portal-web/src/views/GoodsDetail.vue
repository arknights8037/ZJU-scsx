<template>
  <div class="goods-detail" v-if="goods">
    <el-page-header @back="$router.back()" :content="goods.goodsName" />

    <div class="detail-main">
      <div class="detail-image">
        <el-image :src="goods.goodsPicture" fit="cover" class="cover-img">
          <template #error><div class="img-placeholder">暂无图片</div></template>
        </el-image>
      </div>
      <div class="detail-info">
        <h2>{{ goods.goodsName }}</h2>
        <p class="intro">{{ goods.goodsIntroduce || '暂无简介' }}</p>
        <p class="price">¥{{ goods.goodsMarketPrice || '暂无标价' }}</p>
        <div class="detail-actions">
          <el-input-number v-model="amount" :min="1" :max="99" size="large" />
          <el-button type="primary" size="large" @click="addCart">加入购物车</el-button>
          <el-button size="large" @click="toggleFavorite">
            <el-icon>
              <StarFilled v-if="isFav" style="color: var(--warning)" />
              <Star v-else />
            </el-icon>
            {{ isFav ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="detail-content">
      <h3>商品详情</h3>
      <div class="content-body" v-html="goods.goodsContent || '<p style=color:var(--text)>暂无详情</p>'"></div>
    </div>
  </div>

  <div v-else class="page-empty">
    <p>商品不存在</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getGoodsDetail, addToCart, getFavorites, addFavorite, removeFavorite } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const goods = ref(null)
const amount = ref(1)
const isFav = ref(false)

onMounted(async () => {
  try {
    const res = await getGoodsDetail(route.params.goodsNo)
    goods.value = res?.data
    checkFav()
  } catch (e) { /* handled */ }
})

async function checkFav() {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await getFavorites(userId)
    if (res?.data) {
      isFav.value = res.data.some(f => f.goodsNo === route.params.goodsNo)
    }
  } catch (e) { /* handled */ }
}

async function addCart() {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await addToCart({ userId: Number(userId), goodsNo: goods.value.goodsNo, amount: amount.value })
    ElMessage.success('已加入购物车')
  } catch (e) { /* handled */ }
}

async function toggleFavorite() {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (isFav.value) {
      await removeFavorite(userId, goods.value.goodsNo)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(userId, goods.value.goodsNo)
      isFav.value = true
      ElMessage.success('已收藏')
    }
  } catch (e) { /* handled */ }
}
</script>

<style scoped>
.goods-detail {
  /* constrained by parent */
}

.detail-main {
  display: flex;
  gap: 32px;
  margin-top: 24px;
}

.detail-image {
  flex: 0 0 45%;
  min-width: 0;
}

.cover-img {
  width: 100%;
  border-radius: var(--card-radius);
}

.cover-img :deep(img) {
  border-radius: var(--card-radius);
}

.detail-info {
  flex: 1;
}

.intro {
  color: var(--text);
  margin: 8px 0;
  font-size: 15px;
  line-height: 1.6;
}

.price {
  color: var(--price-color);
  font-size: 28px;
  font-weight: bold;
  margin: 16px 0;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.detail-content {
  margin-top: 36px;
}

.content-body {
  background: var(--bg-card);
  padding: 20px;
  border-radius: var(--card-radius);
  border: 1px solid var(--border);
  min-height: 200px;
}

.content-body :deep(img) {
  max-width: 100%;
  border-radius: 4px;
}

.img-placeholder {
  width: 100%;
  aspect-ratio: 1;
  background: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text);
  border-radius: var(--card-radius);
}

@media (max-width: 1024px) {
  .detail-main {
    flex-direction: column;
    gap: 20px;
  }

  .detail-image {
    flex: none;
  }

  .price {
    font-size: 24px;
  }

  .detail-actions {
    gap: 8px;
  }

  .detail-actions .el-button {
    flex: 1;
    min-width: 0;
  }
}
</style>
