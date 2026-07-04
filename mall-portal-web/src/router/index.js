import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
      { path: 'goods', name: 'GoodsList', component: () => import('@/views/GoodsList.vue'), meta: { title: '商品列表' } },
      { path: 'goods/:goodsNo', name: 'GoodsDetail', component: () => import('@/views/GoodsDetail.vue'), meta: { title: '商品详情' } },
      { path: 'community', name: 'Community', component: () => import('@/views/Community.vue'), meta: { title: '社区服务' } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心' } },
      { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue'), meta: { title: '购物车' } },
      { path: 'order', name: 'Order', component: () => import('@/views/Order.vue'), meta: { title: '我的订单' } },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
