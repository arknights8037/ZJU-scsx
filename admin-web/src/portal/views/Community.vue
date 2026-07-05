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
            <el-table-column label="操作" width="90" fixed="right">
              <template #default="{ row }">
                <el-button text type="primary" @click="openNotice(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="报事报修" name="complaint">
        <div class="repair-workbench">
          <!-- 左侧是提交表单，右侧是当前用户自己的历史记录 -->
          <section class="form-panel repair-form-panel">
            <div class="panel-title-row">
              <div>
                <h3>提交报修</h3>
                <p class="panel-desc">把问题类型、位置和联系方式说清楚，物业处理会更快。</p>
              </div>
              <el-tag :type="complaintForm.urgency === '紧急' ? 'danger' : 'info'">
                {{ complaintForm.urgency }}
              </el-tag>
            </div>

            <div class="template-grid">
              <button
                v-for="item in repairTemplates"
                :key="item.title"
                type="button"
                class="template-card"
                @click="applyRepairTemplate(item)"
              >
                <strong>{{ item.title }}</strong>
                <small>{{ item.desc }}</small>
              </button>
            </div>

            <el-form label-position="top">
              <div class="form-grid compact">
                <el-form-item label="报修类型">
                  <el-select v-model="complaintForm.repairType" placeholder="请选择类型">
                    <el-option v-for="type in repairTypes" :key="type" :label="type" :value="type" />
                  </el-select>
                </el-form-item>
                <el-form-item label="紧急程度">
                  <el-segmented v-model="complaintForm.urgency" :options="urgencyOptions" />
                </el-form-item>
              </div>

              <el-form-item label="问题位置">
                <el-input
                  v-model="complaintForm.location"
                  maxlength="80"
                  placeholder="例如：3 栋 2 单元 1201 / 地下车库 B2"
                />
              </el-form-item>

              <div class="form-grid compact">
                <el-form-item label="联系人">
                  <el-input v-model="complaintForm.contactName" maxlength="20" placeholder="方便物业联系" />
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="complaintForm.contactPhone" maxlength="20" placeholder="默认可填登录手机号" />
                </el-form-item>
              </div>

              <el-form-item label="期望上门时间">
                <el-date-picker
                  v-model="complaintForm.preferredTime"
                  type="datetime"
                  value-format="YYYY-MM-DDTHH:mm:ss"
                  placeholder="可选，便于物业安排"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="问题描述">
                <el-input
                  v-model="complaintForm.complaintContent"
                  type="textarea"
                  :rows="6"
                  maxlength="500"
                  show-word-limit
                  placeholder="请补充现象、影响范围、是否持续发生等信息"
                />
              </el-form-item>
              <div class="form-actions">
                <el-button @click="resetComplaintForm">清空</el-button>
                <el-button type="primary" :icon="Plus" :loading="submittingComplaint" @click="submitComplaint">
                  提交报修
                </el-button>
              </div>
            </el-form>
          </section>
          <section class="list-panel">
            <div class="panel-title-row">
              <div>
                <h3>我的报修</h3>
                <p class="panel-desc">卡片会保留提交信息，方便后续跟进。</p>
              </div>
              <el-button text :icon="Refresh" @click="loadComplaints">刷新</el-button>
            </div>

            <div v-loading="loading.complaint" class="repair-list">
              <article v-for="row in complaints" :key="row.id" class="repair-card">
                <div class="repair-card-head">
                  <div>
                    <strong>{{ parseComplaint(row).repairType }}</strong>
                    <small>{{ formatDate(row.createTime) }}</small>
                  </div>
                  <el-tag :type="row.complaintStatus === 1 ? 'success' : 'warning'">
                    {{ row.complaintStatus === 1 ? '已处理' : '待处理' }}
                  </el-tag>
                </div>
                <p class="repair-desc">{{ parseComplaint(row).description }}</p>
                <div class="repair-meta">
                  <span>位置：{{ parseComplaint(row).location || '未填写' }}</span>
                  <span>紧急：{{ parseComplaint(row).urgency }}</span>
                  <span>联系：{{ parseComplaint(row).contactPhone || '未填写' }}</span>
                  <span v-if="parseComplaint(row).preferredTime">期望：{{ formatDate(parseComplaint(row).preferredTime) }}</span>
                </div>
                <el-steps :active="row.complaintStatus === 1 ? 3 : 1" simple finish-status="success" class="repair-steps">
                  <el-step title="已提交" />
                  <el-step title="物业处理" />
                  <el-step title="已完成" />
                </el-steps>
              </article>
              <div v-if="!complaints.length && !loading.complaint" class="page-empty compact-empty">
                <p>暂无报修记录</p>
                <small>遇到设施故障、漏水、电梯、照明等问题，可以从左侧快速提交。</small>
              </div>
            </div>
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
              <el-button type="primary" :icon="Plus" :loading="submittingVisitor" @click="submitVisitor">登记</el-button>
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
                <el-button
                  v-if="row.chargeStatus !== 1"
                  text
                  type="success"
                  :icon="Check"
                  :loading="payingNo === row.chargeNo"
                  @click="payCharge(row)"
                >
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

    <!-- 公告详情抽屉：居民端用详情阅读，比表格里挤内容更自然 -->
    <el-drawer v-model="noticeDrawer" title="公告详情" size="460px">
      <article v-if="activeNotice" class="notice-detail">
        <h3>{{ activeNotice.noticeTitle }}</h3>
        <p class="notice-time">{{ activeNotice.createTime }}</p>
        <div class="notice-content">{{ activeNotice.noticeContent }}</div>
      </article>
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
/**
 * Community - 门户端社区服务页面
 *
 * 功能：
 * - 概览卡片展示各模块待处理数量
 * - 通知公告：查看社区公告列表及详情
 * - 报事报修：提交报修工单（支持模板快捷填写）、查看报修历史及进度
 * - 访客登记：新增访客登记、查看访客记录
 * - 物业缴费：查看物业账单明细、在线缴费（模拟）
 * - 我的车位：查看车位信息和绑定车辆
 *
 * 路由路径：/portal/community（支持 query 参数：tab 指定打开的 Tab）
 */

