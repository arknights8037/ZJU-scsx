<template>
  <div class="home">
    <!-- 首页第一屏：让评审一眼看出这是“社区服务 + 商城”的居民端门户 -->
    <section class="home-hero">
      <div class="hero-copy">
        <span class="eyebrow">SmartCommunity Portal</span>
        <h1>智慧社区生活</h1>
        <p>把物业服务、社区购物和日常通知放在同一个入口，居民登录后就能办理常用事项。</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" :icon="OfficeBuilding" @click="$router.push('/community')">
            社区服务
          </el-button>
          <el-button size="large" :icon="Goods" @click="$router.push('/goods')">
            浏览商品
          </el-button>
        </div>
      </div>
      <div class="hero-panel">
        <div class="panel-title">今日服务</div>
        <div class="panel-row" v-for="item in heroStats" :key="item.label">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
    </section>

    <!-- 快捷入口：点击后跳转到社区服务页，并通过 query.tab 自动打开对应业务 tab -->
    <section class="service-strip">
      <button v-for="item in serviceCards" :key="item.title" class="service-card" @click="goService(item.tab)">
        <span class="service-icon">
          <el-icon><component :is="item.icon" /></el-icon>
        </span>
        <span class="service-main">
          <strong>{{ item.title }}</strong>
          <small>{{ item.desc }}</small>
        </span>
        <el-icon class="service-arrow"><ArrowRight /></el-icon>
      </button>
    </section>

    <!-- 分类筛选：顶级分类用按钮，子分类用 tag，适合做商城浏览入口 -->
    <section class="category-band">
      <div class="section-head">
        <div>
          <h2>商品分类</h2>
          <p>按社区生活场景快速筛选商品。</p>
        </div>
        <el-button text :icon="Refresh" @click="resetCategory">全部商品</el-button>
      </div>
      <div class="category-nav">
        <el-button
          v-for="cat in topCategories"
          :key="cat.id"
          :type="activeCategory === cat.id ? 'primary' : 'default'"
          @click="onCategoryClick(cat)"
        >
          {{ cat.categoryName }}
        </el-button>
      </div>
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
    </section>

    <!-- 促销商品：只展示前 4 个，避免首页太长太乱 -->
    <section v-if="specials.length && specialGoods.length" class="section">
      <div class="section-head">
        <div>
          <h2>限时促销</h2>
          <p>社区精选优惠商品。</p>
        </div>
        <el-button text :icon="ArrowRight" @click="$router.push('/goods')">更多</el-button>
      </div>
      <div class="goods-grid compact">
        <div v-for="item in specialGoods.slice(0, 4)" :key="item.goodsNo" class="goods-card" @click="goDetail(item)">
          <el-image :src="item.goodsPicture" fit="cover" class="goods-img">
            <template #error><div class="img-placeholder">暂无图片</div></template>
          </el-image>
          <div class="goods-info">
            <p class="goods-name">{{ item.goodsName }}</p>
            <p class="goods-price">¥{{ item.goodsMarketPrice }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 普通商品列表：和商品列表页共用同一套后端分页接口 -->
    <section class="section">
      <div class="section-head">
        <div>
          <h2>社区好物</h2>
          <p>当前共 {{ total }} 件商品可选。</p>
        </div>
      </div>
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
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRight,
  Bell,
  Document,
  Goods,
  HomeFilled,
  Key,
  OfficeBuilding,
  Refresh,
  ShoppingCart,
  Van,
  Wallet
} from '@element-plus/icons-vue'
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

// 顶部右侧“今日服务”面板的数据，只做展示，不依赖后端。
const heroStats = [
  { label: '物业公告', value: '实时', icon: Bell },
  { label: '报修工单', value: '在线提交', icon: Document },
  { label: '社区配送', value: '到家', icon: Van }
]

// 首页服务卡片配置。以后要新增入口，只需要往数组里加一项。
const serviceCards = [
  { title: '通知公告', desc: '查看社区最新通知', tab: 'notice', icon: Bell },
  { title: '报事报修', desc: '问题在线提交跟进', tab: 'complaint', icon: Document },
  { title: '访客登记', desc: '亲友来访提前登记', tab: 'visitor', icon: Key },
  { title: '物业缴费', desc: '账单明细一键查看', tab: 'charge', icon: Wallet },
  { title: '我的车位', desc: '车位与车辆绑定信息', tab: 'parking', icon: HomeFilled },
  { title: '购物车', desc: '继续处理待购商品', tab: 'cart', icon: ShoppingCart }
]

