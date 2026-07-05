/**
 * 居民端 API 接口模块。
 * 居民端所有 API 调用集中在此文件中，页面组件不直接拼接后端 URL。
 * 鉴权用户身份统一由 Token 决定，函数参数里尽量不暴露 userId。
 */
import request from './request'

// ========== 商品 ==========

/**
 * 分页查询商品列表。
 * @param {Object} params - 查询参数（page、size、categoryId、keyword 等）
 * @returns {Promise} 返回分页商品数据
 */
export function getGoodsPage(params) {
  return request.get('/goods/page', { params })
}

/**
 * 获取商品详情。
 * @param {string} goodsNo - 商品编号
 * @returns {Promise} 返回商品详细信息
 */
export function getGoodsDetail(goodsNo) {
  return request.get(`/goods/${goodsNo}`)
}

/**
 * 获取商品图片列表。
 * @param {string} goodsNo - 商品编号
 * @returns {Promise} 返回商品图片 URL 列表
 */
export function getGoodsPictures(goodsNo) {
  return request.get(`/goods/${goodsNo}/pictures`)
}

/**
 * 获取有售该商品的门店列表。
 * @param {string} goodsNo - 商品编号
 * @returns {Promise} 返回门店列表
 */
export function getGoodsStores(goodsNo) {
  return request.get(`/goods/${goodsNo}/stores`)
}

/**
 * 记录商品点击行为。
 * 点击记录只认 Token 中的用户身份，前端不需要传 userId。
 * @param {string} goodsNo - 商品编号
 * @returns {Promise} 返回操作结果
 */
export function recordGoodsClick(goodsNo) {
  return request.post(`/goods/${goodsNo}/click`)
}

/**
 * 获取商品推荐列表。
 * @param {Object} params - 推荐查询参数
 * @returns {Promise} 返回推荐商品列表
 */
export function getGoodsRecommendations(params) {
  return request.get('/goods/recommend', { params })
}

// ========== 分类 ==========

/**
 * 获取全部分类列表。
 * @returns {Promise} 返回分类列表数据
 */
export function getCategories() {
  return request.get('/category/all')
}

// ========== 门店 ==========

/**
 * 根据区域获取门店列表。
 * @param {number|string} areaId - 区域 ID
 * @returns {Promise} 返回门店列表
 */
export function getStores(areaId) {
  return request.get('/store/list', { params: { areaId } })
}

// ========== 用户 ==========

/**
 * 居民端登录。
 * @param {string} phone - 手机号
 * @param {string} password - 密码
 * @returns {Promise} 返回登录结果，包含 token 和用户信息
 */
export function login(phone, password) {
  return request.post('/user/login', { phone, password })
}

/**
 * 居民端注册。
 * @param {Object} data - 注册信息（phone、password、userName 等）
 * @returns {Promise} 返回注册结果
 */
export function register(data) {
  return request.post('/user/register', data)
}

/**
 * 获取当前登录用户资料。
 * userId 由后端从 Token 中识别，前端无需传入。
 * @returns {Promise} 返回用户资料对象
 */
export function getUserProfile() {
  return request.get('/user/profile')
}

/**
 * 更新当前登录用户资料。
 * @param {Object} data - 要更新的用户资料字段
 * @returns {Promise} 返回操作结果
 */
export function updateUserProfile(data) {
  return request.put('/user/profile', data)
}

/**
 * 更新用户头像。
 * @param {string} avatar - 头像 URL
 * @returns {Promise} 返回操作结果
 */
export function updateUserAvatar(avatar) {
  return request.put('/user/avatar', { avatar })
}

/**
 * 修改登录密码。
 * @param {Object} data - 密码数据（oldPassword、newPassword）
 * @returns {Promise} 返回操作结果
 */
export function changeUserPassword(data) {
  return request.put('/user/password', data)
}

/**
 * 上传用户图片（头像等）。
 * 使用 FormData 以 multipart/form-data 格式提交文件。
 * @param {File} file - 待上传的文件对象
 * @returns {Promise} 返回上传结果，包含图片 URL
 */
export function uploadUserImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/image', formData)
}

// ========== 购物车 ==========

/**
 * 获取购物车列表。
 * @returns {Promise} 返回购物车商品列表
 */
