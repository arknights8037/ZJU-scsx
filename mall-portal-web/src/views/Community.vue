<template>
  <div class="community-page">
    <!-- 页面头部：说明这个页面聚合了哪些居民服务 -->
    <div class="page-head">
      <div>
        <h2>社区服务</h2>
        <p>通知公告、报事报修、访客登记、物业缴费和车位信息集中办理。</p>
      </div>
      <el-button :icon="Refresh" @click="reloadActive">刷新</el-button>
    </div>

    <!-- 概览卡片：用 computed 从列表数据里算数量，点击卡片会切换到对应 tab -->
    <div class="summary-grid">
      <button v-for="item in summaryCards" :key="item.tab" class="summary-card" @click="switchTab(item.tab)">
        <span class="summary-icon">
          <el-icon><component :is="item.icon" /></el-icon>
        </span>
        <span class="summary-text">
          <strong>{{ item.value }}</strong>
          <small>{{ item.label }}</small>
        </span>
      </button>
    </div>

    <!-- 业务 tab：每个 tab 对应一个社区服务模块 -->
    <el-tabs v-model="activeTab" class="service-tabs" @tab-change="loadActive">
      <el-tab-pane label="通知公告" name="notice">
        <div class="table-wrap">
          <el-table :data="notices" v-loading="loading.notice" empty-text="暂无公告">
            <el-table-column prop="noticeTitle" label="标题" min-width="180" />
            <el-table-column prop="noticeContent" label="内容" min-width="320" show-overflow-tooltip />
            <el-table-column prop="createTime" label="发布时间" min-width="170" />
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="报事报修" name="complaint">
        <div class="service-grid">
          <!-- 左侧是提交表单，右侧是当前用户自己的历史记录 -->
          <section class="form-panel">
            <h3>提交事项</h3>
            <p class="panel-desc">维修、投诉、公共设施问题都可以提交，物业端处理后状态会同步更新。</p>
            <el-form label-position="top">
              <el-form-item label="事项描述">
                <el-input
                  v-model="complaintForm.complaintContent"
                  type="textarea"
                  :rows="6"
                  maxlength="300"
                  show-word-limit
                  placeholder="请描述需要物业处理的问题"
                />
              </el-form-item>
              <el-button type="primary" :icon="Plus" @click="submitComplaint">提交</el-button>
            </el-form>
          </section>
          <section class="list-panel">
            <h3>我的记录</h3>
            <el-table :data="complaints" v-loading="loading.complaint" empty-text="暂无报修记录">
              <el-table-column prop="complaintContent" label="内容" min-width="220" show-overflow-tooltip />
              <el-table-column label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.complaintStatus === 1 ? 'success' : 'warning'">
                    {{ row.complaintStatus === 1 ? '已处理' : '待处理' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="提交时间" min-width="160" />
            </el-table>
          </section>
        </div>
      </el-tab-pane>

      <el-tab-pane label="访客登记" name="visitor">
        <div class="service-grid">
          <!-- 访客登记和报修结构类似：左侧录入，右侧查看状态 -->
          <section class="form-panel">
            <h3>新增访客</h3>
            <p class="panel-desc">提前登记来访目的和预计时间，方便物业核验。</p>
            <el-form label-position="top">
              <el-form-item label="来访目的">
                <el-input v-model="visitorForm.visitorObjective" placeholder="例如：亲友来访、维修服务" />
              </el-form-item>
              <el-form-item label="预计来访时间">
                <el-date-picker
                  v-model="visitorForm.visitorTime"
                  type="datetime"
                  value-format="YYYY-MM-DDTHH:mm:ss"
                  placeholder="选择时间"
                  style="width: 100%"
                />
              </el-form-item>
              <el-button type="primary" :icon="Plus" @click="submitVisitor">登记</el-button>
            </el-form>
          </section>
          <section class="list-panel">
            <h3>访客记录</h3>
            <el-table :data="visitors" v-loading="loading.visitor" empty-text="暂无访客记录">
              <el-table-column prop="visitorObjective" label="来访目的" min-width="220" show-overflow-tooltip />
              <el-table-column prop="visitorTime" label="来访时间" min-width="170" />
              <el-table-column label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.visitorStatus === 1 ? 'success' : 'info'">
                    {{ row.visitorStatus === 1 ? '已完成' : '待访' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </section>
        </div>
      </el-tab-pane>

      <el-tab-pane label="物业缴费" name="charge">
        <div class="table-wrap">
          <el-table :data="charges" v-loading="loading.charge" empty-text="暂无物业账单">
            <el-table-column prop="chargeNo" label="账单编号" min-width="180" />
            <el-table-column prop="chargeName" label="账单名称" min-width="160" />
            <el-table-column label="金额" width="120">
              <template #default="{ row }">¥{{ formatMoney(row.totalPrice) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.chargeStatus === 1 ? 'success' : 'warning'">
                  {{ row.chargeStatus === 1 ? '已缴' : '未缴' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="生成时间" min-width="170" />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button text type="primary" :icon="Document" @click="openCharge(row)">明细</el-button>
                <el-button v-if="row.chargeStatus !== 1" text type="success" :icon="Check" @click="payCharge(row)">
                  缴费
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我的车位" name="parking">
        <div class="table-wrap">
          <el-table :data="parkings" v-loading="loading.parking" empty-text="暂无车位信息">
            <el-table-column prop="parkingName" label="车位名称" min-width="150" />
            <el-table-column label="类型" width="120">
              <template #default="{ row }">{{ row.parkingType === 1 ? '固定车位' : '临停车位' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="row.parkingStatus === 1 ? 'success' : 'info'">
                  {{ row.parkingStatus === 1 ? '正常' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="绑定时间" min-width="170" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button text type="primary" :icon="Document" @click="openParking(row)">车辆</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 账单明细抽屉：点击“明细”时打开，不占主页面空间 -->
    <el-drawer v-model="chargeDrawer" title="账单明细" size="420px">
      <el-table :data="chargeDetails">
        <el-table-column prop="chargeContent" label="项目" min-width="170" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">¥{{ formatMoney(row.chargePrice) }}</template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <!-- 车位绑定车辆抽屉 -->
    <el-drawer v-model="parkingDrawer" title="绑定车辆" size="360px">
      <el-table :data="parkingBinds">
        <el-table-column prop="licensePlate" label="车牌号" min-width="160" />
        <el-table-column prop="createTime" label="绑定时间" min-width="160" />
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Check, Document, Key, Plus, Refresh, Wallet, HomeFilled } from '@element-plus/icons-vue'
import {
  createCommunityComplaint,
  createCommunityVisitor,
  getCommunityChargeDetails,
  getCommunityCharges,
  getCommunityComplaints,
  getCommunityNotices,
  getCommunityParkingBinds,
  getCommunityParkings,
  getCommunityVisitors,
  payCommunityCharge
} from '@/api'

const router = useRouter()
const route = useRoute()
const activeTab = ref('notice')
const notices = ref([])
const complaints = ref([])
const visitors = ref([])
const charges = ref([])
const parkings = ref([])
const chargeDetails = ref([])
const parkingBinds = ref([])
const chargeDrawer = ref(false)
const parkingDrawer = ref(false)

// 表单数据用 reactive，适合存放一组字段。
const complaintForm = reactive({ complaintContent: '' })
const visitorForm = reactive({ visitorObjective: '', visitorTime: '' })

// 每个模块单独维护 loading，避免一个 tab 刷新时影响其它 tab。
const loading = reactive({
  notice: false,
  complaint: false,
  visitor: false,
  charge: false,
  parking: false
})

// 概览卡片的数据由当前列表计算得到，不需要额外写后端统计接口。
const unpaidCount = computed(() => charges.value.filter(item => item.chargeStatus !== 1).length)
const pendingComplaintCount = computed(() => complaints.value.filter(item => item.complaintStatus !== 1).length)
const pendingVisitorCount = computed(() => visitors.value.filter(item => item.visitorStatus !== 1).length)
const summaryCards = computed(() => [
  { tab: 'notice', label: '可查看公告', value: notices.value.length, icon: Bell },
  { tab: 'complaint', label: '待处理事项', value: pendingComplaintCount.value, icon: Document },
  { tab: 'visitor', label: '待访记录', value: pendingVisitorCount.value, icon: Key },
  { tab: 'charge', label: '未缴账单', value: unpaidCount.value, icon: Wallet },
  { tab: 'parking', label: '我的车位', value: parkings.value.length, icon: HomeFilled }
])

onMounted(() => {
  // 社区服务需要登录。未登录时直接去登录页。
  if (!localStorage.getItem('token')) {
    router.push('/login')
    return
  }
  if (typeof route.query.tab === 'string') {
    activeTab.value = route.query.tab
  }
  // 先加载当前 tab，再补齐概览卡需要的其它模块数据。
  loadActive()
  loadSummaryData()
})

function reloadActive() {
  loadActive()
}

function loadActive() {
  // 用对象映射代替一堆 if/else，tab 名称和加载函数一一对应。
  const loaders = {
    notice: loadNotices,
    complaint: loadComplaints,
    visitor: loadVisitors,
    charge: loadCharges,
    parking: loadParkings
  }
  loaders[activeTab.value]?.()
}

// 首页快捷卡点击后会走这里，切换 tab 并加载对应数据。
function switchTab(tab) {
  activeTab.value = tab
  loadActive()
}

// 概览卡需要多个模块的数据，所以进入页面时统一加载一次。
function loadSummaryData() {
  loadNotices()
  loadComplaints()
  loadVisitors()
  loadCharges()
  loadParkings()
}

// 以下 loadXxx 方法都只负责“查列表并赋值”，提交/缴费逻辑单独写。
async function loadNotices() {
  loading.notice = true
  try {
    const res = await getCommunityNotices({ page: 1, size: 30 })
    notices.value = res?.data?.records || []
  } finally {
    loading.notice = false
  }
}

async function loadComplaints() {
  loading.complaint = true
  try {
    const res = await getCommunityComplaints({ page: 1, size: 30 })
    complaints.value = res?.data?.records || []
  } finally {
    loading.complaint = false
  }
}

// 提交报修：先做前端校验，再调用后端接口。
async function submitComplaint() {
  if (!complaintForm.complaintContent.trim()) {
    ElMessage.warning('请填写事项描述')
    return
  }
  await createCommunityComplaint({ complaintContent: complaintForm.complaintContent.trim() })
  ElMessage.success('已提交')
  complaintForm.complaintContent = ''
  loadComplaints()
}

async function loadVisitors() {
  loading.visitor = true
  try {
    const res = await getCommunityVisitors({ page: 1, size: 30 })
    visitors.value = res?.data?.records || []
  } finally {
    loading.visitor = false
  }
}

// 提交访客登记：时间格式由 el-date-picker 的 value-format 控制。
async function submitVisitor() {
  if (!visitorForm.visitorObjective.trim() || !visitorForm.visitorTime) {
    ElMessage.warning('请填写来访目的和时间')
    return
  }
  await createCommunityVisitor({
    visitorObjective: visitorForm.visitorObjective.trim(),
    visitorTime: visitorForm.visitorTime
  })
  ElMessage.success('访客已登记')
  visitorForm.visitorObjective = ''
  visitorForm.visitorTime = ''
  loadVisitors()
}

async function loadCharges() {
  loading.charge = true
  try {
    const res = await getCommunityCharges({ page: 1, size: 30 })
    charges.value = res?.data?.records || []
  } finally {
    loading.charge = false
  }
}

// 打开账单明细：先根据账单号请求明细，再打开抽屉。
async function openCharge(row) {
  const res = await getCommunityChargeDetails(row.chargeNo)
  chargeDetails.value = res?.data || []
  chargeDrawer.value = true
}

// 这里是模拟缴费，后端会把账单状态改成已缴。
async function payCharge(row) {
  await payCommunityCharge(row.chargeNo)
  ElMessage.success('缴费成功')
  row.chargeStatus = 1
}

async function loadParkings() {
  loading.parking = true
  try {
    const res = await getCommunityParkings({ page: 1, size: 30 })
    parkings.value = res?.data?.records || []
  } finally {
    loading.parking = false
  }
}

// 打开车位绑定车辆明细。
async function openParking(row) {
  const res = await getCommunityParkingBinds(row.id)
  parkingBinds.value = res?.data || []
  parkingDrawer.value = true
}

// 金额展示统一保留两位小数。
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<style scoped>
.community-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.page-head p {
  max-width: 640px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.summary-card {
  min-height: 82px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg);
  cursor: pointer;
  text-align: left;
  transition: all 0.2s;
}

.summary-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.summary-icon {
  width: 38px;
  height: 38px;
  border-radius: var(--radius);
  background: var(--accent-bg);
  color: var(--accent);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
}

.summary-text strong,
.summary-text small {
  display: block;
}

.summary-text strong {
  font-size: 22px;
  line-height: 1;
  color: var(--text-h);
}

.summary-text small {
  color: var(--text);
  margin-top: 6px;
  white-space: nowrap;
}

.service-tabs {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 0 16px 16px;
}

.service-grid {
  display: grid;
  grid-template-columns: minmax(280px, 360px) minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}

.form-panel,
.list-panel,
.table-wrap {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg);
  overflow: hidden;
}

.form-panel,
.list-panel {
  padding: 18px;
}

.list-panel {
  min-width: 0;
}

.form-panel h3,
.list-panel h3 {
  margin-bottom: 8px;
}

.panel-desc {
  margin-bottom: 14px;
  font-size: 13px;
}

@media (max-width: 1024px) {
  .page-head {
    flex-direction: column;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
