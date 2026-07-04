<template>
  <div class="goods-list-page">
    <h2>{{ $route.query.keyword ? `搜索: "${$route.query.keyword}"` : '商品列表' }}</h2>

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
        :pager-count="5"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="loadGoods"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsPage } from '@/api'

const route = useRoute()
const router = useRouter()
const goodsList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => loadGoods())

async function loadGoods() {
  try {
    const res = await getGoodsPage({
      page: currentPage.value,
      size: pageSize.value,
      keyword: route.query.keyword || undefined
    })
    if (res?.data) {
      goodsList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (e) { /* handled */ }
}

function goDetail(item) {
  router.push({ name: 'GoodsDetail', params: { goodsNo: item.goodsNo } })
}
</script>
