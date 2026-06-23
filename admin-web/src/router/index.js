import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页' } },
      { path: 'auth/role', name: 'RoleMgmt', component: () => import('@/views/RoleMgmt.vue'), meta: { title: '角色管理' } },
      { path: 'auth/user', name: 'UserMgmt', component: () => import('@/views/UserMgmt.vue'), meta: { title: '用户管理' } },
      { path: 'goods/category', name: 'CategoryMgmt', component: () => import('@/views/CategoryMgmt.vue'), meta: { title: '类别管理' } },
      { path: 'goods/index', name: 'GoodsMgmt', component: () => import('@/views/GoodsMgmt.vue'), meta: { title: '商品管理' } },
      { path: 'area/index', name: 'AreaMgmt', component: () => import('@/views/AreaMgmt.vue'), meta: { title: '区域管理' } },
      { path: 'area/store', name: 'StoreMgmt', component: () => import('@/views/StoreMgmt.vue'), meta: { title: '门店管理' } },
      { path: 'order/index', name: 'OrderMgmt', component: () => import('@/views/OrderMgmt.vue'), meta: { title: '订单管理' } },
      { path: 'special/index', name: 'SpecialMgmt', component: () => import('@/views/SpecialMgmt.vue'), meta: { title: '促销管理' } },
      { path: 'community/notice', name: 'NoticeMgmt', component: () => import('@/views/NoticeMgmt.vue'), meta: { title: '通知公告' } },
      { path: 'community/parking', name: 'ParkingMgmt', component: () => import('@/views/ParkingMgmt.vue'), meta: { title: '车位管理' } },
      { path: 'community/visitor', name: 'VisitorMgmt', component: () => import('@/views/VisitorMgmt.vue'), meta: { title: '访客记录' } },
      { path: 'community/complaint', name: 'ComplaintMgmt', component: () => import('@/views/ComplaintMgmt.vue'), meta: { title: '报事报修' } },
      { path: 'community/charge', name: 'ChargeMgmt', component: () => import('@/views/ChargeMgmt.vue'), meta: { title: '缴纳记录' } },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
]

const router = createRouter({ history: createWebHistory(), routes })
export default router