onMounted(async () => {
  // Promise.allSettled 的好处：某个接口失败时，不影响其它区域继续渲染。
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

// 加载商品分页。分类筛选时会带 categoryId，不筛选时传 undefined。
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

// 加载促销专题商品。当前只取第一个专题做首页展示。
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

// 点击一级分类：先找它的子分类；如果没有子分类，就直接按一级分类查商品。
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

// 点击子分类后重新请求商品列表。
function searchByCategory(categoryId) {
  selectedCategoryId.value = categoryId
  currentPage.value = 1
  loadGoods()
}

// 清空分类筛选，回到全部商品。
function resetCategory() {
  activeCategory.value = null
  selectedCategoryId.value = null
  subCategories.value = []
  currentPage.value = 1
  loadGoods()
}

// 社区服务入口统一走这里，方便维护首页卡片跳转逻辑。
function goService(tab) {
  if (tab === 'cart') {
    router.push('/cart')
    return
  }
  router.push({ path: '/community', query: { tab } })
}

function goDetail(item) {
  router.push({ name: 'GoodsDetail', params: { goodsNo: item.goodsNo } })
}
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.home-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 24px;
  min-height: 260px;
  padding: 34px;
  color: #fff;
  background:
    linear-gradient(110deg, rgba(9, 31, 52, 0.88), rgba(15, 108, 189, 0.68), rgba(0, 120, 128, 0.42)),
    url('/smartcommunitybackground.webp') center/cover;
  border-radius: var(--radius);
  border: 1px solid rgba(255, 255, 255, 0.34);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.hero-copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 620px;
}

.eyebrow {
  font-size: 13px;
  letter-spacing: 0;
  color: rgba(255, 255, 255, 0.72);
  margin-bottom: 8px;
}

.hero-copy h1 {
  color: #fff;
  font-size: 42px;
  line-height: 1.12;
  margin: 0 0 14px;
  letter-spacing: 0;
}

.hero-copy p {
  color: rgba(255, 255, 255, 0.82);
  font-size: 16px;
  max-width: 520px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 26px;
}

.hero-panel {
  align-self: center;
  background: rgba(240, 248, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.34);
  backdrop-filter: blur(22px) saturate(1.25);
  -webkit-backdrop-filter: blur(22px) saturate(1.25);
  box-shadow: 0 12px 30px rgba(4, 23, 39, 0.14), 0 1px 0 rgba(255, 255, 255, 0.26) inset;
  border-radius: var(--radius);
  padding: 18px;
}

.panel-title {
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  margin-bottom: 12px;
}

.panel-row {
  display: grid;
  grid-template-columns: 24px 1fr auto;
  gap: 10px;
  align-items: center;
  min-height: 38px;
  color: rgba(255, 255, 255, 0.86);
}

.panel-row strong {
  color: #fff;
  font-weight: 600;
}

.service-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

.service-card {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) 20px;
  align-items: center;
  gap: 12px;
  min-height: 86px;
  padding: 16px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius);
  background: var(--surface);
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(18px) saturate(1.2);
  color: var(--text);
  cursor: pointer;
  text-align: left;
  transition: transform var(--motion), box-shadow var(--motion), border-color var(--motion);
}

.service-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.service-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 20px;
}

.service-main {
  min-width: 0;
}

.service-main strong,
.service-main small {
  display: block;
}

.service-main strong {
  color: var(--text-h);
  font-size: 15px;
}

.service-main small {
  color: var(--text);
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-arrow {
  color: var(--text);
}

.category-band,
.section {
  border-radius: var(--radius);
}

.category-band {
  padding: 20px;
  background: var(--surface);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  backdrop-filter: blur(18px) saturate(1.2);
}

.section {
  padding: 4px 0;
  background: transparent;
  border: 0;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.section-head h2 {
  margin-bottom: 4px;
}

.category-nav {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sub-nav {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 14px;
}

.sub-tag {
  cursor: pointer;
}

.goods-grid.compact .goods-img {
  height: 180px;
}

@media (max-width: 1024px) {
  .home-hero {
    grid-template-columns: 1fr;
    padding: 24px;
  }

  .hero-copy h1 {
    font-size: 32px;
  }

  .service-strip {
    grid-template-columns: 1fr;
  }
}
</style>