export function getCartList() {
  return request.get('/cart/list')
}

/**
 * 添加商品到购物车。
 * @param {Object} data - 添加数据（goodsNo、amount 等）
 * @returns {Promise} 返回操作结果
 */
export function addToCart(data) {
  return request.post('/cart/add', data)
}

/**
 * 更新购物车中某个商品的数量。
 * @param {number|string} id - 购物车记录 ID
 * @param {number} amount - 更新后的数量
 * @returns {Promise} 返回操作结果
 */
export function updateCartAmount(id, amount) {
  return request.put(`/cart/${id}`, null, { params: { amount } })
}

/**
 * 从购物车中移除指定商品。
 * @param {number|string} id - 购物车记录 ID
 * @returns {Promise} 返回操作结果
 */
export function removeFromCart(id) {
  return request.delete(`/cart/${id}`)
}

// ========== 虚拟钱包 ==========

/**
 * 获取虚拟钱包信息（余额等）。
 * @returns {Promise} 返回钱包信息
 */
export function getWallet() {
  return request.get('/wallet')
}

/**
 * 获取钱包交易流水列表。
 * @param {Object} params - 查询参数（page、size 等）
 * @returns {Promise} 返回交易流水列表
 */
export function getWalletTransactions(params) {
  return request.get('/wallet/transactions', { params })
}

/**
 * 钱包充值。
 * @param {Object} data - 充值数据（amount、paymentMethod 等）
 * @returns {Promise} 返回充值结果
 */
export function rechargeWallet(data) {
  return request.post('/wallet/recharge', data)
}

/**
 * 钱包转账。
 * @param {Object} data - 转账数据（targetUserId、amount 等）
 * @returns {Promise} 返回转账结果
 */
export function transferWallet(data) {
  return request.post('/wallet/transfer', data)
}

// ========== 个人消费账单（与钱包流水独立） ==========

/**
 * 分页查询个人消费账单。
 * @param {Object} params - 查询参数（page、size、dateRange 等）
 * @returns {Promise} 返回分页账单数据
 */
export function getPersonalExpensePage(params) {
  return request.get('/personal-expense/page', { params })
}

/**
 * 获取个人消费汇总统计。
 * @returns {Promise} 返回消费汇总数据
 */
export function getPersonalExpenseSummary() {
  return request.get('/personal-expense/summary')
}

/**
 * 创建个人消费记录。
 * @param {Object} data - 消费记录数据
 * @returns {Promise} 返回操作结果
 */
export function createPersonalExpense(data) {
  return request.post('/personal-expense', data)
}

/**
 * 删除指定个人消费记录。
 * @param {number|string} id - 消费记录 ID
 * @returns {Promise} 返回操作结果
 */
export function deletePersonalExpense(id) {
  return request.delete(`/personal-expense/${id}`)
}

// ========== 订单 ==========

/**
 * 分页查询订单列表。
 * @param {Object} params - 查询参数（page、size、status 等）
 * @returns {Promise} 返回分页订单数据
 */
export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

/**
 * 从购物车创建订单（批量下单）。
 * @param {number[]} cartIds - 购物车记录 ID 数组
 * @returns {Promise} 返回创建结果，包含订单号
 */
export function createOrder(cartIds) {
  return request.post('/order/create', cartIds)
}

/**
 * 立即购买（跳过购物车，直接下单）。
 * @param {Object} data - 下单数据（goodsNo、amount、storeNo 等）
 * @returns {Promise} 返回创建结果，包含订单号
 */
export function buyNowOrder(data) {
  return request.post('/order/buy-now', data)
}

/**
 * 获取指定订单的详细信息。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回订单详情
 */
export function getOrderDetails(orderNo) {
  return request.get(`/order/${orderNo}/details`)
}

/**
 * 支付订单。
 * @param {string} orderNo - 订单号
 * @param {string} [paymentMethod='QR'] - 支付方式，默认二维码支付
 * @returns {Promise} 返回支付结果
 */
export function payOrder(orderNo, paymentMethod = 'QR') {
  return request.put(`/order/${orderNo}/pay`, { paymentMethod })
}

/**
 * 取消订单。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回操作结果
 */
