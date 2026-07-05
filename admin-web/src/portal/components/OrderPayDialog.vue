<template>
  <el-dialog
    :model-value="modelValue"
    title="订单付款"
    width="min(480px, 92vw)"
    :close-on-click-modal="false"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div v-if="order" class="order-pay-dialog">
      <div class="payment-methods">
        <button type="button" :class="{ active: paymentMethod === 'QR' }" @click="paymentMethod = 'QR'">
          <el-icon><Grid /></el-icon>
          <span><strong>扫码支付</strong><small>微信 / 支付宝模拟支付</small></span>
        </button>
        <button type="button" :class="{ active: paymentMethod === 'WALLET' }" @click="paymentMethod = 'WALLET'">
          <el-icon><Wallet /></el-icon>
          <span><strong>钱包余额</strong><small>可用 ¥{{ formatMoney(walletBalance) }}</small></span>
        </button>
      </div>

      <div v-if="paymentMethod === 'QR'" class="qr-area">
        <p>请扫描二维码完成模拟付款</p>
        <div class="fake-qr" aria-label="模拟付款二维码">
          <span v-for="i in 49" :key="i" :class="{ dark: qrCells(i) }"></span>
        </div>
        <small>课程演示用假二维码，不会产生真实扣款。</small>
      </div>

      <div v-else class="wallet-area" :class="{ insufficient: walletBalance < orderAmount }">
        <el-icon><Wallet /></el-icon>
        <span>钱包可用余额</span>
        <strong>¥{{ formatMoney(walletBalance) }}</strong>
        <p v-if="walletBalance < orderAmount">余额不足，还差 ¥{{ formatMoney(orderAmount - walletBalance) }}</p>
        <p v-else>确认后将从虚拟钱包扣除本次订单金额。</p>
      </div>

      <div class="pay-summary">
        <p><span>订单编号</span><strong>{{ order.orderNo }}</strong></p>
        <p><span>支付金额</span><strong class="price">¥{{ formatMoney(orderAmount) }}</strong></p>
      </div>
    </div>
    <template #footer>
      <el-button @click="emit('update:modelValue', false)">稍后支付</el-button>
      <el-button
        type="success"
        :loading="paying"
        :disabled="paymentMethod === 'WALLET' && walletBalance < orderAmount"
        @click="confirmPayment"
      >
        {{ paymentMethod === 'WALLET' ? '余额支付' : '我已完成支付' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
/**
 * OrderPayDialog - 订单支付弹窗组件
 *
 * 功能：
 * - 提供扫码支付（模拟二维码）和钱包余额支付两种方式
 * - 扫码支付：展示模拟二维码，用户点击"我已完成支付"确认
 * - 钱包支付：验证余额是否充足，使用余额直接扣款
 * - 支付成功后通过 emit('paid') 通知父组件
 *
 * 使用方式：
 * - 通过 v-model 控制弹窗显示/隐藏
 * - 传入 order 对象（需包含 orderNo 和 totalPrice）
 * - 监听 paid 事件处理支付完成后的跳转
 */

import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Grid, Wallet } from '@element-plus/icons-vue'
import { getWallet, payOrder } from '@/portal/api'

// ========== Props / Emits ==========
const props = defineProps({
  modelValue: { type: Boolean, default: false },  // 弹窗显示状态（v-model）
  order: { type: Object, default: null }           // 待支付订单对象
})
const emit = defineEmits(['update:modelValue', 'paid'])

// ========== 响应式状态 ==========
const paymentMethod = ref('QR')       // 当前选中的支付方式：'QR'（扫码）或 'WALLET'（钱包）
const walletBalance = ref(0)          // 钱包可用余额
const paying = ref(false)             // 支付进行中

/** 订单金额（计算属性） */
const orderAmount = computed(() => Number(props.order?.totalPrice || 0))

/**
 * 监听弹窗打开事件：重置支付方式并加载余额
 */
watch(() => props.modelValue, async visible => {
  if (!visible) return
  paymentMethod.value = 'QR'
  try {
    const response = await getWallet()
    walletBalance.value = Number(response?.data?.balance || 0)
  } catch (error) {
    walletBalance.value = 0
  }
})

/**
 * 确认支付
 * - 扫码支付：直接调用 payOrder，传入 'QR'
 * - 钱包支付：需校验余额充足，然后调用 payOrder 传入 'WALLET'，并扣减本地余额
 */
async function confirmPayment() {
  if (!props.order?.orderNo) return
  if (paymentMethod.value === 'WALLET' && walletBalance.value < orderAmount.value) {
    ElMessage.warning('钱包余额不足，请先充值')
    return
  }
  paying.value = true
  try {
    await payOrder(props.order.orderNo, paymentMethod.value)
    if (paymentMethod.value === 'WALLET') walletBalance.value -= orderAmount.value
    ElMessage.success(paymentMethod.value === 'WALLET' ? '余额支付成功' : '付款成功')
    emit('paid', { orderNo: props.order.orderNo, paymentMethod: paymentMethod.value })
    emit('update:modelValue', false)
  } finally {
    paying.value = false
  }
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
 * 生成模拟二维码的单元格明暗
 * 在 7x7 网格中，角落区域固定为深色，其余按算法随机填充
 * @param {number} index - 单元格索引（1-49）
 * @returns {boolean} true 为深色，false 为浅色
 */
function qrCells(index) {
  const row = Math.ceil(index / 7)
  const col = ((index - 1) % 7) + 1
  const inCorner = (row <= 2 && col <= 2) || (row <= 2 && col >= 6) || (row >= 6 && col <= 2)
  return inCorner || ((row * 3 + col * 5 + index) % 4 === 0)
}
</script>

<style scoped>
.order-pay-dialog { display: grid; gap: 16px; }
.payment-methods { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.payment-methods button { display: flex; min-width: 0; align-items: center; gap: 10px; padding: 13px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface); color: var(--text); text-align: left; cursor: pointer; }
.payment-methods button.active { border-color: var(--accent); background: var(--accent-bg); color: var(--accent); box-shadow: inset 0 0 0 1px var(--accent); }
.payment-methods .el-icon { flex: 0 0 auto; font-size: 22px; }
.payment-methods span { display: grid; min-width: 0; gap: 2px; }
.payment-methods strong { color: var(--text-h); font-size: 14px; }
.payment-methods small { overflow: hidden; color: var(--text-soft); font-size: 11px; text-overflow: ellipsis; white-space: nowrap; }
.qr-area { display: grid; justify-items: center; gap: 10px; padding: 14px; text-align: center; }
.qr-area p { color: var(--text-h); font-weight: 600; }
.qr-area > small { color: var(--text-soft); }
.fake-qr { display: grid; grid-template-columns: repeat(7, 16px); grid-auto-rows: 16px; gap: 4px; padding: 16px; border: 1px solid var(--border); border-radius: var(--radius); background: #fff; }
.fake-qr span { border-radius: 2px; background: #eef2f7; }
.fake-qr span.dark { background: #111827; }
.wallet-area { display: grid; justify-items: center; gap: 6px; padding: 24px 16px; border-radius: var(--radius); background: linear-gradient(135deg, var(--accent-bg), rgba(71, 169, 232, .12)); color: var(--accent); }
.wallet-area .el-icon { font-size: 30px; }
.wallet-area span { color: var(--text); font-size: 13px; }
.wallet-area strong { font-size: 30px; }
.wallet-area p { color: var(--text-soft); font-size: 12px; }
.wallet-area.insufficient { background: rgba(196, 43, 28, .08); color: var(--danger); }
.wallet-area.insufficient p { color: var(--danger); }
.pay-summary { display: grid; gap: 8px; padding: 12px; border: 1px solid var(--border); border-radius: var(--radius); background: var(--surface-muted); }
.pay-summary p { display: flex; justify-content: space-between; gap: 12px; }
.pay-summary strong { overflow-wrap: anywhere; }
@media (max-width: 520px) { .payment-methods { grid-template-columns: 1fr; } }
</style>