import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
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
} from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const activeTab = ref('notice')      // 当前激活的 Tab 名称
const notices = ref([])              // 公告列表
const complaints = ref([])           // 报修列表
const visitors = ref([])             // 访客登记列表
const charges = ref([])              // 物业账单列表
const parkings = ref([])             // 车位列表
const chargeDetails = ref([])        // 账单明细（抽屉内容）
const parkingBinds = ref([])         // 车位绑定车辆列表
const chargeDrawer = ref(false)      // 账单明细抽屉可见性
const parkingDrawer = ref(false)     // 车位车辆抽屉可见性
const noticeDrawer = ref(false)      // 公告详情抽屉可见性
const activeNotice = ref(null)       // 当前打开的公告对象
const submittingComplaint = ref(false)  // 提交报修按钮加载状态
const submittingVisitor = ref(false)    // 提交访客登记按钮加载状态
const payingNo = ref('')             // 正在缴费的账单编号（用于按钮 loading）

/** 报修类型选项 */
const repairTypes = ['水电维修', '电梯故障', '公共照明', '门禁安防', '环境卫生', '邻里投诉', '其他问题']
/** 紧急程度选项 */
const urgencyOptions = [
  { label: '普通', value: '普通' },
  { label: '较急', value: '较急' },
  { label: '紧急', value: '紧急' }
]
/** 报修快速模板 */
const repairTemplates = [
  { title: '家中漏水', desc: '水管、天花、卫生间渗漏', type: '水电维修', urgency: '较急', content: '发现漏水/渗水情况，请物业协助检查并安排维修。' },
  { title: '电梯异常', desc: '异响、停运、按钮失灵', type: '电梯故障', urgency: '紧急', content: '电梯运行异常，影响居民通行，请尽快安排排查。' },
  { title: '照明损坏', desc: '楼道、车库、园区灯不亮', type: '公共照明', urgency: '普通', content: '公共区域照明损坏，夜间通行不便，请安排更换或维修。' },
  { title: '环境卫生', desc: '垃圾、异味、公共区域脏乱', type: '环境卫生', urgency: '普通', content: '公共区域存在卫生问题，请安排保洁处理。' }
]

/** 报修表单数据，表单数据用 reactive，适合存放一组字段。数据库仍只保存 complaintContent，所以提交前会打包成结构化文本 */
const complaintForm = reactive({
  repairType: '水电维修',
  urgency: '普通',
  location: '',
  contactName: '',
  contactPhone: '',
  preferredTime: '',
  complaintContent: ''
})

