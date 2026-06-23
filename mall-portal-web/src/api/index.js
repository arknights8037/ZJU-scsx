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
