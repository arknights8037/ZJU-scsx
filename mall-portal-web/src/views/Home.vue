<template>
  <div class="home">
    <!-- 分类导航 -->
    <div class="category-nav">
      <el-button
        v-for="cat in topCategories"
        :key="cat.id"
        :type="activeCategory === cat.id ? 'primary' : 'default'"
        size="large"
        @click="onCategoryClick(cat)"
      >
        {{ cat.categoryName }}
      </el-button>
    </div>

    <!-- 子分类 -->
    <div v-if="subCategories.length" class="sub-nav">
      <el-tag
        v-for="sub in subCategories"
        :key="sub.id"
        :type="selectedCategoryId === sub.id ? 'primary' : 'info'"
        class="sub-tag"
        size="large"
        @click="searchByCategory(sub.id)"
      >
        {{ sub.categoryName }}
      </el-tag>
    </div>

    <!-- 促销板块 -->
    <div v-if="specials.length && specialGoods.length" class="section">
      <h3>🔥 限时促销</h3>
      <div class="goods-grid">
        <div v-for="item in specialGoods" :key="item.goodsNo" class="goods-card" @click="goDetail(item)">
          <el-image :src="item.goodsPicture" fit="cover" class="goods-img">
            <template #error><div class="img-placeholder">暂无图片</div></template>
          </el-image>
          <div class="goods-info">
            <p class="goods-name">{{ item.goodsName }}</p>
            <p class="goods-price">¥{{ item.goodsMarketPrice }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 全部商品 -->
    <div class="section">
      <h3>🛒 全部商品</h3>
      <div class="goods-grid" v-if="goodsList.length">
        <div v-for="item in goodsList" :key="item.goodsNo" class="goods-card" @click="goDetail(item)">
          <el-image :src="item.goodsPicture" fit="cover" class="goods-img">
            <template #error><div class="img-placeholder">暂无图片</div></template>
          </el-image>
          <div class="goods-info">
            <p class="goods-name">{{ item.goodsName }}</p>
            <p class="goods-price">¥{{ item.goodsMarketPrice || '暂无标价' }}</p>
          </div>
        </div>
      </div>
      <div v-else class="page-empty">
        <p>暂无商品</p>
      </div>
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="loadGoods"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getGoodsPage, getCategories, getSpecialList, getSpecialGoods } from '@/api'

const router = useRouter()

const topCategories = ref([])
const subCategories = ref([])
const specials = ref([])
const specialGoods = ref([])
const goodsList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const selectedCategoryId = ref(null)
const activeCategory = ref(null)

onMounted(async () => {
  const [catRes] = await Promise.allSettled([
    getCategories(),
    loadGoods(),
    loadSpecials()
  ])
  if (catRes.status === 'fulfilled' && catRes.value?.data) {
    const all = catRes.value.data
    topCategories.value = all.filter(c => c.categoryType === 1 || !c.parentId)
  }
})

async function loadGoods() {
  try {
    const res = await getGoodsPage({
      page: currentPage.value,
      size: pageSize.value,
      categoryId: selectedCategoryId.value || undefined
    })
    if (res?.data) {
      goodsList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) { /* handled by interceptor */ }
}

async function loadSpecials() {
  try {
    const res = await getSpecialList()
    if (res?.data) {
      specials.value = res.data
      if (res.data.length) {
        const sg = await getSpecialGoods(res.data[0].id)
        specialGoods.value = sg?.data || []
      }
    }
  } catch (e) { /* handled */ }
}

function onCategoryClick(cat) {
  activeCategory.value = cat.id
  subCategories.value = []
  getCategories().then(res => {
    if (res?.data) {
      subCategories.value = res.data.filter(c => c.parentId === cat.id)
      if (!subCategories.value.length) {
        searchByCategory(cat.id)
      }
    }
  })
}

function searchByCategory(categoryId) {
  selectedCategoryId.value = categoryId
  currentPage.value = 1
  loadGoods()
}

function goDetail(item) {
  router.push({ name: 'GoodsDetail', params: { goodsNo: item.goodsNo } })
}
</script>

<style scoped>
.home {
  /* content constrained by parent .main-content */
}

.category-nav {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 0 0 16px;
}

.sub-nav {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.sub-tag {
  cursor: pointer;
  transition: transform 0.2s;
}

.sub-tag:hover {
  transform: scale(1.05);
}

.section {
  margin: 28px 0;
}

.section h3 {
  margin-bottom: 14px;
}

@media (max-width: 1024px) {
  .category-nav .el-button {
    font-size: 13px;
    padding: 8px 14px;
  }

  .section {
    margin: 20px 0;
  }
}
</style>
