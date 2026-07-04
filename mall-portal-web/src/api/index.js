import request from './request'

// ========== 商品 ==========
export function getGoodsPage(params) {
  return request.get('/goods/page', { params })
}

export function getGoodsDetail(goodsNo) {
  return request.get(`/goods/${goodsNo}`)
}

export function getGoodsPictures(goodsNo) {
  return request.get(`/goods/${goodsNo}/pictures`)
}

// ========== 分类 ==========
export function getCategories() {
  return request.get('/category/all')
}

// ========== 门店 ==========
export function getStores(areaId) {
  return request.get('/store/list', { params: { areaId } })
}

// ========== 用户 ==========
export function login(phone, password) {
  return request.post('/user/login', { phone, password })
}

export function register(data) {
  return request.post('/user/register', data)
}

// ========== 购物车 ==========
export function getCartList(userId) {
  return request.get('/cart/list', { params: { userId } })
}

export function addToCart(data) {
  return request.post('/cart/add', data)
}

export function updateCartAmount(id, amount) {
  return request.put(`/cart/${id}`, null, { params: { amount } })
}

export function removeFromCart(id) {
  return request.delete(`/cart/${id}`)
}

// ========== 订单 ==========
export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function createOrder(userId, items) {
  return request.post(`/order/create?userId=${userId}`, items)
}

// ========== 收藏 ==========
export function getFavorites(userId) {
  return request.get('/favorite/list', { params: { userId } })
}

export function addFavorite(userId, goodsNo, storeNo) {
  return request.post('/favorite/add', null, { params: { userId, goodsNo, storeNo } })
}

export function removeFavorite(userId, goodsNo) {
  return request.delete('/favorite/remove', { params: { userId, goodsNo } })
}

// ========== 促销 ==========
export function getSpecialList() {
  return request.get('/special/list')
}

export function getSpecialGoods(specialId) {
  return request.get(`/special/${specialId}/goods`)
}

// ========== 社区服务 ==========
export function getCommunityNotices(params) {
  return request.get('/community/notice/page', { params })
}

export function getCommunityComplaints(params) {
  return request.get('/community/complaint/page', { params })
}

export function createCommunityComplaint(data) {
  return request.post('/community/complaint', data)
}

export function getCommunityVisitors(params) {
  return request.get('/community/visitor/page', { params })
}

export function createCommunityVisitor(data) {
  return request.post('/community/visitor', data)
}

export function getCommunityCharges(params) {
  return request.get('/community/charge/page', { params })
}

export function getCommunityChargeDetails(chargeNo) {
  return request.get(`/community/charge/${chargeNo}/details`)
}

export function payCommunityCharge(chargeNo) {
  return request.put(`/community/charge/${chargeNo}/pay`)
}

export function getCommunityParkings(params) {
  return request.get('/community/parking/page', { params })
}

export function getCommunityParkingBinds(parkingId) {
  return request.get(`/community/parking/${parkingId}/binds`)
}