export function cancelOrder(orderNo) {
  return request.put(`/order/${orderNo}/cancel`)
}

/**
 * 签收订单（确认已收到商品）。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回操作结果
 */
export function signOrder(orderNo) {
  return request.put(`/order/${orderNo}/sign`)
}

/**
 * 确认收货（与签收类似，触发后续结算流程）。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回操作结果
 */
export function confirmOrderReceipt(orderNo) {
  return request.put(`/order/${orderNo}/confirm-receipt`)
}

/**
 * 申请订单退款。
 * @param {string} orderNo - 订单号
 * @param {Object} data - 退款原因等数据
 * @returns {Promise} 返回操作结果
 */
export function requestOrderRefund(orderNo, data) {
  return request.post(`/order/${orderNo}/refund`, data)
}

// ========== 收藏 ==========

/**
 * 获取收藏列表。
 * @returns {Promise} 返回收藏商品列表
 */
export function getFavorites() {
  return request.get('/favorite/list')
}

/**
 * 添加收藏。
 * @param {string} goodsNo - 商品编号
 * @param {string} [storeNo] - 门店编号（可选）
 * @returns {Promise} 返回操作结果
 */
export function addFavorite(goodsNo, storeNo) {
  return request.post('/favorite/add', null, { params: { goodsNo, storeNo } })
}

/**
 * 取消收藏。
 * @param {string} goodsNo - 商品编号
 * @returns {Promise} 返回操作结果
 */
export function removeFavorite(goodsNo) {
  return request.delete('/favorite/remove', { params: { goodsNo } })
}

// ========== 促销 ==========

/**
 * 获取促销活动列表。
 * @returns {Promise} 返回促销活动列表
 */
export function getSpecialList() {
  return request.get('/special/list')
}

/**
 * 获取指定促销活动中的商品列表。
 * @param {number|string} specialId - 促销活动 ID
 * @returns {Promise} 返回促销商品列表
 */
export function getSpecialGoods(specialId) {
  return request.get(`/special/${specialId}/goods`)
}

// ========== 社区服务 ==========

/**
 * 分页查询社区通知公告。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页通知数据
 */
export function getCommunityNotices(params) {
  return request.get('/community/notice/page', { params })
}

/**
 * 分页查询社区投诉报修记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页投诉数据
 */
export function getCommunityComplaints(params) {
  return request.get('/community/complaint/page', { params })
}

/**
 * 创建社区投诉报修记录。
 * @param {Object} data - 投诉报修数据
 * @returns {Promise} 返回操作结果
 */
export function createCommunityComplaint(data) {
  return request.post('/community/complaint', data)
}

/**
 * 分页查询社区访客记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页访客数据
 */
export function getCommunityVisitors(params) {
  return request.get('/community/visitor/page', { params })
}

/**
 * 创建社区访客邀请/登记。
 * @param {Object} data - 访客数据
 * @returns {Promise} 返回操作结果
 */
export function createCommunityVisitor(data) {
  return request.post('/community/visitor', data)
}

/**
 * 分页查询社区缴费记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页缴费数据
 */
export function getCommunityCharges(params) {
  return request.get('/community/charge/page', { params })
}

/**
 * 获取指定缴费单的明细。
 * @param {string} chargeNo - 缴费单号
 * @returns {Promise} 返回缴费明细数据
 */
export function getCommunityChargeDetails(chargeNo) {
  return request.get(`/community/charge/${chargeNo}/details`)
}

/**
 * 支付社区缴费单。
 * @param {string} chargeNo - 缴费单号
 * @returns {Promise} 返回支付结果
 */
export function payCommunityCharge(chargeNo) {
  return request.put(`/community/charge/${chargeNo}/pay`)
}

/**
 * 分页查询社区车位信息。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页车位数据
 */
export function getCommunityParkings(params) {
  return request.get('/community/parking/page', { params })
}

/**
 * 获取指定车位的绑定信息（绑定的车辆/业主）。
 * @param {number|string} parkingId - 车位 ID
 * @returns {Promise} 返回绑定信息列表
 */
export function getCommunityParkingBinds(parkingId) {
  return request.get(`/community/parking/${parkingId}/binds`)
}