/** 访客登记表单 */
const visitorForm = reactive({ visitorObjective: '', visitorTime: '' })

/** 各模块独立 loading 状态，避免一个 tab 刷新时影响其它 tab */
const loading = reactive({
  notice: false,
  complaint: false,
  visitor: false,
  charge: false,
  parking: false
})

// ========== 计算属性 ==========
/** 未缴费账单数量 */
const unpaidCount = computed(() => charges.value.filter(item => item.chargeStatus !== 1).length)
/** 待处理报修数量 */
const pendingComplaintCount = computed(() => complaints.value.filter(item => item.complaintStatus !== 1).length)
/** 待访客记录数量 */
const pendingVisitorCount = computed(() => visitors.value.filter(item => item.visitorStatus !== 1).length)
/** 概览卡片数据，由当前列表计算得到，不需要额外写后端统计接口 */
const summaryCards = computed(() => [
  { tab: 'notice', label: '可查看公告', value: notices.value.length, icon: Bell },
  { tab: 'complaint', label: '待处理事项', value: pendingComplaintCount.value, icon: Document },
  { tab: 'visitor', label: '待访记录', value: pendingVisitorCount.value, icon: Key },
  { tab: 'charge', label: '未缴账单', value: unpaidCount.value, icon: Wallet },
  { tab: 'parking', label: '我的车位', value: parkings.value.length, icon: HomeFilled }
])

// ========== 生命周期 ==========
onMounted(() => {
  // 社区服务需要登录。未登录时直接去登录页。
  if (!auth.isLoggedIn) {
    router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
    return
  }
  // 从 URL query 中读取初始 tab
  if (typeof route.query.tab === 'string') {
    activeTab.value = route.query.tab
  }
  complaintForm.contactPhone = auth.user.phone || ''
  complaintForm.contactName = auth.user.userName || ''
  // 先加载当前 tab，再补齐概览卡需要的其它模块数据
  loadActive()
  loadSummaryData()
})

/** 刷新当前 Tab 数据 */
function reloadActive() {
  loadActive()
}

/**
 * 加载当前激活 Tab 对应的数据
 * 用对象映射代替一堆 if/else，tab 名称和加载函数一一对应
 */
function loadActive() {
  const loaders = {
    notice: loadNotices,
    complaint: loadComplaints,
    visitor: loadVisitors,
    charge: loadCharges,
    parking: loadParkings
  }
  loaders[activeTab.value]?.()
}

/**
 * 切换 Tab
 * @param {string} tab - 目标 Tab 名称
 */
function switchTab(tab) {
  activeTab.value = tab
  loadActive()
}

/**
 * 加载概览卡片需要的所有模块数据
 * 概览卡需要多个模块的数据，所以进入页面时统一加载一次
 */
function loadSummaryData() {
  loadNotices()
  loadComplaints()
  loadVisitors()
  loadCharges()
  loadParkings()
}

/**
 * 加载通知公告列表
 * 以下 loadXxx 方法都只负责”查列表并赋值”，提交/缴费逻辑单独写
 */
async function loadNotices() {
  loading.notice = true
  try {
    const res = await getCommunityNotices({ page: 1, size: 30 })
    notices.value = res?.data?.records || []
  } finally {
    loading.notice = false
  }
}

/**
 * 加载报修列表
 */
async function loadComplaints() {
  loading.complaint = true
  try {
    const res = await getCommunityComplaints({ page: 1, size: 30 })
    complaints.value = res?.data?.records || []
  } finally {
    loading.complaint = false
  }
}

/**
 * 提交报修：先做前端校验，再调用后端接口
 */
async function submitComplaint() {
  if (!complaintForm.location.trim()) {
    ElMessage.warning('请填写问题位置')
    return
  }
  if (!complaintForm.complaintContent.trim()) {
    ElMessage.warning('请填写事项描述')
    return
  }
  if (complaintForm.contactPhone.trim() && !/^[-+()\d\\s]{6,20}$/.test(complaintForm.contactPhone.trim())) {
    ElMessage.warning('请填写有效联系电话')
    return
  }
  submittingComplaint.value = true
  try {
    await ElMessageBox.confirm(
      `确认提交”${complaintForm.repairType}”报修？物业将按你填写的联系方式跟进。`,
      '确认提交',
      {
        confirmButtonText: '提交',
        cancelButtonText: '再检查',
        type: complaintForm.urgency === '紧急' ? 'warning' : 'info'
      }
    )
    await createCommunityComplaint({ complaintContent: buildComplaintContent() })
    ElMessage.success('已提交')
    resetComplaintForm()
    await loadComplaints()
  } catch (e) {
    /* 用户取消或请求错误由拦截器处理 */
  } finally {
    submittingComplaint.value = false
  }
}

