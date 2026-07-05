<template>
  <div class="profile-page" v-loading="loading">
    <!-- 左侧账号摘要：让用户快速确认当前登录的是哪个账号 -->
    <aside class="profile-summary">
      <el-avatar :size="88" :src="profileForm.avatar || undefined" class="profile-avatar">
        {{ avatarText }}
      </el-avatar>
      <h2>{{ profileForm.userName || '社区居民' }}</h2>
      <p>{{ profileForm.phone || '未绑定手机号' }}</p>

      <div class="summary-list">
        <div class="summary-item">
          <el-icon><Phone /></el-icon>
          <span>登录账号</span>
          <strong>{{ profileForm.phone || '--' }}</strong>
        </div>
        <div class="summary-item">
          <el-icon><Message /></el-icon>
          <span>联系邮箱</span>
          <strong>{{ profileForm.mail || '未填写' }}</strong>
        </div>
        <div class="summary-item">
          <el-icon><Location /></el-icon>
          <span>住户地址</span>
          <strong>{{ residenceSummary || '未填写' }}</strong>
        </div>
        <div class="summary-item">
          <el-icon><Wallet /></el-icon>
          <span>钱包余额</span>
          <strong>¥{{ formatMoney(wallet.balance) }}</strong>
        </div>
      </div>
    </aside>

    <section class="profile-main">
      <div class="page-head">
        <div>
          <h1>个人中心</h1>
          <p>维护居民基础资料、业主门牌号和登录密码。</p>
        </div>
      </div>

      <!-- 登录信息来自后端 User 表，最近登录时间会在每次登录成功后更新 -->
      <div class="account-grid">
        <div class="account-item">
          <span>登录手机号</span>
          <strong>{{ profileForm.phone || '--' }}</strong>
        </div>
        <div class="account-item">
          <span>账号状态</span>
          <strong class="status-normal">{{ accountStatus }}</strong>
        </div>
        <div class="account-item">
          <span>业主门牌</span>
          <strong>{{ residenceSummary || '未填写' }}</strong>
        </div>
        <div class="account-item">
          <span>最近登录</span>
          <strong>{{ formatDate(profileForm.lastLoginTime) }}</strong>
        </div>
        <div class="account-item">
          <span>注册时间</span>
          <strong>{{ formatDate(profileForm.createTime) }}</strong>
        </div>
        <div class="account-item wallet-entry" @click="activeTab = 'wallet'">
          <span>虚拟钱包</span>
          <strong>¥{{ formatMoney(wallet.balance) }}</strong>
          <small>点击进入钱包</small>
        </div>
      </div>

      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="基本资料" name="basic">
          <el-form label-position="top" class="profile-form">
            <div class="form-grid">
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" disabled>
                  <template #prefix><el-icon><Phone /></el-icon></template>
                </el-input>
                <p class="field-help">手机号是登录账号，暂不支持自行修改。</p>
              </el-form-item>

              <el-form-item label="用户名">
                <el-input v-model="profileForm.userName" maxlength="30" show-word-limit>
                  <template #prefix><el-icon><User /></el-icon></template>
                </el-input>
              </el-form-item>

              <el-form-item label="性别">
                <el-segmented v-model="profileForm.sex" :options="sexOptions" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="profileForm.mail" placeholder="例如：name@example.com">
                  <template #prefix><el-icon><Message /></el-icon></template>
                </el-input>
              </el-form-item>
            </div>

            <section class="resident-panel">
              <div class="section-title">
                <h3>住户信息</h3>
                <p>填写楼栋、单元和门牌号，后续报修、账单、配送都会更准确。</p>
              </div>
              <div class="form-grid">
                <el-form-item label="业主姓名">
                  <el-input v-model="profileForm.ownerName" maxlength="30" placeholder="例如：张三">
                    <template #prefix><el-icon><User /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-form-item label="完整住址">
                  <el-input v-model="profileForm.fullAddress" maxlength="120" placeholder="例如：智慧社区 3栋2单元1201">
                    <template #prefix><el-icon><Location /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-form-item label="楼栋号">
                  <el-input v-model="profileForm.buildingNo" maxlength="20" placeholder="例如：3栋" />
                </el-form-item>
                <el-form-item label="单元号">
                  <el-input v-model="profileForm.unitNo" maxlength="20" placeholder="例如：2单元" />
                </el-form-item>
                <el-form-item label="门牌号">
                  <el-input v-model="profileForm.roomNo" maxlength="20" placeholder="例如：1201" />
                </el-form-item>
                <el-form-item label="紧急联系人">
                  <el-input v-model="profileForm.emergencyContact" maxlength="30" placeholder="例如：家属/管家姓名" />
                </el-form-item>
                <el-form-item label="紧急联系电话">
                  <el-input v-model="profileForm.emergencyPhone" maxlength="30" placeholder="例如：13800000000">
                    <template #prefix><el-icon><Phone /></el-icon></template>
                  </el-input>
                </el-form-item>
              </div>
            </section>

            <el-form-item label="个人头像">
              <div class="avatar-upload-row">
                <el-upload
                  class="avatar-uploader"
                  accept=".jpg,.jpeg,.png,.webp,.gif"
                  :show-file-list="false"
                  :before-upload="beforeImageUpload"
                  :http-request="uploadAvatar"
                >
                  <div class="avatar-upload-box" v-loading="avatarUploading">
                    <img v-if="profileForm.avatar" :src="assetUrl(profileForm.avatar)" alt="头像预览" />
                    <el-icon v-else><Plus /></el-icon>
                  </div>
                </el-upload>
                <div class="upload-help">
                  <strong>上传头像</strong>
                  <p>支持 JPG、PNG、WebP、GIF，大小不超过 5MB。</p>
                  <el-button v-if="profileForm.avatar" text type="danger" :icon="Delete" @click="removeAvatar">
                    移除头像
                  </el-button>
                </div>
              </div>
            </el-form-item>

            <div class="form-actions">
              <el-button :icon="RefreshLeft" @click="loadProfile">恢复</el-button>
              <el-button type="primary" :icon="Check" :loading="saving" @click="saveProfile">
                保存资料
              </el-button>
            </div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="我的钱包" name="wallet">
          <div class="wallet-panel" v-loading="walletLoading">
            <section class="wallet-card">
              <div class="wallet-card-copy">
                <span>可用余额</span>
                <strong><small>¥</small>{{ formatMoney(wallet.balance) }}</strong>
                <p>钱包号 {{ wallet.walletNo || '--' }} · 虚拟演示钱包</p>
              </div>
              <div class="wallet-actions">
                <el-button type="primary" @click="openRecharge">充值</el-button>
                <el-button plain :disabled="Number(wallet.balance) <= 0" @click="openTransfer">转账</el-button>
              </div>
            </section>

            <section class="transaction-panel">
              <div class="transaction-head">
                <div>
                  <h3>收支明细</h3>
                  <p>充值和转账都会生成不可编辑的虚拟流水。</p>
                </div>
                <el-button :icon="RefreshLeft" :loading="transactionLoading" @click="loadTransactions">刷新</el-button>
              </div>
              <el-table v-loading="transactionLoading" :data="transactions" stripe>
                <el-table-column label="类型" width="110">
                  <template #default="{ row }">
                    <el-tag :type="transactionTagType(row.transactionType)" effect="light">
                      {{ transactionTypeText(row.transactionType) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="说明" min-width="190">
                  <template #default="{ row }">
                    <div class="transaction-copy">
                      <strong>{{ row.remark || transactionTypeText(row.transactionType) }}</strong>
                      <small v-if="row.counterpartyName">交易对方：{{ row.counterpartyName }}</small>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="金额" width="130" align="right">
                  <template #default="{ row }">
                    <strong :class="['transaction-amount', Number(row.amount) >= 0 ? 'income' : 'expense']">
                      {{ Number(row.amount) >= 0 ? '+' : '' }}{{ formatMoney(row.amount) }}
                    </strong>
                  </template>
                </el-table-column>
                <el-table-column label="余额" width="120" align="right">
                  <template #default="{ row }">¥{{ formatMoney(row.balanceAfter) }}</template>
                </el-table-column>
                <el-table-column label="时间" width="170">
                  <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
                </el-table-column>
                <template #empty><el-empty description="暂无钱包流水" /></template>
              </el-table>
              <el-pagination
                v-if="transactionTotal > transactionSize"
                v-model:current-page="transactionPage"
                class="transaction-pagination"
                background
                layout="total, prev, pager, next"
                :page-size="transactionSize"
                :total="transactionTotal"
                @current-change="loadTransactions"
              />
            </section>
          </div>
        </el-tab-pane>

        <el-tab-pane label="我的账单" name="expense">
          <div class="expense-panel" v-loading="expenseLoading">
            <div class="expense-heading">
              <div>
                <h3>个人消费账单</h3>
                <p>记录商城消费和日常支出，仅用于记账，不会改变钱包余额。</p>
              </div>
              <el-button type="primary" :icon="Plus" @click="openExpenseDialog">记一笔支出</el-button>
            </div>

            <div class="expense-summary">
              <div><span>累计支出</span><strong>¥{{ formatMoney(expenseSummary.totalExpense) }}</strong></div>
              <div><span>本月支出</span><strong>¥{{ formatMoney(expenseSummary.monthExpense) }}</strong></div>
              <div><span>账单笔数</span><strong>{{ expenseSummary.recordCount || 0 }} 笔</strong></div>
            </div>

            <div class="expense-table-wrap">
              <el-table :data="expenses" stripe>
                <el-table-column label="支出项目" min-width="190">
                  <template #default="{ row }">
                    <div class="transaction-copy">
                      <strong>{{ row.expenseTitle }}</strong>
                      <small>{{ row.remark || row.expenseNo }}</small>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="category" label="分类" width="110" />
                <el-table-column label="来源" width="100">
                  <template #default="{ row }">
                    <el-tag size="small" :type="expenseSourceType(row.sourceType)" effect="light">
                      {{ expenseSourceText(row.sourceType) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="金额" width="130" align="right">
                  <template #default="{ row }">
                    <strong :class="['expense-amount', Number(row.amount) < 0 ? 'refund' : '']">
                      {{ Number(row.amount) < 0 ? '-' : '' }}¥{{ formatMoney(Math.abs(Number(row.amount || 0))) }}
                    </strong>
                  </template>
                </el-table-column>
                <el-table-column label="支出时间" width="170">
                  <template #default="{ row }">{{ formatDate(row.expenseTime) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="90" align="center">
                  <template #default="{ row }">
                    <el-button v-if="row.sourceType === 'MANUAL'" link type="danger" @click="removeExpense(row)">删除</el-button>
                    <span v-else class="auto-record">自动</span>
                  </template>
                </el-table-column>
                <template #empty><el-empty description="暂无消费账单" /></template>
              </el-table>
            </div>
            <el-pagination
              v-if="expenseTotal > expenseSize"
              v-model:current-page="expensePage"
              class="transaction-pagination"
              background
              layout="total, prev, pager, next"
              :page-size="expenseSize"
              :total="expenseTotal"
              @current-change="loadExpenses"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="账户安全" name="security">
          <div class="security-intro">
            <span class="security-icon"><el-icon><Lock /></el-icon></span>
            <div>
              <h3>修改登录密码</h3>
              <p>新密码至少 6 位，修改成功后需要重新登录。</p>
            </div>
          </div>

          <el-form label-position="top" class="password-form" @keyup.enter="savePassword">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="current-password" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" />
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" />
            </el-form-item>
            <el-button type="primary" :icon="Lock" :loading="passwordSaving" @click="savePassword">
              修改密码
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="rechargeVisible" title="钱包充值" width="min(460px, 92vw)" :close-on-click-modal="false">
        <div class="wallet-dialog-balance">当前余额 <strong>¥{{ formatMoney(wallet.balance) }}</strong></div>
        <el-form label-position="top">
          <el-form-item label="充值金额">
            <div class="amount-presets">
              <el-button
                v-for="amount in rechargePresets"
                :key="amount"
                :type="rechargeForm.amount === amount ? 'primary' : ''"
                plain
                @click="rechargeForm.amount = amount"
              >¥{{ amount }}</el-button>
            </div>
            <el-input-number
              v-model="rechargeForm.amount"
              :min="0.01"
              :max="50000"
              :precision="2"
              :step="10"
              controls-position="right"
              class="money-input"
            />
          </el-form-item>
          <el-form-item label="模拟充值渠道">
            <el-radio-group v-model="rechargeForm.channel">
              <el-radio-button label="微信支付" />
              <el-radio-button label="支付宝" />
              <el-radio-button label="银行卡" />
            </el-radio-group>
          </el-form-item>
          <p class="virtual-tip">本功能为演示环境，不会产生真实扣款。</p>
        </el-form>
        <template #footer>
          <el-button @click="rechargeVisible = false">取消</el-button>
          <el-button type="primary" :loading="walletSubmitting" @click="submitRecharge">确认充值</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="transferVisible" title="钱包转账" width="min(460px, 92vw)" :close-on-click-modal="false">
        <div class="wallet-dialog-balance">可用余额 <strong>¥{{ formatMoney(wallet.balance) }}</strong></div>
        <el-form label-position="top" @keyup.enter="submitTransfer">
          <el-form-item label="收款人手机号">
            <el-input v-model.trim="transferForm.recipientPhone" maxlength="20" placeholder="请输入平台用户手机号" />
          </el-form-item>
          <el-form-item label="转账金额">
            <el-input-number
              v-model="transferForm.amount"
              :min="0.01"
              :max="Math.min(50000, Number(wallet.balance || 0))"
              :precision="2"
              :step="10"
              controls-position="right"
              class="money-input"
            />
          </el-form-item>
          <el-form-item label="转账备注">
            <el-input v-model.trim="transferForm.remark" maxlength="80" show-word-limit placeholder="选填" />
          </el-form-item>
          <p class="virtual-tip">请核对手机号；虚拟转账成功后会同步更新双方余额。</p>
        </el-form>
        <template #footer>
          <el-button @click="transferVisible = false">取消</el-button>
          <el-button type="primary" :loading="walletSubmitting" @click="submitTransfer">确认转账</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="expenseVisible" title="记录一笔支出" width="min(500px, 92vw)" :close-on-click-modal="false">
        <el-alert title="这是一条独立消费记录，不会扣减钱包余额。" type="info" :closable="false" show-icon />
        <el-form label-position="top" class="expense-form">
          <el-form-item label="支出名称">
            <el-input v-model.trim="expenseForm.expenseTitle" maxlength="60" show-word-limit placeholder="例如：购买生活用品" />
          </el-form-item>
          <div class="form-grid">
            <el-form-item label="支出分类">
              <el-select v-model="expenseForm.category" style="width:100%">
                <el-option v-for="category in expenseCategories" :key="category" :label="category" :value="category" />
              </el-select>
            </el-form-item>
            <el-form-item label="支出金额">
              <el-input-number v-model="expenseForm.amount" :min="0.01" :max="1000000" :precision="2" controls-position="right" class="money-input" />
            </el-form-item>
          </div>
          <el-form-item label="支出时间">
            <el-date-picker v-model="expenseForm.expenseTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model.trim="expenseForm.remark" type="textarea" :rows="3" maxlength="200" show-word-limit placeholder="选填" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="expenseVisible = false">取消</el-button>
          <el-button type="primary" :loading="expenseSubmitting" @click="submitExpense">保存账单</el-button>
        </template>
      </el-dialog>
    </section>
  </div>
</template>

<script setup>
/**
 * Profile - 门户端个人中心页面
 *
 * 功能：
 * - 基本资料维护（用户名、性别、邮箱、住户信息）
 * - 头像上传/移除
 * - 虚拟钱包：查看余额、充值、转账、流水明细
 * - 个人消费账单：手动记录、自动记录、删除手工账单
 * - 账户安全：修改登录密码
 *
 * 路由路径：/portal/profile
 */

import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Delete, Location, Lock, Message, Phone, Plus, RefreshLeft, User, Wallet } from '@element-plus/icons-vue'
import {
  changeUserPassword,
  createPersonalExpense,
  deletePersonalExpense,
  getPersonalExpensePage,
  getPersonalExpenseSummary,
  getWallet,
  getWalletTransactions,
  rechargeWallet,
  transferWallet,
  updateUserAvatar,
  updateUserProfile,
  uploadUserImage
} from '@/portal/api'
import { usePortalAuthStore } from '@/portal/stores/auth'
import { assetUrl } from '@/utils/asset'

// ========== 路由实例 ==========
const router = useRouter()
const route = useRoute()
// ========== 认证状态 ==========
const auth = usePortalAuthStore()

// ========== 响应式状态 ==========
const activeTab = ref('basic')           // 当前激活的 Tab
const loading = ref(false)               // 个人资料加载状态
const saving = ref(false)                // 保存资料按钮加载状态
const passwordSaving = ref(false)        // 修改密码按钮加载状态
const avatarUploading = ref(false)       // 头像上传中
const walletLoading = ref(false)         // 钱包数据加载中
const transactionLoading = ref(false)    // 交易记录加载中
const walletSubmitting = ref(false)      // 钱包充值/转账提交中
const rechargeVisible = ref(false)       // 充值弹窗可见性
const transferVisible = ref(false)       // 转账弹窗可见性
const wallet = reactive({ walletNo: '', balance: 0 })  // 钱包信息
const transactions = ref([])             // 交易流水列表
const transactionPage = ref(1)           // 交易流水页码
const transactionSize = 10               // 交易流水每页条数
const transactionTotal = ref(0)          // 交易流水总数
const rechargePresets = [50, 100, 200, 500]  // 快捷充值金额
const rechargeForm = reactive({ amount: 100, channel: '微信支付' })  // 充值表单
const transferForm = reactive({ recipientPhone: '', amount: 10, remark: '' })  // 转账表单
const expenseLoading = ref(false)        // 消费账单加载中
const expenseSubmitting = ref(false)     // 提交消费记录中
const expenseVisible = ref(false)        // 记一笔支出弹窗可见性
const expenses = ref([])                 // 消费账单列表
const expensePage = ref(1)               // 消费账单页码
const expenseSize = 10                   // 消费账单每页条数
const expenseTotal = ref(0)              // 消费账单总数
const expenseSummary = reactive({ totalExpense: 0, monthExpense: 0, recordCount: 0 })  // 消费账单汇总
const expenseCategories = ['餐饮', '购物', '交通', '居住', '生活缴费', '医疗健康', '休闲娱乐', '其他支出']
const expenseForm = reactive({
  expenseTitle: '',
  category: '购物',
  amount: 10,
  expenseTime: '',
  remark: ''
})

/** profileForm 对应后端 UserProfileForm；phone 只用于展示，不会提交修改 */
const profileForm = reactive({
  phone: '',
  userName: '',
  sex: '',
  mail: '',
  avatar: '',
  ownerName: '',
  buildingNo: '',
  unitNo: '',
  roomNo: '',
  fullAddress: '',
  emergencyContact: '',
  emergencyPhone: '',
  userStatus: null,
  lastLoginTime: '',
  createTime: ''
})

/** 修改密码表单 */
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

/** 性别选项 */
const sexOptions = [
  { label: '未设置', value: '' },
  { label: '男', value: '男' },
  { label: '女', value: '女' }
]

// ========== 计算属性 ==========
/** 头像文字（取用户名或手机号首字符） */
const avatarText = computed(() => (profileForm.userName || profileForm.phone || '用').charAt(0))
/** 账号状态文本 */
const accountStatus = computed(() => profileForm.userStatus === 1 ? '正常' : '停用')
/** 住户地址摘要：优先展示完整地址，否则拼接楼栋/单元/门牌号 */
const residenceSummary = computed(() => {
  if (profileForm.fullAddress) return profileForm.fullAddress
  return [profileForm.buildingNo, profileForm.unitNo, profileForm.roomNo].filter(Boolean).join('')
})

// ========== 生命周期 ==========
onMounted(() => {
  if (!auth.isLoggedIn) {
    router.push({ path: '/portal/login', query: { redirect: route.fullPath } })
    return
  }
  Promise.all([loadProfile(), loadWallet(), loadExpenses()])
})

/**
 * 从后端重新读取资料，也用于”恢复”尚未保存的表单改动
 */
async function loadProfile() {
  loading.value = true
  try {
    const user = await auth.loadProfile() || {}
    profileForm.phone = user.phone || ''
    profileForm.userName = user.userName || ''
    profileForm.sex = user.sex || ''
    profileForm.mail = user.mail || ''
    profileForm.avatar = user.avatar || ''
    profileForm.ownerName = user.ownerName || ''
    profileForm.buildingNo = user.buildingNo || ''
    profileForm.unitNo = user.unitNo || ''
    profileForm.roomNo = user.roomNo || ''
    profileForm.fullAddress = user.fullAddress || ''
    profileForm.emergencyContact = user.emergencyContact || ''
    profileForm.emergencyPhone = user.emergencyPhone || ''
    profileForm.userStatus = user.userStatus
    profileForm.lastLoginTime = user.lastLoginTime || getTokenLoginTime()
    profileForm.createTime = user.createTime || ''
  } finally {
    loading.value = false
  }
}

/**
 * 保存个人资料
 * 后端会把账号字段保存到 user 表，把住户字段保存到 user_resident_profile 扩展表
 */
async function saveProfile() {
  if (!profileForm.userName.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    const res = await updateUserProfile({
      userName: profileForm.userName.trim(),
      sex: profileForm.sex,
      mail: profileForm.mail.trim(),
      avatar: profileForm.avatar.trim(),
      ownerName: profileForm.ownerName.trim(),
      buildingNo: profileForm.buildingNo.trim(),
      unitNo: profileForm.unitNo.trim(),
      roomNo: profileForm.roomNo.trim(),
      fullAddress: profileForm.fullAddress.trim(),
      emergencyContact: profileForm.emergencyContact.trim(),
      emergencyPhone: profileForm.emergencyPhone.trim()
    })
    auth.updateUser(res?.data || {
      userName: profileForm.userName.trim(),
      avatar: profileForm.avatar.trim()
    })
    ElMessage.success('个人资料已保存')
  } finally {
    saving.value = false
  }
}

/**
 * 上传头像：el-upload 的自定义上传函数
 * 先上传文件，成功后把后端返回的 URL 放进个人资料表单
 * @param {Object} options - el-upload 的自定义上传参数，包含 file 对象
 */
async function uploadAvatar(options) {
  avatarUploading.value = true
  try {
    const res = await uploadUserImage(options.file)
    const avatar = res?.data?.url || ''
    await updateUserAvatar(avatar)
    profileForm.avatar = avatar
    auth.updateUser({ avatar })
    ElMessage.success('头像已上传并保存')
  } finally {
    avatarUploading.value = false
  }
}

/**
 * 移除头像
 */
async function removeAvatar() {
  await updateUserAvatar('')
  profileForm.avatar = ''
  auth.updateUser({ avatar: '' })
  ElMessage.success('头像已移除')
}

/**
 * 加载钱包信息和交易流水
 */
async function loadWallet() {
  walletLoading.value = true
  try {
    const [walletResponse] = await Promise.all([
      getWallet(),
      loadTransactions()
    ])
    wallet.walletNo = walletResponse?.data?.walletNo || ''
    wallet.balance = Number(walletResponse?.data?.balance || 0)
  } finally {
    walletLoading.value = false
  }
}

/**
 * 加载钱包交易流水
 */
async function loadTransactions() {
  transactionLoading.value = true
  try {
    const response = await getWalletTransactions({ page: transactionPage.value, size: transactionSize })
    transactions.value = response?.data?.records || []
    transactionTotal.value = Number(response?.data?.total || 0)
  } finally {
    transactionLoading.value = false
  }
}

/**
 * 加载个人消费账单及汇总
 */
async function loadExpenses() {
  expenseLoading.value = true
  try {
    const [pageResponse, summaryResponse] = await Promise.all([
      getPersonalExpensePage({ page: expensePage.value, size: expenseSize }),
      getPersonalExpenseSummary()
    ])
    expenses.value = pageResponse?.data?.records || []
    expenseTotal.value = Number(pageResponse?.data?.total || 0)
    expenseSummary.totalExpense = Number(summaryResponse?.data?.totalExpense || 0)
    expenseSummary.monthExpense = Number(summaryResponse?.data?.monthExpense || 0)
    expenseSummary.recordCount = Number(summaryResponse?.data?.recordCount || 0)
  } finally {
    expenseLoading.value = false
  }
}

/** 打开”记一笔支出”弹窗，并重置表单 */
function openExpenseDialog() {
  expenseForm.expenseTitle = ''
  expenseForm.category = '购物'
  expenseForm.amount = 10
  expenseForm.expenseTime = localDateTimeValue()
  expenseForm.remark = ''
  expenseVisible.value = true
}

/**
 * 提交个人支出记录
 */
async function submitExpense() {
  if (!expenseForm.expenseTitle) {
    ElMessage.warning('请填写支出名称')
    return
  }
  if (!Number(expenseForm.amount)) {
    ElMessage.warning('请输入支出金额')
    return
  }
  expenseSubmitting.value = true
  try {
    await createPersonalExpense(expenseForm)
    expenseVisible.value = false
    expensePage.value = 1
    await loadExpenses()
    ElMessage.success('支出账单已记录，钱包余额未改变')
  } finally {
    expenseSubmitting.value = false
  }
}

/**
 * 删除手工消费账单
 * @param {Object} row - 账单行数据
 */
async function removeExpense(row) {
  try {
    await ElMessageBox.confirm(`确认删除”${row.expenseTitle}”这条手工账单吗？`, '删除账单', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePersonalExpense(row.id)
    await loadExpenses()
    ElMessage.success('账单已删除')
  } catch (error) {
    if (error !== 'cancel') throw error
  }
}

/** 打开充值弹窗，重置表单 */
function openRecharge() {
  rechargeForm.amount = 100
  rechargeForm.channel = '微信支付'
  rechargeVisible.value = true
}

/**
 * 提交充值请求
 */
async function submitRecharge() {
  if (!Number(rechargeForm.amount)) {
    ElMessage.warning('请输入充值金额')
    return
  }
  walletSubmitting.value = true
  try {
    const response = await rechargeWallet(rechargeForm)
    wallet.balance = Number(response?.data?.balance || 0)
    rechargeVisible.value = false
    transactionPage.value = 1
    await loadTransactions()
    ElMessage.success(`充值成功，已到账 ¥${formatMoney(rechargeForm.amount)}`)
  } finally {
    walletSubmitting.value = false
  }
}

/** 打开转账弹窗，重置表单 */
function openTransfer() {
  transferForm.recipientPhone = ''
  transferForm.amount = Math.min(10, Number(wallet.balance || 0))
  transferForm.remark = ''
  transferVisible.value = true
}

/**
 * 提交转账请求
 */
async function submitTransfer() {
  if (!transferForm.recipientPhone) {
    ElMessage.warning('请输入收款人手机号')
    return
  }
  if (!Number(transferForm.amount)) {
    ElMessage.warning('请输入转账金额')
    return
  }
  walletSubmitting.value = true
  try {
    const response = await transferWallet(transferForm)
    wallet.balance = Number(response?.data?.balance || 0)
    transferVisible.value = false
    transactionPage.value = 1
    await loadTransactions()
    ElMessage.success('转账成功')
  } finally {
    walletSubmitting.value = false
  }
}

/**
 * 交易类型转文本
 * @param {string} type - 交易类型代码
 * @returns {string} 中文描述
 */
function transactionTypeText(type) {
  return ({
    RECHARGE: '充值',
    TRANSFER_IN: '转入',
    TRANSFER_OUT: '转出',
    ORDER_PAYMENT: '商城支付',
    ORDER_REFUND: '订单退款'
  })[type] || '资金变动'
}

/**
 * 交易类型对应的 Tag 样式
 * @param {string} type - 交易类型代码
 * @returns {string} Element Plus Tag 类型
 */
function transactionTagType(type) {
  return ({
    RECHARGE: 'success',
    TRANSFER_IN: 'success',
    TRANSFER_OUT: 'warning',
    ORDER_PAYMENT: 'warning',
    ORDER_REFUND: 'success'
  })[type] || 'info'
}

/**
 * 账单来源类型转文本
 * @param {string} type - 来源类型代码
 * @returns {string} 中文描述
 */
function expenseSourceText(type) {
  return ({ ORDER: '商城自动', ORDER_REFUND: '商城退款', CHARGE: '缴费自动', MANUAL: '手工记录' })[type] || '自动记录'
}

/**
 * 账单来源类型对应的 Tag 样式
 * @param {string} type - 来源类型代码
 * @returns {string} Element Plus Tag 类型
 */
function expenseSourceType(type) {
  return ({ ORDER: 'success', ORDER_REFUND: 'warning', CHARGE: 'primary', MANUAL: 'info' })[type] || 'info'
}

/**
 * 金额格式化，保留两位小数
 * @param {number} value - 金额数值
 * @returns {string} 格式化后的金额字符串
 */
function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}

/**
 * 获取当前时间的本地 ISO 字符串（不含时区）
 * @returns {string} 格式为 YYYY-MM-DDTHH:mm:ss 的本地时间字符串
 */
function localDateTimeValue() {
  const now = new Date()
  return new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 19)
}

/**
 * 上传前的图片校验
 * @param {File} file - 待上传的文件
 * @returns {boolean} 是否通过校验
 */
function beforeImageUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']
  if (!allowed.includes(file.type)) {
    ElMessage.warning('请选择 JPG、PNG、WebP 或 GIF 图片')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过5MB')
    return false
  }
  return true
}

/**
 * 修改密码
 * 校验原密码、新密码长度及一致性，修改成功后需重新登录
 */
async function savePassword() {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写原密码和新密码')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码不能少于6位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }

  passwordSaving.value = true
  try {
    await changeUserPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    auth.logout()
    router.replace({ path: '/portal/login', query: { redirect: '/portal/profile' } })
  } finally {
    passwordSaving.value = false
  }
}

/**
 * 格式化日期为中国本地时间字符串
 * 后端返回 ISO 时间字符串，页面上统一转换为中国本地时间
 * @param {string|null} value - ISO 日期字符串
 * @returns {string} 格式化后的日期字符串
 */
function formatDate(value) {
  if (!value) return '暂无记录'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

/**
 * 从 JWT Token 中获取登录时间
 * 旧会话可能还没有写入 lastLoginTime，此时从 JWT 的 iat（签发时间）中取本次登录时间
 * @returns {string} ISO 格式的时间字符串，解析失败返回空字符串
 */
function getTokenLoginTime() {
  try {
    const token = auth.token
    const payloadPart = token?.split('.')[1]
    if (!payloadPart) return ''
    const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/')
    const payload = JSON.parse(atob(base64))
    return payload.iat ? new Date(payload.iat * 1000).toISOString() : ''
  } catch (e) {
    return ''
  }
}
</script>

<style scoped>
.profile-page {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}

.profile-summary,
.profile-main {
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--surface);
}

.profile-summary {
  padding: 28px 20px 20px;
  text-align: center;
}

.profile-avatar {
  background: var(--accent);
  color: #fff;
  font-size: 30px;
  border: 4px solid var(--accent-bg);
}

.profile-summary h2 {
  margin: 14px 0 2px;
}

.summary-list {
  margin-top: 24px;
  border-top: 1px solid var(--border);
  padding-top: 10px;
}

.summary-item {
  display: grid;
  grid-template-columns: 22px 72px minmax(0, 1fr);
  gap: 8px;
  align-items: center;
  min-height: 44px;
  text-align: left;
  font-size: 13px;
}

.summary-item strong {
  color: var(--text-h);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-main {
  padding: 24px;
  min-width: 0;
}

.page-head h1 {
  margin-bottom: 4px;
}

.account-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-top: 18px;
}

.account-item {
  min-height: 72px;
  padding: 13px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
  box-sizing: border-box;
}

.account-item span,
.account-item strong {
  display: block;
}

.account-item span {
  font-size: 12px;
  color: var(--text);
  margin-bottom: 7px;
}

.account-item strong {
  color: var(--text-h);
  font-size: 14px;
  overflow-wrap: anywhere;
}

.account-item .status-normal {
  color: var(--success);
}

.profile-tabs {
  margin-top: 18px;
}

.profile-form {
  max-width: 860px;
  padding-top: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

.field-help {
  font-size: 12px;
  margin-top: 5px;
}

.resident-panel {
  margin: 8px 0 20px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
}

.section-title {
  margin-bottom: 14px;
}

.section-title h3 {
  margin-bottom: 4px;
}

.section-title p {
  font-size: 13px;
}

.avatar-upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-upload-box {
  width: 104px;
  height: 104px;
  border: 1px dashed var(--accent-border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
  color: var(--accent);
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;
}

.avatar-upload-box:hover {
  border-color: var(--accent);
}

.avatar-upload-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-help strong {
  display: block;
  color: var(--text-h);
  margin-bottom: 4px;
}

.upload-help p {
  font-size: 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 8px;
}

.security-intro {
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 16px 0 22px;
  max-width: 520px;
}

.security-icon {
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius);
  background: var(--accent-bg);
  color: var(--accent);
  font-size: 20px;
}

.security-intro h3 {
  margin-bottom: 3px;
}

.password-form {
  max-width: 460px;
}

.wallet-entry {
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.wallet-entry:hover {
  border-color: var(--accent-border);
  background: var(--accent-bg);
}

.wallet-entry small {
  display: block;
  margin-top: 4px;
  color: var(--accent);
  font-size: 11px;
}

.wallet-panel { padding-top: 14px; }

.wallet-card {
  display: flex;
  min-height: 168px;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
  overflow: hidden;
  border-radius: 18px;
  background:
    radial-gradient(circle at 85% 15%, rgba(255, 255, 255, 0.26), transparent 28%),
    linear-gradient(135deg, #075a9c, #1685d1 58%, #47a9e8);
  color: #fff;
  box-shadow: 0 14px 30px rgba(15, 108, 189, 0.2);
}

.wallet-card-copy > span { font-size: 13px; opacity: 0.82; }
.wallet-card-copy > strong { display: block; margin: 10px 0 8px; font-size: 40px; line-height: 1; letter-spacing: -1px; }
.wallet-card-copy > strong small { margin-right: 5px; font-size: 18px; font-weight: 500; }
.wallet-card-copy p { margin: 0; font-size: 12px; opacity: 0.72; }
.wallet-actions { display: flex; gap: 8px; }
.wallet-actions :deep(.el-button) { min-width: 88px; }
.wallet-actions :deep(.el-button:not(.el-button--primary)) { border-color: rgba(255,255,255,.62); background: rgba(255,255,255,.13); color: #fff; }

.transaction-panel {
  margin-top: 18px;
  padding: 18px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-secondary);
}

.transaction-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 14px;
}

.transaction-head h3 { margin-bottom: 4px; }
.transaction-head p { font-size: 12px; }
.transaction-copy { display: grid; gap: 3px; }
.transaction-copy strong { color: var(--text-h); }
.transaction-copy small { color: var(--text-soft); font-size: 12px; }
.transaction-amount { font-size: 15px; }
.transaction-amount.income { color: var(--success); }
.transaction-amount.expense { color: var(--danger); }
.transaction-pagination { justify-content: flex-end; margin-top: 16px; }

.wallet-dialog-balance {
  margin: -4px 0 18px;
  padding: 12px 14px;
  border-radius: var(--radius);
  background: var(--accent-bg);
  color: var(--text);
}

.wallet-dialog-balance strong { margin-left: 6px; color: var(--accent); font-size: 20px; }
.amount-presets { display: grid; width: 100%; grid-template-columns: repeat(4, 1fr); gap: 8px; margin-bottom: 12px; }
.amount-presets :deep(.el-button) { margin: 0; }
.money-input { width: 100%; }
.virtual-tip { margin: -2px 0 0; color: var(--text-soft); font-size: 12px; }

.expense-panel { padding-top: 14px; }
.expense-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 16px; }
.expense-heading h3 { margin-bottom: 4px; }
.expense-heading p { color: var(--text-soft); font-size: 13px; }
.expense-summary { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 16px; }
.expense-summary > div { padding: 16px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--bg-secondary); }
.expense-summary span, .expense-summary strong { display: block; }
.expense-summary span { margin-bottom: 8px; color: var(--text-soft); font-size: 12px; }
.expense-summary strong { color: var(--text-h); font-size: 22px; }
.expense-table-wrap { overflow: hidden; border: 1px solid var(--border); border-radius: var(--radius); }
.expense-amount { color: var(--danger); font-size: 15px; }
.expense-amount.refund { color: var(--success); }
.auto-record { color: var(--text-soft); font-size: 12px; }
.expense-form { margin-top: 18px; }

@media (max-width: 1024px) {
  .profile-page {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .account-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .wallet-card { align-items: stretch; flex-direction: column; }
  .wallet-actions :deep(.el-button) { flex: 1; }
  .transaction-panel { padding: 12px; overflow-x: auto; }
  .transaction-panel :deep(.el-table) { min-width: 680px; }
  .amount-presets { grid-template-columns: repeat(2, 1fr); }
  .expense-heading { flex-direction: column; }
  .expense-summary { grid-template-columns: 1fr; }
  .expense-table-wrap { overflow-x: auto; }
  .expense-table-wrap :deep(.el-table) { min-width: 760px; }
}
</style>