/**
 * 加载访客记录列表
 */
async function loadVisitors() {
  loading.visitor = true
  try {
    const res = await getCommunityVisitors({ page: 1, size: 30 })
    visitors.value = res?.data?.records || []
  } finally {
    loading.visitor = false
  }
}

/**
 * 提交访客登记：时间格式由 el-date-picker 的 value-format 控制
 */
async function submitVisitor() {
  if (!visitorForm.visitorObjective.trim() || !visitorForm.visitorTime) {
    ElMessage.warning('请填写来访目的和时间')
    return
  }
  submittingVisitor.value = true
  try {
    await createCommunityVisitor({
      visitorObjective: visitorForm.visitorObjective.trim(),
      visitorTime: visitorForm.visitorTime
    })
    ElMessage.success('访客已登记')
    visitorForm.visitorObjective = ''
    visitorForm.visitorTime = ''
    loadVisitors()
  } finally {
    submittingVisitor.value = false
  }
}

/**
 * 加载物业账单列表
 */
async function loadCharges() {
  loading.charge = true
  try {
    const res = await getCommunityCharges({ page: 1, size: 30 })
    charges.value = res?.data?.records || []
  } finally {
    loading.charge = false
  }
}

/**
 * 打开账单明细抽屉：先根据账单号请求明细，再打开抽屉
 * @param {Object} row - 账单行数据
 */
async function openCharge(row) {
  const res = await getCommunityChargeDetails(row.chargeNo)
  chargeDetails.value = res?.data || []
  chargeDrawer.value = true
}

/**
 * 模拟缴费，后端会把账单状态改成已缴
 * @param {Object} row - 账单行数据
 */
async function payCharge(row) {
  try {
    await ElMessageBox.confirm(`确认缴纳”${row.chargeName}”，金额 ¥${formatMoney(row.totalPrice)}？`, '确认缴费', {
      confirmButtonText: '确认缴费',
      cancelButtonText: '取消',
      type: 'warning'
    })
    payingNo.value = row.chargeNo
    await payCommunityCharge(row.chargeNo)
    ElMessage.success('缴费成功')
    row.chargeStatus = 1  // 更新本地状态为已缴
  } catch (e) {
    /* cancel or handled */
  } finally {
    payingNo.value = ''
  }
}

/**
 * 加载车位列表
 */
async function loadParkings() {
  loading.parking = true
  try {
    const res = await getCommunityParkings({ page: 1, size: 30 })
    parkings.value = res?.data?.records || []
  } finally {
    loading.parking = false
  }
}

/**
 * 打开车位绑定车辆明细抽屉
 * @param {Object} row - 车位行数据
 */
async function openParking(row) {
  const res = await getCommunityParkingBinds(row.id)
  parkingBinds.value = res?.data || []
  parkingDrawer.value = true
}

/**
 * 金额展示统一保留两位小数
 * @param {number} value - 金额数值
 * @returns {string} 格式化后的金额字符串
 */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

/**
 * 打开公告详情抽屉
 * @param {Object} row - 公告行数据
 */
function openNotice(row) {
  activeNotice.value = row
  noticeDrawer.value = true
}

/**
 * 应用报修快速模板
 * @param {Object} item - 模板对象（包含 type, urgency, content）
 */
function applyRepairTemplate(item) {
  complaintForm.repairType = item.type
  complaintForm.urgency = item.urgency
  complaintForm.complaintContent = item.content
}

/**
 * 重置报修表单为默认值
 */
function resetComplaintForm() {
  complaintForm.repairType = '水电维修'
  complaintForm.urgency = '普通'
  complaintForm.location = ''
  complaintForm.contactName = auth.user.userName || ''
  complaintForm.contactPhone = auth.user.phone || ''
  complaintForm.preferredTime = ''
  complaintForm.complaintContent = ''
}

/**
 * 构建报修提交内容（结构化文本）
 * 将所有表单字段打包成带标记的文本字符串，存入 complaintContent 字段
 * @returns {string} 结构化的报修内容文本
 */
function buildComplaintContent() {
  return [
    `【报修类型】${complaintForm.repairType}`,
    `【紧急程度】${complaintForm.urgency}`,
    `【问题位置】${complaintForm.location.trim()}`,
    `【联系人】${complaintForm.contactName.trim() || '未填写'}`,
    `【联系电话】${complaintForm.contactPhone.trim() || '未填写'}`,
    `【期望上门】${complaintForm.preferredTime || '未填写'}`,
    `【问题描述】${complaintForm.complaintContent.trim()}`
  ].join('\n')
}

/**
 * 解析报修记录的结构化文本
 * 从 complaintContent 中提取各字段信息
 * @param {Object} row - 报修记录行数据
 * @returns {Object} 解析后的报修信息对象
 */
function parseComplaint(row) {
  const text = stripHtml(row.complaintContent || '')
  const data = {}
  for (const line of text.split(/\r?\n/)) {
    const match = line.match(/^【(.+?)】(.*)$/)
    if (match) data[match[1]] = match[2].trim()
  }
  return {
    repairType: data['报修类型'] || '报事报修',
    urgency: data['紧急程度'] || '普通',
    location: data['问题位置'] || '',
    contactName: data['联系人'] || '',
    contactPhone: data['联系电话'] === '未填写' ? '' : (data['联系电话'] || ''),
    preferredTime: data['期望上门'] === '未填写' ? '' : (data['期望上门'] || ''),
    description: data['问题描述'] || text || '暂无描述'
  }
}

/**
 * 去除 HTML 标签，将换行标签转换为 \n
 * @param {string} value - 包含 HTML 的原始文本
 * @returns {string} 清洗后的纯文本
 */
function stripHtml(value) {
  return String(value)
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/p>/gi, '\n')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .trim()
}

/**
 * 格式化日期为中国本地时间字符串
 * @param {string|null} value - ISO 日期字符串
 * @returns {string} 格式化后的日期字符串
 */
function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
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

.repair-workbench {
  display: grid;
  grid-template-columns: minmax(320px, 420px) minmax(0, 1fr);
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

.panel-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.panel-title-row .panel-desc {
  margin-bottom: 0;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 16px;
}

.template-card {
  min-height: 68px;
  padding: 11px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface-muted);
  color: var(--text);
  text-align: left;
  cursor: pointer;
  transition: border-color var(--motion-fast), box-shadow var(--motion-fast), transform var(--motion-fast);
}

.template-card:hover {
  border-color: var(--accent-border);
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.template-card strong,
.template-card small {
  display: block;
}

.template-card strong {
  color: var(--text-h);
  font-size: 14px;
}

.template-card small {
  margin-top: 4px;
  color: var(--text-soft);
  font-size: 12px;
}

.form-grid.compact {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.repair-list {
  min-height: 180px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.repair-card {
  padding: 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface-muted);
}

.repair-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.repair-card-head strong,
.repair-card-head small {
  display: block;
}

.repair-card-head strong {
  color: var(--text-h);
  font-size: 15px;
}

.repair-card-head small {
  margin-top: 3px;
  color: var(--text-soft);
  font-size: 12px;
}

.repair-desc {
  margin: 12px 0;
  color: var(--text-h);
  line-height: 1.7;
  white-space: pre-wrap;
}

.repair-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.repair-meta span {
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(15, 108, 189, 0.08);
  color: var(--text);
  font-size: 12px;
}

.repair-steps {
  border-radius: var(--radius);
}

.compact-empty {
  padding: 34px 16px;
}

.compact-empty small {
  display: block;
  margin-top: 6px;
  color: var(--text-soft);
}

.notice-detail h3 {
  margin-bottom: 8px;
}

.notice-time {
  margin-bottom: 16px;
  color: var(--text-soft);
  font-size: 13px;
}

.notice-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--text-h);
}

@media (max-width: 1024px) {
  .page-head {
    flex-direction: column;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .repair-workbench {
    grid-template-columns: 1fr;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .template-grid,
  .form-grid.compact {
    grid-template-columns: 1fr;
  }
}
</style>

